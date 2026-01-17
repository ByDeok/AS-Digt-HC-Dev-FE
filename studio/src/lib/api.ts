import axios, { type AxiosRequestConfig } from 'axios';
import { ulid } from 'ulid';
import type { TokenResponse } from '@/lib/auth';
import { clearAuthSession, getAccessToken, getRefreshToken, setAuthSession } from '@/lib/auth';
import { logRequest, logResponse, logError } from '@/lib/apiLogger';

type ApiResponse<T> = {
  success: boolean;
  message: string;
  data?: T;
  timestamp: string;
};

type RetryableRequestConfig = AxiosRequestConfig & { _retry?: boolean };

type RefreshSubscriber = (token: string | null) => void;

const REQUEST_ID_STORAGE_KEY = 'sessionRequestId';

const baseURL = import.meta.env.VITE_API_URL || import.meta.env.VITE_API_BASE_URL || '/api';

const authPaths = ['/v1/auth/login', '/v1/auth/signup', '/v1/auth/refresh'];

function isAuthEndpoint(url?: string) {
  if (!url) return false;
  return authPaths.some((path) => url.includes(path));
}

function getSessionRequestId() {
  if (typeof window === 'undefined') {
    return ulid();
  }

  try {
    const existing = window.sessionStorage.getItem(REQUEST_ID_STORAGE_KEY);
    if (existing) return existing;

    // ULID는 시간 정보를 포함하므로 정렬에 유리함
    const newRequestId = ulid();
    window.sessionStorage.setItem(REQUEST_ID_STORAGE_KEY, newRequestId);
    return newRequestId;
  } catch {
    return ulid();
  }
}

const api = axios.create({
  // 개발: Vite 프록시(/api -> BE) 사용
  // 배포/통합: 같은 오리진에서 /api로 호출 가능
  // 환경변수 호환:
  // - 신규: VITE_API_URL
  // - 문서/기존: VITE_API_BASE_URL
  baseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

let isRefreshing = false;
let refreshSubscribers: RefreshSubscriber[] = [];

function subscribeTokenRefresh(callback: RefreshSubscriber) {
  refreshSubscribers.push(callback);
}

function notifyTokenRefresh(token: string | null) {
  refreshSubscribers.forEach((callback) => callback(token));
  refreshSubscribers = [];
}

async function refreshAccessToken() {
  const refreshToken = getRefreshToken();
  if (!refreshToken) {
    throw new Error('리프레시 토큰이 없습니다.');
  }
  
  const requestId = getSessionRequestId();
  const refreshRequest = { refreshToken };
  const requestStartTime = logRequest(
    'POST',
    '/v1/auth/refresh',
    { 'Content-Type': 'application/json', 'X-Request-ID': requestId },
    refreshRequest,
    undefined,
    requestId
  );

  try {
    const res = await axios.post<ApiResponse<TokenResponse>>(
      '/v1/auth/refresh',
      refreshRequest,
      {
        baseURL,
        timeout: 10000,
        headers: {
          'Content-Type': 'application/json',
          'X-Request-ID': requestId,
        },
      }
    );
    
    // 토큰 갱신 응답 로깅
    logResponse(
      'POST',
      '/v1/auth/refresh',
      res.status,
      res.statusText,
      res.data,
      res.headers as Record<string, unknown>,
      requestStartTime,
      requestId
    );
    
    if (!res.data?.success || !res.data.data) {
      throw new Error(res.data?.message || '토큰 갱신에 실패했습니다.');
    }
    return res.data.data;
  } catch (error: any) {
    // 토큰 갱신 에러 로깅
    logError('POST', '/v1/auth/refresh', error, requestStartTime, requestId);
    throw error;
  }
}

function redirectToLogin() {
  if (typeof window !== 'undefined') {
    window.location.href = '/login';
  }
}

api.interceptors.request.use(
  (config) => {
    // 로그인 후 저장된 토큰이 있으면 Authorization 헤더 자동 부착
    const token = getAccessToken();
    if (token) {
      config.headers = config.headers ?? {};
      config.headers.Authorization = `Bearer ${token}`;
    }

    const requestId = getSessionRequestId();
    config.headers = config.headers ?? {};
    config.headers['X-Request-ID'] = requestId;

    // API 요청 로깅
    const requestStartTime = logRequest(
      config.method?.toUpperCase() || 'GET',
      config.url || '',
      config.headers as Record<string, unknown>,
      config.data,
      config.params,
      requestId
    );
    
    // Duration 계산을 위해 시작 시간 저장
    (config as AxiosRequestConfig & { _requestStartTime?: number; _requestId?: string })._requestStartTime = requestStartTime;
    (config as AxiosRequestConfig & { _requestId?: string })._requestId = requestId;

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => {
    // API 응답 로깅 (성공)
    const requestStartTime = (response.config as AxiosRequestConfig & { _requestStartTime?: number })._requestStartTime;
    const requestId = (response.config as AxiosRequestConfig & { _requestId?: string })._requestId;

    logResponse(
      response.config.method?.toUpperCase() || 'GET',
      response.config.url || '',
      response.status,
      response.statusText,
      response.data,
      response.headers as Record<string, unknown>,
      requestStartTime,
      requestId
    );

    return response;
  },
  async (error) => {
    // API 에러 로깅
    const requestStartTime = (error.config as AxiosRequestConfig & { _requestStartTime?: number })?._requestStartTime;
    const requestId = (error.config as AxiosRequestConfig & { _requestId?: string })?._requestId;
    
    if (error.response) {
      // HTTP 에러 응답이 있는 경우
      logResponse(
        error.config?.method?.toUpperCase() || 'GET',
        error.config?.url || '',
        error.response.status,
        error.response.statusText,
        error.response.data,
        error.response.headers as Record<string, unknown>,
        requestStartTime,
        requestId
      );
    } else if (error.config) {
      // 네트워크 에러 등 응답이 없는 경우
      logError(
        error.config.method?.toUpperCase() || 'GET',
        error.config.url || '',
        error,
        requestStartTime,
        requestId
      );
    } else {
      // 설정 정보도 없는 경우
      logError('UNKNOWN', 'UNKNOWN', error, requestStartTime, requestId);
    }
    const originalRequest = error?.config as RetryableRequestConfig | undefined;

    if (error?.response?.status === 401 && originalRequest && !originalRequest._retry && !isAuthEndpoint(originalRequest.url)) {
      const refreshToken = getRefreshToken();
      if (!refreshToken) {
        clearAuthSession();
        redirectToLogin();
        return Promise.reject(error);
      }

      if (isRefreshing) {
        originalRequest._retry = true;
        return new Promise((resolve, reject) => {
          subscribeTokenRefresh((token) => {
            if (!token) {
              reject(error);
              return;
            }
            originalRequest.headers = originalRequest.headers ?? {};
            originalRequest.headers.Authorization = `Bearer ${token}`;
            resolve(api(originalRequest));
          });
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;
      try {
        const tokenResponse = await refreshAccessToken();
        setAuthSession(tokenResponse);
        notifyTokenRefresh(tokenResponse.accessToken);
        originalRequest.headers = originalRequest.headers ?? {};
        originalRequest.headers.Authorization = `Bearer ${tokenResponse.accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        notifyTokenRefresh(null);
        clearAuthSession();
        redirectToLogin();
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    if (error?.response?.status === 401 && !isAuthEndpoint(originalRequest?.url)) {
      clearAuthSession();
      redirectToLogin();
    }

    return Promise.reject(error);
  }
);

export default api;




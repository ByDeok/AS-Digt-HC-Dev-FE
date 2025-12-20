import axios from 'axios';
import { clearAuthSession, getAccessToken } from '@/lib/auth';

const api = axios.create({
  // 개발: Vite 프록시(/api -> BE) 사용
  // 배포/통합: 같은 오리진에서 /api로 호출 가능
  // 환경변수 호환:
  // - 신규: VITE_API_URL
  // - 문서/기존: VITE_API_BASE_URL
  baseURL: import.meta.env.VITE_API_URL || import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    // 로그인 후 저장된 토큰이 있으면 Authorization 헤더 자동 부착
    const token = getAccessToken();
    if (token) {
      config.headers = config.headers ?? {};
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // 토큰이 만료/무효일 때는 세션을 정리해서 UI가 로그인으로 유도될 수 있게 함
    if (error?.response?.status === 401) {
      clearAuthSession();
    }
    return Promise.reject(error);
  }
);

export default api;




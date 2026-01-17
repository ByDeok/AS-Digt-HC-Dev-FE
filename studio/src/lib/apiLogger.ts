/**
 * API 요청/응답 로깅 유틸리티
 * 
 * 백엔드 API 호출 시 요청, 응답, 에러를 로깅하는 유틸리티 함수들
 */

type LogLevel = 'info' | 'warn' | 'error';

interface LogConfig {
  enabled: boolean;
  level: LogLevel;
}

// 환경 변수 기반 기본 설정
const defaultConfig: LogConfig = {
  enabled: import.meta.env.VITE_API_LOGGING_ENABLED === 'true' || import.meta.env.DEV,
  level: 'info',
};

let config: LogConfig = { ...defaultConfig };

/**
 * 로깅 설정 업데이트
 */
export function setLogConfig(newConfig: Partial<LogConfig>) {
  config = { ...config, ...newConfig };
}

/**
 * 로깅 활성화/비활성화
 */
export function setLoggingEnabled(enabled: boolean) {
  config.enabled = enabled;
}

/**
 * 현재 로깅 설정 조회
 */
export function getLogConfig(): Readonly<LogConfig> {
  return { ...config };
}

/**
 * 민감한 정보 마스킹 (토큰, 비밀번호 등)
 */
function maskSensitiveData(data: unknown): unknown {
  if (typeof data !== 'object' || data === null) {
    return data;
  }

  if (Array.isArray(data)) {
    return data.map(maskSensitiveData);
  }

  const masked = { ...(data as Record<string, unknown>) };
  const sensitiveKeys = ['password', 'accessToken', 'refreshToken', 'token', 'authorization'];

  for (const key in masked) {
    const lowerKey = key.toLowerCase();
    if (sensitiveKeys.some((sk) => lowerKey.includes(sk))) {
      masked[key] = '***MASKED***';
    } else if (typeof masked[key] === 'object' && masked[key] !== null) {
      masked[key] = maskSensitiveData(masked[key]);
    }
  }

  return masked;
}

/**
 * 데이터 포맷팅 (JSON 문자열로 변환)
 */
function formatData(data: unknown): string {
  try {
    const masked = maskSensitiveData(data);
    return JSON.stringify(masked, null, 2);
  } catch (e) {
    return String(data);
  }
}

/**
 * 헤더 포맷팅 (민감한 정보 마스킹)
 */
function formatHeaders(headers: Record<string, unknown> | undefined): string {
  if (!headers) {
    return '{}';
  }

  try {
    const masked = maskSensitiveData(headers);
    return JSON.stringify(masked, null, 2);
  } catch (e) {
    return String(headers);
  }
}

/**
 * 요청 로깅
 * 
 * @param method HTTP 메서드 (GET, POST, PUT, DELETE 등)
 * @param url 요청 URL
 * @param headers 요청 헤더 (선택)
 * @param data 요청 본문 데이터 (선택)
 * @param params 쿼리 파라미터 (선택)
 * @returns 요청 시작 시간 (Duration 계산용)
 */
export function logRequest(
  method: string,
  url: string,
  headers?: Record<string, unknown>,
  data?: unknown,
  params?: unknown,
  requestId?: string
): number {
  if (!config.enabled) {
    return Date.now();
  }

  const timestamp = new Date().toISOString();
  const requestStartTime = Date.now();

  const requestIdLabel = requestId ? `{${requestId}}` : '{-}';
  console.group(`${requestIdLabel} 🚀 [Request] ${method.toUpperCase()} ${url}`);
  console.log('🆔 Request ID:', requestId ?? '-');
  console.log('📅 Timestamp:', timestamp);
  
  if (headers && Object.keys(headers).length > 0) {
    console.log('📋 Headers:', formatHeaders(headers));
  }
  
  if (params) {
    console.log('🔍 Query Params:', formatData(params));
  }
  
  if (data) {
    console.log('📦 Request Body:', formatData(data));
  }
  
  console.groupEnd();

  return requestStartTime;
}

/**
 * 응답 로깅
 * 
 * @param method HTTP 메서드
 * @param url 요청 URL
 * @param status HTTP 상태 코드
 * @param statusText HTTP 상태 텍스트
 * @param data 응답 데이터
 * @param headers 응답 헤더 (선택)
 * @param requestStartTime 요청 시작 시간 (logRequest에서 반환된 값)
 */
export function logResponse(
  method: string,
  url: string,
  status: number,
  statusText: string,
  data: unknown,
  headers?: Record<string, unknown>,
  requestStartTime?: number,
  requestId?: string
): void {
  if (!config.enabled) {
    return;
  }

  const timestamp = new Date().toISOString();
  const duration = requestStartTime ? Date.now() - requestStartTime : undefined;

  const isError = status >= 400;
  const emoji = isError ? '❌' : '✅';
  const statusColor = isError ? 'color: red' : status >= 300 ? 'color: orange' : 'color: green';

  const requestIdLabel = requestId ? `{${requestId}}` : '{-}';
  console.group(`${requestIdLabel} ${emoji} [Response] ${method.toUpperCase()} ${url}`);
  console.log('🆔 Request ID:', requestId ?? '-');
  console.log('📅 Timestamp:', timestamp);
  console.log(`%cStatus: ${status} ${statusText}`, statusColor);
  
  if (duration !== undefined) {
    const durationColor = duration > 1000 ? 'color: orange' : 'color: green';
    console.log(`%c⏱️  Duration: ${duration}ms`, durationColor);
  }
  
  if (headers && Object.keys(headers).length > 0) {
    console.log('📋 Headers:', formatHeaders(headers));
  }
  
  if (data) {
    console.log('📦 Response Body:', formatData(data));
  }
  
  console.groupEnd();
}

/**
 * 에러 로깅
 * 
 * @param method HTTP 메서드
 * @param url 요청 URL
 * @param error 에러 객체
 * @param requestStartTime 요청 시작 시간 (logRequest에서 반환된 값)
 */
export function logError(
  method: string,
  url: string,
  error: unknown,
  requestStartTime?: number,
  requestId?: string
): void {
  if (!config.enabled) {
    return;
  }

  const timestamp = new Date().toISOString();
  const duration = requestStartTime ? Date.now() - requestStartTime : undefined;

  const requestIdLabel = requestId ? `{${requestId}}` : '{-}';
  console.group(`${requestIdLabel} ❌ [Error] ${method.toUpperCase()} ${url}`);
  console.log('🆔 Request ID:', requestId ?? '-');
  console.log('📅 Timestamp:', timestamp);
  
  if (duration !== undefined) {
    console.log(`%c⏱️  Duration: ${duration}ms`, 'color: red');
  }

  if (error instanceof Error) {
    console.error('💥 Error Message:', error.message);
    console.error('📚 Error Stack:', error.stack);
  } else if (typeof error === 'object' && error !== null) {
    console.error('💥 Error Object:', formatData(error));
  } else {
    console.error('💥 Error:', error);
  }

  // Axios 에러인 경우 추가 정보 표시
  if (error && typeof error === 'object' && 'response' in error) {
    const axiosError = error as {
      response?: {
        status?: number;
        statusText?: string;
        data?: unknown;
        headers?: Record<string, unknown>;
      };
      request?: unknown;
      message?: string;
    };

    if (axiosError.response) {
      console.error('📡 Response Status:', axiosError.response.status, axiosError.response.statusText);
      if (axiosError.response.data) {
        console.error('📦 Response Data:', formatData(axiosError.response.data));
      }
      if (axiosError.response.headers) {
        console.error('📋 Response Headers:', formatHeaders(axiosError.response.headers));
      }
    } else if (axiosError.request) {
      console.error('⚠️  No response received. Request:', axiosError.request);
    } else {
      console.error('⚠️  Error setting up request:', axiosError.message);
    }
  }

  console.groupEnd();
}

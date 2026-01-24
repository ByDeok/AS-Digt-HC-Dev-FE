/**
 * API 요청/응답 로깅 유틸리티
 * 
 * 백엔드 API 호출 시 요청, 응답, 에러를 로깅하는 유틸리티 함수들
 * 
 * 4가지 로거 유형:
 * 1. Frontend Request Logger - 프론트엔드에서 백엔드로 보내는 요청
 * 2. Frontend Response Logger - 백엔드로부터 받는 응답
 * 3. Frontend Error Logger - API 호출 중 발생한 에러
 * 4. (백엔드 로거는 백엔드에서 별도 관리)
 * 
 * 각 로거는 독립적으로 활성화/비활성화 가능
 */

type LogLevel = 'debug' | 'info' | 'warn' | 'error';

/**
 * 로깅 설정 인터페이스
 */
interface LogConfig {
  /** 전체 로깅 활성화 여부 (마스터 스위치) */
  enabled: boolean;
  /** 기본 로그 레벨 */
  level: LogLevel;
  /** 요청 로깅 활성화 여부 */
  requestEnabled: boolean;
  /** 응답 로깅 활성화 여부 */
  responseEnabled: boolean;
  /** 에러 로깅 활성화 여부 */
  errorEnabled: boolean;
  /** 헤더 로깅 포함 여부 */
  includeHeaders: boolean;
  /** 본문 로깅 포함 여부 */
  includeBody: boolean;
  /** 최대 본문 길이 */
  maxBodyLength: number;
}

/**
 * 환경 변수 기반 기본 설정
 * 
 * 환경 변수:
 * - VITE_API_LOGGING_ENABLED: 전체 로깅 활성화 (true/false)
 * - VITE_API_LOGGING_REQUEST: 요청 로깅 활성화 (true/false)
 * - VITE_API_LOGGING_RESPONSE: 응답 로깅 활성화 (true/false)
 * - VITE_API_LOGGING_ERROR: 에러 로깅 활성화 (true/false)
 * - VITE_API_LOGGING_HEADERS: 헤더 로깅 포함 (true/false)
 * - VITE_API_LOGGING_BODY: 본문 로깅 포함 (true/false)
 */
const defaultConfig: LogConfig = {
  enabled: import.meta.env.VITE_API_LOGGING_ENABLED === 'true' || import.meta.env.DEV,
  level: 'info',
  requestEnabled: import.meta.env.VITE_API_LOGGING_REQUEST !== 'false',
  responseEnabled: import.meta.env.VITE_API_LOGGING_RESPONSE !== 'false',
  errorEnabled: import.meta.env.VITE_API_LOGGING_ERROR !== 'false',
  includeHeaders: import.meta.env.VITE_API_LOGGING_HEADERS !== 'false',
  includeBody: import.meta.env.VITE_API_LOGGING_BODY !== 'false',
  maxBodyLength: 5000,
};

let config: LogConfig = { ...defaultConfig };

// ============================================================================
// 설정 관리 함수들
// ============================================================================

/**
 * 로깅 설정 업데이트
 * 
 * @example
 * setLogConfig({ enabled: false }); // 전체 로깅 비활성화
 * setLogConfig({ requestEnabled: false }); // 요청 로깅만 비활성화
 */
export function setLogConfig(newConfig: Partial<LogConfig>): void {
  config = { ...config, ...newConfig };
}

/**
 * 전체 로깅 활성화/비활성화 (마스터 스위치)
 */
export function setLoggingEnabled(enabled: boolean): void {
  config.enabled = enabled;
}

/**
 * 요청 로깅 활성화/비활성화
 */
export function setRequestLoggingEnabled(enabled: boolean): void {
  config.requestEnabled = enabled;
}

/**
 * 응답 로깅 활성화/비활성화
 */
export function setResponseLoggingEnabled(enabled: boolean): void {
  config.responseEnabled = enabled;
}

/**
 * 에러 로깅 활성화/비활성화
 */
export function setErrorLoggingEnabled(enabled: boolean): void {
  config.errorEnabled = enabled;
}

/**
 * 현재 로깅 설정 조회
 */
export function getLogConfig(): Readonly<LogConfig> {
  return { ...config };
}

/**
 * 로깅 설정 초기화 (기본값으로 복원)
 */
export function resetLogConfig(): void {
  config = { ...defaultConfig };
}

// ============================================================================
// 유틸리티 함수들
// ============================================================================

/**
 * 민감한 정보 마스킹 키 목록
 */
const SENSITIVE_KEYS = [
  'password',
  'accessToken',
  'refreshToken',
  'token',
  'authorization',
  'apiKey',
  'api_key',
  'secret',
  'credential',
];

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

  for (const key in masked) {
    const lowerKey = key.toLowerCase();
    if (SENSITIVE_KEYS.some((sk) => lowerKey.includes(sk.toLowerCase()))) {
      masked[key] = '***MASKED***';
    } else if (typeof masked[key] === 'object' && masked[key] !== null) {
      masked[key] = maskSensitiveData(masked[key]);
    }
  }

  return masked;
}

/**
 * 데이터 포맷팅 (JSON 문자열로 변환, 길이 제한 적용)
 */
function formatData(data: unknown): string {
  try {
    const masked = maskSensitiveData(data);
    const jsonStr = JSON.stringify(masked, null, 2);
    
    if (jsonStr.length > config.maxBodyLength) {
      return jsonStr.substring(0, config.maxBodyLength) + 
        `\n... [truncated ${jsonStr.length - config.maxBodyLength} chars]`;
    }
    
    return jsonStr;
  } catch {
    return String(data);
  }
}

/**
 * 헤더 포맷팅 (민감한 정보 마스킹)
 */
function formatHeaders(headers: Record<string, unknown> | undefined): string {
  if (!headers || !config.includeHeaders) {
    return '{}';
  }

  try {
    const masked = maskSensitiveData(headers);
    return JSON.stringify(masked, null, 2);
  } catch {
    return String(headers);
  }
}

/**
 * 현재 타임스탬프 반환
 */
function getTimestamp(): string {
  return new Date().toISOString();
}

// ============================================================================
// 요청 로거 (Frontend Request Logger)
// ============================================================================

/**
 * API 요청 로깅
 * 
 * 프론트엔드에서 백엔드로 보내는 API 요청을 로깅합니다.
 * 
 * @param method HTTP 메서드 (GET, POST, PUT, DELETE 등)
 * @param url 요청 URL
 * @param headers 요청 헤더 (선택)
 * @param data 요청 본문 데이터 (선택)
 * @param params 쿼리 파라미터 (선택)
 * @param requestId 요청 ID (선택)
 * @returns 요청 시작 시간 (Duration 계산용)
 * 
 * @example
 * const startTime = logRequest('POST', '/api/v1/auth/login', headers, { email, password });
 */
export function logRequest(
  method: string,
  url: string,
  headers?: Record<string, unknown>,
  data?: unknown,
  params?: unknown,
  requestId?: string
): number {
  const requestStartTime = Date.now();

  // 마스터 스위치 또는 요청 로깅이 비활성화된 경우 조기 반환
  if (!config.enabled || !config.requestEnabled) {
    return requestStartTime;
  }

  const timestamp = getTimestamp();
  const requestIdLabel = requestId ? `{${requestId}}` : '{-}';

  console.group(`${requestIdLabel} 🚀 [FE Request] ${method.toUpperCase()} ${url}`);
  console.log('🆔 Request ID:', requestId ?? '-');
  console.log('📅 Timestamp:', timestamp);
  
  if (config.includeHeaders && headers && Object.keys(headers).length > 0) {
    console.log('📋 Headers:', formatHeaders(headers));
  }
  
  if (params) {
    console.log('🔍 Query Params:', formatData(params));
  }
  
  if (config.includeBody && data) {
    console.log('📦 Request Body:', formatData(data));
  }
  
  console.groupEnd();

  return requestStartTime;
}

// ============================================================================
// 응답 로거 (Frontend Response Logger)
// ============================================================================

/**
 * API 응답 로깅
 * 
 * 백엔드로부터 받은 API 응답을 로깅합니다.
 * 
 * @param method HTTP 메서드
 * @param url 요청 URL
 * @param status HTTP 상태 코드
 * @param statusText HTTP 상태 텍스트
 * @param data 응답 데이터
 * @param headers 응답 헤더 (선택)
 * @param requestStartTime 요청 시작 시간 (logRequest에서 반환된 값)
 * @param requestId 요청 ID (선택)
 * 
 * @example
 * logResponse('POST', '/api/v1/auth/login', 200, 'OK', responseData, headers, startTime, requestId);
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
  // 마스터 스위치 또는 응답 로깅이 비활성화된 경우 조기 반환
  if (!config.enabled || !config.responseEnabled) {
    return;
  }

  const timestamp = getTimestamp();
  const duration = requestStartTime ? Date.now() - requestStartTime : undefined;

  const isError = status >= 400;
  const emoji = isError ? '❌' : '✅';
  const statusColor = isError ? 'color: red' : status >= 300 ? 'color: orange' : 'color: green';

  const requestIdLabel = requestId ? `{${requestId}}` : '{-}';
  console.group(`${requestIdLabel} ${emoji} [FE Response] ${method.toUpperCase()} ${url}`);
  console.log('🆔 Request ID:', requestId ?? '-');
  console.log('📅 Timestamp:', timestamp);
  console.log(`%cStatus: ${status} ${statusText}`, statusColor);
  
  if (duration !== undefined) {
    const durationColor = duration > 1000 ? 'color: orange' : 'color: green';
    console.log(`%c⏱️  Duration: ${duration}ms`, durationColor);
  }
  
  if (config.includeHeaders && headers && Object.keys(headers).length > 0) {
    console.log('📋 Headers:', formatHeaders(headers));
  }
  
  if (config.includeBody && data) {
    console.log('📦 Response Body:', formatData(data));
  }
  
  console.groupEnd();
}

// ============================================================================
// 에러 로거 (Frontend Error Logger)
// ============================================================================

/**
 * API 에러 로깅
 * 
 * API 호출 중 발생한 에러를 로깅합니다.
 * 
 * @param method HTTP 메서드
 * @param url 요청 URL
 * @param error 에러 객체
 * @param requestStartTime 요청 시작 시간 (logRequest에서 반환된 값)
 * @param requestId 요청 ID (선택)
 * 
 * @example
 * logError('POST', '/api/v1/auth/login', error, startTime, requestId);
 */
export function logError(
  method: string,
  url: string,
  error: unknown,
  requestStartTime?: number,
  requestId?: string
): void {
  // 마스터 스위치 또는 에러 로깅이 비활성화된 경우 조기 반환
  if (!config.enabled || !config.errorEnabled) {
    return;
  }

  const timestamp = getTimestamp();
  const duration = requestStartTime ? Date.now() - requestStartTime : undefined;

  const requestIdLabel = requestId ? `{${requestId}}` : '{-}';
  console.group(`${requestIdLabel} ❌ [FE Error] ${method.toUpperCase()} ${url}`);
  console.log('🆔 Request ID:', requestId ?? '-');
  console.log('📅 Timestamp:', timestamp);
  
  if (duration !== undefined) {
    console.log(`%c⏱️  Duration: ${duration}ms`, 'color: red');
  }

  // 에러 타입에 따른 로깅
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
      if (config.includeHeaders && axiosError.response.headers) {
        console.error('📋 Response Headers:', formatHeaders(axiosError.response.headers));
      }
    } else if (axiosError.request) {
      console.error('⚠️  No response received. Request was made but no response.');
    } else {
      console.error('⚠️  Error setting up request:', axiosError.message);
    }
  }

  console.groupEnd();
}

// ============================================================================
// 편의 함수들
// ============================================================================

/**
 * 모든 로깅 비활성화
 */
export function disableAllLogging(): void {
  config.enabled = false;
}

/**
 * 모든 로깅 활성화
 */
export function enableAllLogging(): void {
  config.enabled = true;
  config.requestEnabled = true;
  config.responseEnabled = true;
  config.errorEnabled = true;
}

/**
 * 요청 로깅만 활성화
 */
export function enableRequestLoggingOnly(): void {
  config.enabled = true;
  config.requestEnabled = true;
  config.responseEnabled = false;
  config.errorEnabled = false;
}

/**
 * 응답 로깅만 활성화
 */
export function enableResponseLoggingOnly(): void {
  config.enabled = true;
  config.requestEnabled = false;
  config.responseEnabled = true;
  config.errorEnabled = false;
}

/**
 * 에러 로깅만 활성화
 */
export function enableErrorLoggingOnly(): void {
  config.enabled = true;
  config.requestEnabled = false;
  config.responseEnabled = false;
  config.errorEnabled = true;
}

/**
 * 현재 로깅 상태 콘솔 출력 (디버깅용)
 */
export function printLogConfig(): void {
  console.table({
    '전체 활성화 (enabled)': config.enabled,
    '요청 로깅 (requestEnabled)': config.requestEnabled,
    '응답 로깅 (responseEnabled)': config.responseEnabled,
    '에러 로깅 (errorEnabled)': config.errorEnabled,
    '헤더 포함 (includeHeaders)': config.includeHeaders,
    '본문 포함 (includeBody)': config.includeBody,
    '최대 본문 길이 (maxBodyLength)': config.maxBodyLength,
  });
}

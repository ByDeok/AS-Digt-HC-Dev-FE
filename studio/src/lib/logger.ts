/**
 * API 요청/응답 로깅 유틸리티
 * 
 * 4가지 로거 유형:
 * 1. Frontend Request Logger - 프론트엔드에서 백엔드로 보내는 요청
 * 2. Frontend Response Logger - 백엔드로부터 받는 응답
 * 3. Backend Request Logger - 백엔드에서 받는 요청 (프론트엔드용)
 * 4. Backend Response Logger - 백엔드에서 보내는 응답 (프론트엔드용)
 */

type LogLevel = 'info' | 'warn' | 'error' | 'debug';

interface LogConfig {
  enabled: boolean;
  level: LogLevel;
  includeHeaders: boolean;
  includeBody: boolean;
  maxBodyLength: number;
}

const defaultConfig: LogConfig = {
  enabled: import.meta.env.VITE_API_LOGGING_ENABLED === 'true' || import.meta.env.DEV,
  level: 'info',
  includeHeaders: true,
  includeBody: true,
  maxBodyLength: 1000, // 최대 1000자까지만 로깅
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
 * 본문 데이터 포맷팅 (길이 제한 및 마스킹)
 */
function formatBody(body: unknown): string {
  if (!config.includeBody) {
    return '[Body logging disabled]';
  }

  try {
    const masked = maskSensitiveData(body);
    const json = JSON.stringify(masked, null, 2);
    
    if (json.length > config.maxBodyLength) {
      return `${json.substring(0, config.maxBodyLength)}... [truncated ${json.length - config.maxBodyLength} chars]`;
    }
    
    return json;
  } catch (e) {
    return String(body);
  }
}

/**
 * 헤더 포맷팅 (민감한 정보 마스킹)
 */
function formatHeaders(headers: Record<string, unknown> | undefined): string {
  if (!config.includeHeaders || !headers) {
    return '[Headers logging disabled]';
  }

  try {
    const masked = maskSensitiveData(headers);
    return JSON.stringify(masked, null, 2);
  } catch (e) {
    return String(headers);
  }
}

/**
 * 1. 프론트엔드 요청 로거
 * 프론트엔드에서 백엔드로 보내는 요청을 로깅
 */
export function logFrontendRequest(
  method: string,
  url: string,
  headers?: Record<string, unknown>,
  data?: unknown,
  params?: unknown
) {
  if (!config.enabled) return;

  const logData = {
    type: 'FRONTEND_REQUEST',
    timestamp: new Date().toISOString(),
    method: method.toUpperCase(),
    url,
    headers: config.includeHeaders ? formatHeaders(headers) : undefined,
    params: params ? formatBody(params) : undefined,
    body: data ? formatBody(data) : undefined,
  };

  console.group(`🚀 [Frontend Request] ${method.toUpperCase()} ${url}`);
  console.log('Timestamp:', logData.timestamp);
  if (logData.headers) console.log('Headers:', logData.headers);
  if (logData.params) console.log('Query Params:', logData.params);
  if (logData.body) console.log('Request Body:', logData.body);
  console.groupEnd();
}

/**
 * 2. 프론트엔드 응답 로거
 * 백엔드로부터 받는 응답을 로깅
 */
export function logFrontendResponse(
  method: string,
  url: string,
  status: number,
  statusText: string,
  headers?: Record<string, unknown>,
  data?: unknown,
  duration?: number
) {
  if (!config.enabled) return;

  const isError = status >= 400;
  const logData = {
    type: 'FRONTEND_RESPONSE',
    timestamp: new Date().toISOString(),
    method: method.toUpperCase(),
    url,
    status,
    statusText,
    duration: duration ? `${duration}ms` : undefined,
    headers: config.includeHeaders ? formatHeaders(headers) : undefined,
    body: data ? formatBody(data) : undefined,
  };

  const emoji = isError ? '❌' : '✅';
  const statusColor = isError ? 'color: red' : status >= 300 ? 'color: orange' : 'color: green';

  console.group(`${emoji} [Frontend Response] ${method.toUpperCase()} ${url}`);
  console.log('Timestamp:', logData.timestamp);
  console.log(`%cStatus: ${status} ${statusText}`, statusColor);
  if (logData.duration) console.log('Duration:', logData.duration);
  if (logData.headers) console.log('Headers:', logData.headers);
  if (logData.body) console.log('Response Body:', logData.body);
  console.groupEnd();
}

/**
 * 3. 백엔드 요청 로거 (프론트엔드용)
 * 백엔드에서 받는 요청을 로깅 (프론트엔드에서 호출하는 경우)
 * 주로 디버깅 목적으로 사용
 */
export function logBackendRequest(
  method: string,
  url: string,
  headers?: Record<string, unknown>,
  data?: unknown,
  params?: unknown
) {
  if (!config.enabled) return;

  const logData = {
    type: 'BACKEND_REQUEST',
    timestamp: new Date().toISOString(),
    method: method.toUpperCase(),
    url,
    headers: config.includeHeaders ? formatHeaders(headers) : undefined,
    params: params ? formatBody(params) : undefined,
    body: data ? formatBody(data) : undefined,
  };

  console.group(`📥 [Backend Request] ${method.toUpperCase()} ${url}`);
  console.log('Timestamp:', logData.timestamp);
  if (logData.headers) console.log('Headers:', logData.headers);
  if (logData.params) console.log('Query Params:', logData.params);
  if (logData.body) console.log('Request Body:', logData.body);
  console.groupEnd();
}

/**
 * 4. 백엔드 응답 로거 (프론트엔드용)
 * 백엔드에서 보내는 응답을 로깅 (프론트엔드에서 받는 경우)
 * 주로 디버깅 목적으로 사용
 */
export function logBackendResponse(
  method: string,
  url: string,
  status: number,
  statusText: string,
  headers?: Record<string, unknown>,
  data?: unknown
) {
  if (!config.enabled) return;

  const isError = status >= 400;
  const logData = {
    type: 'BACKEND_RESPONSE',
    timestamp: new Date().toISOString(),
    method: method.toUpperCase(),
    url,
    status,
    statusText,
    headers: config.includeHeaders ? formatHeaders(headers) : undefined,
    body: data ? formatBody(data) : undefined,
  };

  const emoji = isError ? '❌' : '✅';
  const statusColor = isError ? 'color: red' : status >= 300 ? 'color: orange' : 'color: green';

  console.group(`${emoji} [Backend Response] ${method.toUpperCase()} ${url}`);
  console.log('Timestamp:', logData.timestamp);
  console.log(`%cStatus: ${status} ${statusText}`, statusColor);
  if (logData.headers) console.log('Headers:', logData.headers);
  if (logData.body) console.log('Response Body:', logData.body);
  console.groupEnd();
}

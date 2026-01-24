package vibe.digthc.as_digt_hc_dev_fe.infrastructure.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * API 요청/응답 로깅 유틸리티 클래스
 * 
 * 모든 HTTP 요청과 응답을 일관된 포맷으로 로깅합니다.
 * API 로그는 별도의 로거(apiRequestLogger)를 통해 logs/api-requests.log 파일에 기록됩니다.
 * 
 * 4가지 로거 유형 (각각 독립적으로 활성화/비활성화 가능):
 * 1. Backend Request Logger - 백엔드에서 받는 요청
 * 2. Backend Response Logger - 백엔드에서 보내는 응답
 * 3. Frontend Request Logger - 프론트엔드에서 백엔드로 보내는 요청 (백엔드 관점, logBackendRequest와 동일)
 * 4. Frontend Response Logger - 백엔드에서 프론트엔드로 보내는 응답 (백엔드 관점, logBackendResponse와 동일)
 * 
 * 설정 속성:
 * - app.logging.api.enabled: 전체 로깅 활성화 (마스터 스위치)
 * - app.logging.api.request-enabled: 요청 로깅 활성화
 * - app.logging.api.response-enabled: 응답 로깅 활성화
 * - app.logging.api.include-headers: 헤더 로깅 포함
 * - app.logging.api.include-body: 본문 로깅 포함
 * - app.logging.api.max-body-length: 최대 본문 길이
 */
@Component
public class ApiLogger {

    /**
     * API 요청/응답 전용 로거
     * logback-spring.xml에서 이 로거의 로그를 logs/api-requests.log 파일에 저장하도록 설정됨
     */
    private static final Logger apiRequestLogger = LoggerFactory.getLogger("apiRequestLogger");
    
    /**
     * 일반 애플리케이션 로거 (에러 등)
     */
    private static final Logger log = LoggerFactory.getLogger(ApiLogger.class);

    /**
     * 민감한 헤더 목록 (마스킹 대상)
     */
    private static final Set<String> SENSITIVE_HEADERS = Set.of(
            "authorization", "cookie", "x-api-key", "x-auth-token", "set-cookie"
    );
    
    /**
     * 민감한 필드 목록 (마스킹 대상)
     */
    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password", "accesstoken", "refreshtoken", "token", 
            "authorization", "apikey", "api_key", "secret", "credential"
    );

    // ========================================================================
    // 설정 속성
    // ========================================================================

    /** 전체 로깅 활성화 (마스터 스위치) */
    @Value("${app.logging.api.enabled:true}")
    private boolean enabled;

    /** 요청 로깅 활성화 */
    @Value("${app.logging.api.request-enabled:true}")
    private boolean requestEnabled;

    /** 응답 로깅 활성화 */
    @Value("${app.logging.api.response-enabled:true}")
    private boolean responseEnabled;

    /** 헤더 로깅 포함 여부 */
    @Value("${app.logging.api.include-headers:true}")
    private boolean includeHeaders;

    /** 본문 로깅 포함 여부 */
    @Value("${app.logging.api.include-body:true}")
    private boolean includeBody;

    /** 최대 본문 길이 */
    @Value("${app.logging.api.max-body-length:5000}")
    private int maxBodyLength;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ========================================================================
    // 설정 조회 메서드
    // ========================================================================

    /**
     * 전체 로깅 활성화 여부 확인
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 요청 로깅 활성화 여부 확인
     */
    public boolean isRequestEnabled() {
        return enabled && requestEnabled;
    }

    /**
     * 응답 로깅 활성화 여부 확인
     */
    public boolean isResponseEnabled() {
        return enabled && responseEnabled;
    }

    /**
     * 요청/응답 본문 캐싱 시 사용할 최대 길이
     */
    public int getMaxBodyLength() {
        return maxBodyLength;
    }

    // ========================================================================
    // 런타임 설정 변경 메서드 (동적 온/오프 지원)
    // ========================================================================

    /**
     * 전체 로깅 활성화/비활성화 (마스터 스위치)
     * 
     * @param enabled 활성화 여부
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        log.info("API 로깅 전체 활성화 상태 변경: {}", enabled);
    }

    /**
     * 요청 로깅 활성화/비활성화
     * 
     * @param enabled 활성화 여부
     */
    public void setRequestEnabled(boolean enabled) {
        this.requestEnabled = enabled;
        log.info("API 요청 로깅 활성화 상태 변경: {}", enabled);
    }

    /**
     * 응답 로깅 활성화/비활성화
     * 
     * @param enabled 활성화 여부
     */
    public void setResponseEnabled(boolean enabled) {
        this.responseEnabled = enabled;
        log.info("API 응답 로깅 활성화 상태 변경: {}", enabled);
    }

    /**
     * 헤더 로깅 포함 여부 설정
     * 
     * @param include 포함 여부
     */
    public void setIncludeHeaders(boolean include) {
        this.includeHeaders = include;
    }

    /**
     * 본문 로깅 포함 여부 설정
     * 
     * @param include 포함 여부
     */
    public void setIncludeBody(boolean include) {
        this.includeBody = include;
    }

    /**
     * 모든 로깅 활성화
     */
    public void enableAllLogging() {
        this.enabled = true;
        this.requestEnabled = true;
        this.responseEnabled = true;
        log.info("API 로깅 전체 활성화");
    }

    /**
     * 모든 로깅 비활성화
     */
    public void disableAllLogging() {
        this.enabled = false;
        log.info("API 로깅 전체 비활성화");
    }

    /**
     * 요청 로깅만 활성화
     */
    public void enableRequestLoggingOnly() {
        this.enabled = true;
        this.requestEnabled = true;
        this.responseEnabled = false;
        log.info("API 요청 로깅만 활성화");
    }

    /**
     * 응답 로깅만 활성화
     */
    public void enableResponseLoggingOnly() {
        this.enabled = true;
        this.requestEnabled = false;
        this.responseEnabled = true;
        log.info("API 응답 로깅만 활성화");
    }

    /**
     * 현재 로깅 설정 정보 반환
     */
    public Map<String, Object> getLoggingStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("enabled", enabled);
        status.put("requestEnabled", requestEnabled);
        status.put("responseEnabled", responseEnabled);
        status.put("includeHeaders", includeHeaders);
        status.put("includeBody", includeBody);
        status.put("maxBodyLength", maxBodyLength);
        return status;
    }

    // ========================================================================
    // 백엔드 요청 로거 (Backend Request Logger)
    // ========================================================================

    /**
     * 백엔드 요청 로거
     * 백엔드에서 받는 요청을 로깅
     * 
     * @param request HTTP 요청 객체
     * @param requestBody 요청 본문
     */
    public void logBackendRequest(HttpServletRequest request, String requestBody) {
        // 마스터 스위치 또는 요청 로깅이 비활성화된 경우 조기 반환
        if (!enabled || !requestEnabled) {
            return;
        }

        try {
            String requestId = MDC.get("requestId");
            
            Map<String, Object> logData = new LinkedHashMap<>();
            logData.put("type", "BACKEND_REQUEST");
            logData.put("requestId", requestId != null ? requestId : "-");
            logData.put("timestamp", new Date().toInstant().toString());
            logData.put("method", request.getMethod());
            logData.put("url", getFullUrl(request));
            logData.put("remoteAddr", request.getRemoteAddr());

            if (includeHeaders) {
                logData.put("headers", maskSensitiveHeaders(getHeaders(request)));
            }

            if (includeBody && requestBody != null && !requestBody.isEmpty()) {
                logData.put("body", maskSensitiveData(formatBody(requestBody)));
            }

            // API 로그는 별도 파일에 기록
            apiRequestLogger.info("📥 [BE Request] {} {} - {}", 
                    request.getMethod(), 
                    getFullUrl(request),
                    formatLogData(logData));
        } catch (Exception e) {
            log.warn("Failed to log backend request", e);
        }
    }

    // ========================================================================
    // 백엔드 응답 로거 (Backend Response Logger)
    // ========================================================================

    /**
     * 백엔드 응답 로거
     * 백엔드에서 보내는 응답을 로깅
     * 
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param responseBody 응답 본문
     * @param duration 처리 시간 (ms)
     */
    public void logBackendResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            String responseBody,
            long duration) {
        // 마스터 스위치 또는 응답 로깅이 비활성화된 경우 조기 반환
        if (!enabled || !responseEnabled) {
            return;
        }

        try {
            String requestId = MDC.get("requestId");
            int status = response.getStatus();
            
            Map<String, Object> logData = new LinkedHashMap<>();
            logData.put("type", "BACKEND_RESPONSE");
            logData.put("requestId", requestId != null ? requestId : "-");
            logData.put("timestamp", new Date().toInstant().toString());
            logData.put("method", request.getMethod());
            logData.put("url", getFullUrl(request));
            logData.put("status", status);
            logData.put("duration", duration + "ms");

            if (includeHeaders) {
                logData.put("headers", maskSensitiveHeaders(getResponseHeaders(response)));
            }

            if (includeBody && responseBody != null && !responseBody.isEmpty()) {
                logData.put("body", maskSensitiveData(formatBody(responseBody)));
            }

            String emoji = status >= 400 ? "❌" : "✅";
            // API 로그는 별도 파일에 기록
            apiRequestLogger.info("{} [BE Response] {} {} - Status: {} - Duration: {}ms - {}", 
                    emoji,
                    request.getMethod(), 
                    getFullUrl(request),
                    status,
                    duration,
                    formatLogData(logData));
        } catch (Exception e) {
            log.warn("Failed to log backend response", e);
        }
    }

    // ========================================================================
    // 프론트엔드 요청/응답 로거 (백엔드 관점에서의 별칭)
    // ========================================================================

    /**
     * 프론트엔드 요청 로거 (백엔드 관점)
     * 프론트엔드에서 백엔드로 보내는 요청을 로깅
     * (logBackendRequest와 동일하지만 의미상 구분)
     * 
     * @param request HTTP 요청 객체
     * @param requestBody 요청 본문
     */
    public void logFrontendRequest(HttpServletRequest request, String requestBody) {
        logBackendRequest(request, requestBody);
    }

    /**
     * 프론트엔드 응답 로거 (백엔드 관점)
     * 백엔드에서 프론트엔드로 보내는 응답을 로깅
     * (logBackendResponse와 동일하지만 의미상 구분)
     * 
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param responseBody 응답 본문
     * @param duration 처리 시간 (ms)
     */
    public void logFrontendResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            String responseBody,
            long duration) {
        logBackendResponse(request, response, responseBody, duration);
    }

    // ========================================================================
    // 유틸리티 메서드
    // ========================================================================

    /**
     * 전체 URL 구성 (쿼리 파라미터 포함)
     */
    private String getFullUrl(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null) {
            requestURL.append("?").append(queryString);
        }
        return requestURL.toString();
    }

    /**
     * 요청 헤더 추출
     */
    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;
    }

    /**
     * 응답 헤더 추출
     */
    private Map<String, String> getResponseHeaders(HttpServletResponse response) {
        Map<String, String> headers = new LinkedHashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            headers.put(headerName, response.getHeader(headerName));
        }
        return headers;
    }

    /**
     * 민감한 헤더 마스킹
     */
    private Map<String, String> maskSensitiveHeaders(Map<String, String> headers) {
        Map<String, String> masked = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey().toLowerCase();
            if (SENSITIVE_HEADERS.contains(key)) {
                masked.put(entry.getKey(), "***MASKED***");
            } else {
                masked.put(entry.getKey(), entry.getValue());
            }
        }
        return masked;
    }

    /**
     * 민감한 데이터 마스킹 (JSON 본문)
     */
    private Object maskSensitiveData(String body) {
        try {
            Object json = objectMapper.readValue(body, Object.class);
            return maskSensitiveDataRecursive(json);
        } catch (Exception e) {
            // JSON 파싱 실패 시 원본 반환
            return body;
        }
    }

    /**
     * 재귀적으로 민감한 필드 마스킹
     */
    @SuppressWarnings("unchecked")
    private Object maskSensitiveDataRecursive(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) obj;
            Map<String, Object> masked = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey().toLowerCase();
                if (SENSITIVE_FIELDS.stream().anyMatch(key::contains)) {
                    masked.put(entry.getKey(), "***MASKED***");
                } else {
                    masked.put(entry.getKey(), maskSensitiveDataRecursive(entry.getValue()));
                }
            }
            return masked;
        } else if (obj instanceof List) {
            List<Object> list = (List<Object>) obj;
            List<Object> masked = new ArrayList<>();
            for (Object item : list) {
                masked.add(maskSensitiveDataRecursive(item));
            }
            return masked;
        }

        return obj;
    }

    /**
     * 본문 포맷팅 (길이 제한)
     */
    private String formatBody(String body) {
        if (body.length() > maxBodyLength) {
            return body.substring(0, maxBodyLength) + 
                   "... [truncated " + (body.length() - maxBodyLength) + " chars]";
        }
        return body;
    }

    /**
     * 로그 데이터 포맷팅
     */
    private String formatLogData(Map<String, Object> logData) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logData);
        } catch (Exception e) {
            return logData.toString();
        }
    }
}

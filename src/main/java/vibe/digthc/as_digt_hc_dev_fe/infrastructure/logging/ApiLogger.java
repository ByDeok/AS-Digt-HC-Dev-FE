package vibe.digthc.as_digt_hc_dev_fe.infrastructure.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * API 요청/응답 로깅 유틸리티 클래스
 * 
 * 모든 HTTP 요청과 응답을 일관된 포맷으로 로깅합니다.
 * API 로그는 별도의 로거(apiRequestLogger)를 통해 logs/api-requests.log 파일에 기록됩니다.
 * 
 * 4가지 로거 유형:
 * 1. Frontend Request Logger - 프론트엔드에서 백엔드로 보내는 요청 (백엔드 관점)
 * 2. Frontend Response Logger - 백엔드로부터 받는 응답 (프론트엔드 관점) - 미사용
 * 3. Backend Request Logger - 백엔드에서 받는 요청
 * 4. Backend Response Logger - 백엔드에서 보내는 응답
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

    private static final int MAX_BODY_LENGTH = 1000;
    private static final Set<String> SENSITIVE_HEADERS = Set.of(
            "authorization", "cookie", "x-api-key", "x-auth-token"
    );
    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password", "accessToken", "refreshToken", "token", "authorization"
    );

    @Value("${app.logging.api.enabled:true}")
    private boolean enabled;

    @Value("${app.logging.api.include-headers:true}")
    private boolean includeHeaders;

    @Value("${app.logging.api.include-body:true}")
    private boolean includeBody;

    @Value("${app.logging.api.max-body-length:1000}")
    private int maxBodyLength;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 로깅 활성화 여부 확인
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 요청/응답 본문 캐싱 시 사용할 최대 길이
     */
    public int getMaxBodyLength() {
        return maxBodyLength;
    }

    /**
     * 3. 백엔드 요청 로거
     * 백엔드에서 받는 요청을 로깅
     */
    public void logBackendRequest(HttpServletRequest request, String requestBody) {
        if (!enabled) {
            return;
        }

        try {
            Map<String, Object> logData = new LinkedHashMap<>();
            logData.put("type", "BACKEND_REQUEST");
            logData.put("timestamp", new Date().toInstant().toString());
            logData.put("method", request.getMethod());
            logData.put("url", getFullUrl(request));
            logData.put("remoteAddr", request.getRemoteAddr());
            logData.put("remoteHost", request.getRemoteHost());

            if (includeHeaders) {
                logData.put("headers", maskSensitiveHeaders(getHeaders(request)));
            }

            if (includeBody && requestBody != null && !requestBody.isEmpty()) {
                logData.put("body", maskSensitiveData(formatBody(requestBody)));
            }

            // API 로그는 별도 파일에 기록
            apiRequestLogger.info("📥 [Backend Request] {} {} - {}", 
                    request.getMethod(), 
                    getFullUrl(request),
                    formatLogData(logData));
        } catch (Exception e) {
            log.warn("Failed to log backend request", e);
        }
    }

    /**
     * 4. 백엔드 응답 로거
     * 백엔드에서 보내는 응답을 로깅
     */
    public void logBackendResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            String responseBody,
            long duration) {
        if (!enabled) {
            return;
        }

        try {
            Map<String, Object> logData = new LinkedHashMap<>();
            logData.put("type", "BACKEND_RESPONSE");
            logData.put("timestamp", new Date().toInstant().toString());
            logData.put("method", request.getMethod());
            logData.put("url", getFullUrl(request));
            logData.put("status", response.getStatus());
            logData.put("duration", duration + "ms");

            if (includeHeaders) {
                logData.put("headers", maskSensitiveHeaders(getResponseHeaders(response)));
            }

            if (includeBody && responseBody != null && !responseBody.isEmpty()) {
                logData.put("body", maskSensitiveData(formatBody(responseBody)));
            }

            String emoji = response.getStatus() >= 400 ? "❌" : "✅";
            // API 로그는 별도 파일에 기록
            apiRequestLogger.info("{} [Backend Response] {} {} - Status: {} - {}", 
                    emoji,
                    request.getMethod(), 
                    getFullUrl(request),
                    response.getStatus(),
                    formatLogData(logData));
        } catch (Exception e) {
            log.warn("Failed to log backend response", e);
        }
    }

    /**
     * 1. 프론트엔드 요청 로거 (백엔드 관점)
     * 프론트엔드에서 백엔드로 보내는 요청을 로깅
     * (logBackendRequest와 동일하지만 의미상 구분)
     */
    public void logFrontendRequest(HttpServletRequest request, String requestBody) {
        logBackendRequest(request, requestBody);
    }

    /**
     * 2. 프론트엔드 응답 로거 (백엔드 관점)
     * 백엔드로부터 받는 응답을 로깅
     * (logBackendResponse와 동일하지만 의미상 구분)
     */
    public void logFrontendResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            String responseBody,
            long duration) {
        logBackendResponse(request, response, responseBody, duration);
    }

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
    @SuppressWarnings("unchecked")
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

    /**
     * 요청 본문 읽기 (스트림은 한 번만 읽을 수 있으므로 캐싱 필요)
     */
    public String readRequestBody(HttpServletRequest request) throws IOException {
        if (includeBody) {
            byte[] bodyBytes = StreamUtils.copyToByteArray(request.getInputStream());
            return new String(bodyBytes, StandardCharsets.UTF_8);
        }
        return "";
    }
}

package vibe.digthc.as_digt_hc_dev_fe.infrastructure.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * API 요청/응답 로깅 필터
 * 
 * 모든 HTTP 요청과 응답을 로깅합니다.
 * ContentCachingRequestWrapper와 ContentCachingResponseWrapper를 사용하여
 * 본문을 여러 번 읽을 수 있도록 합니다.
 * 
 * 요청 로깅과 응답 로깅은 ApiLogger의 설정에 따라 각각 독립적으로 활성화/비활성화됩니다.
 */
@Slf4j
@Component
@Order(1) // LoggingFilter(Order=0) 다음에 실행
@RequiredArgsConstructor
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final ApiLogger apiLogger;

    /**
     * 로깅에서 제외할 경로 목록
     */
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/actuator",
            "/health",
            "/favicon.ico",
            "/static",
            "/assets"
    );

    /**
     * 로깅에서 제외할 Content-Type 목록
     */
    private static final List<String> EXCLUDED_CONTENT_TYPES = Arrays.asList(
            "image/",
            "video/",
            "audio/",
            "application/octet-stream"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 로깅이 완전히 비활성화된 경우 또는 제외 대상인 경우 스킵
        if (!apiLogger.isEnabled() || shouldSkipLogging(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();

        // 요청 본문을 캐싱할 수 있도록 래핑
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(
                request,
                apiLogger.getMaxBodyLength()
        );
        
        // 응답 본문을 캐싱할 수 있도록 래핑
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            // 필터 체인 실행
            filterChain.doFilter(wrappedRequest, wrappedResponse);

        } finally {
            // 요청 본문 읽기
            String requestBody = "";
            byte[] requestBodyBytes = wrappedRequest.getContentAsByteArray();
            if (requestBodyBytes.length > 0) {
                requestBody = new String(requestBodyBytes, StandardCharsets.UTF_8);
            }

            // 응답 본문 읽기
            String responseBody = "";
            byte[] responseBodyBytes = wrappedResponse.getContentAsByteArray();
            if (responseBodyBytes.length > 0 && shouldLogResponseBody(wrappedResponse)) {
                responseBody = new String(responseBodyBytes, StandardCharsets.UTF_8);
            }

            long duration = System.currentTimeMillis() - startTime;

            // 요청 로깅 (요청 로깅이 활성화된 경우에만)
            if (apiLogger.isRequestEnabled()) {
                apiLogger.logBackendRequest(wrappedRequest, requestBody);
            }

            // 응답 로깅 (응답 로깅이 활성화된 경우에만)
            if (apiLogger.isResponseEnabled()) {
                apiLogger.logBackendResponse(wrappedRequest, wrappedResponse, responseBody, duration);
            }

            // 응답 본문을 클라이언트에 복사 (래퍼가 본문을 소비했으므로)
            wrappedResponse.copyBodyToResponse();
        }
    }

    /**
     * 로깅을 건너뛸 경로인지 확인
     * 
     * @param request HTTP 요청 객체
     * @return 로깅 스킵 여부
     */
    private boolean shouldSkipLogging(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * 응답 본문을 로깅해야 하는지 확인
     * 바이너리 컨텐츠(이미지, 비디오 등)는 로깅에서 제외
     * 
     * @param response HTTP 응답 래퍼 객체
     * @return 응답 본문 로깅 여부
     */
    private boolean shouldLogResponseBody(ContentCachingResponseWrapper response) {
        String contentType = response.getContentType();
        if (contentType == null) {
            return true;
        }
        
        return EXCLUDED_CONTENT_TYPES.stream()
                .noneMatch(contentType.toLowerCase()::startsWith);
    }
}

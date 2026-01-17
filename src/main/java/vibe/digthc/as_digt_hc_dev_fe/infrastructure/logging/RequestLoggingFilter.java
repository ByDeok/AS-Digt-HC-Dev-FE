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
 */
@Slf4j
@Component
@Order(1) // 다른 필터보다 먼저 실행되도록 설정
@RequiredArgsConstructor
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final ApiLogger apiLogger;

    // 로깅에서 제외할 경로
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/actuator",
            "/health",
            "/favicon.ico"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (!apiLogger.isEnabled() || shouldSkipLogging(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();
        String requestBody = "";

        // 요청 본문을 캐싱할 수 있도록 래핑
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        
        // 응답 본문을 캐싱할 수 있도록 래핑
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            // 필터 체인 실행
            filterChain.doFilter(wrappedRequest, wrappedResponse);

            // 요청 본문 읽기
            byte[] requestBodyBytes = wrappedRequest.getContentAsByteArray();
            if (requestBodyBytes.length > 0) {
                requestBody = new String(requestBodyBytes, StandardCharsets.UTF_8);
            }

            // 응답 본문 읽기
            byte[] responseBodyBytes = wrappedResponse.getContentAsByteArray();
            String responseBody = "";
            if (responseBodyBytes.length > 0) {
                responseBody = new String(responseBodyBytes, StandardCharsets.UTF_8);
            }

            long duration = System.currentTimeMillis() - startTime;

            // 로깅
            apiLogger.logBackendRequest(wrappedRequest, requestBody);
            apiLogger.logBackendResponse(wrappedRequest, wrappedResponse, responseBody, duration);

            // 응답 본문을 클라이언트에 복사 (래퍼가 본문을 소비했으므로)
            wrappedResponse.copyBodyToResponse();

        } catch (Exception e) {
            log.error("Error in request logging filter", e);
            throw e;
        }
    }

    /**
     * 로깅을 건너뛸 경로인지 확인
     */
    private boolean shouldSkipLogging(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }
}

package vibe.digthc.as_digt_hc_dev_fe.infrastructure.logging;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 요청 ID(ULID) 기반 전구간 로그 추적 필터
 *
 * - 요청 헤더에서 X-Request-ID를 읽음
 * - 없으면 서버에서 ULID 생성
 * - MDC에 requestId로 저장
 * - 응답 헤더에 동일한 ID 추가
 * - 요청 처리 종료 후 MDC.clear()
 */
@Component
@Order(0)
public class LoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String MDC_KEY = "requestId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UlidCreator.getUlid().toString();
        }

        MDC.put(MDC_KEY, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}

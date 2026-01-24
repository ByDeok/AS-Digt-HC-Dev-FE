package vibe.digthc.as_digt_hc_dev_fe.infrastructure.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Google Analytics 4 Measurement Protocol 클라이언트
 *
 * 서버 사이드에서 GA4로 이벤트를 전송합니다.
 * - 비동기 처리로 메인 요청에 영향 없음
 * - 실패 시에도 서비스에 영향 없음 (best-effort)
 *
 * @see <a href="https://developers.google.com/analytics/devguides/collection/protocol/ga4">GA4 Measurement Protocol</a>
 */
@Service
public class Ga4Client {

    private static final Logger log = LoggerFactory.getLogger(Ga4Client.class);

    // GA4 Measurement Protocol 엔드포인트
    private static final String GA4_ENDPOINT = "https://www.google-analytics.com/mp/collect";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.analytics.ga4.enabled:false}")
    private boolean enabled;

    @Value("${app.analytics.ga4.measurement-id:}")
    private String measurementId;

    @Value("${app.analytics.ga4.api-secret:}")
    private String apiSecret;

    public Ga4Client(
            @Qualifier("analyticsRestTemplate") RestTemplate restTemplate,
            ObjectMapper objectMapper
    ) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 단일 이벤트 전송
     *
     * @param clientId  클라이언트 ID (세션 식별자, 프론트엔드의 X-Request-ID 사용 권장)
     * @param eventName 이벤트 이름 (GA4 이벤트 명명 규칙 준수)
     * @param params    이벤트 매개변수
     */
    @Async
    public void sendEvent(String clientId, String eventName, Map<String, Object> params) {
        sendEvents(clientId, null, List.of(new Ga4Event(eventName, params)));
    }

    /**
     * 사용자 ID와 함께 이벤트 전송
     *
     * @param clientId  클라이언트 ID
     * @param userId    사용자 ID (로그인된 사용자)
     * @param eventName 이벤트 이름
     * @param params    이벤트 매개변수
     */
    @Async
    public void sendEventWithUserId(String clientId, String userId, String eventName, Map<String, Object> params) {
        sendEvents(clientId, userId, List.of(new Ga4Event(eventName, params)));
    }

    /**
     * 여러 이벤트 일괄 전송
     *
     * @param clientId 클라이언트 ID
     * @param userId   사용자 ID (nullable)
     * @param events   전송할 이벤트 목록
     */
    @Async
    public void sendEvents(String clientId, String userId, List<Ga4Event> events) {
        if (!enabled) {
            log.debug("[GA4] Analytics disabled, skipping event");
            return;
        }

        if (measurementId == null || measurementId.isBlank() ||
            apiSecret == null || apiSecret.isBlank()) {
            log.warn("[GA4] Missing measurement_id or api_secret configuration");
            return;
        }

        try {
            // 요청 URL 구성
            String url = String.format("%s?measurement_id=%s&api_secret=%s",
                    GA4_ENDPOINT, measurementId, apiSecret);

            // 요청 본문 구성
            Map<String, Object> body = new HashMap<>();
            body.put("client_id", clientId);
            if (userId != null && !userId.isBlank()) {
                body.put("user_id", userId);
            }

            // 이벤트 목록 변환
            List<Map<String, Object>> eventList = new ArrayList<>();
            for (Ga4Event event : events) {
                Map<String, Object> eventMap = new HashMap<>();
                eventMap.put("name", event.name());
                if (event.params() != null && !event.params().isEmpty()) {
                    eventMap.put("params", event.params());
                }
                eventList.add(eventMap);
            }
            body.put("events", eventList);

            // HTTP 요청 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String jsonBody = objectMapper.writeValueAsString(body);
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

            // 요청 전송
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("[GA4] Event sent successfully: {} events", events.size());
            } else {
                log.warn("[GA4] Event sending failed with status: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            // 분석 실패가 서비스에 영향을 주지 않도록 예외를 로깅만 함
            log.error("[GA4] Failed to send event: {}", e.getMessage());
        }
    }

    /**
     * GA4 활성화 여부 확인
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * GA4 이벤트 DTO
     */
    public record Ga4Event(String name, Map<String, Object> params) {
        public Ga4Event(String name) {
            this(name, Map.of());
        }
    }
}

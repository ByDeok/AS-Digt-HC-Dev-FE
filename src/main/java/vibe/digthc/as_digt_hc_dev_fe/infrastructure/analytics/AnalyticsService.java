package vibe.digthc.as_digt_hc_dev_fe.infrastructure.analytics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Analytics 이벤트 서비스
 *
 * 비즈니스 로직에서 호출되는 분석 이벤트를 정의합니다.
 * GA4 Measurement Protocol을 통해 서버 사이드 이벤트를 전송합니다.
 *
 * 사용 예:
 * - API 에러/성공 추적
 * - 서버 사이드 전환 이벤트 (결제 완료, 리포트 생성 등)
 * - 프론트엔드에서 추적하기 어려운 서버 측 이벤트
 */
@Service
public class AnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);

    private final Ga4Client ga4Client;

    public AnalyticsService(Ga4Client ga4Client) {
        this.ga4Client = ga4Client;
    }

    // ============================================
    // API 관련 이벤트
    // ============================================

    /**
     * API 에러 이벤트 전송
     *
     * @param requestId   요청 ID (X-Request-ID)
     * @param endpoint    API 엔드포인트
     * @param statusCode  HTTP 상태 코드
     * @param errorType   에러 타입 (예: "validation_error", "authentication_error")
     * @param errorMessage 에러 메시지 (민감 정보 제외)
     */
    public void trackApiError(String requestId, String endpoint, int statusCode,
                              String errorType, String errorMessage) {
        Map<String, Object> params = new HashMap<>();
        params.put("endpoint", endpoint);
        params.put("status_code", statusCode);
        params.put("error_type", errorType);
        params.put("error_message", truncate(errorMessage, 100));
        params.put("timestamp", System.currentTimeMillis());

        ga4Client.sendEvent(requestId, "api_error", params);
        log.debug("[Analytics] api_error: endpoint={}, status={}", endpoint, statusCode);
    }

    /**
     * API 성공 이벤트 전송 (선택적: 중요한 API만)
     *
     * @param requestId  요청 ID
     * @param endpoint   API 엔드포인트
     * @param durationMs 요청 처리 시간 (밀리초)
     */
    public void trackApiSuccess(String requestId, String endpoint, long durationMs) {
        Map<String, Object> params = new HashMap<>();
        params.put("endpoint", endpoint);
        params.put("duration_ms", durationMs);
        params.put("timestamp", System.currentTimeMillis());

        ga4Client.sendEvent(requestId, "api_success", params);
        log.debug("[Analytics] api_success: endpoint={}, duration={}ms", endpoint, durationMs);
    }

    // ============================================
    // 사용자 관련 이벤트
    // ============================================

    /**
     * 회원가입 완료 이벤트 (서버 확인)
     *
     * @param requestId 요청 ID
     * @param userId    사용자 ID
     * @param method    가입 방법 (email, kakao, naver 등)
     */
    public void trackSignupComplete(String requestId, String userId, String method) {
        Map<String, Object> params = new HashMap<>();
        params.put("signup_method", method);
        params.put("timestamp", System.currentTimeMillis());

        ga4Client.sendEventWithUserId(requestId, userId, "signup_complete_server", params);
        log.info("[Analytics] signup_complete_server: userId={}, method={}", userId, method);
    }

    /**
     * 로그인 성공 이벤트 (서버 확인)
     *
     * @param requestId 요청 ID
     * @param userId    사용자 ID
     * @param method    로그인 방법
     */
    public void trackLoginSuccess(String requestId, String userId, String method) {
        Map<String, Object> params = new HashMap<>();
        params.put("login_method", method);
        params.put("timestamp", System.currentTimeMillis());

        ga4Client.sendEventWithUserId(requestId, userId, "login_success_server", params);
        log.debug("[Analytics] login_success_server: userId={}", userId);
    }

    // ============================================
    // 리포트 관련 이벤트
    // ============================================

    /**
     * 리포트 생성 완료 이벤트
     *
     * @param requestId   요청 ID
     * @param userId      사용자 ID
     * @param reportId    리포트 ID
     * @param reportType  리포트 타입 (weekly, monthly)
     * @param dataCoverage 데이터 커버리지 (0.0 ~ 1.0)
     */
    public void trackReportGenerated(String requestId, String userId, String reportId,
                                     String reportType, double dataCoverage) {
        Map<String, Object> params = new HashMap<>();
        params.put("report_id", reportId);
        params.put("report_type", reportType);
        params.put("data_coverage", dataCoverage);
        params.put("timestamp", System.currentTimeMillis());

        ga4Client.sendEventWithUserId(requestId, userId, "report_generated", params);
        log.info("[Analytics] report_generated: reportId={}, type={}", reportId, reportType);
    }

    // ============================================
    // 미션 관련 이벤트
    // ============================================

    /**
     * 미션 완료 이벤트 (서버 확인)
     *
     * @param requestId    요청 ID
     * @param userId       사용자 ID
     * @param missionId    미션 ID
     * @param missionTitle 미션 제목
     * @param category     미션 카테고리
     */
    public void trackMissionCompleted(String requestId, String userId, String missionId,
                                      String missionTitle, String category) {
        Map<String, Object> params = new HashMap<>();
        params.put("mission_id", missionId);
        params.put("mission_title", truncate(missionTitle, 50));
        params.put("mission_category", category);
        params.put("timestamp", System.currentTimeMillis());

        ga4Client.sendEventWithUserId(requestId, userId, "mission_completed_server", params);
        log.debug("[Analytics] mission_completed_server: missionId={}", missionId);
    }

    // ============================================
    // 가족 보드 관련 이벤트
    // ============================================

    /**
     * 가족 초대 완료 이벤트
     *
     * @param requestId    요청 ID
     * @param inviterUserId 초대한 사용자 ID
     * @param intendedRole 부여할 역할
     */
    public void trackFamilyInviteSent(String requestId, String inviterUserId, String intendedRole) {
        Map<String, Object> params = new HashMap<>();
        params.put("intended_role", intendedRole);
        params.put("timestamp", System.currentTimeMillis());

        ga4Client.sendEventWithUserId(requestId, inviterUserId, "family_invite_sent", params);
        log.debug("[Analytics] family_invite_sent: inviter={}", inviterUserId);
    }

    /**
     * 가족 참여 완료 이벤트
     *
     * @param requestId 요청 ID
     * @param userId    참여한 사용자 ID
     * @param boardId   가족 보드 ID
     * @param role      부여받은 역할
     */
    public void trackFamilyJoinCompleted(String requestId, String userId, String boardId, String role) {
        Map<String, Object> params = new HashMap<>();
        params.put("board_id", boardId);
        params.put("role", role);
        params.put("timestamp", System.currentTimeMillis());

        ga4Client.sendEventWithUserId(requestId, userId, "family_join_completed", params);
        log.info("[Analytics] family_join_completed: userId={}, boardId={}", userId, boardId);
    }

    // ============================================
    // 건강 데이터 관련 이벤트
    // ============================================

    /**
     * 건강 기록 저장 이벤트
     *
     * @param requestId    요청 ID
     * @param userId       사용자 ID
     * @param recordDate   기록 날짜
     * @param metricsCount 저장된 지표 수
     * @param source       데이터 소스 (manual, device)
     */
    public void trackHealthRecordSaved(String requestId, String userId, String recordDate,
                                       int metricsCount, String source) {
        Map<String, Object> params = new HashMap<>();
        params.put("record_date", recordDate);
        params.put("metrics_count", metricsCount);
        params.put("source", source);
        params.put("timestamp", System.currentTimeMillis());

        ga4Client.sendEventWithUserId(requestId, userId, "health_record_saved", params);
        log.debug("[Analytics] health_record_saved: date={}, count={}", recordDate, metricsCount);
    }

    // ============================================
    // 유틸리티 메서드
    // ============================================

    /**
     * 문자열을 최대 길이로 자르기
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return null;
        return str.length() <= maxLength ? str : str.substring(0, maxLength) + "...";
    }

    /**
     * 분석 활성화 여부 확인
     */
    public boolean isEnabled() {
        return ga4Client.isEnabled();
    }
}

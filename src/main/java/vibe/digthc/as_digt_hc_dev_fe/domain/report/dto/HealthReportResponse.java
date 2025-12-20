package vibe.digthc.as_digt_hc_dev_fe.domain.report.dto;

import vibe.digthc.as_digt_hc_dev_fe.domain.report.entity.HealthReport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * HealthReport API 응답 전용 DTO.
 *
 * 주의:
 * - 엔티티(HealthReport)를 그대로 JSON으로 내려주면, User <-> UserProfile 등 연관관계가
 *   순환 참조로 직렬화되어 JSON 중첩 깊이 초과/민감정보 노출(비밀번호 등) 문제가 생길 수 있다.
 * - 따라서 API 응답은 필요한 필드만 담은 DTO로 제한한다.
 */
public record HealthReportResponse(
        UUID reportId,
        UUID userId,
        LocalDate startDate,
        LocalDate endDate,
        ReportMetrics metrics,
        ReportContext context,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static HealthReportResponse from(HealthReport report) {
        return new HealthReportResponse(
                report.getId(),
                report.getUser() != null ? report.getUser().getId() : null,
                report.getStartDate(),
                report.getEndDate(),
                report.getMetrics(),
                report.getContext(),
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }
}



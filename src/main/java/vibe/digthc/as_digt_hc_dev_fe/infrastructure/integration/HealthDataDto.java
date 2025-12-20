package vibe.digthc.as_digt_hc_dev_fe.infrastructure.integration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 건강 데이터 DTO
 */
public record HealthDataDto(
    LocalDate recordDate,
    String metricType,
    Map<String, Object> dataValue,
    LocalDateTime measuredAt
) {}


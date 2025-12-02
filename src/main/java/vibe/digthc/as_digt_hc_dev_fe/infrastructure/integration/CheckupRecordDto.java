package vibe.digthc.as_digt_hc_dev_fe.infrastructure.integration;

import java.time.LocalDate;
import java.util.Map;

/**
 * 검진 결과 DTO
 */
public record CheckupRecordDto(
    LocalDate checkupDate,
    String institutionName,
    String checkupType,
    Map<String, Object> results
) {}


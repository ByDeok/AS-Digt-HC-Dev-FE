package vibe.digthc.as_digt_hc_dev_fe.infrastructure.integration;

import java.time.LocalDate;
import java.util.Map;

/**
 * 진료 기록 DTO
 */
public record MedicalRecordDto(
    LocalDate visitDate,
    String institutionName,
    String department,
    String diagnosis,
    Map<String, Object> details
) {}


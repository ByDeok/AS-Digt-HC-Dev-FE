package vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 동기화 결과 응답 DTO
 */
public record SyncResultRes(
    int recordsSynced,
    LocalDateTime syncedAt,
    String status,
    List<String> errors
) {}


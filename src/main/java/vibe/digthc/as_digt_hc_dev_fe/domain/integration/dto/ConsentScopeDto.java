package vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

/**
 * 동의 범위 DTO
 */
public record ConsentScopeDto(
    @NotEmpty(message = "데이터 타입은 필수입니다.")
    List<String> dataTypes,
    
    @NotBlank(message = "동기화 빈도는 필수입니다.")
    String frequency,
    
    String retentionPeriod,
    
    Map<String, Boolean> sharingAllowed
) {}


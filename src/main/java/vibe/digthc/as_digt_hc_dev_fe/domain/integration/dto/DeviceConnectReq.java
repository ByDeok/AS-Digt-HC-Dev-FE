package vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 디바이스 연동 요청 DTO
 */
public record DeviceConnectReq(
    @NotBlank(message = "벤더는 필수입니다.")
    String vendor,
    
    @NotBlank(message = "디바이스 타입은 필수입니다.")
    String deviceType,
    
    @NotBlank(message = "인증 코드는 필수입니다.")
    String authCode,
    
    @NotNull(message = "동의 범위는 필수입니다.")
    ConsentScopeDto consentScope
) {}


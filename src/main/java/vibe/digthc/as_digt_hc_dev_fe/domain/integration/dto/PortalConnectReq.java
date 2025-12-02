package vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * 포털 연동 요청 DTO
 */
public record PortalConnectReq(
    @NotBlank(message = "포털 타입은 필수입니다.")
    String portalType,
    
    String portalId,
    
    @NotNull(message = "인증 정보는 필수입니다.")
    Map<String, String> credentials
) {}


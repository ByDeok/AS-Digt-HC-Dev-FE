package vibe.digthc.as_digt_hc_dev_fe.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    @NotBlank String refreshToken
) {}


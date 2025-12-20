package vibe.digthc.as_digt_hc_dev_fe.domain.user.dto;

public record TokenResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    UserResponse user
) {}


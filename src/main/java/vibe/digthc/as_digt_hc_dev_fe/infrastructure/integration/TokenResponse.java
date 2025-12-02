package vibe.digthc.as_digt_hc_dev_fe.infrastructure.integration;

/**
 * OAuth 토큰 응답
 */
public record TokenResponse(
    String accessToken,
    String refreshToken,
    long expiresIn,
    String tokenType
) {}


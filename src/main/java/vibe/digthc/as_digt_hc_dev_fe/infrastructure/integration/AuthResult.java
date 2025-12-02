package vibe.digthc.as_digt_hc_dev_fe.infrastructure.integration;

/**
 * 인증 결과
 */
public record AuthResult(
    String token,
    String portalUserId,
    String portalName,
    boolean success
) {}


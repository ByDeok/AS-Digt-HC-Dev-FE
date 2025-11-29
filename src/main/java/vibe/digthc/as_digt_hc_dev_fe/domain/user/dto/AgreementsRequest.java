package vibe.digthc.as_digt_hc_dev_fe.domain.user.dto;

public record AgreementsRequest(
    boolean termsService,
    boolean privacyPolicy,
    boolean marketingConsent
) {}


package vibe.digthc.as_digt_hc_dev_fe.domain.integration.service;

import vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto.ConsentRes;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.enums.ConsentSubjectType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ConsentService {
    List<ConsentRes> getConsents(UUID userId);
    ConsentRes grantConsent(UUID userId, ConsentSubjectType subjectType, UUID subjectId, Map<String, Object> scope);
    void revokeConsent(UUID userId, UUID consentId, String reason);
    boolean checkConsent(UUID userId, ConsentSubjectType subjectType, UUID subjectId);
}


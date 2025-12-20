package vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto;

import vibe.digthc.as_digt_hc_dev_fe.domain.integration.entity.ConsentRecord;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.enums.ConsentStatus;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.enums.ConsentSubjectType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 동의 응답 DTO
 */
public record ConsentRes(
    UUID consentId,
    ConsentSubjectType subjectType,
    String subjectName,
    ConsentScopeDto scope,
    ConsentStatus status,
    LocalDateTime consentedAt
) {
    @SuppressWarnings("unchecked")
    public static ConsentRes from(ConsentRecord consentRecord, String subjectName) {
        Map<String, Object> scopeMap = consentRecord.getConsentScope();
        ConsentScopeDto scope = scopeMap != null ? new ConsentScopeDto(
            (java.util.List<String>) scopeMap.get("dataTypes"),
            (String) scopeMap.get("frequency"),
            (String) scopeMap.get("retentionPeriod"),
            (Map<String, Boolean>) scopeMap.get("sharingAllowed")
        ) : null;
        
        return new ConsentRes(
            consentRecord.getId(),
            consentRecord.getSubjectType(),
            subjectName,
            scope,
            consentRecord.getStatus(),
            consentRecord.getConsentedAt()
        );
    }
}


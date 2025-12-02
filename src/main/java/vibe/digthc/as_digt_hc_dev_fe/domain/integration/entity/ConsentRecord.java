package vibe.digthc.as_digt_hc_dev_fe.domain.integration.entity;

import vibe.digthc.as_digt_hc_dev_fe.domain.common.BaseTimeEntity;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.enums.ConsentStatus;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.enums.ConsentSubjectType;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.enums.ConsentType;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 동의 기록 Entity
 * - 데이터 수집/공유 동의 관리
 */
@Entity
@Table(name = "consent_records",
    indexes = {
        @Index(name = "idx_consent_user", columnList = "user_id"),
        @Index(name = "idx_consent_subject", columnList = "subject_type, subject_id"),
        @Index(name = "idx_consent_status", columnList = "status")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsentRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "subject_type", nullable = false, length = 30)
    private ConsentSubjectType subjectType;

    @Column(name = "subject_id", columnDefinition = "BINARY(16)")
    private UUID subjectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "consent_type", nullable = false, length = 50)
    private ConsentType consentType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "consent_scope", columnDefinition = "JSON")
    private Map<String, Object> consentScope;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConsentStatus status;

    @Column(name = "consent_version", length = 10)
    private String consentVersion;

    @Column(name = "consented_at", nullable = false)
    private LocalDateTime consentedAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "revoke_reason", length = 100)
    private String revokeReason;

    // ========================================
    // Builder
    // ========================================
    @Builder
    private ConsentRecord(User user, ConsentSubjectType subjectType, UUID subjectId,
                          ConsentType consentType, Map<String, Object> consentScope,
                          String consentVersion) {
        this.user = user;
        this.subjectType = subjectType;
        this.subjectId = subjectId;
        this.consentType = consentType;
        this.consentScope = consentScope;
        this.consentVersion = consentVersion;
        this.status = ConsentStatus.ACTIVE;
        this.consentedAt = LocalDateTime.now();
    }

    // ========================================
    // Factory Method
    // ========================================

    /**
     * 디바이스 연동 동의 생성
     */
    public static ConsentRecord grantDeviceConsent(User user, UUID deviceId,
                                                    Map<String, Object> scope) {
        return ConsentRecord.builder()
                .user(user)
                .subjectType(ConsentSubjectType.DEVICE)
                .subjectId(deviceId)
                .consentType(ConsentType.DATA_COLLECTION)
                .consentScope(scope)
                .consentVersion("1.0")
                .build();
    }

    /**
     * 포털 연동 동의 생성
     */
    public static ConsentRecord grantPortalConsent(User user, UUID portalId,
                                                    Map<String, Object> scope) {
        return ConsentRecord.builder()
                .user(user)
                .subjectType(ConsentSubjectType.PORTAL)
                .subjectId(portalId)
                .consentType(ConsentType.DATA_COLLECTION)
                .consentScope(scope)
                .consentVersion("1.0")
                .build();
    }

    /**
     * 가족 보드 공유 동의 생성
     */
    public static ConsentRecord grantFamilyBoardConsent(User user, UUID boardId,
                                                         Map<String, Object> scope) {
        return ConsentRecord.builder()
                .user(user)
                .subjectType(ConsentSubjectType.FAMILY_BOARD)
                .subjectId(boardId)
                .consentType(ConsentType.DATA_SHARING)
                .consentScope(scope)
                .consentVersion("1.0")
                .build();
    }

    // ========================================
    // Business Methods
    // ========================================

    /**
     * 동의 철회
     */
    public void revoke(String reason) {
        if (status != ConsentStatus.ACTIVE) {
            throw new IllegalStateException("활성 상태의 동의만 철회할 수 있습니다.");
        }
        this.status = ConsentStatus.REVOKED;
        this.revokedAt = LocalDateTime.now();
        this.revokeReason = reason;
    }

    /**
     * 동의 만료 처리
     */
    public void expire() {
        if (status == ConsentStatus.ACTIVE) {
            this.status = ConsentStatus.EXPIRED;
        }
    }

    /**
     * 활성 동의 여부
     */
    public boolean isActive() {
        return status == ConsentStatus.ACTIVE;
    }

    /**
     * 동의 범위 내 특정 데이터 타입 허용 여부
     */
    @SuppressWarnings("unchecked")
    public boolean allowsDataType(String dataType) {
        if (consentScope == null) return false;
        var dataTypes = (java.util.List<String>) consentScope.get("dataTypes");
        return dataTypes != null && dataTypes.contains(dataType);
    }
}


package vibe.digthc.as_digt_hc_dev_fe.domain.integration.entity;

import vibe.digthc.as_digt_hc_dev_fe.domain.common.BaseTimeEntity;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.enums.PortalStatus;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 포털 연동 Entity
 * - 병원 포털 연동 정보
 */
@Entity
@Table(name = "portal_connections",
    indexes = {
        @Index(name = "idx_portal_conn_user", columnList = "user_id"),
        @Index(name = "idx_portal_conn_user_type", columnList = "user_id, portal_type"),
        @Index(name = "idx_portal_conn_status", columnList = "status")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortalConnection extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "portal_type", nullable = false, length = 50)
    private String portalType;

    @Column(name = "portal_id", length = 100)
    private String portalId;

    @Column(name = "portal_name", length = 100)
    private String portalName;

    @Column(name = "portal_user_id", length = 255)
    private String portalUserId;

    @Column(columnDefinition = "TEXT")
    private String credentials;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PortalStatus status;

    @Column(name = "error_code", length = 100)
    private String errorCode;

    @Column(name = "error_message", length = 255)
    private String errorMessage;

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;

    // ========================================
    // Builder
    // ========================================
    @Builder
    private PortalConnection(User user, String portalType, String portalId, String portalName, String portalUserId, String credentials) {
        this.user = user;
        this.portalType = portalType;
        this.portalId = portalId;
        this.portalName = portalName;
        this.portalUserId = portalUserId;
        this.credentials = credentials;
        this.status = PortalStatus.PENDING;
    }

    // ========================================
    // Factory Method
    // ========================================
    public static PortalConnection create(User user, String portalType, String portalId) {
        return PortalConnection.builder()
                .user(user)
                .portalType(portalType)
                .portalId(portalId)
                .build();
    }

    // ========================================
    // Business Methods
    // ========================================

    /**
     * 활성 상태로 변경
     */
    public void markActive() {
        this.status = PortalStatus.ACTIVE;
        this.errorCode = null;
        this.errorMessage = null;
    }

    /**
     * 실패 상태 설정
     */
    public void markFailed(String errorCode, String errorMessage) {
        this.status = PortalStatus.FAILED;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 미지원 상태 설정
     */
    public void markUnsupported() {
        this.status = PortalStatus.UNSUPPORTED;
    }

    /**
     * 연동 해제
     */
    public void revoke() {
        this.status = PortalStatus.REVOKED;
        this.credentials = null;
    }

    /**
     * 동기화 완료 표시
     */
    public void markSynced() {
        this.lastSyncAt = LocalDateTime.now();
        this.status = PortalStatus.ACTIVE;
    }

    /**
     * 포털 이름 설정
     */
    public void setPortalName(String portalName) {
        this.portalName = portalName;
    }

    /**
     * 포털 사용자 ID 설정
     */
    public void setPortalUserId(String portalUserId) {
        this.portalUserId = portalUserId;
    }

    /**
     * 인증 정보 설정
     */
    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }
}


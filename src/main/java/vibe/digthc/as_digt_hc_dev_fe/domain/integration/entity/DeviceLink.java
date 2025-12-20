package vibe.digthc.as_digt_hc_dev_fe.domain.integration.entity;

import vibe.digthc.as_digt_hc_dev_fe.domain.common.BaseTimeEntity;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.enums.DeviceStatus;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 디바이스 연동 Entity
 * - 워치, 혈압계 등 외부 디바이스 OAuth 연동 정보
 */
@Entity
@Table(name = "device_links",
    indexes = {
        @Index(name = "idx_device_links_user", columnList = "user_id"),
        @Index(name = "idx_device_links_user_vendor", columnList = "user_id, vendor"),
        @Index(name = "idx_device_links_status", columnList = "status"),
        @Index(name = "idx_device_links_expires", columnList = "token_expires_at")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceLink extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String vendor;

    @Column(name = "device_type", nullable = false, length = 50)
    private String deviceType;

    @Column(name = "vendor_user_id", length = 30)
    private String vendorUserId;

    @Column(name = "access_token", columnDefinition = "TEXT", nullable = false)
    private String accessToken;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "token_expires_at", nullable = false)
    private LocalDateTime tokenExpiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeviceStatus status;

    @Column(name = "error_message", length = 100)
    private String errorMessage;

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sync_config", columnDefinition = "JSON")
    private Map<String, Object> syncConfig;

    // ========================================
    // Builder
    // ========================================
    @Builder
    private DeviceLink(User user, String vendor, String deviceType, String vendorUserId) {
        this.user = user;
        this.vendor = vendor;
        this.deviceType = deviceType;
        this.vendorUserId = vendorUserId;
        this.status = DeviceStatus.PENDING;
        this.syncConfig = getDefaultSyncConfig();
    }

    // ========================================
    // Factory Method
    // ========================================
    public static DeviceLink create(User user, String vendor, String deviceType) {
        return DeviceLink.builder()
                .user(user)
                .vendor(vendor)
                .deviceType(deviceType)
                .build();
    }

    // ========================================
    // Business Methods
    // ========================================

    /**
     * 토큰 설정 및 활성화
     */
    public void setTokens(String accessToken, String refreshToken, LocalDateTime expiresAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenExpiresAt = expiresAt;
        this.status = DeviceStatus.ACTIVE;
        this.errorMessage = null;
    }

    /**
     * 토큰 갱신
     */
    public void refreshTokens(String newAccessToken, String newRefreshToken, LocalDateTime expiresAt) {
        this.accessToken = newAccessToken;
        if (newRefreshToken != null) {
            this.refreshToken = newRefreshToken;
        }
        this.tokenExpiresAt = expiresAt;
        this.status = DeviceStatus.ACTIVE;
        this.errorMessage = null;
    }

    /**
     * 동기화 완료 표시
     */
    public void markSynced() {
        this.lastSyncAt = LocalDateTime.now();
        this.status = DeviceStatus.ACTIVE;
        this.errorMessage = null;
    }

    /**
     * 오류 상태 설정
     */
    public void markError(String message) {
        this.status = DeviceStatus.ERROR;
        this.errorMessage = message;
    }

    /**
     * 토큰 만료 상태 설정
     */
    public void markExpired() {
        this.status = DeviceStatus.EXPIRED;
    }

    /**
     * 연동 해제
     */
    public void revoke() {
        this.status = DeviceStatus.REVOKED;
        this.accessToken = null;
        this.refreshToken = null;
    }

    /**
     * 토큰 만료 여부
     */
    public boolean isTokenExpired() {
        return LocalDateTime.now().isAfter(tokenExpiresAt);
    }

    /**
     * 토큰 갱신 필요 여부 (만료 1시간 전)
     */
    public boolean needsTokenRefresh() {
        return LocalDateTime.now().plusHours(1).isAfter(tokenExpiresAt);
    }

    /**
     * 동기화 가능 여부
     */
    public boolean canSync() {
        return status == DeviceStatus.ACTIVE && !isTokenExpired();
    }

    /**
     * 벤더 사용자 ID 설정
     */
    public void setVendorUserId(String vendorUserId) {
        this.vendorUserId = vendorUserId;
    }

    // ========================================
    // Private Methods
    // ========================================

    private Map<String, Object> getDefaultSyncConfig() {
        return Map.of(
            "syncFrequency", "hourly",
            "dataTypes", java.util.List.of("steps", "heartRate", "sleep"),
            "batchSize", 1000
        );
    }
}


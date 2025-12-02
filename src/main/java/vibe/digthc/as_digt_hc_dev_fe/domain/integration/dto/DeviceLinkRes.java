package vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto;

import vibe.digthc.as_digt_hc_dev_fe.domain.integration.entity.DeviceLink;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.enums.DeviceStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 디바이스 연동 응답 DTO
 */
public record DeviceLinkRes(
    UUID deviceId,
    String vendor,
    String deviceType,
    DeviceStatus status,
    LocalDateTime lastSyncAt,
    boolean hasActiveConsent
) {
    public static DeviceLinkRes from(DeviceLink deviceLink, boolean hasActiveConsent) {
        return new DeviceLinkRes(
            deviceLink.getId(),
            deviceLink.getVendor(),
            deviceLink.getDeviceType(),
            deviceLink.getStatus(),
            deviceLink.getLastSyncAt(),
            hasActiveConsent
        );
    }
}


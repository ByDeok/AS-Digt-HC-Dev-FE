package vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto;

import vibe.digthc.as_digt_hc_dev_fe.domain.integration.entity.PortalConnection;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.enums.PortalStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 포털 연동 응답 DTO
 */
public record PortalConnectionRes(
    UUID portalId,
    String portalType,
    String portalName,
    PortalStatus status,
    LocalDateTime lastSyncAt
) {
    public static PortalConnectionRes from(PortalConnection portalConnection) {
        return new PortalConnectionRes(
            portalConnection.getId(),
            portalConnection.getPortalType(),
            portalConnection.getPortalName(),
            portalConnection.getStatus(),
            portalConnection.getLastSyncAt()
        );
    }
}


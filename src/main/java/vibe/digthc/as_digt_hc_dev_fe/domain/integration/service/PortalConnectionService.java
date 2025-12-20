package vibe.digthc.as_digt_hc_dev_fe.domain.integration.service;

import vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto.PortalConnectReq;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto.PortalConnectionRes;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto.SyncResultRes;

import java.util.List;
import java.util.UUID;

public interface PortalConnectionService {
    List<PortalConnectionRes> getConnections(UUID userId);
    PortalConnectionRes connectPortal(UUID userId, PortalConnectReq req);
    void disconnectPortal(UUID userId, UUID portalId);
    SyncResultRes syncPortal(UUID userId, UUID portalId);
}


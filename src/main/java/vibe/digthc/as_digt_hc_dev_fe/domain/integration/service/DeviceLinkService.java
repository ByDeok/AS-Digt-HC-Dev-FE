package vibe.digthc.as_digt_hc_dev_fe.domain.integration.service;

import vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto.DeviceConnectReq;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto.DeviceLinkRes;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto.SyncResultRes;

import java.util.List;
import java.util.UUID;

public interface DeviceLinkService {
    List<DeviceLinkRes> getDeviceLinks(UUID userId);
    DeviceLinkRes connectDevice(UUID userId, DeviceConnectReq req);
    void disconnectDevice(UUID userId, UUID deviceId);
    SyncResultRes syncDevice(UUID userId, UUID deviceId);
    void refreshToken(UUID deviceId);
}


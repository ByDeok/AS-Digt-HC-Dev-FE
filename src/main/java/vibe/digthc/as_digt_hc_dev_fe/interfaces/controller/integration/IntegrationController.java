package vibe.digthc.as_digt_hc_dev_fe.interfaces.controller.integration;

import vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto.*;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.service.ConsentService;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.service.DeviceLinkService;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.service.PortalConnectionService;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.security.CurrentUserId;
import vibe.digthc.as_digt_hc_dev_fe.interfaces.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 외부 연동 API Controller
 */
@RestController
@RequestMapping("/v1/integration")
@RequiredArgsConstructor
public class IntegrationController {

    private final DeviceLinkService deviceService;
    private final PortalConnectionService portalService;
    private final ConsentService consentService;

    // ========================================
    // 디바이스 연동 API
    // ========================================

    /**
     * 디바이스 연동 목록 조회
     */
    @GetMapping("/devices")
    public ResponseEntity<ApiResponse<List<DeviceLinkRes>>> getDeviceLinks(@CurrentUserId UUID userId) {
        List<DeviceLinkRes> devices = deviceService.getDeviceLinks(userId);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", devices));
    }

    /**
     * 디바이스 연동
     */
    @PostMapping("/devices")
    public ResponseEntity<ApiResponse<DeviceLinkRes>> connectDevice(
            @CurrentUserId UUID userId,
            @RequestBody @Valid DeviceConnectReq req) {
        DeviceLinkRes device = deviceService.connectDevice(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("디바이스가 연동되었습니다.", device));
    }

    /**
     * 디바이스 수동 동기화
     */
    @PostMapping("/devices/{deviceId}/sync")
    public ResponseEntity<ApiResponse<SyncResultRes>> syncDevice(
            @CurrentUserId UUID userId,
            @PathVariable UUID deviceId) {
        SyncResultRes result = deviceService.syncDevice(userId, deviceId);
        return ResponseEntity.ok(ApiResponse.success("동기화가 완료되었습니다.", result));
    }

    /**
     * 디바이스 연동 해제
     */
    @DeleteMapping("/devices/{deviceId}")
    public ResponseEntity<ApiResponse<Void>> disconnectDevice(
            @CurrentUserId UUID userId,
            @PathVariable UUID deviceId) {
        deviceService.disconnectDevice(userId, deviceId);
        return ResponseEntity.noContent().build();
    }

    // ========================================
    // 포털 연동 API
    // ========================================

    /**
     * 포털 연동 목록 조회
     */
    @GetMapping("/portals")
    public ResponseEntity<ApiResponse<List<PortalConnectionRes>>> getPortalConnections(@CurrentUserId UUID userId) {
        List<PortalConnectionRes> portals = portalService.getConnections(userId);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", portals));
    }

    /**
     * 포털 연동
     */
    @PostMapping("/portals")
    public ResponseEntity<ApiResponse<PortalConnectionRes>> connectPortal(
            @CurrentUserId UUID userId,
            @RequestBody @Valid PortalConnectReq req) {
        PortalConnectionRes portal = portalService.connectPortal(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("포털이 연동되었습니다.", portal));
    }

    /**
     * 포털 수동 동기화
     */
    @PostMapping("/portals/{portalId}/sync")
    public ResponseEntity<ApiResponse<SyncResultRes>> syncPortal(
            @CurrentUserId UUID userId,
            @PathVariable UUID portalId) {
        SyncResultRes result = portalService.syncPortal(userId, portalId);
        return ResponseEntity.ok(ApiResponse.success("동기화가 완료되었습니다.", result));
    }

    /**
     * 포털 연동 해제
     */
    @DeleteMapping("/portals/{portalId}")
    public ResponseEntity<ApiResponse<Void>> disconnectPortal(
            @CurrentUserId UUID userId,
            @PathVariable UUID portalId) {
        portalService.disconnectPortal(userId, portalId);
        return ResponseEntity.noContent().build();
    }

    // ========================================
    // 동의 관리 API
    // ========================================

    /**
     * 동의 목록 조회
     */
    @GetMapping("/consents")
    public ResponseEntity<ApiResponse<List<ConsentRes>>> getConsents(@CurrentUserId UUID userId) {
        List<ConsentRes> consents = consentService.getConsents(userId);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", consents));
    }

    /**
     * 동의 철회
     */
    @DeleteMapping("/consents/{consentId}")
    public ResponseEntity<ApiResponse<Void>> revokeConsent(
            @CurrentUserId UUID userId,
            @PathVariable UUID consentId,
            @RequestBody(required = false) RevokeConsentReq req) {
        String reason = req != null ? req.revokeReason() : "사용자 요청";
        consentService.revokeConsent(userId, consentId, reason);
        return ResponseEntity.noContent().build();
    }
}


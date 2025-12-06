package vibe.digthc.as_digt_hc_dev_fe.domain.integration.service;

import vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto.DeviceConnectReq;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto.DeviceLinkRes;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto.SyncResultRes;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.entity.DeviceLink;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.enums.ConsentSubjectType;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.exception.DeviceAlreadyLinkedException;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.exception.DeviceNotFoundException;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.repository.ConsentRecordRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.repository.DeviceLinkRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserRepository;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.integration.DeviceDataProvider;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.integration.DeviceProviderFactory;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.integration.HealthDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceLinkServiceImpl implements DeviceLinkService {

    private final DeviceLinkRepository deviceRepository;
    private final ConsentRecordRepository consentRepository;
    private final UserRepository userRepository;
    private final DeviceProviderFactory providerFactory;
    private final ConsentService consentService;

    @Value("${oauth.callback.url:http://localhost:8080/oauth/callback}")
    private String oauthCallbackUrl;

    @Override
    public List<DeviceLinkRes> getDeviceLinks(UUID userId) {
        List<DeviceLink> devices = deviceRepository.findAllByUserId(userId);
        return devices.stream()
                .map(device -> {
                    boolean hasActiveConsent = consentRepository.existsActiveConsent(
                            userId, ConsentSubjectType.DEVICE, device.getId());
                    return DeviceLinkRes.from(device, hasActiveConsent);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DeviceLinkRes connectDevice(UUID userId, DeviceConnectReq req) {
        // 기존 연동 확인
        deviceRepository.findByUserIdAndVendor(userId, req.vendor())
                .ifPresent(d -> {
                    throw new DeviceAlreadyLinkedException("이미 연동된 디바이스입니다.");
                });

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // Provider를 통한 OAuth 토큰 교환
        DeviceDataProvider provider = providerFactory.getProvider(req.vendor());
        var tokenResponse = provider.authorize(req.authCode(), oauthCallbackUrl);

        // DeviceLink 생성
        DeviceLink deviceLink = DeviceLink.create(user, req.vendor(), req.deviceType());
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(tokenResponse.expiresIn());
        deviceLink.setTokens(tokenResponse.accessToken(), tokenResponse.refreshToken(), expiresAt);

        DeviceLink savedDevice = deviceRepository.save(deviceLink);

        // 동의 기록 생성
        Map<String, Object> scopeMap = new HashMap<>();
        scopeMap.put("dataTypes", req.consentScope().dataTypes());
        scopeMap.put("frequency", req.consentScope().frequency());
        scopeMap.put("retentionPeriod", req.consentScope().retentionPeriod());
        scopeMap.put("sharingAllowed", req.consentScope().sharingAllowed());

        consentService.grantConsent(userId, ConsentSubjectType.DEVICE, savedDevice.getId(), scopeMap);

        // 초기 데이터 동기화
        try {
            syncDevice(userId, savedDevice.getId());
        } catch (Exception e) {
            log.warn("초기 데이터 동기화 실패: {}", e.getMessage());
        }

        boolean hasActiveConsent = consentRepository.existsActiveConsent(
                userId, ConsentSubjectType.DEVICE, savedDevice.getId());
        return DeviceLinkRes.from(savedDevice, hasActiveConsent);
    }

    @Override
    @Transactional
    public void disconnectDevice(UUID userId, UUID deviceId) {
        DeviceLink device = deviceRepository.findByUserIdAndId(userId, deviceId)
                .orElseThrow(() -> new DeviceNotFoundException("디바이스를 찾을 수 없습니다."));

        // 동의 철회
        consentRepository.findActiveConsent(userId, ConsentSubjectType.DEVICE, deviceId)
                .ifPresent(consent -> {
                    try {
                        DeviceDataProvider provider = providerFactory.getProvider(device.getVendor());
                        provider.revokeAccess(device.getAccessToken());
                    } catch (Exception e) {
                        log.warn("벤더 측 연동 해제 실패: {}", e.getMessage());
                    }
                    consent.revoke("사용자 요청");
                    consentRepository.save(consent);
                });

        // 연동 해제
        device.revoke();
        deviceRepository.save(device);
    }

    @Override
    @Transactional
    public SyncResultRes syncDevice(UUID userId, UUID deviceId) {
        DeviceLink device = deviceRepository.findByUserIdAndId(userId, deviceId)
                .orElseThrow(() -> new DeviceNotFoundException("디바이스를 찾을 수 없습니다."));

        if (!device.canSync()) {
            throw new IllegalStateException("동기화할 수 없는 상태입니다.");
        }

        try {
            DeviceDataProvider provider = providerFactory.getProvider(device.getVendor());
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = device.getLastSyncAt() != null
                    ? device.getLastSyncAt().toLocalDate()
                    : endDate.minusDays(7);

            List<HealthDataDto> healthData = provider.getHealthData(
                    device.getAccessToken(), startDate, endDate);

            // TODO: HealthDataDaily 저장 로직 구현 필요
            // saveHealthData(device, healthData);

            device.markSynced();
            deviceRepository.save(device);

            return new SyncResultRes(
                    healthData.size(),
                    LocalDateTime.now(),
                    "SUCCESS",
                    Collections.emptyList()
            );
        } catch (Exception e) {
            log.error("디바이스 동기화 실패: deviceId={}", deviceId, e);
            device.markError(e.getMessage());
            deviceRepository.save(device);

            return new SyncResultRes(
                    0,
                    LocalDateTime.now(),
                    "FAILED",
                    List.of(e.getMessage())
            );
        }
    }

    @Override
    @Transactional
    public void refreshToken(UUID deviceId) {
        DeviceLink device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException("디바이스를 찾을 수 없습니다."));

        if (device.getRefreshToken() == null) {
            throw new IllegalStateException("리프레시 토큰이 없습니다.");
        }

        try {
            DeviceDataProvider provider = providerFactory.getProvider(device.getVendor());
            var tokenResponse = provider.refreshToken(device.getRefreshToken());

            LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(tokenResponse.expiresIn());
            device.refreshTokens(tokenResponse.accessToken(), tokenResponse.refreshToken(), expiresAt);
            deviceRepository.save(device);
        } catch (Exception e) {
            log.error("토큰 갱신 실패: deviceId={}", deviceId, e);
            device.markError("토큰 갱신 실패: " + e.getMessage());
            deviceRepository.save(device);
            throw e;
        }
    }
}


package vibe.digthc.as_digt_hc_dev_fe.infrastructure.scheduler;

import vibe.digthc.as_digt_hc_dev_fe.domain.integration.entity.DeviceLink;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.repository.DeviceLinkRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.service.DeviceLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 데이터 동기화 스케줄러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SyncScheduler {

    private final DeviceLinkRepository deviceRepository;
    private final DeviceLinkService deviceService;

    /**
     * 활성 디바이스 데이터 동기화 (매 시간)
     */
    @Scheduled(cron = "0 0 * * * *")
    public void syncActiveDevices() {
        log.info("디바이스 데이터 동기화 시작");
        
        try {
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            List<DeviceLink> devices = deviceRepository.findDevicesNeedingSync(oneHourAgo);
            
            int successCount = 0;
            int failCount = 0;
            
            for (DeviceLink device : devices) {
                try {
                    deviceService.syncDevice(device.getUser().getId(), device.getId());
                    successCount++;
                } catch (Exception e) {
                    log.error("디바이스 동기화 실패: deviceId={}", device.getId(), e);
                    failCount++;
                }
            }
            
            log.info("디바이스 데이터 동기화 완료: 성공={}, 실패={}", successCount, failCount);
        } catch (Exception e) {
            log.error("디바이스 데이터 동기화 배치 오류", e);
        }
    }

    /**
     * 토큰 갱신 체크 (30분마다)
     */
    @Scheduled(cron = "0 */30 * * * *")
    public void refreshExpiredTokens() {
        log.info("토큰 갱신 체크 시작");
        
        try {
            LocalDateTime threshold = LocalDateTime.now().plusHours(1);
            List<DeviceLink> devices = deviceRepository.findDevicesNeedingTokenRefresh(threshold);
            
            for (DeviceLink device : devices) {
                try {
                    deviceService.refreshToken(device.getId());
                    log.debug("토큰 갱신 완료: deviceId={}", device.getId());
                } catch (Exception e) {
                    log.error("토큰 갱신 실패: deviceId={}", device.getId(), e);
                }
            }
            
            log.info("토큰 갱신 체크 완료: {}개 디바이스 처리", devices.size());
        } catch (Exception e) {
            log.error("토큰 갱신 배치 오류", e);
        }
    }
}


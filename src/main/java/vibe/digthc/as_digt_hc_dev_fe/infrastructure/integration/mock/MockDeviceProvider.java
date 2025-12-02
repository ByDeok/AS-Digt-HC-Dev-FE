package vibe.digthc.as_digt_hc_dev_fe.infrastructure.integration.mock;

import vibe.digthc.as_digt_hc_dev_fe.infrastructure.integration.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 테스트용 Mock 디바이스 데이터 제공자
 */
@Component
@Profile({"local", "test"})
public class MockDeviceProvider implements DeviceDataProvider {

    private static final String VENDOR = "mock";

    @Override
    public TokenResponse authorize(String authCode, String redirectUri) {
        return new TokenResponse(
            "mock_access_token_" + UUID.randomUUID(),
            "mock_refresh_token_" + UUID.randomUUID(),
            3600L,
            "Bearer"
        );
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        return new TokenResponse(
            "mock_access_token_" + UUID.randomUUID(),
            refreshToken,
            3600L,
            "Bearer"
        );
    }

    @Override
    public List<HealthDataDto> getHealthData(String accessToken, 
                                              LocalDate startDate, 
                                              LocalDate endDate) {
        List<HealthDataDto> data = new ArrayList<>();
        
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            // 걸음수 데이터
            data.add(new HealthDataDto(
                current,
                "STEPS",
                Map.of("steps", randomInt(3000, 12000)),
                current.atStartOfDay()
            ));
            
            // 심박수 데이터
            data.add(new HealthDataDto(
                current,
                "HEART_RATE",
                Map.of(
                    "resting", randomInt(55, 75),
                    "average", randomInt(65, 85),
                    "max", randomInt(100, 150)
                ),
                current.atStartOfDay()
            ));
            
            // 수면 데이터
            data.add(new HealthDataDto(
                current,
                "SLEEP",
                Map.of(
                    "duration", randomDouble(5.0, 9.0),
                    "quality", randomInt(60, 95)
                ),
                current.atStartOfDay()
            ));
            
            current = current.plusDays(1);
        }
        
        return data;
    }

    @Override
    public void revokeAccess(String accessToken) {
        // Mock: 아무 작업 없음
    }

    @Override
    public String getVendor() {
        return VENDOR;
    }

    @Override
    public List<String> getSupportedDataTypes() {
        return List.of("STEPS", "HEART_RATE", "SLEEP");
    }

    // ========================================
    // Private Methods
    // ========================================

    private int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private double randomDouble(double min, double max) {
        return Math.round(ThreadLocalRandom.current().nextDouble(min, max) * 10) / 10.0;
    }
}


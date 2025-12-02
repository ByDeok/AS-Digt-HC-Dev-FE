package vibe.digthc.as_digt_hc_dev_fe.infrastructure.integration;

import java.time.LocalDate;
import java.util.List;

/**
 * 디바이스 데이터 제공자 인터페이스
 * - 각 벤더별 구현체가 이 인터페이스를 구현
 */
public interface DeviceDataProvider {

    /**
     * OAuth 인증 코드로 토큰 교환
     */
    TokenResponse authorize(String authCode, String redirectUri);

    /**
     * 토큰 갱신
     */
    TokenResponse refreshToken(String refreshToken);

    /**
     * 건강 데이터 조회
     */
    List<HealthDataDto> getHealthData(String accessToken, LocalDate startDate, LocalDate endDate);

    /**
     * 연동 해제
     */
    void revokeAccess(String accessToken);

    /**
     * 벤더 식별자
     */
    String getVendor();

    /**
     * 지원하는 데이터 타입
     */
    List<String> getSupportedDataTypes();
}


package vibe.digthc.as_digt_hc_dev_fe.infrastructure.integration;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 포털 데이터 제공자 인터페이스
 */
public interface PortalDataProvider {

    /**
     * 포털 인증
     */
    AuthResult authenticate(Map<String, String> credentials);

    /**
     * 검진 결과 조회
     */
    List<CheckupRecordDto> getCheckupRecords(String token, LocalDate startDate, LocalDate endDate);

    /**
     * 진료 기록 조회
     */
    List<MedicalRecordDto> getMedicalRecords(String token, LocalDate startDate, LocalDate endDate);

    /**
     * 포털 타입
     */
    String getPortalType();
}


package vibe.digthc.as_digt_hc_dev_fe.infrastructure.integration.mock;

import vibe.digthc.as_digt_hc_dev_fe.infrastructure.integration.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

/**
 * 테스트용 Mock 포털 데이터 제공자
 */
@Component
@Profile({"local", "test"})
public class MockPortalProvider implements PortalDataProvider {

    private static final String PORTAL_TYPE = "NHIS";

    @Override
    public AuthResult authenticate(Map<String, String> credentials) {
        return new AuthResult(
            "mock_portal_token_" + UUID.randomUUID(),
            "mock_portal_user_" + UUID.randomUUID().toString().substring(0, 8),
            "건강보험심사평가원",
            true
        );
    }

    @Override
    public List<CheckupRecordDto> getCheckupRecords(String token, LocalDate startDate, LocalDate endDate) {
        List<CheckupRecordDto> records = new ArrayList<>();
        
        // 최근 6개월 내 검진 결과 생성
        LocalDate current = startDate;
        int recordCount = 0;
        while (!current.isAfter(endDate) && recordCount < 3) {
            if (current.getDayOfMonth() == 15) { // 매월 15일 검진 결과 생성
                records.add(new CheckupRecordDto(
                    current,
                    "건강보험심사평가원",
                    "일반건강검진",
                    Map.of(
                        "bloodPressure", Map.of("systolic", 120, "diastolic", 80),
                        "bloodSugar", 95,
                        "cholesterol", 180,
                        "bmi", 22.5
                    )
                ));
                recordCount++;
            }
            current = current.plusDays(1);
        }
        
        return records;
    }

    @Override
    public List<MedicalRecordDto> getMedicalRecords(String token, LocalDate startDate, LocalDate endDate) {
        List<MedicalRecordDto> records = new ArrayList<>();
        
        // 최근 6개월 내 진료 기록 생성
        LocalDate current = startDate;
        int recordCount = 0;
        while (!current.isAfter(endDate) && recordCount < 5) {
            if (current.getDayOfMonth() % 10 == 0) { // 10일, 20일, 30일에 진료 기록 생성
                records.add(new MedicalRecordDto(
                    current,
                    "서울대학교병원",
                    "내과",
                    "고혈압",
                    Map.of(
                        "prescription", "항고혈압제",
                        "symptoms", "두통, 어지러움"
                    )
                ));
                recordCount++;
            }
            current = current.plusDays(1);
        }
        
        return records;
    }

    @Override
    public String getPortalType() {
        return PORTAL_TYPE;
    }
}


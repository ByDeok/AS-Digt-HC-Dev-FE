package vibe.digthc.as_digt_hc_dev_fe.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.entity.HealthMetricDaily;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthMetricDailyResponse {
    private UUID userId;
    private LocalDate recordDate;
    private Integer steps;
    private Integer heartRate;
    private Double weight;
    private Integer systolic;
    private Integer diastolic;

    public static HealthMetricDailyResponse from(HealthMetricDaily entity) {
        return HealthMetricDailyResponse.builder()
                .userId(entity.getUser().getId())
                .recordDate(entity.getRecordDate())
                .steps(entity.getSteps())
                .heartRate(entity.getHeartRate())
                .weight(entity.getWeight())
                .systolic(entity.getSystolic())
                .diastolic(entity.getDiastolic())
                .build();
    }

    public static HealthMetricDailyResponse empty(UUID userId, LocalDate recordDate) {
        return HealthMetricDailyResponse.builder()
                .userId(userId)
                .recordDate(recordDate)
                .build();
    }
}

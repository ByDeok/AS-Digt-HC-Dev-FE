package vibe.digthc.as_digt_hc_dev_fe.domain.report.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthMetricDailyRequest {
    @NotNull
    private LocalDate recordDate;
    private Integer steps;
    private Integer heartRate;
    private Double weight;
    private Integer systolic;
    private Integer diastolic;

    public boolean hasAnyValue() {
        return steps != null || heartRate != null || weight != null || systolic != null || diastolic != null;
    }
}

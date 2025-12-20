package vibe.digthc.as_digt_hc_dev_fe.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportMetrics {
    private ActivityMetrics activity;
    private HeartRateMetrics heartRate;
    private BloodPressureMetrics bloodPressure;
    private WeightMetrics weight;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityMetrics {
        private Integer steps;
        private Integer activeMinutes;
        private Double caloriesBurned;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HeartRateMetrics {
        private Integer avgBpm;
        private Integer minBpm;
        private Integer maxBpm;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BloodPressureMetrics {
        private Integer systolic;
        private Integer diastolic;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeightMetrics {
        private Double value;
        private String unit; // e.g. "kg", "lbs"
    }
}


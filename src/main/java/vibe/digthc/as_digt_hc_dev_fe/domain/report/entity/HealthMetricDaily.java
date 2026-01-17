package vibe.digthc.as_digt_hc_dev_fe.domain.report.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vibe.digthc.as_digt_hc_dev_fe.domain.common.BaseTimeEntity;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "health_metrics_daily",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_metrics_user_date", columnNames = {"user_id", "record_date"})
        },
        indexes = {
                @Index(name = "idx_metrics_user_date", columnList = "user_id, record_date")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HealthMetricDaily extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "metric_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "steps")
    private Integer steps;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "bp_systolic")
    private Integer systolic;

    @Column(name = "bp_diastolic")
    private Integer diastolic;

    @Builder
    private HealthMetricDaily(User user, LocalDate recordDate, Integer steps, Integer heartRate, Double weight,
                              Integer systolic, Integer diastolic) {
        this.user = user;
        this.recordDate = recordDate;
        this.steps = steps;
        this.heartRate = heartRate;
        this.weight = weight;
        this.systolic = systolic;
        this.diastolic = diastolic;
    }

    public static HealthMetricDaily create(User user, LocalDate recordDate, Integer steps, Integer heartRate,
                                           Double weight, Integer systolic, Integer diastolic) {
        return HealthMetricDaily.builder()
                .user(user)
                .recordDate(recordDate)
                .steps(steps)
                .heartRate(heartRate)
                .weight(weight)
                .systolic(systolic)
                .diastolic(diastolic)
                .build();
    }

    public void update(Integer steps, Integer heartRate, Double weight, Integer systolic, Integer diastolic) {
        this.steps = steps;
        this.heartRate = heartRate;
        this.weight = weight;
        this.systolic = systolic;
        this.diastolic = diastolic;
    }
}

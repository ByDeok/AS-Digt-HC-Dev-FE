package vibe.digthc.as_digt_hc_dev_fe.domain.report.entity;

import jakarta.persistence.*;
import lombok.*;
import vibe.digthc.as_digt_hc_dev_fe.domain.common.BaseTimeEntity;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.converter.ReportContextConverter;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.converter.ReportMetricsConverter;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.dto.ReportContext;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.dto.ReportMetrics;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "health_reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HealthReport extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "report_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "metrics", columnDefinition = "TEXT")
    @Convert(converter = ReportMetricsConverter.class)
    private ReportMetrics metrics;

    @Column(name = "context", columnDefinition = "TEXT")
    @Convert(converter = ReportContextConverter.class)
    private ReportContext context;
    
    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder
    public HealthReport(User user, ReportMetrics metrics, ReportContext context, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.metrics = metrics;
        this.context = context;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}


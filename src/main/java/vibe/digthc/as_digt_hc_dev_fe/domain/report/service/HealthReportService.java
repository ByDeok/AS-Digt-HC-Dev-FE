package vibe.digthc.as_digt_hc_dev_fe.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.repository.FamilyBoardRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.dto.ReportContext;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.dto.ReportMetrics;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.entity.HealthReport;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.repository.HealthReportRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HealthReportService {

    private final HealthReportRepository healthReportRepository;
    private final UserRepository userRepository;
    private final FamilyBoardRepository familyBoardRepository;

    @Transactional
    public HealthReport generateReport(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(3);

        // Mock data aggregation
        ReportMetrics metrics = mockAggregation(startDate, endDate);
        ReportContext context = mockContext();

        HealthReport report = HealthReport.builder()
                .user(user)
                .metrics(metrics)
                .context(context)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        HealthReport savedReport = healthReportRepository.save(report);
        
        // 가족 보드 활동 시간 갱신 (Polling 대응)
        updateFamilyBoardActivity(userId);
        
        return savedReport;
    }

    public HealthReport getReport(UUID reportId) {
        return healthReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
    }

    public List<HealthReport> getReportsByUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return healthReportRepository.findByUserOrderByCreatedAtDesc(user);
    }

    private ReportMetrics mockAggregation(LocalDate start, LocalDate end) {
        Random random = new Random();
        return ReportMetrics.builder()
                .activity(ReportMetrics.ActivityMetrics.builder()
                        .steps(5000 + random.nextInt(5000)) // 5000-10000
                        .activeMinutes(30 + random.nextInt(60))
                        .caloriesBurned(2000 + random.nextDouble() * 500)
                        .build())
                .heartRate(ReportMetrics.HeartRateMetrics.builder()
                        .avgBpm(70 + random.nextInt(10))
                        .minBpm(60 + random.nextInt(5))
                        .maxBpm(120 + random.nextInt(20))
                        .build())
                .bloodPressure(ReportMetrics.BloodPressureMetrics.builder()
                        .systolic(110 + random.nextInt(20))
                        .diastolic(70 + random.nextInt(10))
                        .build())
                .weight(ReportMetrics.WeightMetrics.builder()
                        .value(60 + random.nextDouble() * 20)
                        .unit("kg")
                        .build())
                .build();
    }

    private ReportContext mockContext() {
        return ReportContext.builder()
                .deviceId("DEVICE-" + UUID.randomUUID().toString().substring(0, 8))
                .deviceType("SMART_WATCH")
                .isMissingData(false)
                .missingDataFields(List.of())
                .metadata("Mocked data for MVP")
                .build();
    }

    /**
     * 가족 보드 활동 시간 갱신
     * - 보드 내 데이터 변경 시 Polling 대응을 위한 타임스탬프 관리
     */
    private void updateFamilyBoardActivity(UUID userId) {
        familyBoardRepository.findBySeniorId(userId)
                .ifPresent(board -> {
                    board.updateLastActivity();
                    familyBoardRepository.save(board);
                });
    }
}


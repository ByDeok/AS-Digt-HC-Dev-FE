package vibe.digthc.as_digt_hc_dev_fe.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.repository.FamilyBoardRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.dto.ReportContext;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.dto.ReportMetrics;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.entity.HealthMetricDaily;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.entity.HealthReport;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.entity.PeriodType;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.repository.HealthMetricDailyRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.repository.HealthReportRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HealthReportService {

    private final HealthReportRepository healthReportRepository;
    private final HealthMetricDailyRepository healthMetricDailyRepository;
    private final UserRepository userRepository;
    private final FamilyBoardRepository familyBoardRepository;

    @Transactional
    public HealthReport generateReport(UUID userId, PeriodType periodType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        PeriodType resolvedType = periodType != null ? periodType : PeriodType.WEEKLY;
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = resolvedType == PeriodType.WEEKLY
                ? endDate.minusDays(6)
                : endDate.withDayOfMonth(1);

        List<HealthMetricDaily> dailyMetrics = healthMetricDailyRepository
                .findByUserIdAndRecordDateBetween(userId, startDate, endDate);

        ReportMetrics metrics = aggregateMetrics(dailyMetrics);
        ReportContext context = buildContext(startDate, endDate, dailyMetrics);

        HealthReport report = HealthReport.builder()
                .user(user)
                .metrics(metrics)
                .context(context)
                .startDate(startDate)
                .endDate(endDate)
                .periodType(resolvedType)
                .build();

        HealthReport savedReport = healthReportRepository.save(report);

        updateFamilyBoardActivity(userId);

        return savedReport;
    }

    public HealthReport getReport(UUID reportId) {
        return healthReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
    }

    public List<HealthReport> getReportsByUser(UUID userId, PeriodType periodType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (periodType == null) {
            return healthReportRepository.findByUserOrderByCreatedAtDesc(user);
        }
        return healthReportRepository.findByUserAndPeriodTypeOrderByCreatedAtDesc(user, periodType);
    }

    /**
     * 리포트 삭제
     */
    @Transactional
    public void deleteReport(UUID userId, UUID reportId) {
        HealthReport report = healthReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        if (!report.getUser().getId().equals(userId)) {
            throw new SecurityException("You do not have permission to delete this report");
        }

        healthReportRepository.delete(report);

        updateFamilyBoardActivity(userId);
    }

    private ReportMetrics aggregateMetrics(List<HealthMetricDaily> dailyMetrics) {
        Integer avgSteps = averageInt(dailyMetrics, HealthMetricDaily::getSteps);
        Integer avgHeartRate = averageInt(dailyMetrics, HealthMetricDaily::getHeartRate);
        Integer minHeartRate = minInt(dailyMetrics, HealthMetricDaily::getHeartRate);
        Integer maxHeartRate = maxInt(dailyMetrics, HealthMetricDaily::getHeartRate);
        Integer avgSystolic = averageInt(dailyMetrics, HealthMetricDaily::getSystolic);
        Integer avgDiastolic = averageInt(dailyMetrics, HealthMetricDaily::getDiastolic);
        Double avgWeight = averageDouble(dailyMetrics, HealthMetricDaily::getWeight);

        ReportMetrics.ActivityMetrics activity = avgSteps == null
                ? null
                : ReportMetrics.ActivityMetrics.builder()
                .steps(avgSteps)
                .build();

        ReportMetrics.HeartRateMetrics heartRate = avgHeartRate == null && minHeartRate == null && maxHeartRate == null
                ? null
                : ReportMetrics.HeartRateMetrics.builder()
                .avgBpm(avgHeartRate)
                .minBpm(minHeartRate)
                .maxBpm(maxHeartRate)
                .build();

        ReportMetrics.BloodPressureMetrics bloodPressure = avgSystolic == null && avgDiastolic == null
                ? null
                : ReportMetrics.BloodPressureMetrics.builder()
                .systolic(avgSystolic)
                .diastolic(avgDiastolic)
                .build();

        ReportMetrics.WeightMetrics weight = avgWeight == null
                ? null
                : ReportMetrics.WeightMetrics.builder()
                .value(avgWeight)
                .unit("kg")
                .build();

        return ReportMetrics.builder()
                .activity(activity)
                .heartRate(heartRate)
                .bloodPressure(bloodPressure)
                .weight(weight)
                .build();
    }

    private ReportContext buildContext(LocalDate startDate, LocalDate endDate, List<HealthMetricDaily> dailyMetrics) {
        int totalDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

        long stepsCount = dailyMetrics.stream().filter(m -> m.getSteps() != null).count();
        long heartRateCount = dailyMetrics.stream().filter(m -> m.getHeartRate() != null).count();
        long weightCount = dailyMetrics.stream().filter(m -> m.getWeight() != null).count();
        long systolicCount = dailyMetrics.stream().filter(m -> m.getSystolic() != null).count();
        long diastolicCount = dailyMetrics.stream().filter(m -> m.getDiastolic() != null).count();

        List<String> missingFields = new ArrayList<>();
        if (stepsCount < totalDays) {
            missingFields.add("steps");
        }
        if (heartRateCount < totalDays) {
            missingFields.add("heartRate");
        }
        if (weightCount < totalDays) {
            missingFields.add("weight");
        }
        if (systolicCount < totalDays || diastolicCount < totalDays) {
            missingFields.add("bloodPressure");
        }

        return ReportContext.builder()
                .deviceId("MANUAL")
                .deviceType("MANUAL")
                .isMissingData(!missingFields.isEmpty())
                .missingDataFields(missingFields)
                .metadata("manual-entry")
                .build();
    }

    private Integer averageInt(List<HealthMetricDaily> data, java.util.function.Function<HealthMetricDaily, Integer> getter) {
        List<Integer> values = data.stream()
                .map(getter)
                .filter(value -> value != null)
                .toList();
        if (values.isEmpty()) {
            return null;
        }
        double avg = values.stream().mapToInt(Integer::intValue).average().orElse(0);
        return (int) Math.round(avg);
    }

    private Double averageDouble(List<HealthMetricDaily> data, java.util.function.Function<HealthMetricDaily, Double> getter) {
        List<Double> values = data.stream()
                .map(getter)
                .filter(value -> value != null)
                .toList();
        if (values.isEmpty()) {
            return null;
        }
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private Integer minInt(List<HealthMetricDaily> data, java.util.function.Function<HealthMetricDaily, Integer> getter) {
        return data.stream()
                .map(getter)
                .filter(value -> value != null)
                .min(Integer::compareTo)
                .orElse(null);
    }

    private Integer maxInt(List<HealthMetricDaily> data, java.util.function.Function<HealthMetricDaily, Integer> getter) {
        return data.stream()
                .map(getter)
                .filter(value -> value != null)
                .max(Integer::compareTo)
                .orElse(null);
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

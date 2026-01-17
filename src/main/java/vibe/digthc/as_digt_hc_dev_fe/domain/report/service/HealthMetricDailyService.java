package vibe.digthc.as_digt_hc_dev_fe.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.dto.HealthMetricDailyRequest;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.dto.HealthMetricDailyResponse;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.entity.HealthMetricDaily;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.repository.HealthMetricDailyRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HealthMetricDailyService {

    private final HealthMetricDailyRepository healthMetricDailyRepository;
    private final UserRepository userRepository;

    @Transactional
    public HealthMetricDailyResponse upsertDailyMetrics(UUID userId, HealthMetricDailyRequest request) {
        if (request == null || request.getRecordDate() == null) {
            throw new IllegalArgumentException("recordDate is required");
        }
        if (!request.hasAnyValue()) {
            throw new IllegalArgumentException("At least one metric value is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        HealthMetricDaily metric = healthMetricDailyRepository
                .findByUserIdAndRecordDate(userId, request.getRecordDate())
                .map(existing -> {
                    existing.update(
                            request.getSteps(),
                            request.getHeartRate(),
                            request.getWeight(),
                            request.getSystolic(),
                            request.getDiastolic()
                    );
                    return existing;
                })
                .orElseGet(() -> HealthMetricDaily.create(
                        user,
                        request.getRecordDate(),
                        request.getSteps(),
                        request.getHeartRate(),
                        request.getWeight(),
                        request.getSystolic(),
                        request.getDiastolic()
                ));

        HealthMetricDaily saved = healthMetricDailyRepository.save(metric);
        return HealthMetricDailyResponse.from(saved);
    }

    public HealthMetricDailyResponse getDailyMetrics(UUID userId, LocalDate recordDate) {
        if (recordDate == null) {
            throw new IllegalArgumentException("recordDate is required");
        }

        return healthMetricDailyRepository
                .findByUserIdAndRecordDate(userId, recordDate)
                .map(HealthMetricDailyResponse::from)
                .orElseGet(() -> HealthMetricDailyResponse.empty(userId, recordDate));
    }
}

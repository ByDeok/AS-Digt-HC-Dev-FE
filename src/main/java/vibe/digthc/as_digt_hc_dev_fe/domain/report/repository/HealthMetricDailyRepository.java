package vibe.digthc.as_digt_hc_dev_fe.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.entity.HealthMetricDaily;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HealthMetricDailyRepository extends JpaRepository<HealthMetricDaily, UUID> {
    Optional<HealthMetricDaily> findByUserIdAndRecordDate(UUID userId, LocalDate recordDate);

    List<HealthMetricDaily> findByUserIdAndRecordDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);
}

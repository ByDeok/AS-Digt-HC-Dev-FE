package vibe.digthc.as_digt_hc_dev_fe.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.entity.HealthReport;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;

import java.util.List;
import java.util.UUID;

@Repository
public interface HealthReportRepository extends JpaRepository<HealthReport, UUID> {
    List<HealthReport> findByUserOrderByCreatedAtDesc(User user);
}


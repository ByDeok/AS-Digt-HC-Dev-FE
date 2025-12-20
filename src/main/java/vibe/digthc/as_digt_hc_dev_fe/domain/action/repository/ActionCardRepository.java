package vibe.digthc.as_digt_hc_dev_fe.domain.action.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibe.digthc.as_digt_hc_dev_fe.domain.action.entity.ActionCard;
import vibe.digthc.as_digt_hc_dev_fe.domain.action.enums.ActionStatus;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActionCardRepository extends JpaRepository<ActionCard, Long> {
    
    List<ActionCard> findByUserAndActionDate(User user, LocalDate actionDate);

    boolean existsByUserAndActionDate(User user, LocalDate actionDate);

    @Query("SELECT COUNT(ac) FROM ActionCard ac WHERE ac.user = :user AND ac.actionDate BETWEEN :startDate AND :endDate")
    long countByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(ac) FROM ActionCard ac WHERE ac.user = :user AND ac.status = :status AND ac.actionDate BETWEEN :startDate AND :endDate")
    long countByUserAndStatusAndDateRange(@Param("user") User user, @Param("status") ActionStatus status, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}


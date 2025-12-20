package vibe.digthc.as_digt_hc_dev_fe.domain.integration.repository;

import vibe.digthc.as_digt_hc_dev_fe.domain.integration.entity.ConsentRecord;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.enums.ConsentStatus;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.enums.ConsentSubjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsentRecordRepository extends JpaRepository<ConsentRecord, UUID> {

    /**
     * 사용자의 모든 동의 기록 조회
     */
    @Query("SELECT cr FROM ConsentRecord cr WHERE cr.user.id = :userId ORDER BY cr.consentedAt DESC")
    List<ConsentRecord> findByUserIdOrderByConsentedAtDesc(@Param("userId") UUID userId);

    /**
     * 사용자의 특정 대상에 대한 동의 조회
     */
    @Query("SELECT cr FROM ConsentRecord cr WHERE cr.user.id = :userId " +
           "AND cr.subjectType = :subjectType AND cr.subjectId = :subjectId")
    Optional<ConsentRecord> findByUserIdAndSubjectTypeAndSubjectId(
            @Param("userId") UUID userId,
            @Param("subjectType") ConsentSubjectType subjectType,
            @Param("subjectId") UUID subjectId);

    /**
     * 사용자의 활성 동의 조회
     */
    @Query("SELECT cr FROM ConsentRecord cr " +
           "WHERE cr.user.id = :userId " +
           "AND cr.subjectType = :subjectType " +
           "AND cr.subjectId = :subjectId " +
           "AND cr.status = 'ACTIVE'")
    Optional<ConsentRecord> findActiveConsent(
            @Param("userId") UUID userId,
            @Param("subjectType") ConsentSubjectType subjectType,
            @Param("subjectId") UUID subjectId);

    /**
     * 활성 동의 존재 여부
     */
    @Query("SELECT COUNT(cr) > 0 FROM ConsentRecord cr " +
           "WHERE cr.user.id = :userId " +
           "AND cr.subjectType = :subjectType " +
           "AND cr.subjectId = :subjectId " +
           "AND cr.status = 'ACTIVE'")
    boolean existsActiveConsent(
            @Param("userId") UUID userId,
            @Param("subjectType") ConsentSubjectType subjectType,
            @Param("subjectId") UUID subjectId);

    /**
     * 사용자의 활성 동의 목록
     */
    @Query("SELECT cr FROM ConsentRecord cr WHERE cr.user.id = :userId AND cr.status = :status")
    List<ConsentRecord> findByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") ConsentStatus status);
}


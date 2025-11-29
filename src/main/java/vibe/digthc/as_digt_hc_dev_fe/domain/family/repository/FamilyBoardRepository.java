package vibe.digthc.as_digt_hc_dev_fe.domain.family.repository;

import vibe.digthc.as_digt_hc_dev_fe.domain.family.entity.FamilyBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyBoardRepository extends JpaRepository<FamilyBoard, UUID> {

    /**
     * 시니어의 가족 보드 조회
     */
    Optional<FamilyBoard> findBySeniorId(UUID seniorId);

    /**
     * 멤버의 가족 보드 조회
     */
    @Query("SELECT fb FROM FamilyBoard fb " +
           "JOIN fb.members m " +
           "WHERE m.member.id = :memberId AND m.memberStatus = 'ACTIVE'")
    Optional<FamilyBoard> findByActiveMemberId(@Param("memberId") UUID memberId);

    /**
     * 보드 존재 여부 확인
     */
    boolean existsBySeniorId(UUID seniorId);
}


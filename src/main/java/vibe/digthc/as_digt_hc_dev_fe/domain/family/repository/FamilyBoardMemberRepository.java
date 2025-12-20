package vibe.digthc.as_digt_hc_dev_fe.domain.family.repository;

import vibe.digthc.as_digt_hc_dev_fe.domain.family.entity.FamilyBoardMember;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.enums.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyBoardMemberRepository extends JpaRepository<FamilyBoardMember, Long> {

    /**
     * 보드와 사용자로 멤버십 조회
     */
    Optional<FamilyBoardMember> findByBoardIdAndMemberId(UUID boardId, UUID memberId);

    /**
     * 보드의 모든 멤버 조회
     */
    List<FamilyBoardMember> findAllByBoardIdOrderByInvitedAt(UUID boardId);

    /**
     * 보드의 활성 멤버 조회
     */
    @Query("SELECT m FROM FamilyBoardMember m " +
           "WHERE m.board.id = :boardId AND m.memberStatus = 'ACTIVE' " +
           "ORDER BY m.boardRole, m.invitedAt")
    List<FamilyBoardMember> findActiveMembersByBoardId(@Param("boardId") UUID boardId);

    /**
     * 사용자의 모든 멤버십 조회
     */
    List<FamilyBoardMember> findAllByMemberIdAndMemberStatus(UUID memberId, MemberStatus status);

    /**
     * 멤버십 존재 여부
     */
    boolean existsByBoardIdAndMemberIdAndMemberStatus(UUID boardId, UUID memberId, MemberStatus status);
}


































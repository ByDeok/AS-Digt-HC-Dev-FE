package vibe.digthc.as_digt_hc_dev_fe.domain.family.repository;

import vibe.digthc.as_digt_hc_dev_fe.domain.family.entity.BoardInvitation;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoardInvitationRepository extends JpaRepository<BoardInvitation, UUID> {

    /**
     * 초대 코드로 조회
     */
    Optional<BoardInvitation> findByInviteCode(String inviteCode);

    /**
     * 보드의 대기 중인 초대 목록
     */
    List<BoardInvitation> findByBoardIdAndStatus(UUID boardId, InvitationStatus status);

    /**
     * 만료된 초대 일괄 처리
     */
    @Modifying
    @Query("UPDATE BoardInvitation bi SET bi.status = 'EXPIRED' " +
           "WHERE bi.status = 'PENDING' AND bi.expiresAt < :now")
    int expireOldInvitations(@Param("now") LocalDateTime now);
}

















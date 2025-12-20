package vibe.digthc.as_digt_hc_dev_fe.domain.family.entity;

import vibe.digthc.as_digt_hc_dev_fe.domain.common.BaseTimeEntity;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.enums.BoardRole;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.enums.MemberStatus;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 가족 보드 멤버 Entity
 * - 보드와 사용자 간의 N:M 관계 테이블
 * - 역할 및 상태 관리
 */
@Entity
@Table(name = "family_board_members",
    indexes = {
        @Index(name = "idx_members_board", columnList = "board_id"),
        @Index(name = "idx_members_member", columnList = "member_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_members_board_member", 
                          columnNames = {"board_id", "member_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FamilyBoardMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private FamilyBoard board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @Enumerated(EnumType.STRING)
    @Column(name = "board_role", nullable = false, length = 20)
    private BoardRole boardRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_status", nullable = false, length = 20)
    private MemberStatus memberStatus;

    @Column(name = "invited_at", nullable = false)
    private LocalDateTime invitedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "removed_at")
    private LocalDateTime removedAt;

    // ========================================
    // Builder
    // ========================================
    @Builder
    private FamilyBoardMember(FamilyBoard board, User member, BoardRole boardRole,
                              MemberStatus memberStatus) {
        this.board = board;
        this.member = member;
        this.boardRole = boardRole;
        this.memberStatus = memberStatus;
        this.invitedAt = LocalDateTime.now();
    }

    // ========================================
    // Factory Methods
    // ========================================

    /**
     * 시니어를 ADMIN으로 등록 (보드 생성 시)
     */
    public static FamilyBoardMember createAdmin(FamilyBoard board, User senior) {
        FamilyBoardMember member = FamilyBoardMember.builder()
                .board(board)
                .member(senior)
                .boardRole(BoardRole.ADMIN)
                .memberStatus(MemberStatus.ACTIVE)
                .build();
        member.acceptedAt = member.invitedAt;
        return member;
    }

    /**
     * 초대 수락 시 멤버 생성
     */
    public static FamilyBoardMember createMember(FamilyBoard board, User user, 
                                                  BoardRole role) {
        FamilyBoardMember member = FamilyBoardMember.builder()
                .board(board)
                .member(user)
                .boardRole(role)
                .memberStatus(MemberStatus.ACCEPTED)
                .build();
        member.acceptedAt = LocalDateTime.now();
        member.memberStatus = MemberStatus.ACTIVE;
        return member;
    }

    // ========================================
    // Business Methods
    // ========================================

    /**
     * 초대 수락
     */
    public void accept() {
        if (memberStatus != MemberStatus.INVITED) {
            throw new IllegalStateException("수락할 수 없는 상태입니다: " + memberStatus);
        }
        this.memberStatus = MemberStatus.ACTIVE;
        this.acceptedAt = LocalDateTime.now();
    }

    /**
     * 멤버 제거
     */
    public void remove() {
        this.memberStatus = MemberStatus.REMOVED;
        this.removedAt = LocalDateTime.now();
    }

    /**
     * 역할 변경
     */
    public void changeRole(BoardRole newRole) {
        if (!isActive()) {
            throw new IllegalStateException("활성 멤버만 역할을 변경할 수 있습니다.");
        }
        this.boardRole = newRole;
    }

    /**
     * 활성 멤버 여부
     */
    public boolean isActive() {
        return memberStatus == MemberStatus.ACTIVE;
    }

    /**
     * ADMIN 여부
     */
    public boolean isAdmin() {
        return boardRole == BoardRole.ADMIN && isActive();
    }

    /**
     * 편집 권한 여부
     */
    public boolean canEdit() {
        return (boardRole == BoardRole.ADMIN || boardRole == BoardRole.EDITOR) && isActive();
    }
}


































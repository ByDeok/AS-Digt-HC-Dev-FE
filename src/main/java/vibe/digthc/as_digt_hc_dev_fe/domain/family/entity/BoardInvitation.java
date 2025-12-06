package vibe.digthc.as_digt_hc_dev_fe.domain.family.entity;

import vibe.digthc.as_digt_hc_dev_fe.domain.common.BaseTimeEntity;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.enums.BoardRole;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.enums.InvitationStatus;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 보드 초대 Entity
 * - 초대 코드 및 상태 관리
 */
@Entity
@Table(name = "board_invitations",
    indexes = {
        @Index(name = "idx_invitations_board", columnList = "board_id"),
        @Index(name = "idx_invitations_status_expires", columnList = "status, expires_at")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_invitations_code", columnNames = "invite_code")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardInvitation extends BaseTimeEntity {

    private static final int INVITATION_VALIDITY_DAYS = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private FamilyBoard board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    private User inviter;

    @Column(name = "invite_code", nullable = false, unique = true, length = 100)
    private String inviteCode;

    @Column(name = "invitee_email", length = 100)
    private String inviteeEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "intended_role", nullable = false, length = 20)
    private BoardRole intendedRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvitationStatus status;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    // ========================================
    // Builder
    // ========================================
    @Builder
    private BoardInvitation(FamilyBoard board, User inviter, String inviteCode,
                            String inviteeEmail, BoardRole intendedRole) {
        this.board = board;
        this.inviter = inviter;
        this.inviteCode = inviteCode;
        this.inviteeEmail = inviteeEmail;
        this.intendedRole = intendedRole;
        this.status = InvitationStatus.PENDING;
        this.expiresAt = LocalDateTime.now().plusDays(INVITATION_VALIDITY_DAYS);
    }

    // ========================================
    // Factory Method
    // ========================================
    public static BoardInvitation create(FamilyBoard board, User inviter,
                                          String inviteCode, String email,
                                          BoardRole role) {
        return BoardInvitation.builder()
                .board(board)
                .inviter(inviter)
                .inviteCode(inviteCode)
                .inviteeEmail(email)
                .intendedRole(role)
                .build();
    }

    // ========================================
    // Business Methods
    // ========================================

    /**
     * 초대 수락
     */
    public void accept() {
        if (!isValid()) {
            throw new IllegalStateException("유효하지 않은 초대입니다.");
        }
        this.status = InvitationStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
    }

    /**
     * 초대 거절
     */
    public void decline() {
        if (!isValid()) {
            throw new IllegalStateException("유효하지 않은 초대입니다.");
        }
        this.status = InvitationStatus.DECLINED;
    }

    /**
     * 초대 만료 처리
     */
    public void expire() {
        if (status == InvitationStatus.PENDING) {
            this.status = InvitationStatus.EXPIRED;
        }
    }

    /**
     * 유효한 초대 여부
     */
    public boolean isValid() {
        return status == InvitationStatus.PENDING && !isExpired();
    }

    /**
     * 만료 여부
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}

















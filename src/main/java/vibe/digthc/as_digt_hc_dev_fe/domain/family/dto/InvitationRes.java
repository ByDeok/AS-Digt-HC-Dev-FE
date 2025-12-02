package vibe.digthc.as_digt_hc_dev_fe.domain.family.dto;

import vibe.digthc.as_digt_hc_dev_fe.domain.family.entity.BoardInvitation;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.enums.BoardRole;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.enums.InvitationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record InvitationRes(
    UUID invitationId,
    String inviteCode,
    String inviteeEmail,
    BoardRole intendedRole,
    InvitationStatus status,
    LocalDateTime expiresAt
) {
    public static InvitationRes from(BoardInvitation invitation) {
        return new InvitationRes(
            invitation.getId(),
            invitation.getInviteCode(),
            invitation.getInviteeEmail(),
            invitation.getIntendedRole(),
            invitation.getStatus(),
            invitation.getExpiresAt()
        );
    }
}





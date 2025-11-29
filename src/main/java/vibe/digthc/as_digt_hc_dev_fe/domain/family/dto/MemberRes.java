package vibe.digthc.as_digt_hc_dev_fe.domain.family.dto;

import vibe.digthc.as_digt_hc_dev_fe.domain.family.entity.FamilyBoardMember;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.enums.BoardRole;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.enums.MemberStatus;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.dto.UserResponse;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;

import java.time.LocalDateTime;

public record MemberRes(
    Long membershipId,
    UserResponse user,
    BoardRole role,
    MemberStatus status,
    LocalDateTime joinedAt
) {
    public static MemberRes from(FamilyBoardMember member) {
        User u = member.getMember();
        String userName = u.getUserProfile() != null ? u.getUserProfile().getName() : "Unknown";
        UserResponse userRes = new UserResponse(
            u.getId(), u.getEmail(), userName, u.getRole(), u.getCreatedAt()
        );
        
        return new MemberRes(
            member.getId(),
            userRes,
            member.getBoardRole(),
            member.getMemberStatus(),
            member.getInvitedAt() // or acceptedAt? Usually joined means accepted, but let's show invited time for now or handled in logic
        );
    }
}


package vibe.digthc.as_digt_hc_dev_fe.domain.family.dto;

import vibe.digthc.as_digt_hc_dev_fe.domain.family.entity.FamilyBoard;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.dto.UserResponse;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record FamilyBoardRes(
    UUID boardId,
    String name,
    String description,
    UserResponse senior,
    int memberCount,
    Map<String, Object> settings,
    LocalDateTime lastActivityAt
) {
    public static FamilyBoardRes from(FamilyBoard board) {
        User s = board.getSenior();
        String seniorName = s.getUserProfile() != null ? s.getUserProfile().getName() : "Unknown";
        UserResponse seniorRes = new UserResponse(
            s.getId(), s.getEmail(), seniorName, s.getRole(), s.getCreatedAt()
        );
        
        return new FamilyBoardRes(
            board.getId(),
            board.getName(),
            board.getDescription(),
            seniorRes,
            board.getActiveMemberCount(),
            board.getSettings(),
            board.getLastActivityAt()
        );
    }
}






































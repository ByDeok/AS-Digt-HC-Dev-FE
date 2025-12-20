package vibe.digthc.as_digt_hc_dev_fe.domain.user.dto;

import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.Role;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
    UUID userId,
    String email,
    String name,
    Role role,
    LocalDateTime createdAt
) {}


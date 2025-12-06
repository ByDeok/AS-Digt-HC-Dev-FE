package vibe.digthc.as_digt_hc_dev_fe.domain.family.dto;

import vibe.digthc.as_digt_hc_dev_fe.domain.family.enums.BoardRole;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateReq(
    @NotNull(message = "변경할 역할은 필수입니다.")
    BoardRole newRole
) {}







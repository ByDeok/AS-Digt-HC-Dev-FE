package vibe.digthc.as_digt_hc_dev_fe.domain.family.dto;

import vibe.digthc.as_digt_hc_dev_fe.domain.family.enums.BoardRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record InviteReq(
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    String inviteeEmail,
    
    @NotNull(message = "초대할 역할은 필수입니다.")
    BoardRole intendedRole
) {}





package vibe.digthc.as_digt_hc_dev_fe.domain.family.dto;

import jakarta.validation.constraints.NotBlank;

public record AcceptReq(
    @NotBlank(message = "초대 코드는 필수입니다.")
    String inviteCode
) {}






































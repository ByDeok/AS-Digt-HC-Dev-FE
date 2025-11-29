package vibe.digthc.as_digt_hc_dev_fe.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @Email @NotBlank String email,
    @NotBlank String password
) {}


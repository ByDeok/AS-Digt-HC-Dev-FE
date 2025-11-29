package vibe.digthc.as_digt_hc_dev_fe.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.Role;

public record SignupRequest(
    @Email @NotBlank String email,
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$", message = "Password must be 8-20 characters with letter, number, special char")
    String password,
    @NotBlank @Size(min = 2, max = 50) String name,
    Role role,
    @NotNull AgreementsRequest agreements
) {}


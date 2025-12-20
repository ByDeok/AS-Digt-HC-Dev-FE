package vibe.digthc.as_digt_hc_dev_fe.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.Gender;

import java.time.LocalDate;

/**
 * 사용자 프로필 수정 요청 DTO
 */
public record ProfileUpdateRequest(
    @Size(max = 50, message = "이름은 50자 이하여야 합니다.")
    String name,
    
    @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다.")
    String phoneNumber,
    
    LocalDate birthDate,
    
    Gender gender,
    
    @Size(max = 500, message = "자기소개는 500자 이하여야 합니다.")
    String bio,
    
    String primaryConditions,
    
    String accessibilityPrefs
) {}

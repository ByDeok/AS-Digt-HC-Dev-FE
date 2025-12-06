package vibe.digthc.as_digt_hc_dev_fe.domain.user.dto;

import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.Gender;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.Role;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.UserProfile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 사용자 프로필 응답 DTO
 */
public record ProfileResponse(
    UUID userId,
    String email,
    String name,
    String phoneNumber,
    String profileImageUrl,
    String bio,
    LocalDate birthDate,
    Integer age,
    Gender gender,
    Role role,
    String primaryConditions,
    String accessibilityPrefs,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ProfileResponse from(User user, UserProfile profile) {
        return new ProfileResponse(
            user.getId(),
            user.getEmail(),
            profile != null ? profile.getName() : null,
            profile != null ? profile.getPhoneNumber() : null,
            profile != null ? profile.getProfileImageUrl() : null,
            profile != null ? profile.getBio() : null,
            profile != null ? profile.getBirthDate() : null,
            profile != null ? profile.getAge() : null,
            profile != null ? profile.getGender() : null,
            user.getRole(),
            profile != null ? profile.getPrimaryConditions() : null,
            profile != null ? profile.getAccessibilityPrefs() : null,
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}

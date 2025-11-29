package vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.dto;

import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.enums.OnboardingStep;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.Gender;
import java.time.LocalDate;

public record OnboardingStepRequest(
    OnboardingStep currentStep,
    OnboardingStep nextStep,
    
    // PROFILE_BASIC
    String name,
    String phoneNumber,
    LocalDate birthDate,
    Gender gender,
    
    // PROFILE_DETAILS
    String primaryConditions, // JSON
    String accessibilityPrefs, // JSON
    
    // HOSPITAL_SELECTION
    String hospitalId, // Placeholder for hospital selection
    String regionCode // For region check
) {}


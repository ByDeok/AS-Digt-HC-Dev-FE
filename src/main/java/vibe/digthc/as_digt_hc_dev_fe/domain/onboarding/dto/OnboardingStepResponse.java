package vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.dto;

import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.enums.OnboardingStep;

public record OnboardingStepResponse(
    OnboardingStep currentStep,
    Double progressPercent,
    Integer estimatedMinutesLeft
) {}


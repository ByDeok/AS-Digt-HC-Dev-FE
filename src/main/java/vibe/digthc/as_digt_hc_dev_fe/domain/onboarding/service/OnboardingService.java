package vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.dto.OnboardingStepRequest;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.dto.OnboardingStepResponse;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.entity.OnboardingSession;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.enums.OnboardingStatus;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.enums.OnboardingStep;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.exception.UnsupportedRegionException;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.repository.OnboardingSessionRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.Status;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.UserProfile;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserProfileRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OnboardingService {

    private final OnboardingSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public OnboardingStepResponse startSession(UUID userId) {
        User user = getUser(userId);
        OnboardingSession session = sessionRepository.findByUser(user)
                .orElseGet(() -> {
                    OnboardingSession newSession = OnboardingSession.builder()
                            .user(user)
                            .status(OnboardingStatus.IN_PROGRESS)
                            .currentStep(OnboardingStep.TERMS_AGREEMENT)
                            .build();
                    return sessionRepository.save(newSession);
                });

        return createResponse(session);
    }

    public OnboardingStepResponse updateStep(UUID userId, OnboardingStepRequest request) {
        User user = getUser(userId);
        OnboardingSession session = sessionRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Onboarding session not found"));

        // Restore last step logic check (if needed) or validate sequence
        // For now, simply process the data for the requested step
        
        if (request.nextStep() != null) {
            // Process data based on the step we are transitioning FROM or TO?
            // Usually, we save data for the 'currentStep' then move to 'nextStep'.
            // But the request has 'nextStep' as target.
            
            updateUserProfile(user, request);
            
            // Check Region Exception
            if (request.hospitalId() != null || request.regionCode() != null) {
                validateRegion(request.regionCode());
            }

            session.updateStep(request.nextStep());
        }

        return createResponse(session);
    }

    public void completeSession(UUID userId) {
        User user = getUser(userId);
        OnboardingSession session = sessionRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Onboarding session not found"));

        session.complete();
        user.updateStatus(Status.ACTIVE);
        
        // Explicitly save if not managed (Transactional handles it usually, but explicit save is safe)
        userRepository.save(user); 
    }
    
    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private void updateUserProfile(User user, OnboardingStepRequest request) {
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile newProfile = UserProfile.builder()
                            .user(user)
                            .name(request.name() != null ? request.name() : "User") // Default or placeholder
                            .build();
                    return userProfileRepository.save(newProfile);
                });

        if (request.name() != null || request.phoneNumber() != null || request.birthDate() != null || request.gender() != null) {
            profile.updateBasicInfo(
                request.name() != null ? request.name() : profile.getName(),
                request.phoneNumber() != null ? request.phoneNumber() : profile.getPhoneNumber(),
                request.birthDate() != null ? request.birthDate() : profile.getBirthDate(),
                request.gender() != null ? request.gender() : profile.getGender()
            );
        }

        if (request.primaryConditions() != null || request.accessibilityPrefs() != null) {
            profile.updateDetails(request.primaryConditions(), request.accessibilityPrefs());
        }
    }
    
    private void validateRegion(String regionCode) {
        // Simulate logic for REQ-FUNC-019
        if ("UNSUPPORTED".equalsIgnoreCase(regionCode)) {
            throw new UnsupportedRegionException("Service is not available in this region.");
        }
    }

    private OnboardingStepResponse createResponse(OnboardingSession session) {
        OnboardingStep step = session.getCurrentStep();
        double progress = 0.0;
        int minutesLeft = 3;

        switch (step) {
            case TERMS_AGREEMENT -> { progress = 0.0; minutesLeft = 3; }
            case PROFILE_BASIC -> { progress = 25.0; minutesLeft = 2; }
            case PROFILE_DETAILS -> { progress = 50.0; minutesLeft = 1; }
            case HOSPITAL_SELECTION -> { progress = 75.0; minutesLeft = 1; }
            case COMPLETED -> { progress = 100.0; minutesLeft = 0; }
        }

        return new OnboardingStepResponse(step, progress, minutesLeft);
    }
}


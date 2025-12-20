package vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
@Slf4j
public class OnboardingService {

    private final OnboardingSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public OnboardingStepResponse startSession(UUID userId) {
        User user = getUser(userId);
        OnboardingSession session = getOrCreateSession(user);

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
            // REQ-FUNC-005: 중간 저장 기능 - 세션 상태 변경 후 저장
            sessionRepository.save(session);
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

    /**
     * 온보딩 세션 조회
     */
    public OnboardingStepResponse getSession(UUID userId) {
        User user = getUser(userId);
        OnboardingSession session = getOrCreateSession(user);
        
        return createResponse(session);
    }

    /**
     * 온보딩 세션을 "멱등 + 동시성 안전"하게 생성/조회한다.
     *
     * 배경:
     * - FE 개발 모드(React StrictMode)에서는 동일 컴포넌트가 mount/unmount 되며
     *   useEffect가 2회 호출될 수 있어 /onboarding/start 가 거의 동시에 2번 들어오는 일이 흔하다.
     * - onboarding_sessions.user_id 는 unique 제약이므로, 동시 insert가 발생하면 23505가 터진다.
     * - 따라서 insert 경합이 발생하면 재조회로 회복한다.
     */
    private OnboardingSession getOrCreateSession(User user) {
        return sessionRepository.findByUser(user).orElseGet(() -> {
            OnboardingSession newSession = OnboardingSession.builder()
                    .user(user)
                    .status(OnboardingStatus.IN_PROGRESS)
                    .currentStep(OnboardingStep.TERMS_AGREEMENT)
                    .build();
            try {
                // save()는 flush/commit 시점까지 DB 제약 위반이 지연될 수 있어 레이스를 잡기 어렵다.
                // 동시 start 호출에서 unique(user_id) 경합이 나면 여기서 바로 예외를 발생시켜 회복한다.
                return sessionRepository.saveAndFlush(newSession);
            } catch (DataIntegrityViolationException e) {
                log.warn("Onboarding session already exists (race). Returning existing session. userId={}", user.getId());
                return sessionRepository.findByUser(user).orElseThrow(() -> e);
            }
        });
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


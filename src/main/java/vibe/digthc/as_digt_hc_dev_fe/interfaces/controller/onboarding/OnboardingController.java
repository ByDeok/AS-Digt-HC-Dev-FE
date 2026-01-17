package vibe.digthc.as_digt_hc_dev_fe.interfaces.controller.onboarding;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.dto.OnboardingStepRequest;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.dto.OnboardingStepResponse;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.service.OnboardingService;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.security.CurrentUserId;
import vibe.digthc.as_digt_hc_dev_fe.interfaces.common.ApiResponse;

import java.util.UUID;

@RestController
@RequestMapping("/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;

    @PostMapping("/start")
    public ApiResponse<OnboardingStepResponse> startSession(@CurrentUserId UUID userId) {
        return ApiResponse.success(onboardingService.startSession(userId));
    }

    @PostMapping("/step")
    public ApiResponse<OnboardingStepResponse> updateStep(
            @CurrentUserId UUID userId,
            @RequestBody OnboardingStepRequest request) {
        return ApiResponse.success(onboardingService.updateStep(userId, request));
    }

    @PostMapping("/complete")
    public ApiResponse<Void> completeSession(@CurrentUserId UUID userId) {
        onboardingService.completeSession(userId);
        return ApiResponse.success("Onboarding completed successfully");
    }

    /**
     * 온보딩 세션 조회
     */
    @GetMapping
    public ApiResponse<OnboardingStepResponse> getSession(@CurrentUserId UUID userId) {
        return ApiResponse.success(onboardingService.getSession(userId));
    }
}


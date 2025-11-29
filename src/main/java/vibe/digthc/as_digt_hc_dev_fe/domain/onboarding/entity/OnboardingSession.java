package vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.entity;

import jakarta.persistence.*;
import lombok.*;
import vibe.digthc.as_digt_hc_dev_fe.domain.common.BaseTimeEntity;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.enums.OnboardingStatus;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.enums.OnboardingStep;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;

@Entity
@Table(name = "onboarding_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OnboardingSession extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OnboardingStep currentStep;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OnboardingStatus status;

    @Builder
    public OnboardingSession(User user, OnboardingStep currentStep, OnboardingStatus status) {
        this.user = user;
        this.currentStep = currentStep != null ? currentStep : OnboardingStep.TERMS_AGREEMENT;
        this.status = status != null ? status : OnboardingStatus.IN_PROGRESS;
    }

    public void updateStep(OnboardingStep step) {
        this.currentStep = step;
    }

    public void complete() {
        this.status = OnboardingStatus.COMPLETED;
        this.currentStep = OnboardingStep.COMPLETED;
    }
    
    public void abandon() {
        this.status = OnboardingStatus.ABANDONED;
    }
}


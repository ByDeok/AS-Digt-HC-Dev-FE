package vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.entity.OnboardingSession;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;

import java.util.Optional;

@Repository
public interface OnboardingSessionRepository extends JpaRepository<OnboardingSession, Long> {
    Optional<OnboardingSession> findByUser(User user);
}


package vibe.digthc.as_digt_hc_dev_fe.infrastructure.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.AuthProvider;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.Role;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.Status;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.UserAgreement;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.UserProfile;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserAgreementRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserProfileRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserRepository;

/**
 * 로컬/테스트 환경에서만 사용하는 고정 관리자 계정 시더(seed).
 *
 * 목적:
 * - FE 개발/QA에서 별도 회원가입 없이 즉시 로그인 가능하도록 테스트 계정 제공
 *
 * 생성되는 계정:
 * - id(email): admin
 * - password: 123
 * - role: ADMIN
 *
 * ⚠️ 주의:
 * - 운영/개발서버(dev) 프로필에는 적용되지 않도록 @Profile로 제한한다.
 * - 테스트 용도이므로 비밀번호 정책(복잡도)을 우회한다.
 */
@Slf4j
@Component
@Profile({"local", "test"})
@RequiredArgsConstructor
public class DevAdminAccountSeeder implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "admin";
    private static final String ADMIN_EMAIL_ALIAS = "admin@local.test";
    private static final String ADMIN_PASSWORD = "123";
    private static final String ADMIN_NAME = "관리자";

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserAgreementRepository userAgreementRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // 로그인 검증이 "이메일만" 허용하는 환경에서도 테스트가 가능하도록
        // - admin (요구사항 그대로)
        // - admin@local.test (이메일 형식)
        // 두 계정을 모두 best-effort로 생성한다.
        seedOneIfMissing(ADMIN_EMAIL);
        seedOneIfMissing(ADMIN_EMAIL_ALIAS);
    }

    private void seedOneIfMissing(String email) {
        if (userRepository.existsByEmail(email)) {
            log.info("Dev admin account already exists: {}", email);
            return;
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(ADMIN_PASSWORD))
                .role(Role.ADMIN)
                .authProvider(AuthProvider.EMAIL)
                .status(Status.ACTIVE)
                .build();
        userRepository.save(user);

        // 토큰 응답에서 사용자 이름을 보여주기 위해 프로필도 함께 생성한다.
        UserProfile profile = UserProfile.builder()
                .user(user)
                .name(ADMIN_NAME)
                .build();
        userProfileRepository.save(profile);

        // 약관은 필수 컬럼이므로 기본 동의값으로 생성한다(테스트 편의).
        UserAgreement agreement = UserAgreement.builder()
                .user(user)
                .termsService(true)
                .privacyPolicy(true)
                .marketingConsent(false)
                .build();
        userAgreementRepository.save(agreement);

        log.info("Dev admin account seeded: {} / {}", email, ADMIN_PASSWORD);
    }
}



package vibe.digthc.as_digt_hc_dev_fe.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.dto.*;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.UserProfile;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.*;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserAgreementRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserProfileRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserRepository;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.security.CustomUserDetailsService;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.security.JwtTokenProvider;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.security.UserPrincipal;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserAgreementRepository userAgreementRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;
    private final CustomUserDetailsService customUserDetailsService;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;

    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenValidityInSeconds;

    @Value("${redis.refresh-token-prefix:RT:}")
    private String refreshTokenPrefix;

    @Transactional
    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role() != null ? request.role() : Role.SENIOR)
                .authProvider(AuthProvider.EMAIL)
                .status(Status.ACTIVE)
                .build();

        userRepository.save(user);

        UserProfile profile = UserProfile.builder()
                .user(user)
                .name(request.name())
                .build();
        userProfileRepository.save(profile);

        UserAgreement agreement = UserAgreement.builder()
                .user(user)
                .termsService(request.agreements().termsService())
                .privacyPolicy(request.agreements().privacyPolicy())
                .marketingConsent(request.agreements().marketingConsent())
                .build();
        userAgreementRepository.save(agreement);

        return new UserResponse(user.getId(), user.getEmail(), profile.getName(), user.getRole(), user.getCreatedAt());
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        return generateTokenResponse(authentication);
    }

    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        String storedToken = redisTemplate.opsForValue().get(refreshTokenPrefix + userId);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh token not found or mismatched");
        }

        UserDetails userDetails = customUserDetailsService.loadUserById(userId);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        return generateTokenResponse(authentication);
    }

    private TokenResponse generateTokenResponse(Authentication authentication) {
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        redisTemplate.opsForValue().set(
                refreshTokenPrefix + userPrincipal.getId(),
                refreshToken,
                refreshTokenValidityInSeconds,
                TimeUnit.SECONDS
        );

        UserResponse userResponse = new UserResponse(
                userPrincipal.getId(),
                userPrincipal.getEmail(),
                userPrincipal.getName(),
                Role.valueOf(userPrincipal.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "")),
                null
        );

        return new TokenResponse(accessToken, refreshToken, "Bearer", accessTokenValidityInSeconds, userResponse);
    }

    /**
     * 사용자 프로필 조회
     */
    public ProfileResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElse(null);
        
        return ProfileResponse.from(user, profile);
    }

    /**
     * 사용자 프로필 수정
     */
    @Transactional
    public ProfileResponse updateProfile(UUID userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile newProfile = UserProfile.builder()
                            .user(user)
                            .name(request.name() != null ? request.name() : "User")
                            .build();
                    return userProfileRepository.save(newProfile);
                });
        
        // 프로필 정보 업데이트
        if (request.name() != null || request.phoneNumber() != null || request.bio() != null) {
            profile.updateProfile(
                request.name() != null ? request.name() : profile.getName(),
                request.phoneNumber() != null ? request.phoneNumber() : profile.getPhoneNumber(),
                request.bio() != null ? request.bio() : profile.getBio()
            );
        }
        
        if (request.birthDate() != null || request.gender() != null) {
            profile.updateBasicInfo(
                request.name() != null ? request.name() : profile.getName(),
                request.phoneNumber() != null ? request.phoneNumber() : profile.getPhoneNumber(),
                request.birthDate() != null ? request.birthDate() : profile.getBirthDate(),
                request.gender() != null ? request.gender() : profile.getGender()
            );
        }
        
        if (request.primaryConditions() != null || request.accessibilityPrefs() != null) {
            profile.updateDetails(
                request.primaryConditions() != null ? request.primaryConditions() : profile.getPrimaryConditions(),
                request.accessibilityPrefs() != null ? request.accessibilityPrefs() : profile.getAccessibilityPrefs()
            );
        }
        
        userProfileRepository.save(profile);
        
        return ProfileResponse.from(user, profile);
    }
}


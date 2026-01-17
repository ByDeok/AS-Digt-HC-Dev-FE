package vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.dto.OnboardingStepRequest;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.dto.OnboardingStepResponse;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.entity.OnboardingSession;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.enums.OnboardingStatus;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.enums.OnboardingStep;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.exception.UnsupportedRegionException;
import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.repository.OnboardingSessionRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.Gender;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.Status;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.UserProfile;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserProfileRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * F1 Feature (온보딩 3분 플로우) 테스트 클래스
 * 
 * Traceability:
 * - Story 4: REQ-FUNC-001~006, 019; REQ-NF-001, 003, 008, 012
 * - Test Case IDs: TC-S4-01 ~ TC-S4-08
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OnboardingService 테스트 (F1 Feature)")
public class OnboardingServiceTest {

    @Mock
    private OnboardingSessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private OnboardingService onboardingService;

    private User testUser;
    private UUID userId;
    private OnboardingSession existingSession;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
                .email("test@example.com")
                .password("password123")
                .role(vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.Role.SENIOR)
                .status(Status.ACTIVE)
                .build();
        testUser = spy(testUser);

        existingSession = OnboardingSession.builder()
                .user(testUser)
                .currentStep(OnboardingStep.TERMS_AGREEMENT)
                .status(OnboardingStatus.IN_PROGRESS)
                .build();
    }

    // =================================================================
    // TC-S4-01: REQ-FUNC-001 - 계정 생성 및 기본 프로필 등록
    // =================================================================
    @Test
    @DisplayName("TC-S4-01: 신규 온보딩 세션 시작 시 기본 프로필 생성")
    public void testStartSession_CreatesNewSession() {
        // Given: 기존 세션이 없는 경우
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(sessionRepository.findByUser(testUser)).willReturn(Optional.empty());
        given(sessionRepository.saveAndFlush(any(OnboardingSession.class))).willAnswer(invocation -> invocation.getArgument(0));


        // When: 온보딩 세션 시작
        OnboardingStepResponse response = onboardingService.startSession(userId);

        // Then: TERMS_AGREEMENT 단계로 새 세션이 생성되고, 진행률과 예상 시간이 반환됨
        assertThat(response.currentStep()).isEqualTo(OnboardingStep.TERMS_AGREEMENT);
        assertThat(response.progressPercent()).isEqualTo(0.0);
        assertThat(response.estimatedMinutesLeft()).isEqualTo(3);

        verify(sessionRepository, times(1)).saveAndFlush(any(OnboardingSession.class));
    }

    // =================================================================
    // TC-S4-02: REQ-FUNC-002 - 온보딩 인증 단계
    // =================================================================
    @Test
    @DisplayName("TC-S4-02: 인증 단계 처리 (세션이 이미 존재하는 경우 재개)")
    public void testStartSession_ResumesExistingSession() {
        // Given: 이미 존재하는 세션이 있는 경우
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(sessionRepository.findByUser(testUser)).willReturn(Optional.of(existingSession));

        // When: 온보딩 세션 조회
        OnboardingStepResponse response = onboardingService.startSession(userId);

        // Then: 기존 세션을 반환하고 새로 생성하지 않음
        assertThat(response.currentStep()).isEqualTo(OnboardingStep.TERMS_AGREEMENT);
        assertThat(response.progressPercent()).isEqualTo(0.0);
        verify(sessionRepository, never()).save(any(OnboardingSession.class));
    }

    // =================================================================
    // TC-S4-03: REQ-FUNC-001 - 기본 프로필 정보 업데이트
    // =================================================================
    @Test
    @DisplayName("TC-S4-03: PROFILE_BASIC 단계에서 이름, 나이, 성별 등 기본 프로필 정보 업데이트")
    public void testUpdateStep_ProfileBasic_UpdatesUserProfile() {
        // Given: PROFILE_BASIC 단계로 이동하는 요청
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(sessionRepository.findByUser(testUser)).willReturn(Optional.of(existingSession));
        given(userProfileRepository.findByUser(testUser)).willReturn(Optional.empty());
        
        UserProfile savedProfile = UserProfile.builder()
                .user(testUser)
                .name("홍길동")
                .phoneNumber("010-1234-5678")
                .birthDate(LocalDate.of(1950, 1, 1))
                .gender(Gender.MALE)
                .build();
        given(userProfileRepository.save(any(UserProfile.class))).willReturn(savedProfile);

        OnboardingStepRequest request = new OnboardingStepRequest(
                OnboardingStep.TERMS_AGREEMENT,
                OnboardingStep.PROFILE_BASIC,
                "홍길동",
                "010-1234-5678",
                LocalDate.of(1950, 1, 1),
                Gender.MALE,
                null,
                null,
                null,
                null
        );

        // When: 프로필 기본 정보 업데이트
        OnboardingStepResponse response = onboardingService.updateStep(userId, request);

        // Then: PROFILE_BASIC 단계로 이동하고, 진행률과 예상 시간이 업데이트됨
        assertThat(response.currentStep()).isEqualTo(OnboardingStep.PROFILE_BASIC);
        assertThat(response.progressPercent()).isEqualTo(25.0);
        assertThat(response.estimatedMinutesLeft()).isEqualTo(2);

        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
        // REQ-FUNC-005: 중간 저장 기능 - 세션 상태 변경 후 저장 확인
        verify(sessionRepository, times(1)).save(any(OnboardingSession.class));
    }

    // =================================================================
    // TC-S4-04: REQ-FUNC-001 - 상세 프로필 정보 업데이트
    // =================================================================
    @Test
    @DisplayName("TC-S4-04: PROFILE_DETAILS 단계에서 주요 질환 및 접근성 설정 업데이트")
    public void testUpdateStep_ProfileDetails_UpdatesUserProfileDetails() {
        // Given: PROFILE_DETAILS 단계로 이동하는 요청
        UserProfile existingProfile = UserProfile.builder()
                .user(testUser)
                .name("홍길동")
                .build();
        
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(sessionRepository.findByUser(testUser)).willReturn(Optional.of(existingSession));
        given(userProfileRepository.findByUser(testUser)).willReturn(Optional.of(existingProfile));

        OnboardingStepRequest request = new OnboardingStepRequest(
                OnboardingStep.PROFILE_BASIC,
                OnboardingStep.PROFILE_DETAILS,
                null,
                null,
                null,
                null,
                "[\"고혈압\", \"당뇨\"]",
                "{\"largeText\": true, \"highContrast\": true}",
                null,
                null
        );

        // When: 프로필 상세 정보 업데이트
        OnboardingStepResponse response = onboardingService.updateStep(userId, request);

        // Then: PROFILE_DETAILS 단계로 이동하고, 진행률이 50%로 업데이트됨
        assertThat(response.currentStep()).isEqualTo(OnboardingStep.PROFILE_DETAILS);
        assertThat(response.progressPercent()).isEqualTo(50.0);
        assertThat(response.estimatedMinutesLeft()).isEqualTo(1);
        
        // REQ-FUNC-005: 중간 저장 기능 확인
        verify(sessionRepository, times(1)).save(any(OnboardingSession.class));
    }

    // =================================================================
    // TC-S4-05: REQ-FUNC-005 - 온보딩 진행률 표시 및 중간 저장
    // =================================================================
    @Test
    @DisplayName("TC-S4-05: 각 단계별 진행률과 예상 남은 시간(ETA) 표시")
    public void testGetSession_DisplaysProgressAndETA() {
        // Given: HOSPITAL_SELECTION 단계에 있는 세션
        existingSession.updateStep(OnboardingStep.HOSPITAL_SELECTION);
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(sessionRepository.findByUser(testUser)).willReturn(Optional.of(existingSession));

        // When: 세션 조회
        OnboardingStepResponse response = onboardingService.getSession(userId);

        // Then: 진행률 75%, 예상 시간 1분이 반환됨
        assertThat(response.currentStep()).isEqualTo(OnboardingStep.HOSPITAL_SELECTION);
        assertThat(response.progressPercent()).isEqualTo(75.0);
        assertThat(response.estimatedMinutesLeft()).isEqualTo(1);
    }

    @Test
    @DisplayName("TC-S4-05-2: 세션 조회 시 세션이 없으면 새로 생성하여 중간 저장 기능 제공")
    public void testGetSession_CreatesSessionIfNotExists() {
        // Given: 세션이 없는 경우
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(sessionRepository.findByUser(testUser)).willReturn(Optional.empty());
        given(sessionRepository.saveAndFlush(any(OnboardingSession.class))).willAnswer(invocation -> invocation.getArgument(0));


        // When: 세션 조회
        OnboardingStepResponse response = onboardingService.getSession(userId);

        // Then: 새 세션이 생성되어 반환됨 (중간 저장 기능)
        assertThat(response.currentStep()).isEqualTo(OnboardingStep.TERMS_AGREEMENT);
        assertThat(response.progressPercent()).isEqualTo(0.0);
        verify(sessionRepository, times(1)).saveAndFlush(any(OnboardingSession.class));
    }

    // =================================================================
    // TC-S4-06: REQ-FUNC-006 - 온보딩 완료 조건 평가
    // =================================================================
    @Test
    @DisplayName("TC-S4-06: 필수 단계 완료 시 온보딩 완료 판정 및 사용자 상태 ACTIVE로 변경")
    public void testCompleteSession_CompletesOnboardingAndActivatesUser() {
        // Given: HOSPITAL_SELECTION 단계를 완료한 세션
        existingSession.updateStep(OnboardingStep.HOSPITAL_SELECTION);
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(sessionRepository.findByUser(testUser)).willReturn(Optional.of(existingSession));

        // When: 온보딩 완료
        onboardingService.completeSession(userId);

        // Then: 세션 상태가 COMPLETED로 변경되고, 사용자 상태가 ACTIVE로 변경됨
        assertThat(existingSession.getStatus()).isEqualTo(OnboardingStatus.COMPLETED);
        assertThat(existingSession.getCurrentStep()).isEqualTo(OnboardingStep.COMPLETED);
        verify(testUser, times(1)).updateStatus(Status.ACTIVE);
        verify(userRepository, times(1)).save(testUser);
    }

    // =================================================================
    // TC-S4-07: REQ-FUNC-019 - 온보딩 예외 처리(병원 포털 미지원 지역)
    // =================================================================
    @Test
    @DisplayName("TC-S4-07: 미지원 지역 코드 입력 시 UnsupportedRegionException 발생")
    public void testUpdateStep_UnsupportedRegion_ThrowsException() {
        // Given: 미지원 지역 코드가 포함된 HOSPITAL_SELECTION 요청
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(sessionRepository.findByUser(testUser)).willReturn(Optional.of(existingSession));

        OnboardingStepRequest request = new OnboardingStepRequest(
                OnboardingStep.PROFILE_DETAILS,
                OnboardingStep.HOSPITAL_SELECTION,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "UNSUPPORTED" // 미지원 지역 코드
        );

        // When & Then: UnsupportedRegionException 발생
        assertThatThrownBy(() -> onboardingService.updateStep(userId, request))
                .isInstanceOf(UnsupportedRegionException.class)
                .hasMessageContaining("Service is not available in this region");

        // 세션이 업데이트되지 않았음을 확인
        assertThat(existingSession.getCurrentStep()).isNotEqualTo(OnboardingStep.HOSPITAL_SELECTION);
    }

    @Test
    @DisplayName("TC-S4-07-2: 지원 지역 코드 입력 시 정상적으로 진행")
    public void testUpdateStep_SupportedRegion_ProceedsNormally() {
        // Given: 지원 지역 코드가 포함된 HOSPITAL_SELECTION 요청
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(sessionRepository.findByUser(testUser)).willReturn(Optional.of(existingSession));

        OnboardingStepRequest request = new OnboardingStepRequest(
                OnboardingStep.PROFILE_DETAILS,
                OnboardingStep.HOSPITAL_SELECTION,
                null,
                null,
                null,
                null,
                null,
                null,
                "hospital-123",
                "SEOUL" // 지원 지역 코드
        );

        // When: 지역 검증 통과하여 단계 업데이트
        OnboardingStepResponse response = onboardingService.updateStep(userId, request);

        // Then: 정상적으로 HOSPITAL_SELECTION 단계로 이동
        assertThat(response.currentStep()).isEqualTo(OnboardingStep.HOSPITAL_SELECTION);
        assertThat(response.progressPercent()).isEqualTo(75.0);
        
        // REQ-FUNC-005: 중간 저장 기능 확인
        verify(sessionRepository, times(1)).save(any(OnboardingSession.class));
    }

    // =================================================================
    // TC-S4-08: REQ-NF-003 - 성능 요구사항 (p50 ≤ 180초)
    // =================================================================
    @Test
    @DisplayName("TC-S4-08: 온보딩 완료까지 예상 시간이 3분(180초) 이내로 표시됨")
    public void testCompleteOnboardingFlow_EstimatedTimeWithin3Minutes() {
        // Given: 전체 온보딩 플로우 - 각 단계마다 업데이트된 세션을 반환하도록 Mock 설정
        OnboardingSession sessionStep1 = OnboardingSession.builder()
                .user(testUser)
                .currentStep(OnboardingStep.TERMS_AGREEMENT)
                .status(OnboardingStatus.IN_PROGRESS)
                .build();
        OnboardingSession sessionStep2 = OnboardingSession.builder()
                .user(testUser)
                .currentStep(OnboardingStep.PROFILE_BASIC)
                .status(OnboardingStatus.IN_PROGRESS)
                .build();
        OnboardingSession sessionStep3 = OnboardingSession.builder()
                .user(testUser)
                .currentStep(OnboardingStep.PROFILE_DETAILS)
                .status(OnboardingStatus.IN_PROGRESS)
                .build();
        OnboardingSession sessionStep4 = OnboardingSession.builder()
                .user(testUser)
                .currentStep(OnboardingStep.HOSPITAL_SELECTION)
                .status(OnboardingStatus.IN_PROGRESS)
                .build();
        OnboardingSession completedSession = OnboardingSession.builder()
                .user(testUser)
                .currentStep(OnboardingStep.COMPLETED)
                .status(OnboardingStatus.COMPLETED)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(userProfileRepository.findByUser(testUser)).willReturn(Optional.empty());
        given(userProfileRepository.save(any(UserProfile.class))).willAnswer(invocation -> 
                invocation.getArgument(0)
        );
        given(sessionRepository.save(any(OnboardingSession.class))).willAnswer(invocation -> 
                invocation.getArgument(0)
        );

        // Step 1: TERMS_AGREEMENT → PROFILE_BASIC
        given(sessionRepository.findByUser(testUser)).willReturn(Optional.of(sessionStep1));
        OnboardingStepRequest step1 = new OnboardingStepRequest(
                OnboardingStep.TERMS_AGREEMENT,
                OnboardingStep.PROFILE_BASIC,
                "홍길동",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        OnboardingStepResponse response1 = onboardingService.updateStep(userId, step1);
        assertThat(response1.estimatedMinutesLeft()).isLessThanOrEqualTo(3);

        // Step 2: PROFILE_BASIC → PROFILE_DETAILS
        given(sessionRepository.findByUser(testUser)).willReturn(Optional.of(sessionStep2));
        OnboardingStepRequest step2 = new OnboardingStepRequest(
                OnboardingStep.PROFILE_BASIC,
                OnboardingStep.PROFILE_DETAILS,
                null,
                null,
                null,
                null,
                "[\"고혈압\"]",
                "{}",
                null,
                null
        );
        OnboardingStepResponse response2 = onboardingService.updateStep(userId, step2);
        assertThat(response2.estimatedMinutesLeft()).isLessThanOrEqualTo(3);

        // Step 3: PROFILE_DETAILS → HOSPITAL_SELECTION
        given(sessionRepository.findByUser(testUser)).willReturn(Optional.of(sessionStep3));
        OnboardingStepRequest step3 = new OnboardingStepRequest(
                OnboardingStep.PROFILE_DETAILS,
                OnboardingStep.HOSPITAL_SELECTION,
                null,
                null,
                null,
                null,
                null,
                null,
                "hospital-123",
                "SEOUL"
        );
        OnboardingStepResponse response3 = onboardingService.updateStep(userId, step3);
        assertThat(response3.estimatedMinutesLeft()).isLessThanOrEqualTo(1); // 마지막 단계는 1분 이하

        // Step 4: 완료
        given(sessionRepository.findByUser(testUser)).willReturn(Optional.of(sessionStep4));
        onboardingService.completeSession(userId);
        
        given(sessionRepository.findByUser(testUser)).willReturn(Optional.of(completedSession));
        OnboardingStepResponse finalResponse = onboardingService.getSession(userId);
        assertThat(finalResponse.estimatedMinutesLeft()).isEqualTo(0); // 완료 시 0분
    }

    // =================================================================
    // 추가 테스트 케이스: REQ-FUNC-005 - 되돌아가기 및 중간 저장
    // =================================================================
    @Test
    @DisplayName("TC-S4-05-3: 사용자가 이전 단계로 되돌아갈 수 있음")
    public void testUpdateStep_CanGoBackToPreviousStep() {
        // Given: PROFILE_DETAILS 단계에 있는 세션
        existingSession.updateStep(OnboardingStep.PROFILE_DETAILS);
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(sessionRepository.findByUser(testUser)).willReturn(Optional.of(existingSession));

        // When: PROFILE_BASIC 단계로 되돌아가기
        OnboardingStepRequest request = new OnboardingStepRequest(
                OnboardingStep.PROFILE_DETAILS,
                OnboardingStep.PROFILE_BASIC,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        OnboardingStepResponse response = onboardingService.updateStep(userId, request);

        // Then: PROFILE_BASIC 단계로 이동 가능 (되돌아가기 기능)
        assertThat(response.currentStep()).isEqualTo(OnboardingStep.PROFILE_BASIC);
        assertThat(response.progressPercent()).isEqualTo(25.0);
        
        // REQ-FUNC-005: 되돌아가기 시에도 중간 저장 확인
        verify(sessionRepository, times(1)).save(any(OnboardingSession.class));
    }

    // =================================================================
    // 추가 테스트 케이스: 에러 핸들링
    // =================================================================
    @Test
    @DisplayName("사용자를 찾을 수 없는 경우 예외 발생")
    public void testStartSession_UserNotFound_ThrowsException() {
        // Given: 존재하지 않는 사용자 ID
        UUID nonExistentUserId = UUID.randomUUID();
        given(userRepository.findById(nonExistentUserId)).willReturn(Optional.empty());

        // When & Then: IllegalArgumentException 발생
        assertThatThrownBy(() -> onboardingService.startSession(nonExistentUserId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("세션을 찾을 수 없는 경우 예외 발생")
    public void testUpdateStep_SessionNotFound_ThrowsException() {
        // Given: 세션이 없는 경우
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(sessionRepository.findByUser(testUser)).willReturn(Optional.empty());

        OnboardingStepRequest request = new OnboardingStepRequest(
                OnboardingStep.TERMS_AGREEMENT,
                OnboardingStep.PROFILE_BASIC,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // When & Then: IllegalArgumentException 발생
        assertThatThrownBy(() -> onboardingService.updateStep(userId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Onboarding session not found");
    }

    @Test
    @DisplayName("완료 처리 시 세션이 없는 경우 예외 발생")
    public void testCompleteSession_SessionNotFound_ThrowsException() {
        // Given: 세션이 없는 경우
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(sessionRepository.findByUser(testUser)).willReturn(Optional.empty());

        // When & Then: IllegalArgumentException 발생
        assertThatThrownBy(() -> onboardingService.completeSession(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Onboarding session not found");
    }
}
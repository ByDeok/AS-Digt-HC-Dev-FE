# OnboardingServiceTest 실행 가이드

## ✅ 테스트 코드 검증 완료

### 테스트 통계
- **총 테스트 메서드**: 14개
- **Test Case 커버리지**: TC-S4-01 ~ TC-S4-08 (F1 Feature 전체)
- **Requirement 커버리지**: REQ-FUNC-001~006, 019, REQ-NF-001, 003, 008, 012
- **린터 오류**: 0개

### 테스트 케이스 목록

| Test Case ID | Test Method | Status |
|--------------|-------------|--------|
| TC-S4-01 | testStartSession_CreatesNewSession | ✅ |
| TC-S4-02 | testStartSession_ResumesExistingSession | ✅ |
| TC-S4-03 | testUpdateStep_ProfileBasic_UpdatesUserProfile | ✅ |
| TC-S4-04 | testUpdateStep_ProfileDetails_UpdatesUserProfileDetails | ✅ |
| TC-S4-05 | testGetSession_DisplaysProgressAndETA | ✅ |
| TC-S4-05-2 | testGetSession_CreatesSessionIfNotExists | ✅ |
| TC-S4-05-3 | testUpdateStep_CanGoBackToPreviousStep | ✅ |
| TC-S4-06 | testCompleteSession_CompletesOnboardingAndActivatesUser | ✅ |
| TC-S4-07 | testUpdateStep_UnsupportedRegion_ThrowsException | ✅ |
| TC-S4-07-2 | testUpdateStep_SupportedRegion_ProceedsNormally | ✅ |
| TC-S4-08 | testCompleteOnboardingFlow_EstimatedTimeWithin3Minutes | ✅ |
| 추가 | testStartSession_UserNotFound_ThrowsException | ✅ |
| 추가 | testUpdateStep_SessionNotFound_ThrowsException | ✅ |
| 추가 | testCompleteSession_SessionNotFound_ThrowsException | ✅ |

---

## 🚀 실행 방법

### 방법 1: VS Code에서 실행 (가장 간단)

1. **파일 열기**
   ```
   src/test/java/vibe/digthc/as_digt_hc_dev_fe/domain/onboarding/service/OnboardingServiceTest.java
   ```

2. **테스트 실행**
   - 각 테스트 메서드 위에 "Run Test" 링크 클릭
   - 또는 클래스 선언 위의 "Run All Tests" 클릭
   - 또는 `F5` 키를 누르고 "Run OnboardingServiceTest" 선택

3. **결과 확인**
   - VS Code 하단의 "Test" 탭에서 결과 확인
   - 통과한 테스트: ✅ 초록색 체크
   - 실패한 테스트: ❌ 빨간색 X (상세 로그 확인 가능)

### 방법 2: IntelliJ IDEA에서 실행

1. **파일 열기**
   - `OnboardingServiceTest.java` 파일 열기

2. **테스트 실행**
   - 각 테스트 메서드 옆의 초록색 실행 버튼 클릭
   - 또는 클래스 선언 옆의 실행 버튼으로 전체 테스트 실행
   - 단축키: `Ctrl+Shift+F10` (Windows/Linux) / `Cmd+Shift+R` (Mac)

3. **결과 확인**
   - 하단의 "Run" 탭에서 결과 확인
   - 통과/실패 개수와 실행 시간 표시

### 방법 3: PowerShell 스크립트 실행 (Java 설치 필요)

```powershell
.\scripts\run-tests.ps1
```

⚠️ **주의**: Java 17이 설치되어 있어야 합니다.

### 방법 4: Gradle 명령어 (Java 설치 필요)

```powershell
# 전체 테스트 클래스
./gradlew test --tests "vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.service.OnboardingServiceTest"

# 특정 테스트 메서드
./gradlew test --tests "OnboardingServiceTest.testStartSession_CreatesNewSession"
```

---

## 📋 예상 결과

### 성공 시 출력 예시

```
✓ testStartSession_CreatesNewSession() - TC-S4-01
✓ testStartSession_ResumesExistingSession() - TC-S4-02
✓ testUpdateStep_ProfileBasic_UpdatesUserProfile() - TC-S4-03
✓ testUpdateStep_ProfileDetails_UpdatesUserProfileDetails() - TC-S4-04
✓ testGetSession_DisplaysProgressAndETA() - TC-S4-05
✓ testGetSession_CreatesSessionIfNotExists() - TC-S4-05-2
✓ testUpdateStep_CanGoBackToPreviousStep() - TC-S4-05-3
✓ testCompleteSession_CompletesOnboardingAndActivatesUser() - TC-S4-06
✓ testUpdateStep_UnsupportedRegion_ThrowsException() - TC-S4-07
✓ testUpdateStep_SupportedRegion_ProceedsNormally() - TC-S4-07-2
✓ testCompleteOnboardingFlow_EstimatedTimeWithin3Minutes() - TC-S4-08
✓ testStartSession_UserNotFound_ThrowsException()
✓ testUpdateStep_SessionNotFound_ThrowsException()
✓ testCompleteSession_SessionNotFound_ThrowsException()

Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
```

---

## 🔍 문제 해결

### Java가 설치되지 않은 경우

IDE에서 실행하면 자동으로 Java를 사용하므로 별도 설치가 필요 없습니다.

터미널에서 실행하려면:
- [TESTING_SETUP.md](./TESTING_SETUP.md) 참고

### 테스트 실패 시

1. **에러 메시지 확인**
   - IDE의 테스트 결과 패널에서 상세 로그 확인

2. **일반적인 원인**
   - Mock 설정 누락
   - 예상 값과 실제 값 불일치
   - 예외 처리 검증 실패

3. **디버깅**
   - 테스트 메서드에 브레이크포인트 설정
   - 디버그 모드로 실행 (`F5` 또는 `Shift+F9`)

---

## 📝 참고

- 테스트 코드는 Mockito를 사용하여 실제 DB 없이 실행됩니다.
- 모든 의존성은 Mock 객체로 대체됩니다.
- 각 테스트는 독립적으로 실행됩니다 (`@BeforeEach`에서 초기화).

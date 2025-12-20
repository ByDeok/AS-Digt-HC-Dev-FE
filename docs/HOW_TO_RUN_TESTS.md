# 🧪 OnboardingServiceTest 실행 방법 (VS Code)

## 📋 준비 사항 확인

테스트 실행 전에 확인:
- ✅ Java Extension Pack 설치됨
- ✅ 프로젝트가 Java Projects 뷰에 표시됨
- ✅ JUnit 5 오류가 없음 (빨간 밑줄 없음)

---

## 🚀 테스트 실행 방법

### 방법 1: 각 테스트 메서드 개별 실행 (권장)

1. **테스트 파일 열기**
   ```
   src/test/java/vibe/digthc/as_digt_hc_dev_fe/domain/onboarding/service/OnboardingServiceTest.java
   ```

2. **테스트 메서드 찾기**
   - 파일을 열면 각 `@Test` 메서드 위에 **"Run Test"** 링크가 보입니다
   - 예: `@Test` 아래의 `void testStartSession_CreatesNewSession()` 위에 "▶ Run Test" 링크

3. **실행**
   - "**Run Test**" 링크를 클릭
   - 또는 테스트 메서드 이름 위에서 우클릭 → **"Run Test"**

4. **결과 확인**
   - 하단의 **"TERMINAL"** 또는 **"TEST RESULTS"** 탭에서 결과 확인
   - ✅ 초록색 체크 = 성공
   - ❌ 빨간색 X = 실패 (상세 로그 확인)

### 방법 2: 전체 테스트 클래스 실행

1. **클래스 선언 위에 링크**
   - `class OnboardingServiceTest {` 위에 **"Run All Tests"** 링크 클릭

2. **또는 테스트 뷰에서**
   - 좌측 사이드바의 **"Testing"** 아이콘 (비커 아이콘) 클릭
   - `OnboardingServiceTest` 찾기
   - 우클릭 → **"Run Test"**

### 방법 3: Testing 뷰 사용 (가장 편리)

1. **Testing 뷰 열기**
   - 좌측 사이드바에서 **"Testing"** 아이콘 클릭 (비커 아이콘)
   - 또는 `Ctrl+Shift+T`

2. **테스트 트리 확인**
   - `OnboardingServiceTest` 확장
   - 각 테스트 메서드가 목록으로 표시됨

3. **실행**
   - 전체 클래스 실행: `OnboardingServiceTest` 옆의 ▶ 버튼 클릭
   - 개별 테스트 실행: 각 테스트 메서드 옆의 ▶ 버튼 클릭

---

## 📊 테스트 결과 확인

### 성공 시
```
✓ testStartSession_CreatesNewSession() - TC-S4-01
✓ testStartSession_ResumesExistingSession() - TC-S4-02
✓ testUpdateStep_ProfileBasic_UpdatesUserProfile() - TC-S4-03
...
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
```

### 실패 시
- 실패한 테스트에 빨간색 X 표시
- 클릭하면 상세 오류 메시지 확인 가능
- "AssertionError", "NullPointerException" 등의 오류 확인

---

## 🔍 Testing 뷰에서 확인할 내용

Testing 뷰에서 다음을 확인할 수 있습니다:

1. **테스트 그룹**
   ```
   📁 OnboardingServiceTest
     ✓ testStartSession_CreatesNewSession
     ✓ testStartSession_ResumesExistingSession
     ✓ testUpdateStep_ProfileBasic_UpdatesUserProfile
     ...
   ```

2. **실행 시간**: 각 테스트가 얼마나 걸렸는지

3. **상태 아이콘**:
   - ✅ 초록색 체크 = 통과
   - ❌ 빨간색 X = 실패
   - ⏸ 회색 = 실행 대기

---

## 🐛 문제 해결

### "Run Test" 링크가 보이지 않는 경우

1. **Java Extension Pack 확인**
   - Extensions (`Ctrl+Shift+X`)
   - "Extension Pack for Java" 설치 확인

2. **프로젝트 다시 로드**
   - `Ctrl+Shift+P`
   - `Java: Reload Projects`

3. **Java Projects 뷰 확인**
   - 좌측 사이드바 "Java Projects" 아이콘
   - 프로젝트가 로드되었는지 확인

### 테스트 실행 시 오류 발생

1. **Output 패널 확인**
   - `Ctrl+Shift+U`
   - "Test Runner for Java" 선택
   - 오류 메시지 확인

2. **테스트 클래스 컴파일 확인**
   - 빨간 밑줄(에러)이 있는지 확인
   - 컴파일 오류가 있으면 먼저 수정

---

## 📝 실행할 테스트 목록

현재 `OnboardingServiceTest`에는 다음 테스트들이 있습니다:

1. ✅ TC-S4-01: `testStartSession_CreatesNewSession`
2. ✅ TC-S4-02: `testStartSession_ResumesExistingSession`
3. ✅ TC-S4-03: `testUpdateStep_ProfileBasic_UpdatesUserProfile`
4. ✅ TC-S4-04: `testUpdateStep_ProfileDetails_UpdatesUserProfileDetails`
5. ✅ TC-S4-05: `testGetSession_DisplaysProgressAndETA`
6. ✅ TC-S4-05-2: `testGetSession_CreatesSessionIfNotExists`
7. ✅ TC-S4-05-3: `testUpdateStep_CanGoBackToPreviousStep`
8. ✅ TC-S4-06: `testCompleteSession_CompletesOnboardingAndActivatesUser`
9. ✅ TC-S4-07: `testUpdateStep_UnsupportedRegion_ThrowsException`
10. ✅ TC-S4-07-2: `testUpdateStep_SupportedRegion_ProceedsNormally`
11. ✅ TC-S4-08: `testCompleteOnboardingFlow_EstimatedTimeWithin3Minutes`
12. ✅ 추가: `testStartSession_UserNotFound_ThrowsException`
13. ✅ 추가: `testUpdateStep_SessionNotFound_ThrowsException`
14. ✅ 추가: `testCompleteSession_SessionNotFound_ThrowsException`

---

## 💡 빠른 실행 팁

- **전체 테스트 실행**: `Ctrl+Shift+T` → Testing 뷰 → `OnboardingServiceTest` 옆 ▶ 클릭
- **실패한 테스트만 실행**: Testing 뷰에서 실패한 테스트만 선택하여 실행
- **디버그 모드로 실행**: "Run Test" 대신 "Debug Test" 링크 사용 (브레이크포인트 설정 가능)

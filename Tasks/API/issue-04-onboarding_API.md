# [BE] Issue-04: 3분 온보딩 프로세스 API 명세서

## 📋 개요

**SRS 3.4.1 핵심 온보딩 플로우** 및 **REQ-FUNC-005, 006**에 따라 단계별 온보딩 상태 관리 및 완료 처리를 위한 API 명세서입니다.

**기술 스택**: Java 17+, Spring Boot 3.4+, MySQL 9.x, Jakarta EE 10

---

## 🚀 1. 온보딩 시작 (Start Onboarding)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/onboarding/start` |
| **설명** | 새로운 온보딩 세션 시작 또는 기존 세션 조회 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **하 (Low)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Response Body

**✅ 성공 (201 Created 또는 200 OK)**

```json
{
  "success": true,
  "message": "온보딩 세션이 시작되었습니다.",
  "data": {
    "sessionId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "IN_PROGRESS",
    "currentStep": 1,
    "totalSteps": 4,
    "steps": [
      {
        "stepNumber": 1,
        "stepType": "PROFILE",
        "status": "PENDING",
        "title": "프로필 입력"
      },
      {
        "stepNumber": 2,
        "stepType": "AUTH",
        "status": "PENDING",
        "title": "인증 완료"
      },
      {
        "stepNumber": 3,
        "stepType": "DEVICE",
        "status": "PENDING",
        "title": "디바이스 연동"
      },
      {
        "stepNumber": 4,
        "stepType": "PORTAL",
        "status": "PENDING",
        "title": "병원 포털 연동"
      }
    ],
    "etaSeconds": 180,
    "progressPercent": 0
  }
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `409` | `/errors/session-exists` | 세션 존재 | 이미 진행 중인 온보딩 세션 존재 |

### Logic Steps

```
1. [Check Existing] 사용자의 활성 세션 확인
   - OnboardingSessionRepository.findActiveSessionByUserId()
   - 존재하면 기존 세션 반환 (200 OK)

2. [Create Session] 새 온보딩 세션 생성
   - OnboardingSession.create(user)
   - status = IN_PROGRESS, currentStep = 1
   - expiresAt = now + 24시간

3. [Save Session] 세션 저장
   - OnboardingSessionRepository.save(session)

4. [Response] 세션 정보 반환
   - OnboardingSessionRes.from(session)
```

---

## 📋 2. 온보딩 세션 조회 (Get Session)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/onboarding/session` |
| **설명** | 현재 진행 중인 온보딩 세션 조회 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **하 (Low)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "세션 조회 성공",
  "data": {
    "sessionId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "IN_PROGRESS",
    "currentStep": 2,
    "totalSteps": 4,
    "steps": [
      {
        "stepNumber": 1,
        "stepType": "PROFILE",
        "status": "COMPLETED",
        "completedAt": "2025-01-15T10:05:00"
      },
      {
        "stepNumber": 2,
        "stepType": "AUTH",
        "status": "IN_PROGRESS",
        "title": "인증 완료"
      }
    ],
    "etaSeconds": 135,
    "progressPercent": 25
  }
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `404` | `/errors/session-not-found` | 세션 없음 | 활성 온보딩 세션 없음 |

### Logic Steps

```
1. [Find Session] 활성 세션 조회
   - OnboardingSessionRepository.findActiveSessionByUserId()

2. [Check Expiry] 세션 만료 여부 확인
   - 만료된 경우 EXPIRED 상태로 변경

3. [Response] 세션 정보 반환
   - OnboardingSessionRes.from(session)
```

---

## ✅ 3. 단계 제출 (Submit Step)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/onboarding/step` |
| **설명** | 온보딩 단계 데이터 제출 및 다음 단계로 진행 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **중 (Medium)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Request Body

| 필드명 | 타입 | 필수 | 설명 | Validation |
|:---|:---|:---:|:---|:---|
| `stepNumber` | `Integer` | ✅ | 단계 번호 (1-4) | `@Min(1)`, `@Max(4)` |
| `stepType` | `Enum` | ✅ | 단계 타입 | `PROFILE`, `AUTH`, `DEVICE`, `PORTAL` |
| `stepData` | `Object` | ✅ | 단계별 입력 데이터 | 단계 타입에 따라 다름 |

#### Step 1 (PROFILE) - Request Body 예시

```json
{
  "stepNumber": 1,
  "stepType": "PROFILE",
  "stepData": {
    "name": "홍길동",
    "birthYear": 1960,
    "gender": "MALE",
    "primaryConditions": ["고혈압", "당뇨"]
  }
}
```

#### Step 2 (AUTH) - Request Body 예시

```json
{
  "stepNumber": 2,
  "stepType": "AUTH",
  "stepData": {
    "authMethod": "KAKAO",
    "verifiedAt": "2025-01-15T10:30:00"
  }
}
```

#### Step 3 (DEVICE) - Request Body 예시

```json
{
  "stepNumber": 3,
  "stepType": "DEVICE",
  "stepData": {
    "devices": [
      {
        "vendor": "samsung",
        "type": "watch",
        "linked": true
      }
    ]
  }
}
```

#### Step 4 (PORTAL) - Request Body 예시

```json
{
  "stepNumber": 4,
  "stepType": "PORTAL",
  "stepData": {
    "portalId": "nhis_checkup",
    "status": "CONNECTED"
  }
}
```

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "단계가 완료되었습니다.",
  "data": {
    "stepNumber": 1,
    "status": "COMPLETED",
    "canProceed": true,
    "nextStep": 2,
    "message": "다음 단계로 진행할 수 있습니다."
  }
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `400` | `/errors/invalid-step` | 잘못된 단계 | 이전 단계 미완료 또는 잘못된 단계 번호 |
| `400` | `/errors/validation` | 입력값 검증 실패 | stepData 형식 불일치 |
| `404` | `/errors/session-not-found` | 세션 없음 | 활성 세션 없음 |

### Logic Steps

```
1. [Find Session] 활성 세션 조회
   - OnboardingSessionRepository.findActiveSessionByUserId()

2. [Validate Transition] 단계 전이 유효성 검증
   - session.canProceedToStep(stepNumber) 확인
   - 이전 단계 완료 여부 확인

3. [Validate Data] 단계별 데이터 유효성 검증
   - StepValidatorFactory.getValidator(stepType)
   - 단계별 검증 로직 실행

4. [Process Step] 단계별 비즈니스 로직 처리
   - Step 1: 프로필 정보 저장
   - Step 2: 인증 완료 처리
   - Step 3: 디바이스 연동 처리
   - Step 4: 포털 연동 처리

5. [Complete Step] 단계 완료 처리
   - session.completeStep(stepNumber, stepData)
   - OnboardingStepLog 완료 기록

6. [Move Next] 다음 단계로 이동
   - session.moveToNextStep()
   - ETA 재계산

7. [Save] 세션 저장
   - OnboardingSessionRepository.save(session)

8. [Response] 단계 결과 반환
   - StepResultRes 생성
```

---

## ⏭️ 4. 단계 스킵 (Skip Step)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/onboarding/step/{stepNumber}/skip` |
| **설명** | 선택적 단계(Step 3, 4) 스킵 처리 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **하 (Low)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Path Parameters

| 파라미터명 | 타입 | 필수 | 설명 |
|:---|:---|:---:|:---|
| `stepNumber` | `Integer` | ✅ | 스킵할 단계 번호 (3 또는 4) |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "단계를 건너뛰었습니다.",
  "data": {
    "stepNumber": 3,
    "status": "SKIPPED",
    "canProceed": true,
    "nextStep": 4,
    "message": "다음 단계로 진행할 수 있습니다."
  }
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `400` | `/errors/step-cannot-skip` | 스킵 불가 | Step 1, 2는 필수 단계로 스킵 불가 |
| `404` | `/errors/session-not-found` | 세션 없음 | 활성 세션 없음 |

### Logic Steps

```
1. [Find Session] 활성 세션 조회

2. [Validate Skip] 스킵 가능 여부 확인
   - Step 3, 4만 스킵 가능
   - Step 1, 2는 필수 단계

3. [Skip Step] 단계 스킵 처리
   - session.skipStep(stepNumber)
   - OnboardingStepLog 스킵 기록

4. [Save] 세션 저장

5. [Response] 스킵 결과 반환
```

---

## 🎉 5. 온보딩 완료 (Complete Onboarding)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/onboarding/complete` |
| **설명** | 온보딩 완료 처리 및 사용자 활성화 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **중 (Medium)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "온보딩이 완료되었습니다.",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "isActive": true,
    "welcomeMessage": "환영합니다! 온보딩이 완료되었습니다.",
    "nextAction": "VIEW_FIRST_REPORT"
  }
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `400` | `/errors/incomplete-onboarding` | 필수 단계 미완료 | Step 1, 2는 필수로 완료해야 함 |
| `404` | `/errors/session-not-found` | 세션 없음 | 활성 세션 없음 |

### Logic Steps

```
1. [Find Session] 활성 세션 조회

2. [Validate Completion] 필수 단계 완료 여부 확인
   - session.areAllRequiredStepsCompleted()
   - Step 1(프로필), Step 2(인증)는 필수

3. [Mark Completed] 온보딩 완료 처리
   - session.markCompleted()
   - completedAt = now()

4. [Activate User] 사용자 활성화
   - user.activate()
   - UserRepository.save(user)

5. [Response] 완료 정보 반환
   - OnboardingCompleteRes 생성
```

---

## 🔄 6. 세션 재개 (Resume Session)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/onboarding/resume` |
| **설명** | 이탈 후 재진입 시 마지막 단계 복원 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **하 (Low)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "세션이 복원되었습니다.",
  "data": {
    "sessionId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "IN_PROGRESS",
    "currentStep": 2,
    "totalSteps": 4,
    "steps": [
      {
        "stepNumber": 1,
        "stepType": "PROFILE",
        "status": "COMPLETED",
        "completedAt": "2025-01-15T10:05:00"
      },
      {
        "stepNumber": 2,
        "stepType": "AUTH",
        "status": "PENDING",
        "title": "인증 완료"
      }
    ],
    "etaSeconds": 135,
    "progressPercent": 25
  }
}
```

### Logic Steps

```
1. [Find Session] 활성 세션 조회
   - OnboardingSessionRepository.findActiveSessionByUserId()

2. [Check Expiry] 세션 만료 여부 확인
   - 만료된 경우 새 세션 생성 안내

3. [Response] 세션 정보 반환
   - 마지막 진행 단계 포함
```

---

## 📊 API 요약 테이블

| # | Method | Endpoint | 설명 | 인증 | 난이도 |
|:---:|:---|:---|:---|:---:|:---:|
| 1 | `POST` | `/api/v1/onboarding/start` | 온보딩 시작 | ✅ | 하 |
| 2 | `GET` | `/api/v1/onboarding/session` | 세션 조회 | ✅ | 하 |
| 3 | `POST` | `/api/v1/onboarding/step` | 단계 제출 | ✅ | 중 |
| 4 | `POST` | `/api/v1/onboarding/step/{stepNumber}/skip` | 단계 스킵 | ✅ | 하 |
| 5 | `POST` | `/api/v1/onboarding/complete` | 온보딩 완료 | ✅ | 중 |
| 6 | `GET` | `/api/v1/onboarding/resume` | 세션 재개 | ✅ | 하 |

---

## 🏗️ 구현 참고사항

### 1. 단계별 검증 예시

```java
@Component
public class ProfileStepValidator implements StepValidator {
    
    @Override
    public ValidationResult validate(StepSubmitReq req) {
        Map<String, Object> data = req.getStepData();
        
        // 필수 필드 검증
        if (!data.containsKey("name")) {
            return ValidationResult.fail("이름은 필수입니다.");
        }
        
        // 데이터 타입 검증
        if (!(data.get("name") instanceof String)) {
            return ValidationResult.fail("이름은 문자열이어야 합니다.");
        }
        
        return ValidationResult.success();
    }
}
```

### 2. DTO 예시

```java
public record StepSubmitReq(
    @Min(1) @Max(4) int stepNumber,
    @NotNull StepType stepType,
    @NotNull Map<String, Object> stepData
) {}

public record OnboardingSessionRes(
    UUID sessionId,
    OnboardingStatus status,
    int currentStep,
    int totalSteps,
    List<StepInfo> steps,
    int etaSeconds,
    int progressPercent
) {
    public static OnboardingSessionRes from(OnboardingSession session) {
        return new OnboardingSessionRes(
            session.getId(),
            session.getStatus(),
            session.getCurrentStep(),
            session.getTotalSteps(),
            mapSteps(session),
            session.getEtaSeconds(),
            session.getProgressPercent()
        );
    }
}
```

---

## 📝 참고 자료

- [BE] Issue-04: 3분 온보딩 프로세스 구현 (`studio/Tasks/BE_issue/issue-04-onboarding.md`)
- SRS 3.4.1 핵심 온보딩 플로우
- SRS REQ-FUNC-005, 006
- Java Spring Boot 3.x Cursor Rules (`.cursor/rules/300-java-spring-cursor-rules.mdc`)


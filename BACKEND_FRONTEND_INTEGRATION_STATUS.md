# 백엔드-프론트엔드 API 연동 상태 분석표

## 개요
이 문서는 AS-Digt-HC 프로젝트의 백엔드와 프론트엔드 간 API 연동 상태를 상세히 분석한 표입니다.

---

## 연동 상태 표

| 프론트엔드 페이지 | 백엔드 API 엔드포인트 | 호출 조건 | 구현 여부 | 요청 데이터 (Request Body) | 응답 데이터 (Response Body) | 성공 시 동작 | 실패 시 동작 |
|-----------------|---------------------|---------|---------|--------------------------|---------------------------|------------|------------|
| **랜딩 페이지** (`/`) | - | - | - | - | - | 로그인 상태면 `/dashboard`로 리다이렉트 | - |
| 랜딩 페이지 설명: 서비스 소개 및 가치 제안을 보여주는 마케팅 페이지 | | | | | | | |
| **시작 페이지** (`/start`) | - | - | - | - | - | 로그인/회원가입/온보딩 선택 제공 | - |
| 시작 페이지 설명: 로그인/회원가입/소셜 로그인 선택 페이지 | | | | | | | |
| **로그인 페이지** (`/auth/login`) | `POST /v1/auth/login` | 로그인 버튼 클릭 시 | ✅ O | `{ email: string, password: string }` | `{ success: boolean, message: string, data: { accessToken: string, refreshToken: string, user: { id, email, name, role } } }` | `/dashboard`로 이동, 토큰 저장, 환영 메시지 표시 | 에러 토스트 표시, 페이지 유지 |
| | `POST /v1/auth/refresh` | 토큰 만료 시 자동 (인터셉터) | ✅ O | `{ refreshToken: string }` | `{ success: boolean, data: { accessToken: string, refreshToken: string } }` | 자동 토큰 갱신 | `/login`으로 리다이렉트 |
| **회원가입 페이지** (`/auth/signup`) | `POST /v1/auth/signup` | 회원가입 버튼 클릭 시 | ✅ O | `{ email: string, password: string, name: string, role?: 'SENIOR', agreements: { termsService: boolean, privacyPolicy: boolean, marketingConsent: boolean } }` | `{ success: boolean, message: string, data: { userId, email, name, role, createdAt } }` | `/login`으로 이동, 성공 메시지 표시 | 에러 토스트 표시, 페이지 유지 |
| **대시보드** (`/(main)/dashboard`) | `GET /actions/today` | 페이지 로드 시, 날짜 변경 시, 새로고침 버튼 클릭 시 | ✅ O | - | `{ success: boolean, data: [{ id: number, title: string, description: string, actionDate: string, status: 'PENDING'\|'COMPLETED'\|'SKIPPED', ruleId: string }] }` | 오늘의 미션 목록 표시 | 에러 메시지 표시, 로딩 스피너 |
| | `POST /actions/{id}/complete` | 미션 완료 버튼 클릭 시 | ✅ O | - | `{ success: boolean, data: { id, title, status: 'COMPLETED', ... } }` | 미션 완료 상태로 업데이트, 폭죽 효과, 토스트 메시지 | 에러 토스트 표시 |
| | `GET /metrics/daily?date=YYYY-MM-DD` | 페이지 로드 시 (오늘 날짜) | ✅ O | - (Query: date) | `{ success: boolean, data: { userId: string, recordDate: string, steps?: number, heartRate?: number, weight?: number, systolic?: number, diastolic?: number } }` | 건강 기록 폼에 데이터 채우기 | - |
| | `POST /metrics/daily` | 건강 기록 저장 버튼 클릭 시 | ✅ O | `{ recordDate: string, systolic?: number, diastolic?: number, weight?: number, heartRate?: number, steps?: number }` | `{ success: boolean, data: { userId, recordDate, ... } }` | 저장 완료 토스트 표시 | 에러 토스트 표시 |
| | `POST /actions/today/reset` | 초기화 버튼 클릭 시 | ❌ X (Mock) | - | - | TODO: 백엔드 API 추가 필요 | 토스트로 "준비중" 메시지 표시 |
| 대시보드 설명: 오늘의 미션 목록, 건강 기록 입력, 완료 상태 관리 | | | | | | | |
| **리포트 페이지** (`/(main)/report`) | `GET /reports?periodType=WEEKLY\|MONTHLY` | 페이지 로드 시, 기간 토글 변경 시 | ✅ O | - (Query: periodType) | `{ success: boolean, data: [{ reportId: string, userId: string, startDate: string, endDate: string, periodType: 'WEEKLY'\|'MONTHLY', metrics: { activity?: { steps, activeMinutes }, heartRate?: { avgBpm }, bloodPressure?: { systolic, diastolic } }, context: { deviceType, deviceId }, createdAt: string }] }` | 리포트 목록 표시 | 에러 메시지 표시 |
| | `POST /reports/generate?periodType=WEEKLY\|MONTHLY` | 리포트 생성 버튼 클릭 시 | ✅ O | - (Query: periodType) | `{ success: boolean, data: { reportId, startDate, endDate, metrics, ... } }` | 생성된 리포트를 선택 상태로 설정, 성공 토스트 | 에러 토스트 표시 |
| | `DELETE /reports/{id}` | 리포트 삭제 버튼 클릭 시 | ✅ O | - | `{ success: boolean, message: string }` | 리포트 목록에서 제거, 선택 해제 | 에러 토스트 표시 |
| 리포트 페이지 설명: 건강 리포트 목록 조회, 생성, 삭제, 상세 정보 표시 | | | | | | | |
| **가족 페이지** (`/(main)/family`) | `GET /v1/family-board` | 페이지 로드 시 | ✅ O | - | `{ success: boolean, data: { boardId: string, name: string, description: string, senior: { id, email, name }, memberCount: number, settings: object, lastActivityAt: string } }` | 가족 보드 정보 표시 | 에러 메시지 표시 |
| | `GET /v1/family-board/members` | 페이지 로드 시 | ✅ O | - | `{ success: boolean, data: [{ membershipId: number, user: { id, email, name, role }, role: 'ADMIN'\|'EDITOR'\|'VIEWER', status: 'ACTIVE'\|'INVITED'\|'REMOVED'\|'LEFT', joinedAt: string }] }` | 가족 멤버 목록 표시 | 에러 메시지 표시 |
| | `POST /v1/family-board/invite` | 가족 초대 버튼 클릭 시 | ✅ O | `{ inviteeEmail: string, intendedRole: 'ADMIN'\|'EDITOR'\|'VIEWER' }` | `{ success: boolean, data: { invitationId: string, inviteCode: string, inviteeEmail: string, intendedRole: string, status: 'PENDING', expiresAt: string } }` | 초대 코드 클립보드 복사, 성공 토스트 | 에러 토스트 표시 |
| | `POST /v1/family-board/accept` | 초대 코드 입력 후 참여 버튼 클릭 시 | ✅ O | `{ inviteCode: string }` | `{ success: boolean, data: { membershipId, user: { id, email, name }, role, status: 'ACTIVE', joinedAt: string } }` | 가족 보드에 참여, 멤버 목록 갱신, 성공 토스트 | 에러 토스트 표시 |
| 가족 페이지 설명: 가족 보드 정보 조회, 멤버 목록, 초대 기능, 활동 피드 (준비중) | | | | | | | |
| **온보딩 소셜 로그인** (`/onboarding`) | - | - | - | - | - | 소셜 로그인 선택 후 `/onboarding/profile`로 이동 (현재는 시뮬레이션) | - |
| 온보딩 소셜 로그인 설명: 카카오/네이버 로그인 선택 페이지 (현재는 모의 동작) | | | | | | | |
| **온보딩 프로필** (`/onboarding/profile`) | `POST /onboarding/start` | 페이지 진입 시 (useEffect) | ✅ O (best-effort) | - | `{ success: boolean, data: { currentStep: string, progressPercent: number, estimatedMinutesLeft: number, completed: boolean } }` | 온보딩 세션 시작 (실패해도 진행 가능) | 실패 시 무시 (best-effort) |
| | `POST /onboarding/step` | 각 단계 완료 시 (이름 -> 생년월일, 생년월일 -> 건강정보) | ✅ O (best-effort) | `{ currentStep: 'PROFILE_BASIC'\|'PROFILE_DETAILS', nextStep: string, name?: string, birthDate?: string, gender?: 'MALE'\|'FEMALE', primaryConditions?: string }` | `{ success: boolean, data: { currentStep, progressPercent, ... } }` | 온보딩 진행 상태 저장 (실패해도 진행 가능) | 실패 시 무시 (best-effort) |
| | `PUT /v1/users/me` | 건강정보 단계 완료 시 | ✅ O | `{ name?: string, birthDate?: string (YYYY-MM-DD), gender?: 'MALE'\|'FEMALE'\|'OTHER', primaryConditions?: string, bio?: string, accessibilityPrefs?: string }` | `{ success: boolean, data: { userId, email, name, birthDate, gender, primaryConditions, ... } }` | 프로필 저장 후 `/onboarding/device`로 이동 | 에러 토스트 표시, 페이지 유지 |
| 온보딩 프로필 설명: 이름, 생년월일, 성별, 기저질환 입력 (다단계 폼) | | | | | | | |
| **온보딩 디바이스** (`/onboarding/device`) | `POST /v1/integration/devices` | 기기 연결 버튼 클릭 시 | ✅ O | `{ vendor: string, deviceType: 'WATCH'\|'BP_MONITOR', authCode: string, consentScope: { dataTypes: string[], frequency: string, retentionPeriod?: string, sharingAllowed?: { family: boolean } } }` | `{ success: boolean, data: { deviceId: string, vendor: string, deviceType: string, status: 'PENDING'\|'ACTIVE'\|'REVOKED'\|'EXPIRED'\|'ERROR', lastSyncAt: string, hasActiveConsent: boolean } }` | 기기 연결 상태 업데이트, 성공 토스트 | 에러 토스트 표시, 연결 상태 초기화 |
| 온보딩 디바이스 설명: 스마트 워치/혈압계 등 건강 기기 연동 설정 | | | | | | | |
| **온보딩 완료** (`/onboarding/complete`) | `POST /onboarding/complete` | 페이지 진입 시 (useEffect) | ✅ O (best-effort) | - | `{ success: boolean, message: string }` | 온보딩 완료 처리 (실패해도 진행 가능) | 실패 시 무시 (best-effort) |
| 온보딩 완료 설명: 온보딩 완료 메시지 표시, 2.5초 후 `/dashboard`로 자동 이동 | | | | | | | |

---

## 구현 상태 요약

### ✅ 완전 구현 (O)
- 인증: 로그인, 회원가입, 토큰 갱신
- 사용자: 프로필 조회, 프로필 수정
- 액션: 오늘의 미션 조회, 미션 완료
- 리포트: 리포트 목록 조회, 리포트 생성, 리포트 삭제
- 가족 보드: 보드 정보 조회, 멤버 목록, 초대 생성, 초대 수락
- 건강 지표: 일일 건강 지표 조회, 저장
- 온보딩: 온보딩 세션 관리, 프로필 저장
- 통합: 기기 연동

### ❌ 미구현 (X)
- 액션 초기화: `POST /actions/today/reset` (TODO: 백엔드 API 추가 필요)

### ⚠️ Mock/시뮬레이션
- 소셜 로그인 (카카오/네이버): 현재는 페이지 이동만 처리, 실제 OAuth 연동 미구현
- 가족 보드 활동 피드: UI는 준비되어 있으나 백엔드 API 미준비

### 📝 Best-effort (실패해도 진행 가능)
- 온보딩 세션 시작/진행/완료: 실패해도 사용자 경험을 막지 않음

---

## 주의사항

1. **API 응답 형식**: 모든 API는 `ApiResponse<T>` 형태로 응답하며, `{ success: boolean, message: string, data?: T, timestamp: string }` 구조입니다.

2. **인증**: 대부분의 API는 `Authorization: Bearer {token}` 헤더가 필요합니다. 토큰은 로그인 시 저장되며, 만료 시 자동 갱신됩니다.

3. **에러 처리**: 프론트엔드는 일반적으로 토스트 메시지를 통해 에러를 표시하며, 401 에러 시 자동으로 `/login`으로 리다이렉트됩니다.

4. **Best-effort 패턴**: 온보딩 관련 일부 API는 실패해도 사용자 경험을 막지 않도록 설계되어 있습니다 (best-effort).

5. **날짜 형식**: 날짜는 `YYYY-MM-DD` 형식 (ISO 8601 date)으로 전송됩니다.

6. **미구현 기능**: 일부 기능(액션 초기화, 활동 피드)은 백엔드 API가 준비되지 않아 TODO로 표시되어 있습니다.

---

## 업데이트 이력

- 2024-12-XX: 초기 문서 작성

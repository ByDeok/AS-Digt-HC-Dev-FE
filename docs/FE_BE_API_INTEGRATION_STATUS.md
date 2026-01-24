# 백엔드-프론트엔드 API 연동 상태 분석표

## 개요
- **프론트엔드**: `studio/src/` (React + TanStack Query)
- **백엔드**: `src/main/java/` (Spring Boot)
- **API Base URL**: `http://localhost:8080/api/v1`
- **응답 형식**: `ApiResponse<T>` (`{ success, message, data, timestamp }`)

---

## 1. 랜딩 페이지 (`/`)

| 항목 | 내용 |
|------|------|
| **페이지명/설명** | 랜딩 페이지 - 서비스 소개, 가치 제안, CTA 버튼 제공 |
| **API 엔드포인트** | 없음 (정적 페이지) |
| **호출 조건** | - |
| **구현 여부** | - |
| **Request Body** | - |
| **Response Body** | - |
| **성공 시 동작** | 로그인 상태면 `/dashboard`로 자동 리다이렉트 |
| **실패 시 동작** | - |

---

## 2. 시작 페이지 (`/start`)

| 항목 | 내용 |
|------|------|
| **페이지명/설명** | 시작 페이지 - 로그인/회원가입/소셜 로그인 선택 게이트 |
| **API 엔드포인트** | 없음 (라우팅 페이지) |
| **호출 조건** | - |
| **구현 여부** | - |
| **Request Body** | - |
| **Response Body** | - |
| **성공 시 동작** | 선택에 따라 `/login`, `/signup`, `/onboarding` 이동 |
| **실패 시 동작** | - |

---

## 3. 로그인 페이지 (`/login`)

| 항목 | 내용 |
|------|------|
| **페이지명/설명** | 로그인 페이지 - 이메일/비밀번호 또는 admin 계정 로그인 |

| API 엔드포인트 | 용도 | 호출 조건 | 구현 | Request Body | Response Body | 성공 시 동작 | 실패 시 동작 |
|--------------|------|----------|-----|-------------|--------------|------------|------------|
| `POST /v1/auth/login` | 사용자 로그인 | 로그인 버튼 클릭 | ✅ O | `{ email, password }` | `{ accessToken, refreshToken }` | 토큰 저장, `/dashboard` 이동, 환영 토스트 | 에러 토스트, 페이지 유지 |
| `POST /v1/auth/refresh` | 토큰 갱신 | 401 에러 시 자동 (인터셉터) | ✅ O | `{ refreshToken }` | `{ accessToken, refreshToken }` | 자동 토큰 교체, 원래 요청 재시도 | `/login` 리다이렉트 |

---

## 4. 회원가입 페이지 (`/signup`)

| 항목 | 내용 |
|------|------|
| **페이지명/설명** | 회원가입 페이지 - 이메일 계정 생성 및 약관 동의 |

| API 엔드포인트 | 용도 | 호출 조건 | 구현 | Request Body | Response Body | 성공 시 동작 | 실패 시 동작 |
|--------------|------|----------|-----|-------------|--------------|------------|------------|
| `POST /v1/auth/signup` | 회원가입 | 회원가입 버튼 클릭 | ✅ O | `{ email, password, name, role?, agreements: { termsService, privacyPolicy, marketingConsent } }` | `{ userId, email, name, role, createdAt }` | `/login` 이동, 성공 토스트 | 에러 토스트, 페이지 유지 |

---

## 5. 온보딩 - 소셜 로그인 (`/onboarding`)

| 항목 | 내용 |
|------|------|
| **페이지명/설명** | 온보딩 소셜 로그인 - 카카오/네이버 OAuth2 선택 |

| API 엔드포인트 | 용도 | 호출 조건 | 구현 | Request Body | Response Body | 성공 시 동작 | 실패 시 동작 |
|--------------|------|----------|-----|-------------|--------------|------------|------------|
| `GET /v1/auth/oauth/{provider}/authorize` | OAuth 인가 시작 | 소셜 로그인 버튼 클릭 | ❌ Spec | - | OAuth 리다이렉트 URL | OAuth 제공자로 리다이렉트 | 에러 토스트 |
| `POST /v1/auth/oauth/{provider}/callback` | OAuth 콜백 처리 | OAuth 리다이렉트 후 | ❌ Spec | `{ code, redirectUri }` | `{ accessToken, refreshToken, user }` | 토큰 저장, `/onboarding/profile` 이동 | 에러 토스트 |

---

## 6. 온보딩 - 프로필 입력 (`/onboarding/profile`) 🔒

| 항목 | 내용 |
|------|------|
| **페이지명/설명** | 온보딩 프로필 - 이름, 생년월일, 성별, 기저질환 입력 (다단계 위저드) |

| API 엔드포인트 | 용도 | 호출 조건 | 구현 | Request Body | Response Body | 성공 시 동작 | 실패 시 동작 |
|--------------|------|----------|-----|-------------|--------------|------------|------------|
| `POST /v1/onboarding/start` | 온보딩 세션 시작 | 페이지 진입 시 (useEffect) | ✅ O (best-effort) | - | `{ currentStep, progressPercent, estimatedMinutesLeft, completed }` | 세션 생성 | 무시 (UX 차단 안 함) |
| `POST /v1/onboarding/step` | 단계 진행 저장 | 각 단계 완료 시 | ✅ O (best-effort) | `{ currentStep, nextStep, name?, birthDate?, gender?, primaryConditions? }` | `{ currentStep, progressPercent, ... }` | 진행 상태 저장 | 무시 (UX 차단 안 함) |
| `PUT /v1/users/me` | 프로필 최종 저장 | 건강정보 단계 완료 시 | ✅ O | `{ name?, birthDate?, gender?, primaryConditions?, bio?, accessibilityPrefs? }` | `{ userId, email, name, birthDate, gender, ... }` | `/onboarding/device` 이동 | 에러 토스트, 페이지 유지 |
| `GET /v1/users/me` | 기존 프로필 조회 | 페이지 진입 시 | ✅ O | - | `{ userId, email, name, birthDate, gender, primaryConditions, ... }` | 폼에 기존 데이터 채우기 | 빈 폼 표시 |

---

## 7. 온보딩 - 기기 연결 (`/onboarding/device`) 🔒

| 항목 | 내용 |
|------|------|
| **페이지명/설명** | 온보딩 디바이스 - 스마트 워치, 혈압계 등 건강 기기 연동 (선택사항) |

| API 엔드포인트 | 용도 | 호출 조건 | 구현 | Request Body | Response Body | 성공 시 동작 | 실패 시 동작 |
|--------------|------|----------|-----|-------------|--------------|------------|------------|
| `POST /v1/integration/devices` | 기기 연동 | 기기 연결 버튼 클릭 | ✅ O | `{ vendor, deviceType, authCode, consentScope: { dataTypes[], frequency, retentionPeriod?, sharingAllowed? } }` | `{ deviceId, vendor, deviceType, status, lastSyncAt, hasActiveConsent }` | 연결 상태 업데이트, 성공 토스트 | 에러 토스트, 연결 상태 초기화 |

> **건너뛰기**: "건너뛰기" 버튼 클릭 시 API 호출 없이 `/onboarding/complete`로 이동

---

## 8. 온보딩 - 완료 (`/onboarding/complete`) 🔒

| 항목 | 내용 |
|------|------|
| **페이지명/설명** | 온보딩 완료 - 환영 메시지 표시, 2.5초 후 대시보드 자동 이동 |

| API 엔드포인트 | 용도 | 호출 조건 | 구현 | Request Body | Response Body | 성공 시 동작 | 실패 시 동작 |
|--------------|------|----------|-----|-------------|--------------|------------|------------|
| `POST /v1/onboarding/complete` | 온보딩 완료 처리 | 페이지 진입 시 (useEffect) | ✅ O (best-effort) | - | `{ success, message }` | 온보딩 완료 마킹 | 무시 (2.5초 후 대시보드 이동) |

---

## 9. 대시보드 (`/dashboard`) 🔒

| 항목 | 내용 |
|------|------|
| **페이지명/설명** | 대시보드 - 오늘의 미션 목록, 건강 기록 입력, 미션 완료 시 폭죽 효과 |

| API 엔드포인트 | 용도 | 호출 조건 | 구현 | Request Body | Response Body | 성공 시 동작 | 실패 시 동작 |
|--------------|------|----------|-----|-------------|--------------|------------|------------|
| `GET /v1/actions/today` | 오늘의 미션 조회 | 페이지 로드 시, 날짜 변경 시, 새로고침 버튼 클릭 | ✅ O | - | `[{ id, title, description, actionDate, status: 'PENDING'\|'COMPLETED'\|'SKIPPED', ruleId }]` | 미션 목록 표시 | 에러 메시지, 로딩 스피너 |
| `POST /v1/actions/{id}/complete` | 미션 완료 처리 | 미션 완료 버튼 클릭 | ✅ O | - | `{ id, title, status: 'COMPLETED', ... }` | 미션 완료 상태 업데이트, 폭죽 효과, 토스트 | 에러 토스트 |
| `POST /v1/actions/{id}/skip` | 미션 스킵 처리 | 미션 스킵 버튼 클릭 | ✅ O | - | `{ id, title, status: 'SKIPPED', ... }` | 미션 스킵 상태 업데이트 | 에러 토스트 |
| `GET /v1/metrics/daily?date=YYYY-MM-DD` | 일일 건강 지표 조회 | 페이지 로드 시 (오늘 날짜) | ✅ O | Query: `date` | `{ userId, recordDate, steps?, heartRate?, weight?, systolic?, diastolic? }` | 건강 기록 폼에 데이터 채우기 | 빈 폼 표시 |
| `POST /v1/metrics/daily` | 건강 지표 저장 | 건강 기록 저장 버튼 클릭 | ✅ O | `{ recordDate, steps?, heartRate?, weight?, systolic?, diastolic? }` | `{ userId, recordDate, ... }` | 저장 완료 토스트 | 에러 토스트 |
| `POST /v1/actions/today/reset` | 미션 초기화 | 초기화 버튼 클릭 | ❌ X | - | - | (백엔드 미구현) | 토스트로 "준비중" 메시지 |

---

## 10. 건강 리포트 (`/report`) 🔒

| 항목 | 내용 |
|------|------|
| **페이지명/설명** | 건강 리포트 - 주간/월간 건강 리포트 생성, 조회, 삭제 |

| API 엔드포인트 | 용도 | 호출 조건 | 구현 | Request Body | Response Body | 성공 시 동작 | 실패 시 동작 |
|--------------|------|----------|-----|-------------|--------------|------------|------------|
| `GET /v1/reports?periodType=WEEKLY\|MONTHLY` | 리포트 목록 조회 | 페이지 로드 시, 기간 토글 변경 시 | ✅ O | Query: `periodType` | `[{ reportId, userId, startDate, endDate, periodType, metrics: { activity?, heartRate?, bloodPressure? }, context, createdAt }]` | 리포트 목록 표시 | 에러 메시지 |
| `POST /v1/reports/generate?periodType=WEEKLY\|MONTHLY` | 리포트 생성 | 리포트 생성 버튼 클릭 | ✅ O | Query: `periodType` | `{ reportId, startDate, endDate, metrics, ... }` | 생성된 리포트 선택 상태, 성공 토스트 | 에러 토스트 |
| `GET /v1/reports/{reportId}` | 리포트 상세 조회 | 리포트 항목 클릭 | ✅ O | - | `{ reportId, metrics, context, ... }` | 리포트 상세 표시 | 에러 메시지 |
| `DELETE /v1/reports/{reportId}` | 리포트 삭제 | 리포트 삭제 버튼 클릭 | ✅ O | - | `{ success, message }` | 리포트 목록에서 제거, 선택 해제 | 에러 토스트 |

---

## 11. 가족 보드 (`/family`) 🔒

| 항목 | 내용 |
|------|------|
| **페이지명/설명** | 가족 보드 - 가족 멤버 목록, 역할 관리, 초대 코드로 참여, 활동 피드 (준비중) |

| API 엔드포인트 | 용도 | 호출 조건 | 구현 | Request Body | Response Body | 성공 시 동작 | 실패 시 동작 |
|--------------|------|----------|-----|-------------|--------------|------------|------------|
| `GET /v1/family-board` | 가족 보드 정보 조회 | 페이지 로드 시 | ✅ O | - | `{ boardId, name, description, senior: { id, email, name }, memberCount, settings, lastActivityAt }` | 가족 보드 정보 표시 | 에러 메시지 |
| `GET /v1/family-board/members` | 멤버 목록 조회 | 페이지 로드 시 | ✅ O | - | `[{ membershipId, user: { id, email, name, role }, role: 'ADMIN'\|'EDITOR'\|'VIEWER', status, joinedAt }]` | 가족 멤버 목록 표시 | 에러 메시지 |
| `POST /v1/family-board/invite` | 가족 초대 | 가족 초대 버튼 클릭 (ADMIN 전용) | ✅ O | `{ inviteeEmail, intendedRole: 'ADMIN'\|'EDITOR'\|'VIEWER' }` | `{ invitationId, inviteCode, inviteeEmail, intendedRole, status: 'PENDING', expiresAt }` | 초대 코드 클립보드 복사, 성공 토스트 | 에러 토스트 |
| `POST /v1/family-board/accept` | 초대 수락 | 초대 코드 입력 후 참여 버튼 클릭 | ✅ O | `{ inviteCode }` | `{ membershipId, user, role, status: 'ACTIVE', joinedAt }` | 가족 보드 참여, 멤버 목록 갱신, 성공 토스트 | 에러 토스트 |
| `PUT /v1/family-board/members/{memberId}/role` | 멤버 역할 변경 | 역할 변경 드롭다운 선택 (ADMIN 전용) | ✅ O | `{ newRole }` | `{ membershipId, user, role, ... }` | 역할 변경 완료 | 에러 토스트 |
| `DELETE /v1/family-board/members/{memberId}` | 멤버 제거 | 멤버 제거 버튼 클릭 (ADMIN 전용) | ✅ O | - | `{ success, message }` | 멤버 목록에서 제거 | 에러 토스트 |
| `PUT /v1/family-board/settings` | 보드 설정 변경 | 설정 변경 시 (EDITOR+ 전용) | ✅ O | `{ settings: Record<string, unknown> }` | `{ boardId, settings, ... }` | 설정 저장 완료 | 에러 토스트 |

---

## 구현 상태 요약

| 구분 | 항목 | 상태 |
|-----|------|-----|
| **✅ 완전 구현** | 로그인/회원가입/토큰 갱신 | O |
| | 사용자 프로필 조회/수정 | O |
| | 온보딩 세션 (start/step/complete) | O (best-effort) |
| | 오늘의 미션 조회/완료/스킵 | O |
| | 일일 건강 지표 조회/저장 | O |
| | 건강 리포트 목록/생성/조회/삭제 | O |
| | 가족 보드 조회/멤버/초대/수락/역할변경/제거 | O |
| | 기기 연동 | O |
| **❌ 미구현** | 미션 초기화 (`POST /actions/today/reset`) | X (백엔드 API 미구현) |
| **⚠️ Spec만** | 소셜 로그인 (카카오/네이버 OAuth2) | Spec 정의만 |
| | 가족 보드 활동 피드 | UI만 준비 |

---

## 참고 사항

1. **🔒 표시**: 인증 필요 (RequireAuth 보호 라우트)
2. **best-effort**: 실패해도 UX 차단하지 않음 (온보딩 관련)
3. **API 응답 형식**: `{ success: boolean, message: string, data?: T, timestamp: string }`
4. **인증 헤더**: `Authorization: Bearer {accessToken}`
5. **날짜 형식**: `YYYY-MM-DD` (ISO 8601)

---

## 업데이트 이력

- 2025-01-24: 전체 페이지별 API 연동 상태 분석 및 문서화

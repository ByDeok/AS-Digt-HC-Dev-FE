# FE-BE 통합 연동 계획서

## 1. 목적
프론트엔드(`studio/`)와 백엔드(루트 `src/`) 간 API 연동을 완성하고,
개발/테스트/배포 환경에서 일관된 계약(Contract)을 유지한다.

## 2. 기준(Contract of Truth)
- **Base URL**: `http://localhost:8080/api`
- **모든 API**: `/v1/*`로 통일
- **응답 형식**: `ApiResponse<T>` (`success`, `message`, `data`, `timestamp`)
- **인증**: `Authorization: Bearer {token}`
- **로깅**: `LOGGING_GUIDE.md`의 마스킹 규칙 준수

## 3. 연동 범위 요약
### 3.1 핵심 API 매핑
| 도메인 | FE 화면 | 주요 API |
|---|---|---|
| Auth | `/auth/login`, `/auth/signup` | `POST /api/v1/auth/login`, `POST /api/v1/auth/signup`, `POST /api/v1/auth/refresh` |
| Actions | `/(main)/dashboard` | `GET /api/v1/actions/today`, `POST /api/v1/actions/{id}/complete`, `POST /api/v1/actions/today/reset`(미구현) |
| Metrics | `/(main)/dashboard` | `GET /api/v1/metrics/daily`, `POST /api/v1/metrics/daily` |
| Reports | `/(main)/report` | `GET /api/v1/reports`, `POST /api/v1/reports/generate`, `DELETE /api/v1/reports/{id}` |
| Family | `/(main)/family` | `GET /api/v1/family-board`, `GET /api/v1/family-board/members`, `POST /api/v1/family-board/invite`, `POST /api/v1/family-board/accept` |
| Onboarding | `/onboarding/*` | `POST /api/v1/onboarding/start`, `POST /api/v1/onboarding/step`, `POST /api/v1/onboarding/complete` |
| Profile | `/onboarding/profile` | `PUT /api/v1/users/me` |
| Integration | `/onboarding/device` | `POST /api/v1/integration/devices` |

### 3.2 현재 제약/미구현
- `POST /api/v1/actions/today/reset` : 백엔드 미구현 (Mock 처리 중)
- 소셜 로그인(카카오/네이버) : OAuth2 Authorization Code + PKCE 기준으로 Spec만 정의, 구현 미완료
- 가족 보드 활동 피드 : UI 준비, API 미준비
- 온보딩 API : best-effort로 실패 시 UX 차단 금지

## 4. 단계별 실행 계획
### 4.1 환경 정합성
1) FE `.env.local`에 `VITE_API_URL=http://localhost:8080/api` 설정 (권장 포트: 8080)
2) BE `application.yml` 기준 context-path `/api` 유지
3) CORS 허용: `http://localhost:*`
4) 로컬 DB 기본은 H2 유지 (확장 시 MySQL로 전환)

### 4.2 인증/세션
1) 로그인/회원가입 성공 응답 저장 (access/refresh)
2) axios 인터셉터 기반 자동 갱신 (`/api/v1/auth/refresh`)
3) 401 처리: 1회 리프레시 실패 시 `/login` 이동

### 4.3 핵심 도메인 연동
1) 대시보드: Actions + Metrics
2) 리포트: 목록/생성/삭제
3) 가족 보드: 조회/초대/수락

### 4.4 온보딩 & 외부 연동
1) 온보딩 세션/스텝/완료 호출 (best-effort, `/api/v1/onboarding/*`)
2) 프로필 저장(`PUT /api/v1/users/me`)
3) 디바이스 연동(`POST /api/v1/integration/devices`)

### 4.5 소셜 로그인(추천 스펙)
1) `GET /api/v1/auth/oauth/{provider}/authorize`로 Authorization Code + PKCE 시작
2) `POST /api/v1/auth/oauth/{provider}/callback`에서 코드 교환 후 토큰 발급
3) 성공 시 FE는 토큰 저장 후 `/onboarding/profile` 이동

### 4.6 미구현 API 처리
- FE는 기능 토글/Mock 처리 유지
- BE 추가 구현 시 즉시 연동 전환

## 5. 테스트/검증
### 5.1 FE
- 요청/응답 로깅 활성화 (개발 환경만)
- TanStack Query 캐시/리트라이 검증

### 5.2 BE
- 컨트롤러 단위/통합 테스트: Auth, Onboarding, Integration 우선
- 로깅 마스킹 확인 (token/password 필드)

### 5.3 E2E
- 로그인 → 대시보드 데이터 로딩
- 리포트 생성/삭제
- 가족 초대/수락
- 온보딩 단계 진행 및 프로필 저장

## 6. 리스크 및 대응
- API prefix 혼선: 문서/환경변수 기준을 통일한다.
- 온보딩 best-effort: 실패 로그만 수집하고 UX 차단 금지.
- 미구현 API: FE는 Mock 유지, BE 완료 시 단계적 전환.

## 7. 산출물
- `BACKEND_FRONTEND_INTEGRATION_STATUS.md` 최신화
- `.cursor/rules/110-fe-be-integration-contract.mdc` 추가

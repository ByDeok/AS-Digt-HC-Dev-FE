# Google Analytics 4 테스트 가이드

## 개요

이 문서는 GA4 Analytics 통합 구현 후 테스트 절차를 설명합니다.

---

## 1. 프론트엔드 테스트

### 1.1 개발 환경 설정

1. `studio/.env.local` 파일 생성 또는 수정:

```properties
# GA4 측정 ID (Google Analytics에서 발급)
VITE_GA_MEASUREMENT_ID=G-XXXXXXXXXX

# 개발 환경에서 Analytics 활성화
VITE_ENABLE_ANALYTICS=true
```

2. 개발 서버 실행:

```bash
cd studio
npm run dev
```

### 1.2 콘솔 로그 확인

브라우저 개발자 도구(F12)를 열고 Console 탭에서 다음 로그를 확인합니다:

```
[Analytics] GA4 initialized with ID: G-XXXXXXXXXX
[Analytics] page_view: /
[Analytics Event] click_cta { cta_name: "무료로 시작하기", cta_location: "header" }
```

### 1.3 테스트 시나리오

| 시나리오 | 예상 이벤트 |
|---------|------------|
| 랜딩 페이지 방문 | `page_view` |
| CTA 버튼 클릭 | `click_cta` |
| 스크롤 (25%, 50%, 75%, 100%) | `scroll_depth` |
| 회원가입 페이지 진입 | `signup_start` |
| 회원가입 완료 | `signup_complete` |
| 로그인 성공 | `login_success` |
| 온보딩 단계 완료 | `onboarding_step_complete` |
| 미션 완료 | `mission_complete` |
| 건강 기록 저장 | `health_record_submit` |

### 1.4 GA4 DebugView 확인

1. [Google Analytics Debugger Chrome 확장](https://chrome.google.com/webstore/detail/google-analytics-debugger/jnkmfdileelhofjcijamephohjechhna) 설치
2. 확장 프로그램 활성화
3. GA4 속성 > DebugView에서 실시간 이벤트 확인

---

## 2. 백엔드 테스트

### 2.1 환경 설정

`.env.local` 파일에 다음 환경변수 추가:

```properties
GA4_ENABLED=true
GA4_MEASUREMENT_ID=G-XXXXXXXXXX
GA4_API_SECRET=your_api_secret_here
```

> **API Secret 발급 방법:**
> GA4 관리 > 데이터 스트림 > Measurement Protocol API secrets

### 2.2 서버 시작

```bash
./gradlew bootRun
```

### 2.3 로그 확인

애플리케이션 로그에서 다음 메시지 확인:

```
[GA4] Event sent successfully: 1 events
[Analytics] api_error: endpoint=/api/xxx, status=400
```

### 2.4 테스트 API 호출

```bash
# 로그인 테스트 (성공 시 login_success_server 이벤트)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

---

## 3. 통합 테스트 시나리오

### 시나리오 1: 신규 사용자 전환 퍼널

```
1. 랜딩 페이지 방문
   └── 예상 이벤트: page_view (/)

2. CTA "무료로 시작하기" 클릭
   └── 예상 이벤트: click_cta

3. 회원가입 페이지
   └── 예상 이벤트: page_view (/signup), signup_start

4. 회원가입 폼 제출
   └── 예상 이벤트: signup_complete, signup_complete_server (BE)

5. 온보딩 진행
   └── 예상 이벤트: onboarding_start, onboarding_step_complete (×4)

6. 온보딩 완료
   └── 예상 이벤트: onboarding_complete

7. 대시보드
   └── 예상 이벤트: page_view (/dashboard), mission_view
```

### 시나리오 2: 일일 사용자 참여

```
1. 대시보드 방문
   └── 예상 이벤트: page_view, mission_view

2. 미션 완료
   └── 예상 이벤트: mission_complete, confetti_trigger

3. 건강 기록 저장
   └── 예상 이벤트: health_record_submit, health_record_saved (BE)

4. 리포트 생성
   └── 예상 이벤트: report_generate_click, report_generate_success
```

---

## 4. 트러블슈팅

### 이벤트가 수신되지 않는 경우

1. **측정 ID 확인**: `G-` 접두사 포함 여부
2. **네트워크 탭**: `google-analytics.com/mp/collect` 요청 확인
3. **콘솔 에러**: `[Analytics]` 관련 에러 메시지 확인
4. **DebugView 지연**: 최대 5분 지연 가능

### 백엔드 이벤트가 전송되지 않는 경우

1. **환경변수 확인**: `GA4_ENABLED=true` 설정 여부
2. **API Secret**: 정확한 값 입력 여부
3. **네트워크**: 외부 네트워크 접근 가능 여부
4. **로그 레벨**: `logging.level.vibe.digthc=DEBUG` 설정

---

## 5. 체크리스트

### 프론트엔드

- [ ] `.env.local`에 `VITE_GA_MEASUREMENT_ID` 설정
- [ ] 콘솔에서 `[Analytics] GA4 initialized` 확인
- [ ] 주요 이벤트 콘솔 로그 확인
- [ ] GA4 DebugView에서 이벤트 수신 확인

### 백엔드

- [ ] `.env.local`에 GA4 환경변수 설정
- [ ] 애플리케이션 로그에서 `[GA4]` 메시지 확인
- [ ] API 호출 시 서버 사이드 이벤트 전송 확인

### 통합

- [ ] 전환 퍼널 전체 시나리오 테스트
- [ ] GA4 리포트에서 이벤트 데이터 확인

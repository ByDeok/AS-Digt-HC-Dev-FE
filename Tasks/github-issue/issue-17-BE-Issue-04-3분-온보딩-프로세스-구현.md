# [BE] Issue-04: 3분 온보딩 프로세스 구현

## 1. 개요
**SRS 3.4.1 핵심 온보딩 플로우** 및 **REQ-FUNC-005, 006**에 따라 단계별 온보딩 상태 관리 및 완료 처리를 구현합니다.

## 2. 작업 워크플로우 (설계 및 구현)

| 단계 | 입력(Input) | 도구(Tool) | 출력(Output) |
| --- | --- | --- | --- |
| **Plan** | REQ-FUNC-005~006 | Cursor | 온보딩 상태머신 다이어그램 |
| **Data Schema Design** | OnboardingSession | Mermaid.js | ERD 확인 |
| **Interaction Design** | State Flow | Cursor | **Onboarding Service & Controller** |
| **Review** | API Test | Postman/TestCode | 단계별 상태 전이 검증 |

## 3. 상세 요구사항 (To-Do)

- [x] **Domain: OnboardingSession**
    - `OnboardingSession` Entity 구현 (Current Step, Status 관리)
    - 세션 생성/조회 로직
- [x] **Step Management API**
    - `/api/onboarding/start`: 세션 시작
    - `/api/onboarding/step`: 단계별 데이터 저장 및 진행률(ETA) 계산 반환
    - `/api/onboarding/complete`: 최종 완료 처리 및 User 활성화
- [x] **Exception Handling**
    - 병원 포털 미지원 지역 예외 처리 로직 (REQ-FUNC-019)
    - 이탈 후 재진입 시 마지막 단계 복원 로직

## 4. 참고 자료
- SRS 3.4.1 핵심 온보딩 플로우
- SRS 6.2.2 OnboardingSession
- REQ-NF-003 (p50 시간 측정용 로그 적재 고려)

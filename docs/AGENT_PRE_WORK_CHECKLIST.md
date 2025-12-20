# Agent 작업 수행 전 체크리스트 가이드

## 개요

이 문서는 AI Agent가 작업을 시작하기 전에 반드시 확인하고 준수해야 하는 체크리스트를 정의합니다.  
체크리스트를 엄수함으로써 작업의 품질, 효율성, 추적성을 보장하고 중복 작업을 방지합니다.

---

## 체크리스트 항목

### ✅ 체크리스트 1: Agent 컨텍스트 설정 (충분성과 최소성)

Agent가 작업을 시작하기 전에 다음을 확인하고 설정해야 합니다:

#### 1.1 충분한 컨텍스트 확보

- [ ] **관련 규칙 파일 확인**
  - `.cursor/rules/` 디렉토리에서 작업과 관련된 규칙 파일 확인
  - 특히 도메인별 규칙(예: `301-gradle-groovy-rules.mdc`, `302-jpa-querydsl-dynamic-query-rules.mdc`) 확인
  - 프로젝트 메타 규칙(`000-meta-rule-script.mdc`) 확인

- [ ] **관련 문서 확인**
  - `docs/` 디렉토리에서 관련 문서 확인
  - 특히 `BE_ISSUE_DAG.md` (의존성 그래프), `SRS_V0.3.md` (요구사항), `MVP_SCOPE_AND_MAPPING.md` 확인
  - 작업 대상 이슈의 상세 명세서 확인 (`Tasks/BE_issue/issue-XX-*.md`)

- [ ] **기존 코드베이스 분석**
  - 작업 대상 도메인/모듈의 기존 코드 구조 파악
  - 관련 Entity, Service, Controller, Repository 클래스 확인
  - 기존 패턴 및 컨벤션 파악

#### 1.2 중복 방지 (최소성)

- [ ] **중복 작업 확인**
  - GitHub Issues에서 동일하거나 유사한 작업이 진행 중인지 확인
  - `Tasks/github-complete/` 디렉토리에서 완료된 유사 작업 확인
  - 코드베이스에서 이미 구현된 기능이 있는지 확인

- [ ] **컨텍스트 최소화**
  - 작업에 직접적으로 필요한 컨텍스트만 포함
  - 불필요한 규칙 파일이나 문서는 제외
  - 작업 범위를 명확히 정의하고 범위 밖 작업은 제외

#### 1.3 컨텍스트 설정 체크리스트

```
작업 시작 전 확인 사항:
□ 작업 대상 이슈 번호 및 제목 확인
□ 관련 규칙 파일 목록 작성 (최소 3개 이상)
□ 관련 문서 목록 작성 (최소 2개 이상)
□ 기존 코드베이스 분석 완료 여부 확인
□ 중복 작업 여부 확인 완료
```

---

### ✅ 체크리스트 2: DAG 의존성 그래프 기반 작업 수행

Agent는 작업을 시작하기 전에 반드시 DAG 의존성 그래프를 확인하고 준수해야 합니다.

#### 2.1 DAG 문서 확인

- [ ] **의존성 그래프 확인**
  - `docs/BE_ISSUE_DAG.md` 파일 확인
  - 현재 작업 이슈의 Phase 및 의존성 파악
  - 선행 이슈(Predecessors) 및 후행 이슈(Successors) 확인

- [ ] **선행 이슈 완료 확인**
  - 의존성 그래프에서 선행 이슈가 완료되었는지 확인
  - `Tasks/github-complete/` 디렉토리에서 선행 이슈 완료 여부 확인
  - 완료되지 않은 경우, 선행 이슈 우선 처리 또는 대기

#### 2.2 병렬 실행 가능 여부 확인

- [ ] **병렬 실행 가능 여부 확인**
  - Phase 4의 이슈들(BE-ISSUE-04, 06, 07, 08)은 서로 독립적이므로 병렬 실행 가능
  - 동일한 리소스(예: 동일한 Entity 클래스)를 수정하는 경우 충돌 주의
  - 공통 파일(`application.yml`, `build.gradle`) 수정 시 충돌 가능성 확인

- [ ] **Critical Path 우선순위 확인**
  - BE-ISSUE-01 → BE-ISSUE-02 → BE-ISSUE-03은 Critical Path
  - 다른 모든 이슈의 기반이 되므로 최우선 처리 필요

#### 2.3 DAG 기반 작업 체크리스트

```
DAG 기반 작업 전 확인 사항:
□ docs/BE_ISSUE_DAG.md 파일 확인 완료
□ 현재 이슈의 Phase 및 의존성 파악 완료
□ 선행 이슈 완료 여부 확인 완료
□ 병렬 실행 가능 여부 확인 완료
□ 공통 리소스 충돌 가능성 확인 완료
□ 작업 우선순위 결정 완료
```

---

### ✅ 체크리스트 3: NF-REQ 및 Traceability 추적성 보강

Agent는 작업을 시작하기 전에 NF-REQ 및 Traceability 문서를 확인하고, Issue에 간접적인 추적성을 보강해야 합니다.

#### 3.1 NF-REQ (Non-Functional Requirements) 확인

- [ ] **SRS 문서에서 NF-REQ 확인**
  - `docs/SRS/SRS_V0.3.md`의 4.2 Non-Functional Requirements 섹션 확인
  - 작업과 관련된 REQ-NF-XXX 항목 식별
  - 성능, 보안, 가용성, 확장성 등 비기능 요구사항 파악

- [ ] **주요 NF-REQ 항목**
  - **REQ-NF-001**: 성능 - 앱 초기 로드 p95 ≤ 1.5초, 주요 화면 전환 p95 ≤ 800ms
  - **REQ-NF-002**: 성능(Reports) - 1장 리포트 생성 p95 ≤ 3초, PDF 렌더링 p95 ≤ 2초
  - **REQ-NF-004**: 가용성 - 월 가용성 ≥ 99.5%, 백엔드 오류율 < 0.5%
  - **REQ-NF-005**: 동기화 - 가족 보드 및 알림 관련 데이터 동기화 지연 p95 ≤ 60초
  - **REQ-NF-006**: 보안 - TLS1.2+ 및 AES-256 암호화, 동의/위임/감사 로그 전 항목 기록
  - **REQ-NF-007**: 보안(Auth) - 위험 기반 인증, 고위험 접근 시 2FA 요구
  - **REQ-NF-010**: 모니터링 - 실시간 대시보드, 임계치 초과 시 5분 내 온콜 알림

#### 3.2 Traceability (추적성) 확인

- [ ] **요구사항 추적성 확인**
  - 작업 이슈가 어떤 기능 요구사항(REQ-FUNC-XXX)과 연결되는지 확인
  - PRD의 기능 요구사항(F1~F10)과의 매핑 확인
  - User Story와의 연결 확인

- [ ] **간접적인 추적성 보강**
  - Issue 문서에 관련 NF-REQ 항목 명시
  - Issue 문서에 관련 기능 요구사항(REQ-FUNC-XXX) 명시
  - Issue 문서에 관련 User Story 및 PRD 섹션 참조 추가

#### 3.3 Issue 문서 보강 가이드

작업 이슈 문서에 다음 정보를 추가해야 합니다:

```markdown
## 요구사항 추적성 (Traceability)

### 기능 요구사항 (Functional Requirements)
- REQ-FUNC-XXX: [요구사항 제목]
- REQ-FUNC-YYY: [요구사항 제목]

### 비기능 요구사항 (Non-Functional Requirements)
- REQ-NF-XXX: [요구사항 제목]
- REQ-NF-YYY: [요구사항 제목]

### 관련 User Story
- Story X: [스토리 제목]

### 관련 PRD 섹션
- PRD 4. 기능 요구사항: F1, F2, ...
- PRD 5. 비기능 요구사항: NFR 섹션

### 관련 SRS 섹션
- SRS 4.1 Functional Requirements: REQ-FUNC-XXX
- SRS 4.2 Non-Functional Requirements: REQ-NF-XXX
```

#### 3.4 NF-REQ 및 Traceability 체크리스트

```
NF-REQ 및 Traceability 확인 사항:
□ docs/SRS/SRS_V0.3.md의 NF-REQ 섹션 확인 완료
□ 작업과 관련된 REQ-NF-XXX 항목 식별 완료
□ 작업과 관련된 REQ-FUNC-XXX 항목 식별 완료
□ PRD의 기능 요구사항(F1~F10)과의 매핑 확인 완료
□ Issue 문서에 추적성 정보 추가 완료
```

---

## 통합 체크리스트 (작업 시작 전 필수 확인)

작업을 시작하기 전에 다음 모든 항목을 확인해야 합니다:

### 📋 작업 전 필수 체크리스트

```
□ [체크리스트 1] Agent 컨텍스트 설정 완료
  □ 관련 규칙 파일 확인 완료
  □ 관련 문서 확인 완료
  □ 기존 코드베이스 분석 완료
  □ 중복 작업 확인 완료

□ [체크리스트 2] DAG 의존성 그래프 확인 완료
  □ docs/BE_ISSUE_DAG.md 확인 완료
  □ 선행 이슈 완료 여부 확인 완료
  □ 병렬 실행 가능 여부 확인 완료
  □ 공통 리소스 충돌 가능성 확인 완료

□ [체크리스트 3] NF-REQ 및 Traceability 확인 완료
  □ SRS 문서의 NF-REQ 확인 완료
  □ 관련 기능 요구사항 확인 완료
  □ Issue 문서에 추적성 정보 추가 완료
```

---

## 작업 시작 프로세스

### Step 1: 컨텍스트 수집
1. 작업 대상 이슈 확인
2. 관련 규칙 파일 및 문서 목록 작성
3. 기존 코드베이스 분석

### Step 2: 의존성 확인
1. `docs/BE_ISSUE_DAG.md` 확인
2. 선행 이슈 완료 여부 확인
3. 병렬 실행 가능 여부 확인

### Step 3: 요구사항 추적성 확인
1. SRS 문서에서 관련 NF-REQ 확인
2. 관련 기능 요구사항 확인
3. Issue 문서에 추적성 정보 추가

### Step 4: 작업 시작
1. 모든 체크리스트 항목 완료 확인
2. 작업 계획 수립
3. 작업 시작

---

## 참고 자료

### 관련 문서
- [BE_ISSUE_DAG.md](./BE_ISSUE_DAG.md) - BE 이슈 의존성 그래프
- [SRS_V0.3.md](./SRS/SRS_V0.3.md) - 소프트웨어 요구사항 명세서
- [MVP_SCOPE_AND_MAPPING.md](./MVP_SCOPE_AND_MAPPING.md) - MVP 범위 및 매핑

### 관련 이슈
- Issue 24: Agent 컨텍스트 설정의 충분성과 최소성
- Issue 25: Agent 병렬 실행 구조를 DAG 의존성 그래프로 먼저 표현
- Issue 26: Traceability를 작업전에 완전히 구조화하면 더 좋겠음

### 관련 규칙
- `.cursor/rules/000-meta-rule-script.mdc` - Universal project principles
- `.cursor/rules/202-Issue-workflow.mdc` - Issue workflow standard
- `.cursor/rules/203-github-issue-handling.mdc` - GitHub issue handling

---

## 업데이트 이력

- **2025-01-15**: 초기 체크리스트 가이드 작성
  - 체크리스트 1: Agent 컨텍스트 설정 (Issue 24 기반)
  - 체크리스트 2: DAG 의존성 그래프 기반 작업 수행 (Issue 25 기반)
  - 체크리스트 3: NF-REQ 및 Traceability 추적성 보강 (Issue 26 기반)


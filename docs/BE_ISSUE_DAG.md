# AS-Digt-HC Backend Issue Dependency Graph (DAG)

## 개요

이 문서는 AS-Digt-HC Backend 프로젝트의 BE 이슈들 간의 의존성을 DAG(Directed Acyclic Graph) 형태로 표현하여, Agent가 병렬 실행 구조를 이해하고 효율적으로 작업을 수행할 수 있도록 합니다.

---

## 1. Work Breakdown Structure (WBS)

### **EPIC 1: 인프라 및 기반 구축**
- **Feature: 프로젝트 초기 설정**
  - `BE-ISSUE-01`: Spring Boot 프로젝트 초기 설정 및 환경 구축
- **Feature: 데이터 모델 설계**
  - `BE-ISSUE-02`: 통합 데이터 모델링 (ERD) 설계

### **EPIC 2: 핵심 인증 및 사용자 관리**
- **Feature: 인증 시스템**
  - `BE-ISSUE-03`: 사용자 인증(Auth) 및 회원 도메인 구현

### **EPIC 3: 사용자 온보딩 및 외부 연동**
- **Feature: 온보딩 프로세스**
  - `BE-ISSUE-04`: 3분 온보딩 프로세스 구현
- **Feature: 외부 시스템 연동**
  - `BE-ISSUE-08`: 외부 연동(Device & Portal) 및 동의 관리

### **EPIC 4: 핵심 비즈니스 도메인**
- **Feature: 건강 리포트**
  - `BE-ISSUE-05`: 1장 요약 리포트(Health Report) 도메인 구현
- **Feature: 행동 카드 및 코칭**
  - `BE-ISSUE-06`: 행동 카드(Action Card) 및 코칭 도메인 구현
- **Feature: 가족 보드**
  - `BE-ISSUE-07`: 가족 보드(Family Board) 및 권한 관리 구현

---

## 2. Dependency Graph (DAG)

### **전체 의존성 그래프**

```mermaid
graph TD
    %% Phase 1: 기반 구축
    BE01[BE-ISSUE-01<br/>Spring Boot 초기 설정]
    
    %% Phase 2: 데이터 모델 설계
    BE02[BE-ISSUE-02<br/>통합 데이터 모델링 ERD]
    BE01 --> BE02
    
    %% Phase 3: 인증 시스템
    BE03[BE-ISSUE-03<br/>사용자 인증 및 회원 도메인]
    BE01 --> BE03
    BE02 --> BE03
    
    %% Phase 4: 병렬 실행 가능한 도메인들
    BE04[BE-ISSUE-04<br/>3분 온보딩 프로세스]
    BE06[BE-ISSUE-06<br/>행동 카드 및 코칭]
    BE07[BE-ISSUE-07<br/>가족 보드 및 권한 관리]
    BE08[BE-ISSUE-08<br/>외부 연동 및 동의 관리]
    
    BE03 --> BE04
    BE03 --> BE06
    BE03 --> BE07
    BE03 --> BE08
    
    %% Phase 5: 리포트 도메인 (외부 데이터 의존)
    BE05[BE-ISSUE-05<br/>1장 요약 리포트 도메인]
    BE03 --> BE05
    BE08 --> BE05
    
    %% 스타일링
    classDef phase1 fill:#e1f5ff,stroke:#01579b,stroke-width:2px
    classDef phase2 fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef phase3 fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef phase4 fill:#e8f5e9,stroke:#1b5e20,stroke-width:2px
    classDef phase5 fill:#fce4ec,stroke:#880e4f,stroke-width:2px
    
    class BE01 phase1
    class BE02 phase2
    class BE03 phase3
    class BE04,BE06,BE07,BE08 phase4
    class BE05 phase5
```

### **단계별 실행 전략**

#### **Phase 1: 기반 구축 (순차 실행)**
```mermaid
graph LR
    BE01[BE-ISSUE-01<br/>Spring Boot 초기 설정]
    
    style BE01 fill:#e1f5ff,stroke:#01579b,stroke-width:2px
```

**설명**: 
- 모든 이슈의 기반이 되는 프로젝트 초기 설정
- 단독 실행, 다른 이슈와 병렬 불가

---

#### **Phase 2: 데이터 모델 설계 (순차 실행)**
```mermaid
graph LR
    BE01[BE-ISSUE-01] --> BE02[BE-ISSUE-02<br/>통합 데이터 모델링 ERD]
    
    style BE01 fill:#e1f5ff,stroke:#01579b,stroke-width:2px
    style BE02 fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
```

**설명**:
- BE-ISSUE-01 완료 후 실행 가능
- 모든 도메인 엔티티 설계의 기반

---

#### **Phase 3: 인증 시스템 (순차 실행)**
```mermaid
graph LR
    BE01[BE-ISSUE-01] --> BE03[BE-ISSUE-03<br/>사용자 인증 및 회원]
    BE02[BE-ISSUE-02] --> BE03
    
    style BE01 fill:#e1f5ff,stroke:#01579b,stroke-width:2px
    style BE02 fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    style BE03 fill:#fff3e0,stroke:#e65100,stroke-width:2px
```

**설명**:
- BE-ISSUE-01, BE-ISSUE-02 완료 후 실행 가능
- 다른 모든 도메인 이슈의 인증 기반 제공

---

#### **Phase 4: 병렬 실행 가능한 도메인들 (병렬 실행)**
```mermaid
graph TD
    BE03[BE-ISSUE-03<br/>사용자 인증] --> BE04[BE-ISSUE-04<br/>온보딩]
    BE03 --> BE06[BE-ISSUE-06<br/>행동 카드]
    BE03 --> BE07[BE-ISSUE-07<br/>가족 보드]
    BE03 --> BE08[BE-ISSUE-08<br/>외부 연동]
    
    style BE03 fill:#fff3e0,stroke:#e65100,stroke-width:2px
    style BE04 fill:#e8f5e9,stroke:#1b5e20,stroke-width:2px
    style BE06 fill:#e8f5e9,stroke:#1b5e20,stroke-width:2px
    style BE07 fill:#e8f5e9,stroke:#1b5e20,stroke-width:2px
    style BE08 fill:#e8f5e9,stroke:#1b5e20,stroke-width:2px
```

**설명**:
- BE-ISSUE-03 완료 후 **병렬 실행 가능**
- 각 도메인은 서로 독립적이며 동시에 개발 가능
- **병렬 실행 권장**: 생산성 극대화

---

#### **Phase 5: 리포트 도메인 (순차 실행)**
```mermaid
graph LR
    BE03[BE-ISSUE-03] --> BE05[BE-ISSUE-05<br/>1장 요약 리포트]
    BE08[BE-ISSUE-08] --> BE05
    
    style BE03 fill:#fff3e0,stroke:#e65100,stroke-width:2px
    style BE08 fill:#e8f5e9,stroke:#1b5e20,stroke-width:2px
    style BE05 fill:#fce4ec,stroke:#880e4f,stroke-width:2px
```

**설명**:
- BE-ISSUE-03, BE-ISSUE-08 완료 후 실행 가능
- 외부 연동 데이터(Device/Portal)를 활용하여 리포트 생성

---

## 3. 병렬 실행 전략

### **최적 병렬 실행 시나리오**

#### **시나리오 1: 최대 병렬화 (권장)**
```
Phase 1: [BE-ISSUE-01] (단독)
    ↓
Phase 2: [BE-ISSUE-02] (단독)
    ↓
Phase 3: [BE-ISSUE-03] (단독)
    ↓
Phase 4: [BE-ISSUE-04] [BE-ISSUE-06] [BE-ISSUE-07] [BE-ISSUE-08] (병렬 실행)
    ↓
Phase 5: [BE-ISSUE-05] (단독)
```

**예상 시간 단축**: 
- 순차 실행: 8단계
- 병렬 실행: 5단계 (Phase 4에서 4개 이슈 병렬)

---

#### **시나리오 2: 부분 병렬화**
```
Phase 1: [BE-ISSUE-01]
    ↓
Phase 2: [BE-ISSUE-02]
    ↓
Phase 3: [BE-ISSUE-03]
    ↓
Phase 4a: [BE-ISSUE-04] [BE-ISSUE-06] (병렬)
    ↓
Phase 4b: [BE-ISSUE-07] [BE-ISSUE-08] (병렬)
    ↓
Phase 5: [BE-ISSUE-05]
```

---

## 4. 의존성 상세 매트릭스

| 이슈 ID | 이슈명 | 선행 이슈 | 후행 이슈 | 병렬 가능 여부 |
|:---:|:---|:---|:---|:---:|
| BE-ISSUE-01 | Spring Boot 초기 설정 | 없음 | BE-ISSUE-02, BE-ISSUE-03 | ❌ |
| BE-ISSUE-02 | 통합 데이터 모델링 ERD | BE-ISSUE-01 | BE-ISSUE-03 | ❌ |
| BE-ISSUE-03 | 사용자 인증 및 회원 | BE-ISSUE-01, BE-ISSUE-02 | BE-ISSUE-04, BE-ISSUE-05, BE-ISSUE-06, BE-ISSUE-07, BE-ISSUE-08 | ❌ |
| BE-ISSUE-04 | 3분 온보딩 프로세스 | BE-ISSUE-03 | 없음 | ✅ (BE-ISSUE-06, 07, 08과 병렬) |
| BE-ISSUE-05 | 1장 요약 리포트 | BE-ISSUE-03, BE-ISSUE-08 | 없음 | ❌ |
| BE-ISSUE-06 | 행동 카드 및 코칭 | BE-ISSUE-03 | 없음 | ✅ (BE-ISSUE-04, 07, 08과 병렬) |
| BE-ISSUE-07 | 가족 보드 및 권한 관리 | BE-ISSUE-03 | 없음 | ✅ (BE-ISSUE-04, 06, 08과 병렬) |
| BE-ISSUE-08 | 외부 연동 및 동의 관리 | BE-ISSUE-03 | BE-ISSUE-05 | ✅ (BE-ISSUE-04, 06, 07과 병렬) |

---

## 5. Agent 작업 가이드

### **5.1 병렬 실행 체크리스트**

Agent가 작업을 시작하기 전에 다음을 확인해야 합니다:

- [ ] **선행 이슈 완료 확인**
  - 의존성 그래프에서 선행 이슈가 완료되었는지 확인
  - 완료되지 않은 경우 대기 또는 선행 이슈 우선 처리

- [ ] **병렬 실행 가능 여부 확인**
  - Phase 4의 이슈들(BE-ISSUE-04, 06, 07, 08)은 서로 독립적이므로 병렬 실행 가능
  - 동일한 리소스(예: 동일한 Entity 클래스)를 수정하는 경우 충돌 주의

- [ ] **공통 리소스 충돌 방지**
  - 여러 Agent가 동시에 작업할 경우, 공통 파일(예: `application.yml`, `build.gradle`) 수정 시 충돌 가능
  - 가능한 경우 도메인별로 분리된 파일 구조 사용

---

### **5.2 작업 우선순위 가이드**

#### **높은 우선순위 (Critical Path)**
1. BE-ISSUE-01 → BE-ISSUE-02 → BE-ISSUE-03
   - 다른 모든 이슈의 기반이 되는 Critical Path
   - 최우선 처리 필요

#### **중간 우선순위 (병렬 실행 권장)**
2. BE-ISSUE-04, BE-ISSUE-06, BE-ISSUE-07, BE-ISSUE-08
   - BE-ISSUE-03 완료 후 병렬 실행 가능
   - 생산성 극대화를 위해 동시 진행 권장

#### **낮은 우선순위 (후순위)**
3. BE-ISSUE-05
   - BE-ISSUE-08 완료 후 실행 가능
   - 외부 연동 데이터가 필요한 리포트 도메인

---

## 6. 실행 시나리오 예시

### **시나리오 A: 순차 실행 (비권장)**
```
시간축: |--01--|--02--|--03--|--04--|--06--|--07--|--08--|--05--|
총 소요 시간: 8단계
```

### **시나리오 B: 최대 병렬화 (권장)**
```
시간축: |--01--|--02--|--03--|--04,06,07,08--|--05--|
총 소요 시간: 5단계 (약 37.5% 시간 단축)
```

---

## 7. 참고 자료

- [BE] Issue-01: Spring Boot 프로젝트 초기 설정 (`Tasks/BE_issue/issue-01-be-setup.md`)
- [BE] Issue-02: 통합 데이터 모델링 (`Tasks/BE_issue/issue-02-db-design.md`)
- [BE] Issue-03: 사용자 인증 및 회원 (`Tasks/BE_issue/issue-03-auth-user.md`)
- [BE] Issue-04: 3분 온보딩 프로세스 (`Tasks/BE_issue/issue-04-onboarding.md`)
- [BE] Issue-05: 1장 요약 리포트 (`Tasks/BE_issue/issue-05-report-domain.md`)
- [BE] Issue-06: 행동 카드 및 코칭 (`Tasks/BE_issue/issue-06-action-domain.md`)
- [BE] Issue-07: 가족 보드 및 권한 관리 (`Tasks/BE_issue/issue-07-family-board.md`)
- [BE] Issue-08: 외부 연동 및 동의 관리 (`Tasks/BE_issue/issue-08-integration.md`)

---

## 8. 업데이트 이력

- **2025-01-15**: 초기 DAG 그래프 작성
  - BE 이슈 8개 의존성 분석 완료
  - 병렬 실행 전략 수립


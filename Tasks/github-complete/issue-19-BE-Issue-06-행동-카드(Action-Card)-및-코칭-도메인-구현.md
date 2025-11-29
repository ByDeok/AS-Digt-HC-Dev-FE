# [BE] Issue-06: 행동 카드(Action Card) 및 코칭 도메인 구현

## 1. 개요
**SRS REQ-FUNC-011~014**에 따라 매일 1~3개의 행동 카드를 생성하고 수행 결과를 추적하는 로직을 구현합니다.

## 2. 작업 워크플로우 (설계 및 구현)

| 단계 | 입력(Input) | 도구(Tool) | 출력(Output) |
| --- | --- | --- | --- |
| **Plan** | REQ-FUNC-011~014 | Cursor | 행동 추천 룰(Rule) 정의 |
| **Data Schema Design** | ActionCard | Mermaid.js | ERD 확인 |
| **Interaction Design** | Scheduler | Cursor | **Action Service & Scheduler** |
| **Review** | Batch Test | IDE Console | 일일 카드 생성 배치 확인 |

## 3. 상세 요구사항 (To-Do)

- [ ] **Domain: ActionCard**
    - `ActionCard` Entity 구현 (Status: PENDING, COMPLETED, SKIPPED)
    - 날짜별, 사용자별 인덱싱 고려
- [ ] **Daily Generation Logic**
    - Spring Scheduler를 이용한 일일 배치 (자정 or 새벽) 구현
    - 간단한 룰 기반(Rule-based) 카드 생성 로직 (예: 어제 걷기 부족 -> 오늘 걷기 카드)
- [ ] **Action API**
    - `/api/actions/today`: 오늘의 카드 목록 조회
    - `/api/actions/{id}/complete`: 수행 완료 처리
- [ ] **Statistics**
    - D1, W1 완료율 계산 로직 (간단한 통계 쿼리)

## 4. 참고 자료
- SRS 6.2.4 ActionCard
- SRS 3.4.2 (유사 흐름 참고)


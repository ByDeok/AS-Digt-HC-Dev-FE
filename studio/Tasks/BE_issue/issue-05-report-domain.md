# [BE] Issue-05: 1장 요약 리포트(Health Report) 도메인 구현

## 1. 개요
**SRS REQ-FUNC-007~010**에 따라 건강 데이터를 집계하여 표준화된 리포트를 생성하고 조회하는 기능을 구현합니다.

## 2. 작업 워크플로우 (설계 및 구현)

| 단계 | 입력(Input) | 도구(Tool) | 출력(Output) |
| --- | --- | --- | --- |
| **Plan** | REQ-FUNC-007~010 | Cursor | 리포트 JSON 스키마 정의 |
| **Data Schema Design** | HealthReport | Mermaid.js | JSON Column 구조 설계 |
| **Interaction Design** | Aggregation Logic | Cursor | **Report Service & Entity** |
| **Review** | Mock Data | JUnit | 데이터 집계 정확성 검증 |

## 3. 상세 요구사항 (To-Do)

- [ ] **Domain: HealthReport**
    - `HealthReport` Entity 구현
    - `metrics` (JSON) 구조 정의: 활동, 심박, 혈압, 체중 표준 포맷
    - `context` (JSON) 구조 정의: 측정 기기, 결측 여부 메타데이터
- [ ] **Aggregation Service**
    - (Mock) Device/Portal 데이터 수집 및 집계 로직
    - 리포트 생성 로직 (최근 3~6개월 데이터 기준)
- [ ] **Report API**
    - `/api/reports/generate`: 리포트 생성 요청
    - `/api/reports/{id}`: 리포트 상세 조회
- [ ] **Output Support**
    - PDF 생성 라이브러리(IText, Thymeleaf 등) 연동 검토 및 구현 (Optional for MVP start)

## 4. 참고 자료
- SRS 3.4.2 진료 전 1장 리포트 생성 및 사용
- SRS 6.2.3 HealthReport


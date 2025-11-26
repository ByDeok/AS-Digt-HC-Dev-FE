# [BE] Issue-02: 통합 데이터 모델링 (ERD) 설계

## 1. 개요
**SRS 6.2 Entity & Data Model**을 기반으로 전체 시스템의 데이터 모델(ERD)을 설계하고 JPA Entity로 매핑할 준비를 합니다.

## 2. 작업 워크플로우 (설계 및 구현)

| 단계 | 입력(Input) | 도구(Tool) | 출력(Output) |
| --- | --- | --- | --- |
| **Plan** | SRS 6.2 Data Model | Cursor | 도메인별 엔티티 목록 정리 |
| **Data Schema Design** | 엔티티 목록 | Mermaid.js | **전체 ERD (시각화)** |
| **Interaction Design** | Mermaid 코드 | Cursor | **Base Entity 클래스 설계** |
| **Review** | ERD 다이어그램 | Team Review | 데이터 정합성 검증 |

## 3. 상세 요구사항 (To-Do)

- [ ] **Mermaid ERD 작성**
    - SRS 6.2.1 ~ 6.2.9 참조하여 Mermaid 문법으로 ERD 작성
    - 포함 도메인: User, Profile, Onboarding, Report, Action, FamilyBoard, Consent, AuditLog, DeviceLink
    - 관계(Relation) 정의 (1:1, 1:N, N:M)
- [ ] **논리적 설계 검증**
    - 정규화 위배 사항 점검
    - 성능 고려한 인덱스 키 후보 선정
    - JSON 타입 컬럼(`metrics`, `context`, `metadata`) 정의
- [ ] **Audit(감사) 필드 설계**
    - `BaseTimeEntity` (createdAt, updatedAt) 설계
    - `BaseUserEntity` (createdBy, updatedBy) 설계 (Optional)

## 4. 참고 자료
- SRS 6.2 Entity & Data Model
- `studio/docs/SRS/SRS_V0.3.md`


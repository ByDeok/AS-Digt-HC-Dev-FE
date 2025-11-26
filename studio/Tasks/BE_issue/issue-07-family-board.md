# [BE] Issue-07: 가족 보드(Family Board) 및 권한 관리 구현

## 1. 개요
**SRS REQ-FUNC-015~018**에 따라 시니어와 보호자 간의 데이터 공유, 역할 위임, 초대 기능을 구현합니다.

## 2. 작업 워크플로우 (설계 및 구현)

| 단계 | 입력(Input) | 도구(Tool) | 출력(Output) |
| --- | --- | --- | --- |
| **Plan** | REQ-FUNC-015~018 | Cursor | 초대/수락 시퀀스 정의 |
| **Data Schema Design** | FamilyBoard, Member | Mermaid.js | N:M 관계 테이블 설계 |
| **Interaction Design** | Permission Check | Cursor | **Board Service & Security Logic** |
| **Review** | Scenario Test | Postman | 초대~수락~접근 테스트 |

## 3. 상세 요구사항 (To-Do)

- [ ] **Domain: FamilyBoard**
    - `FamilyBoard` Entity 및 `AccessRole` (User-Board 매핑) 구현
    - 역할 정의: `VIEWER`, `EDITOR`, `ADMIN`
- [ ] **Invitation Logic**
    - 초대 코드 생성 및 검증 로직
    - `/api/family-board/invite`: 초대 발송
    - `/api/family-board/accept`: 초대 수락 및 역할 부여
- [ ] **Data Synchronization (Basic)**
    - 보드 내 데이터(일정, 알림 등) 변경 시 갱신 로직
    - (MVP) Polling 대응을 위한 `lastUpdatedAt` 타임스탬프 관리

## 4. 참고 자료
- SRS 6.2.5 FamilyBoard & AccessRole
- SRS 3.4.1 (연관 흐름)


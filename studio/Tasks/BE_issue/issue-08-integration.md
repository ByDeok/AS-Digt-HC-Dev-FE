# [BE] Issue-08: 외부 연동 (Device & Portal) 및 동의 관리

## 1. 개요
**SRS REQ-FUNC-003, 004** 및 **동의(Consent)** 관리를 위해 외부 시스템과의 인터페이스 및 데이터 연동 구조를 구현합니다. MVP 단계에서는 Mocking 또는 인터페이스 위주로 구현합니다.

## 2. 작업 워크플로우 (설계 및 구현)

| 단계 | 입력(Input) | 도구(Tool) | 출력(Output) |
| --- | --- | --- | --- |
| **Plan** | REQ-FUNC-003, 004 | Cursor | 외부 시스템 인터페이스 정의 |
| **Data Schema Design** | DeviceLink, PortalConn | Mermaid.js | 연동 테이블 설계 |
| **Interaction Design** | OAuth/Mock | Cursor | **Integration Service** |
| **Review** | Integration Test | JUnit | Mock 데이터 연동 확인 |

## 3. 상세 요구사항 (To-Do)

- [ ] **Domain: Consent & Link**
    - `ConsentRecord` Entity 구현 (동의 이력 관리)
    - `DeviceLink`, `PortalConnection` Entity 구현 (토큰 및 상태 관리)
- [ ] **Interface Definition**
    - `DeviceDataProvider` 인터페이스 정의 (getHealthData)
    - `PortalDataProvider` 인터페이스 정의 (getMedicalRecords)
- [ ] **Mock Implementation**
    - 테스트용 Mock Provider 구현 (랜덤 데이터 반환)
- [ ] **Sync API**
    - 연동 상태 조회 및 수동 동기화 트리거 API

## 4. 참고 자료
- SRS 6.2.6 ~ 6.2.9 (Consent, DeviceLink, PortalConnection)
- SRS 3.4.3 병원 포털/디바이스 연동 상태 동기화


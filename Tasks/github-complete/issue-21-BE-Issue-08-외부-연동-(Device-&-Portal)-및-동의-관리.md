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

- [x] **Domain: Consent & Link**
    - `ConsentRecord` Entity 구현 (동의 이력 관리)
    - `DeviceLink`, `PortalConnection` Entity 구현 (토큰 및 상태 관리)
- [x] **Interface Definition**
    - `DeviceDataProvider` 인터페이스 정의 (getHealthData)
    - `PortalDataProvider` 인터페이스 정의 (getMedicalRecords)
- [x] **Mock Implementation**
    - 테스트용 Mock Provider 구현 (랜덤 데이터 반환)
- [x] **Sync API**
    - 연동 상태 조회 및 수동 동기화 트리거 API

## 5. 구현 완료 내역

### 5.1 Domain: Consent & Link
- ✅ `ConsentRecord` Entity 구현 완료 (`src/main/java/vibe/digthc/as_digt_hc_dev_fe/domain/integration/entity/ConsentRecord.java`)
- ✅ `DeviceLink` Entity 구현 완료 (`src/main/java/vibe/digthc/as_digt_hc_dev_fe/domain/integration/entity/DeviceLink.java`)
- ✅ `PortalConnection` Entity 구현 완료 (`src/main/java/vibe/digthc/as_digt_hc_dev_fe/domain/integration/entity/PortalConnection.java`)
- ✅ Enum 클래스 구현 완료 (DeviceStatus, PortalStatus, ConsentSubjectType, ConsentType, ConsentStatus)

### 5.2 Repository
- ✅ `DeviceLinkRepository` 구현 완료
- ✅ `PortalConnectionRepository` 구현 완료
- ✅ `ConsentRecordRepository` 구현 완료

### 5.3 Interface Definition
- ✅ `DeviceDataProvider` 인터페이스 정의 완료
- ✅ `PortalDataProvider` 인터페이스 정의 완료
- ✅ `DeviceProviderFactory` 구현 완료
- ✅ `PortalProviderFactory` 구현 완료

### 5.4 Mock Implementation
- ✅ `MockDeviceProvider` 구현 완료 (랜덤 건강 데이터 반환)
- ✅ `MockPortalProvider` 구현 완료 (랜덤 검진/진료 기록 반환)

### 5.5 Service
- ✅ `DeviceLinkService` 구현 완료
  - 디바이스 연동, 해제, 동기화, 토큰 갱신 기능
- ✅ `PortalConnectionService` 구현 완료
  - 포털 연동, 해제, 동기화 기능
- ✅ `ConsentService` 구현 완료
  - 동의 조회, 부여, 철회 기능

### 5.6 DTO
- ✅ Request DTO 구현 완료 (DeviceConnectReq, PortalConnectReq, ConsentScopeDto, RevokeConsentReq)
- ✅ Response DTO 구현 완료 (DeviceLinkRes, PortalConnectionRes, ConsentRes, SyncResultRes)

### 5.7 Controller
- ✅ `IntegrationController` 구현 완료
  - 디바이스 연동 API: GET/POST/DELETE `/api/v1/integration/devices`
  - 포털 연동 API: GET/POST/DELETE `/api/v1/integration/portals`
  - 동의 관리 API: GET/DELETE `/api/v1/integration/consents`
  - 수동 동기화 API: POST `/api/v1/integration/devices/{id}/sync`, `/api/v1/integration/portals/{id}/sync`

### 5.8 Scheduler
- ✅ `SyncScheduler` 구현 완료
  - 매 시간 활성 디바이스 데이터 동기화
  - 30분마다 토큰 갱신 체크

### 5.9 Exception
- ✅ `DeviceAlreadyLinkedException` 구현 완료
- ✅ `DeviceNotFoundException` 구현 완료
- ✅ `ConsentNotFoundException` 구현 완료

## 4. 참고 자료
- SRS 6.2.6 ~ 6.2.9 (Consent, DeviceLink, PortalConnection)
- SRS 3.4.3 병원 포털/디바이스 연동 상태 동기화


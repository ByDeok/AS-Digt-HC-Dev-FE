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

- [x] **Domain: FamilyBoard**
    - `FamilyBoard` Entity 및 `AccessRole` (User-Board 매핑) 구현
    - 역할 정의: `VIEWER`, `EDITOR`, `ADMIN`
- [x] **Invitation Logic**
    - 초대 코드 생성 및 검증 로직
    - `/api/v1/family-board/invite`: 초대 발송
    - `/api/v1/family-board/accept`: 초대 수락 및 역할 부여
- [x] **Data Synchronization (Basic)**
    - 보드 내 데이터(일정, 알림 등) 변경 시 갱신 로직
    - (MVP) Polling 대응을 위한 `lastActivityAt` 타임스탬프 관리

## 5. 구현 완료 내역

### 5.1 Domain: FamilyBoard
- ✅ `FamilyBoard` Entity 구현 완료 (`src/main/java/vibe/digthc/as_digt_hc_dev_fe/domain/family/entity/FamilyBoard.java`)
- ✅ `FamilyBoardMember` Entity 구현 완료 (N:M 관계, User-Board 매핑)
- ✅ `BoardRole` Enum 구현 완료 (`VIEWER`, `EDITOR`, `ADMIN`)
- ✅ `PermissionService` 구현 완료 (권한 검증 로직)

### 5.2 Invitation Logic
- ✅ `BoardInvitation` Entity 구현 완료 (초대 코드 및 상태 관리)
- ✅ `InvitationService` 구현 완료
  - 초대 코드 생성: SecureRandom 기반 8자리 hex 코드
  - 초대 검증: 만료 시간 및 상태 확인
- ✅ API 엔드포인트 구현 완료 (`FamilyBoardController`)
  - `POST /api/v1/family-board/invite`: 초대 발송
  - `POST /api/v1/family-board/accept`: 초대 수락 및 역할 부여

### 5.3 Data Synchronization
- ✅ `FamilyBoard.lastActivityAt` 필드 구현 완료
- ✅ `ActionCardService` 연동 완료
  - ActionCard 완료 시 `lastActivityAt` 갱신
  - ActionCard 생성 시 `lastActivityAt` 갱신
- ✅ `HealthReportService` 연동 완료
  - HealthReport 생성 시 `lastActivityAt` 갱신
- ✅ Polling 대응을 위한 타임스탬프 관리 완료

## 4. 참고 자료
- SRS 6.2.5 FamilyBoard & AccessRole
- SRS 3.4.1 (연관 흐름)


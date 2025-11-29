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

- [x] **Mermaid ERD 작성**
    - SRS 6.2.1 ~ 6.2.9 참조하여 Mermaid 문법으로 ERD 작성
    - 포함 도메인: User, Profile, Onboarding, Report, Action, FamilyBoard, Consent, AuditLog, DeviceLink
    - 관계(Relation) 정의 (1:1, 1:N, N:M)
- [x] **논리적 설계 검증**
    - 정규화 위배 사항 점검
    - 성능 고려한 인덱스 키 후보 선정
    - JSON 타입 컬럼(`metrics`, `context`, `metadata`) 정의
- [x] **Audit(감사) 필드 설계**
    - `BaseTimeEntity` (createdAt, updatedAt) 설계
    - `BaseUserEntity` (createdBy, updatedBy) 설계 (Optional)

## 4. 참고 자료
- SRS 6.2 Entity & Data Model
- `studio/docs/SRS/SRS_V0.3.md`

---

## 5. 산출물 (2025-11-29)

### 5.1 Mermaid ERD
```mermaid
erDiagram
    USERS {
        UUID user_id PK
        ENUM role "senior|caregiver|admin"
        ENUM status "active|inactive|locked"
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    USER_PROFILES {
        UUID profile_id PK
        UUID user_id UK FK
        STRING name
        INT age
        ENUM gender
        JSON primary_conditions
        JSON accessibility_prefs
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    ONBOARDING_SESSIONS {
        UUID session_id PK
        UUID user_id FK
        ENUM status
        INT step_index
        INT eta_seconds
        TIMESTAMP started_at
        TIMESTAMP completed_at
    }
    HEALTH_REPORTS {
        UUID report_id PK
        UUID user_id FK
        DATE period_start
        DATE period_end
        JSON metrics
        JSON context
        TIMESTAMP generated_at
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    ACTION_CARDS {
        UUID action_id PK
        UUID user_id FK
        DATE action_date
        STRING title
        STRING description
        ENUM status
        TIMESTAMP created_at
        TIMESTAMP completed_at
    }
    FAMILY_BOARDS {
        UUID board_id PK
        UUID senior_id FK
        JSON settings
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    FAMILY_BOARD_MEMBERS {
        UUID membership_id PK
        UUID board_id FK
        UUID member_id FK
        ENUM role
        TIMESTAMP invited_at
        TIMESTAMP accepted_at
        TIMESTAMP removed_at
    }
    CONSENT_RECORDS {
        UUID consent_id PK
        UUID user_id FK
        ENUM subject_type
        UUID subject_id
        JSON consent_scope
        TIMESTAMP created_at
        TIMESTAMP revoked_at
    }
    AUDIT_LOGS {
        UUID log_id PK
        UUID user_id
        UUID actor_id
        STRING action_type
        STRING resource_type
        UUID resource_id
        JSON metadata
        TIMESTAMP created_at
    }
    DEVICE_LINKS {
        UUID device_link_id PK
        UUID user_id FK
        STRING vendor
        STRING device_type
        STRING access_token
        ENUM status
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    PORTAL_CONNECTIONS {
        UUID portal_conn_id PK
        UUID user_id FK
        STRING portal_id
        ENUM status
        TIMESTAMP last_sync_at
        TIMESTAMP created_at
        STRING error_code
    }

    USERS ||--|| USER_PROFILES : "1:1"
    USERS ||--o{ ONBOARDING_SESSIONS : "1:N"
    USERS ||--o{ HEALTH_REPORTS : "1:N"
    USERS ||--o{ ACTION_CARDS : "1:N"
    USERS ||--o{ DEVICE_LINKS : "1:N"
    USERS ||--o{ PORTAL_CONNECTIONS : "1:N"
    USERS ||--o{ CONSENT_RECORDS : "1:N"
    USERS ||--o{ AUDIT_LOGS : "subject"
    USERS ||--o{ AUDIT_LOGS : "actor"
    USERS ||--o{ FAMILY_BOARDS : "senior"
    USERS ||--o{ FAMILY_BOARD_MEMBERS : "member"

    FAMILY_BOARDS ||--o{ FAMILY_BOARD_MEMBERS : "1:N"
    CONSENT_RECORDS }o--|| DEVICE_LINKS : "0..1"
    CONSENT_RECORDS }o--|| PORTAL_CONNECTIONS : "0..1"
    CONSENT_RECORDS }o--|| FAMILY_BOARD_MEMBERS : "0..1 delegation"
```

- User/Profile 분리를 통해 인증 계정과 개인정보를 독립 관리하고, FamilyBoard 멤버십은 별도 테이블로 역할 충돌을 방지합니다.
- Consent는 subject_type/subect_id 다형성을 사용하여 DeviceLink, PortalConnection, FamilyBoard 위임을 하나의 테이블에서 추적합니다.

### 5.2 논리적 설계 검증

#### 5.2.1 정규화 & 제약
- **1:1 User ↔ Profile**: `user_profiles.user_id`를 `UNIQUE`로 정의하여 1차 정규형을 유지하고, 개인정보 변경이 계정 데이터에 영향을 주지 않도록 분리했습니다.
- **FamilyBoard 멤버십 분리**: `family_board_members`가 N:M 관계를 해소하며, 초대/수락/삭제 타임스탬프를 별도 컬럼으로 보관해 감사 추적 요구(REQ-NF-006/007)에 대응합니다.
- **Consent 다형성**: `subject_type` ENUM (`device_link`, `portal_connection`, `family_board`)과 `subject_id` 조합에 `NOT NULL` 제약(선택적)으로 3NF를 충족시키면서 확장 가능성을 확보했습니다.
- **OnboardingSession 단계화**: 진행 상태(`status`, `step_index`)는 세션 테이블에 두고, 단계별 상세 데이터는 별도 JSON/이벤트 테이블로 확장 가능하게 설계해 2NF 위배를 방지했습니다.
- **AuditLog 참조**: `user_id`(대상)와 `actor_id`(실행 주체)를 분리하여 대리 접근 시나리오를 표현하고, `resource_type/id` 조합으로 FK 제약을 유연하게 유지했습니다.

#### 5.2.2 인덱스 후보

| 테이블 | 인덱스 | 컬럼 | 목적 |
| --- | --- | --- | --- |
| `users` | `idx_users_role_status` | `(role, status)` | 사용자 군집별 목록/통계 조회 최적화 |
| `user_profiles` | `idx_profiles_age_gender` | `(age, gender)` | 행동 코칭 대상 필터링(D1/W1 KPI) |
| `onboarding_sessions` | `idx_onboarding_user_status` | `(user_id, status)` | 재시도/ETA 갱신 시 세션 탐색 속도 확보 |
| `health_reports` | `idx_reports_user_period` | `(user_id, period_start, period_end)` | 리포트 기간 중복 방지 및 최신 리포트 조회 |
| `action_cards` | `idx_action_user_date` | `(user_id, action_date)` | “오늘 행동 카드” 조회 p95 ≤ 800ms 충족 |
| `family_board_members` | `uk_board_member` | `(board_id, member_id)` UNIQUE | 중복 초대 차단 및 역할 변경 트랜잭션 단순화 |
| `consent_records` | `idx_consent_subject` | `(subject_type, subject_id)` | 위임 철회/갱신 시 대상 레코드 탐색 |
| `audit_logs` | `idx_audit_action_created` | `(action_type, created_at DESC)` | 위험 기반 인증 감사를 위한 시간 순 정렬 |
| `device_links` | `idx_device_user_vendor` | `(user_id, vendor)` | 토큰 유효성 점검 배치의 where 절 최적화 |
| `portal_connections` | `idx_portal_user_status` | `(user_id, status)` | 지원 불가 지역 예외 처리(REQ-FUNC-019) 대응 |

#### 5.2.3 JSON 컬럼 스키마
- `health_reports.metrics`

```json
{
  "activity": { "avgSteps": 6540, "minutesActive": 45 },
  "heartRate": { "resting": 62, "max": 132 },
  "bloodPressure": { "systolic": 118, "diastolic": 76 },
  "weight": { "avg": 66.1, "unit": "kg" }
}
```

- `health_reports.context`

```json
{
  "period": { "start": "2025-08-01", "end": "2025-10-31" },
  "device": [{ "type": "watch", "vendor": "samsung" }],
  "missingTags": ["bp_monitor"],
  "notes": "Clinic-uploaded labs merged"
}
```

- `audit_logs.metadata`

```json
{
  "ip": "203.0.113.10",
  "userAgent": "Chrome/120.0",
  "geo": { "lat": 37.5651, "lon": 126.98955 },
  "riskScore": 0.18,
  "channel": "family_board"
}
```

JSON 컬럼은 공통 `json_schema_version` 필드를 optional 하게 추가해 마이그레이션 시 역호환성을 보장하도록 합니다.

### 5.3 Audit(감사) 필드 및 Base Entity 설계
- 모든 JPA Entity는 `BaseTimeEntity`를 상속하여 ISO-8601 기반 타임스탬프를 자동 생성합니다.
- 감사가 필요한 도메인(`User`, `FamilyBoardMember`, `ConsentRecord`, `ActionCard`, `HealthReport` 등)은 `BaseUserEntity`를 상속해 작성/수정 주체를 남깁니다.
- JPA Auditing 활성화: `@EnableJpaAuditing(modifyOnCreate = false)`를 `Application` 클래스에 선언하고, `AuditorAware<UUID>` 구현체를 통해 현재 사용자의 `user_id`를 공급합니다.

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

```java
@MappedSuperclass
public abstract class BaseUserEntity extends BaseTimeEntity {

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;
}
```

- DB 레벨에서는 `created_at`, `updated_at`, `created_by`, `updated_by`에 `DEFAULT CURRENT_TIMESTAMP` + `ON UPDATE CURRENT_TIMESTAMP` 제약을 추가하여 애플리케이션 레이어 오류 시에도 데이터 일관성을 확보합니다.
- `AuditLog` 테이블은 별도 엔터티이지만 `BaseTimeEntity`를 사용하여 감사 레코드 삭제를 방지(`@SQLDelete` 대신 논리 삭제 미사용)하도록 설계합니다.

### 5.4 리뷰 및 공유 가이드
- Mermaid 다이어그램은 `docs/` 하위 산출물로 옮기기 전에 `mmdc` 또는 VSCode Mermaid 플러그인으로 PNG/SVG를 export하여 팀 리뷰에 첨부합니다.
- 스키마 확정 시 `scripts/init-local-db.sql`에 동일한 테이블/인덱스를 반영하고, `Flyway` 기준 `V1_1__create_core_tables.sql`을 생성해 형상관리합니다.
- JSON 스키마는 `src/main/resources/json-schema/`에 버전 관리하고, 리포트/감사 로그 직렬화 시 해당 버전을 응답 페이로드에 포함합니다.
- 데이터모델 변경사항은 `Tasks/API/issue-02-db-design_API.md`의 검증 API와 Traceability Matrix(REQ-FUNC-007~017, REQ-NF-005~007)을 이용해 상호 검증 후 공유합니다.


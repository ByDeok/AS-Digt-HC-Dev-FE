# 통합 데이터 모델 (ERD) 설계

## 1. 개요
본 문서는 **AS-Digt-HC-Dev** 프로젝트의 통합 데이터 모델(ERD)을 정의합니다. **SRS 6.2 Entity & Data Model** 요구사항을 준수하며, 회원, 리포트, 행동 코칭, 가족 보드, 외부 연동 등 핵심 도메인의 데이터 구조와 관계를 시각화하고 상세 명세를 기술합니다.

## 2. Mermaid ERD

전체 시스템의 엔터티 관계도는 아래와 같습니다.

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

### 2.1 모델링 핵심 전략
- **계정-프로필 분리**: `USERS` 테이블은 인증 및 계정 상태(`role`, `status`)를 관리하고, 개인정보(`age`, `gender`, `conditions`)는 `USER_PROFILES`로 분리하여 보안과 데이터 관리 효율성을 높였습니다.
- **가족 보드 멤버십**: `FAMILY_BOARDS`와 `FAMILY_BOARD_MEMBERS`를 분리하여 시니어 1인당 하나의 보드를 가지며, 다수의 보호자가 멤버로 참여하는 구조를 명확히 했습니다.
- **동의(Consent) 다형성**: `CONSENT_RECORDS`는 `subject_type`과 `subject_id`를 통해 디바이스 연결, 병원 포털, 가족 보드 위임 등 다양한 대상에 대한 동의 이력을 통합 관리합니다.

---

## 3. 논리적 설계 검증

### 3.1 정규화 및 제약조건 (Normalization & Constraints)
| 검증 항목 | 설계 반영 내용 |
| --- | --- |
| **1NF & User 분리** | `user_profiles.user_id`를 `UNIQUE`로 설정하여 1:1 관계를 강제하고, 원자성을 보장했습니다. |
| **2NF & 온보딩** | `onboarding_sessions`의 진행 상태는 메인 테이블에, 단계별 상세 데이터는 추후 확장 가능한 구조(JSON/별도 테이블)로 분리했습니다. |
| **3NF & 다형성** | `consent_records`에서 `subject_type`에 따라 참조 대상이 달라지는 구조를 `ENUM`과 `UUID` 조합으로 해결하며 중복을 최소화했습니다. |
| **무결성(Integrity)** | `family_board_members`에서 `(board_id, member_id)`를 `UNIQUE` 복합키로 설정하여 중복 초대를 원천 차단했습니다. |
| **감사(Audit)** | `audit_logs`는 `user_id`(대상)와 `actor_id`(행위자)를 분리하여 대리인에 의한 조작 이력을 명확히 기록합니다. |

### 3.2 인덱스 전략 (Index Strategy)
성능 요구사항(p95 < 800ms)을 만족하기 위해 주요 조회 패턴에 맞춘 인덱스를 설계했습니다.

| 테이블 | 인덱스명 | 구성 컬럼 | 최적화 목적 |
| --- | --- | --- | --- |
| `users` | `idx_users_role_status` | `(role, status)` | 사용자 역할/상태별 필터링 및 통계 |
| `user_profiles` | `idx_profiles_age_gender` | `(age, gender)` | 타겟 그룹별 행동 코칭 알고리즘 최적화 |
| `onboarding_sessions` | `idx_onboarding_user_status` | `(user_id, status)` | 유저별 진행 중인 온보딩 세션 고속 조회 |
| `health_reports` | `idx_reports_user_period` | `(user_id, period_start, period_end)` | 리포트 기간 중복 체크 및 최신순 정렬 |
| `action_cards` | `idx_action_user_date` | `(user_id, action_date)` | **"오늘의 행동"** 조회 쿼리 속도 보장 |
| `family_board_members` | `uk_board_member` | `(board_id, member_id)` (Unique) | 멤버십 중복 방지 및 조회 |
| `consent_records` | `idx_consent_subject` | `(subject_type, subject_id)` | 특정 대상(기기/포털)에 대한 동의 여부 확인 |
| `audit_logs` | `idx_audit_action_created` | `(action_type, created_at DESC)` | 최근 보안 감사 로그 조회 |
| `device_links` | `idx_device_user_vendor` | `(user_id, vendor)` | 벤더별 토큰 갱신 배치 작업 최적화 |
| `portal_connections` | `idx_portal_user_status` | `(user_id, status)` | 연동 상태 모니터링 및 장애 대응 |

### 3.3 JSON 컬럼 스키마
유연성이 필요한 데이터는 JSON 타입으로 정의하되, 아래와 같은 표준 스키마를 준수합니다.

#### 3.3.1 Health Report Metrics (`health_reports.metrics`)
```json
{
  "activity": { "avgSteps": 6540, "minutesActive": 45 },
  "heartRate": { "resting": 62, "max": 132 },
  "bloodPressure": { "systolic": 118, "diastolic": 76 },
  "weight": { "avg": 66.1, "unit": "kg" }
}
```

#### 3.3.2 Health Report Context (`health_reports.context`)
```json
{
  "period": { "start": "2025-08-01", "end": "2025-10-31" },
  "device": [{ "type": "watch", "vendor": "samsung" }],
  "missingTags": ["bp_monitor"],
  "notes": "Clinic-uploaded labs merged"
}
```

#### 3.3.3 Audit Log Metadata (`audit_logs.metadata`)
```json
{
  "ip": "203.0.113.10",
  "userAgent": "Chrome/120.0",
  "geo": { "lat": 37.5651, "lon": 126.98955 },
  "riskScore": 0.18,
  "channel": "family_board"
}
```

---

## 4. 구현 가이드

### 4.1 Base Entity (JPA Audit)
모든 JPA Entity는 생성/수정 시간을 자동으로 관리하기 위해 `BaseTimeEntity`를 상속받아야 하며, 사용자 추적이 필요한 경우 `BaseUserEntity`를 사용합니다.

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

### 4.2 DB Physical Schema 반영
- `created_at`, `updated_at` 컬럼에는 DB 레벨에서 `DEFAULT CURRENT_TIMESTAMP` 및 `ON UPDATE CURRENT_TIMESTAMP` 제약을 설정하여 애플리케이션 외적인 데이터 변경 시에도 정합성을 유지합니다.
- 모든 `UUID` 컬럼은 DB 저장 시 `BINARY(16)` 또는 `VARCHAR(36)` (DB 지원 여부에 따름, MySQL 8.0+ `BIN_TO_UUID` 활용 권장)으로 매핑합니다.
- `JSON` 컬럼은 `MySQL 5.7+`의 Native JSON 타입을 사용하여 인덱싱 및 부분 업데이트 기능을 활용합니다.


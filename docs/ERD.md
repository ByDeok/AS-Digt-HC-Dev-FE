# Entity Relationship Diagram (ERD)

## 개요

본 문서는 **AS-Digt-HC-Dev-FE** 프로젝트의 데이터베이스 엔터티 관계도(ERD)를 정의합니다. 
시니어 웰니스 및 가족 건강 관리 플랫폼의 핵심 도메인(사용자, 건강 리포트, 행동 코칭, 가족 보드, 외부 연동)의 데이터 구조와 관계를 시각화합니다.

---

## 전체 ERD 다이어그램

```mermaid
erDiagram
    USERS {
        UUID user_id PK
        STRING email UK
        STRING password
        ENUM role "SENIOR|CAREGIVER|ADMIN"
        ENUM status "ACTIVE|INACTIVE|LOCKED"
        ENUM auth_provider "GOOGLE|KAKAO|LOCAL"
        STRING provider_id
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    USER_PROFILES {
        UUID profile_id PK
        UUID user_id UK FK
        STRING name
        INT age
        ENUM gender "MALE|FEMALE|OTHER"
        JSON primary_conditions
        JSON accessibility_prefs
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    USER_AGREEMENTS {
        UUID agreement_id PK
        UUID user_id FK
        STRING agreement_type
        BOOLEAN agreed
        TIMESTAMP agreed_at
        TIMESTAMP created_at
    }
    
    ONBOARDING_SESSIONS {
        UUID session_id PK
        UUID user_id FK
        ENUM status "IN_PROGRESS|COMPLETED|FAILED"
        ENUM current_step "PROFILE|AUTH|DEVICE|PORTAL|COMPLETE"
        INT step_index
        INT eta_seconds
        TIMESTAMP started_at
        TIMESTAMP completed_at
    }
    
    HEALTH_REPORTS {
        UUID report_id PK
        UUID user_id FK
        DATE start_date
        DATE end_date
        JSON metrics
        JSON context
        TIMESTAMP generated_at
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    ACTION_CARDS {
        BIGINT action_id PK
        UUID user_id FK
        DATE action_date
        STRING title
        STRING description
        ENUM status "PENDING|COMPLETED|SKIPPED"
        STRING rule_id
        TIMESTAMP created_at
        TIMESTAMP updated_at
        TIMESTAMP completed_at
    }
    
    FAMILY_BOARDS {
        UUID board_id PK
        UUID senior_id UK FK
        STRING name
        TEXT description
        JSON settings
        TIMESTAMP last_activity_at
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    FAMILY_BOARD_MEMBERS {
        UUID membership_id PK
        UUID board_id FK
        UUID member_id FK
        ENUM role "VIEWER|EDITOR|ADMIN"
        ENUM status "PENDING|ACTIVE|INACTIVE"
        TIMESTAMP invited_at
        TIMESTAMP accepted_at
        TIMESTAMP removed_at
    }
    
    BOARD_INVITATIONS {
        UUID invitation_id PK
        UUID board_id FK
        UUID inviter_id FK
        STRING invitee_email
        ENUM status "PENDING|ACCEPTED|REJECTED|EXPIRED"
        STRING token UK
        TIMESTAMP expires_at
        TIMESTAMP created_at
        TIMESTAMP responded_at
    }
    
    CONSENT_RECORDS {
        UUID consent_id PK
        UUID user_id FK
        ENUM subject_type "DEVICE|PORTAL|FAMILY_BOARD"
        UUID subject_id
        ENUM consent_type "DATA_SHARING|DELEGATION"
        ENUM status "ACTIVE|REVOKED|EXPIRED"
        JSON consent_scope
        TIMESTAMP created_at
        TIMESTAMP revoked_at
    }
    
    DEVICE_LINKS {
        UUID device_link_id PK
        UUID user_id FK
        STRING vendor
        STRING device_type
        STRING access_token
        ENUM status "ACTIVE|REVOKED|EXPIRED"
        TIMESTAMP last_sync_at
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    PORTAL_CONNECTIONS {
        UUID portal_conn_id PK
        UUID user_id FK
        STRING portal_id
        ENUM status "ACTIVE|PENDING|FAILED|UNSUPPORTED_REGION"
        TIMESTAMP last_sync_at
        STRING error_code
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    %% Relationships
    USERS ||--|| USER_PROFILES : "has"
    USERS ||--o{ USER_AGREEMENTS : "has"
    USERS ||--o{ ONBOARDING_SESSIONS : "has"
    USERS ||--o{ HEALTH_REPORTS : "generates"
    USERS ||--o{ ACTION_CARDS : "receives"
    USERS ||--o{ DEVICE_LINKS : "links"
    USERS ||--o{ PORTAL_CONNECTIONS : "connects"
    USERS ||--o{ CONSENT_RECORDS : "grants"
    USERS ||--o| FAMILY_BOARDS : "owns_as_senior"
    USERS ||--o{ FAMILY_BOARD_MEMBERS : "is_member"
    USERS ||--o{ BOARD_INVITATIONS : "invites"
    
    FAMILY_BOARDS ||--o{ FAMILY_BOARD_MEMBERS : "has"
    FAMILY_BOARDS ||--o{ BOARD_INVITATIONS : "has"
    
    CONSENT_RECORDS }o--o| DEVICE_LINKS : "references"
    CONSENT_RECORDS }o--o| PORTAL_CONNECTIONS : "references"
    CONSENT_RECORDS }o--o| FAMILY_BOARD_MEMBERS : "references"
```

---

## 엔터티 상세 명세

### 1. USERS (사용자)

시스템의 핵심 사용자 엔터티로, 인증 및 계정 정보를 관리합니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| user_id | UUID | PK | 사용자 고유 식별자 |
| email | STRING(100) | UK, NOT NULL | 이메일 주소 (로그인 ID) |
| password | STRING(255) | NULL | 비밀번호 (소셜 로그인 시 NULL) |
| role | ENUM | NOT NULL | 역할: SENIOR, CAREGIVER, ADMIN |
| status | ENUM | NOT NULL | 계정 상태: ACTIVE, INACTIVE, LOCKED |
| auth_provider | ENUM | NULL | 인증 제공자: GOOGLE, KAKAO, LOCAL |
| provider_id | STRING(255) | NULL | 외부 제공자 ID |
| created_at | TIMESTAMP | NOT NULL | 생성 일시 |
| updated_at | TIMESTAMP | NOT NULL | 수정 일시 |

**인덱스:**
- `idx_users_role_status`: (role, status) - 역할/상태별 필터링

**관계:**
- 1:1 `USER_PROFILES` (프로필 정보)
- 1:N `ONBOARDING_SESSIONS` (온보딩 세션)
- 1:N `HEALTH_REPORTS` (건강 리포트)
- 1:N `ACTION_CARDS` (행동 카드)
- 1:N `DEVICE_LINKS` (디바이스 연동)
- 1:N `PORTAL_CONNECTIONS` (병원 포털 연동)
- 1:N `CONSENT_RECORDS` (동의 기록)
- 1:1 `FAMILY_BOARDS` (시니어로서 소유한 가족 보드)
- 1:N `FAMILY_BOARD_MEMBERS` (멤버로 참여한 가족 보드)

---

### 2. USER_PROFILES (사용자 프로필)

사용자의 개인정보 및 건강 관련 기본 정보를 저장합니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| profile_id | UUID | PK | 프로필 고유 식별자 |
| user_id | UUID | UK, FK → USERS | 사용자 ID |
| name | STRING | NULL | 이름 |
| age | INT | NULL | 나이 (0 < age < 120) |
| gender | ENUM | NULL | 성별: MALE, FEMALE, OTHER |
| primary_conditions | JSON | NULL | 주요 질환 목록 |
| accessibility_prefs | JSON | NULL | 접근성 설정 (대글자, 고대비 등) |
| created_at | TIMESTAMP | NOT NULL | 생성 일시 |
| updated_at | TIMESTAMP | NOT NULL | 수정 일시 |

**관계:**
- N:1 `USERS` (소유자)

---

### 3. ONBOARDING_SESSIONS (온보딩 세션)

3분 온보딩 프로세스의 진행 상태를 추적합니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| session_id | UUID | PK | 세션 고유 식별자 |
| user_id | UUID | FK → USERS | 사용자 ID |
| status | ENUM | NOT NULL | 상태: IN_PROGRESS, COMPLETED, FAILED |
| current_step | ENUM | NOT NULL | 현재 단계: PROFILE, AUTH, DEVICE, PORTAL, COMPLETE |
| step_index | INT | NOT NULL | 단계 인덱스 (0부터 시작) |
| eta_seconds | INT | NULL | 예상 남은 시간(초) |
| started_at | TIMESTAMP | NOT NULL | 시작 일시 |
| completed_at | TIMESTAMP | NULL | 완료 일시 |

**관계:**
- N:1 `USERS` (소유자)

---

### 4. HEALTH_REPORTS (건강 리포트)

1장 의사용 요약 리포트를 저장합니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| report_id | UUID | PK | 리포트 고유 식별자 |
| user_id | UUID | FK → USERS | 사용자 ID |
| start_date | DATE | NOT NULL | 리포트 기간 시작일 |
| end_date | DATE | NOT NULL | 리포트 기간 종료일 |
| metrics | JSON | NOT NULL | 건강 지표 (활동, 심박, 혈압, 체중) |
| context | JSON | NOT NULL | 맥락 정보 (기기, 결측 태그, 측정 기간) |
| generated_at | TIMESTAMP | NOT NULL | 생성 일시 |
| created_at | TIMESTAMP | NOT NULL | 생성 일시 |
| updated_at | TIMESTAMP | NOT NULL | 수정 일시 |

**관계:**
- N:1 `USERS` (소유자)

---

### 5. ACTION_CARDS (행동 카드)

일일 1~3개의 행동 코칭 카드를 저장합니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| action_id | BIGINT | PK | 행동 카드 고유 식별자 |
| user_id | UUID | FK → USERS | 사용자 ID |
| action_date | DATE | NOT NULL | 행동 날짜 |
| title | STRING | NOT NULL | 행동 제목 |
| description | STRING(500) | NULL | 상세 설명 |
| status | ENUM | NOT NULL | 상태: PENDING, COMPLETED, SKIPPED |
| rule_id | STRING | NULL | 생성 규칙 ID (추적용) |
| created_at | TIMESTAMP | NOT NULL | 생성 일시 |
| updated_at | TIMESTAMP | NOT NULL | 수정 일시 |
| completed_at | TIMESTAMP | NULL | 완료 일시 |

**인덱스:**
- `idx_action_card_date_user`: (action_date, user_id) - 일일 조회 최적화
- `idx_action_card_status`: (status) - 상태별 필터링

**관계:**
- N:1 `USERS` (대상 사용자)

---

### 6. FAMILY_BOARDS (가족 보드)

시니어와 보호자 간 건강 정보 공유 공간입니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| board_id | UUID | PK | 보드 고유 식별자 |
| senior_id | UUID | UK, FK → USERS | 시니어 사용자 ID |
| name | STRING(100) | NOT NULL | 보드 이름 |
| description | TEXT | NULL | 보드 설명 |
| settings | JSON | NULL | 알림/프라이버시 설정 |
| last_activity_at | TIMESTAMP | NULL | 마지막 활동 일시 |
| created_at | TIMESTAMP | NOT NULL | 생성 일시 |
| updated_at | TIMESTAMP | NOT NULL | 수정 일시 |

**인덱스:**
- `idx_boards_senior`: (senior_id) - 시니어별 조회

**관계:**
- N:1 `USERS` (시니어 소유자)
- 1:N `FAMILY_BOARD_MEMBERS` (멤버)
- 1:N `BOARD_INVITATIONS` (초대)

---

### 7. FAMILY_BOARD_MEMBERS (가족 보드 멤버)

가족 보드에 참여한 멤버와 역할을 관리합니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| membership_id | UUID | PK | 멤버십 고유 식별자 |
| board_id | UUID | FK → FAMILY_BOARDS | 보드 ID |
| member_id | UUID | FK → USERS | 멤버 사용자 ID |
| role | ENUM | NOT NULL | 역할: VIEWER, EDITOR, ADMIN |
| status | ENUM | NOT NULL | 상태: PENDING, ACTIVE, INACTIVE |
| invited_at | TIMESTAMP | NULL | 초대 일시 |
| accepted_at | TIMESTAMP | NULL | 수락 일시 |
| removed_at | TIMESTAMP | NULL | 제거 일시 |

**제약조건:**
- `UK(board_id, member_id)`: 보드당 멤버 중복 방지

**관계:**
- N:1 `FAMILY_BOARDS` (소속 보드)
- N:1 `USERS` (멤버 사용자)

---

### 8. BOARD_INVITATIONS (보드 초대)

가족 보드 초대 이력을 관리합니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| invitation_id | UUID | PK | 초대 고유 식별자 |
| board_id | UUID | FK → FAMILY_BOARDS | 보드 ID |
| inviter_id | UUID | FK → USERS | 초대한 사용자 ID |
| invitee_email | STRING | NOT NULL | 초대받은 이메일 |
| status | ENUM | NOT NULL | 상태: PENDING, ACCEPTED, REJECTED, EXPIRED |
| token | STRING | UK | 초대 토큰 (고유) |
| expires_at | TIMESTAMP | NOT NULL | 만료 일시 |
| created_at | TIMESTAMP | NOT NULL | 생성 일시 |
| responded_at | TIMESTAMP | NULL | 응답 일시 |

**관계:**
- N:1 `FAMILY_BOARDS` (소속 보드)
- N:1 `USERS` (초대한 사용자)

---

### 9. CONSENT_RECORDS (동의 기록)

사용자의 데이터 공유 및 위임 동의를 기록합니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| consent_id | UUID | PK | 동의 고유 식별자 |
| user_id | UUID | FK → USERS | 동의 제공 사용자 ID |
| subject_type | ENUM | NOT NULL | 대상 타입: DEVICE, PORTAL, FAMILY_BOARD |
| subject_id | UUID | NULL | 대상 엔터티 ID (다형성 참조) |
| consent_type | ENUM | NOT NULL | 동의 타입: DATA_SHARING, DELEGATION |
| status | ENUM | NOT NULL | 상태: ACTIVE, REVOKED, EXPIRED |
| consent_scope | JSON | NOT NULL | 동의 범위 (데이터 종류, 기간 등) |
| created_at | TIMESTAMP | NOT NULL | 생성 일시 |
| revoked_at | TIMESTAMP | NULL | 철회 일시 |

**관계:**
- N:1 `USERS` (동의 제공자)
- 다형성 참조: `DEVICE_LINKS`, `PORTAL_CONNECTIONS`, `FAMILY_BOARD_MEMBERS`

---

### 10. DEVICE_LINKS (디바이스 연동)

웨어러블 및 계측 디바이스 연동 정보를 저장합니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| device_link_id | UUID | PK | 디바이스 링크 고유 식별자 |
| user_id | UUID | FK → USERS | 사용자 ID |
| vendor | STRING | NOT NULL | 벤더: SAMSUNG, APPLE, ETC |
| device_type | STRING | NOT NULL | 디바이스 타입: WATCH, BP_MONITOR, SCALE |
| access_token | STRING | NOT NULL | 액세스 토큰 (암호화 저장) |
| status | ENUM | NOT NULL | 상태: ACTIVE, REVOKED, EXPIRED |
| last_sync_at | TIMESTAMP | NULL | 마지막 동기화 일시 |
| created_at | TIMESTAMP | NOT NULL | 생성 일시 |
| updated_at | TIMESTAMP | NOT NULL | 수정 일시 |

**관계:**
- N:1 `USERS` (소유자)

---

### 11. PORTAL_CONNECTIONS (병원 포털 연동)

병원/검진 포털 연동 정보를 저장합니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| portal_conn_id | UUID | PK | 포털 연결 고유 식별자 |
| user_id | UUID | FK → USERS | 사용자 ID |
| portal_id | STRING | NOT NULL | 포털 식별자 |
| status | ENUM | NOT NULL | 상태: ACTIVE, PENDING, FAILED, UNSUPPORTED_REGION |
| last_sync_at | TIMESTAMP | NULL | 마지막 동기화 일시 |
| error_code | STRING | NULL | 최근 오류 코드 |
| created_at | TIMESTAMP | NOT NULL | 생성 일시 |
| updated_at | TIMESTAMP | NOT NULL | 수정 일시 |

**관계:**
- N:1 `USERS` (소유자)

---

## 주요 관계 요약

### 1. 사용자 중심 관계
- **USERS**는 모든 도메인의 중심 엔터티입니다.
- 각 사용자는 하나의 프로필(`USER_PROFILES`)을 가지며, 여러 온보딩 세션, 리포트, 행동 카드를 생성합니다.

### 2. 가족 보드 구조
- 시니어 1인당 1개의 `FAMILY_BOARDS`가 생성됩니다.
- 여러 보호자가 `FAMILY_BOARD_MEMBERS`로 참여하며, 역할(VIEWER/EDITOR/ADMIN)을 가집니다.
- 초대는 `BOARD_INVITATIONS`를 통해 관리됩니다.

### 3. 동의(Consent) 다형성
- `CONSENT_RECORDS`는 `subject_type`과 `subject_id`를 통해 디바이스, 포털, 가족 보드 등 다양한 대상에 대한 동의를 통합 관리합니다.

### 4. 외부 연동
- `DEVICE_LINKS`와 `PORTAL_CONNECTIONS`는 각각 독립적으로 관리되며, 사용자별로 여러 개의 연동을 가질 수 있습니다.

---

## 인덱스 전략

성능 최적화를 위한 주요 인덱스:

| 테이블 | 인덱스명 | 컬럼 | 목적 |
|--------|----------|------|------|
| users | idx_users_role_status | (role, status) | 역할/상태별 필터링 |
| action_cards | idx_action_card_date_user | (action_date, user_id) | 일일 행동 카드 조회 |
| action_cards | idx_action_card_status | (status) | 상태별 필터링 |
| family_boards | idx_boards_senior | (senior_id) | 시니어별 보드 조회 |
| family_board_members | uk_board_member | (board_id, member_id) UNIQUE | 중복 멤버십 방지 |

---

## 데이터 무결성 제약조건

1. **참조 무결성**: 모든 FK는 부모 엔터티 삭제 시 적절한 CASCADE 정책을 적용합니다.
2. **고유성 제약**: 
   - `user_profiles.user_id` (UK): 사용자당 프로필 1개
   - `family_boards.senior_id` (UK): 시니어당 보드 1개
   - `family_board_members(board_id, member_id)` (UK): 보드당 멤버 중복 방지
3. **체크 제약**: 
   - `user_profiles.age`: 0 < age < 120
   - `onboarding_sessions.step_index`: >= 0

---

## JSON 스키마 예시

### health_reports.metrics
```json
{
  "activity": {
    "avgSteps": 6540,
    "minutesActive": 45,
    "caloriesBurned": 180
  },
  "heartRate": {
    "resting": 62,
    "max": 132,
    "avg": 75
  },
  "bloodPressure": {
    "systolic": 118,
    "diastolic": 76,
    "measurements": 12
  },
  "weight": {
    "avg": 66.1,
    "unit": "kg",
    "trend": "stable"
  }
}
```

### health_reports.context
```json
{
  "period": {
    "start": "2025-08-01",
    "end": "2025-10-31",
    "days": 92
  },
  "devices": [
    {
      "type": "watch",
      "vendor": "samsung",
      "dataPoints": 850
    }
  ],
  "missingTags": ["bp_monitor"],
  "notes": "Clinic-uploaded labs merged"
}
```

### family_boards.settings
```json
{
  "notifications": {
    "emergencyAlerts": true,
    "medicationReminders": true,
    "activityUpdates": true
  },
  "privacy": {
    "shareHealthData": true,
    "shareActivityLog": true
  }
}
```

---

## 버전 정보

- **작성일**: 2025-01-27
- **버전**: 1.0
- **작성자**: AI Assistant
- **참고 문서**: 
  - `docs/05-data-model-erd.md` (기존 ERD 문서)
  - `docs/SRS/SRS_V0.3.md` (요구사항 명세)

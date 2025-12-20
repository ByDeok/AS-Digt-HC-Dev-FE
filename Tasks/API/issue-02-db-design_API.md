# [BE] Issue-02: 통합 데이터 모델링 (ERD) 설계 API 명세서

## 📋 개요

**SRS 6.2 Entity & Data Model**을 기반으로 전체 시스템의 데이터 모델(ERD) 설계와 관련된 관리 API 명세서입니다.

**기술 스택**: Java 17+, Spring Boot 3.4+, MySQL 9.x, Jakarta EE 10

> **참고**: 본 이슈는 주로 데이터베이스 설계에 관한 내용이므로, 실제 비즈니스 API는 각 도메인 이슈(issue-03~08)에서 정의됩니다. 본 문서는 데이터 모델 검증 및 관리용 API만 포함합니다.

---

## 🔍 1. 데이터베이스 스키마 검증 (Schema Validation)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/admin/schema/validate` |
| **설명** | 현재 데이터베이스 스키마와 Entity 매핑 일치 여부 검증 |
| **인증** | `ADMIN` 역할 필수 |
| **난이도** | **중 (Medium)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "스키마 검증 완료",
  "data": {
    "isValid": true,
    "validationResults": [
      {
        "entity": "User",
        "table": "users",
        "status": "VALID",
        "issues": []
      },
      {
        "entity": "HealthReport",
        "table": "health_reports",
        "status": "VALID",
        "issues": []
      }
    ],
    "summary": {
      "totalEntities": 12,
      "validEntities": 12,
      "invalidEntities": 0,
      "warnings": 0
    }
  }
}
```

**❌ 실패 (400 Bad Request)**

```json
{
  "type": "/errors/schema-validation",
  "title": "스키마 검증 실패",
  "status": 400,
  "detail": "일부 Entity와 테이블 간 매핑 불일치가 발견되었습니다.",
  "errors": [
    {
      "entity": "OnboardingSession",
      "table": "onboarding_sessions",
      "issue": "컬럼 'step_data' 타입 불일치: Entity는 Map<String,Object>, DB는 VARCHAR"
    }
  ]
}
```

### Logic Steps

```
1. [Load Entities] 모든 JPA Entity 클래스 스캔
   - @Entity 어노테이션이 있는 클래스 수집

2. [DB Schema] 데이터베이스 메타데이터 조회
   - INFORMATION_SCHEMA에서 테이블/컬럼 정보 조회

3. [Mapping Validation] Entity ↔ Table 매핑 검증
   - @Table(name)과 실제 테이블 존재 여부 확인
   - 컬럼 타입 매핑 검증 (VARCHAR ↔ String, JSON ↔ Map 등)
   - 인덱스 존재 여부 확인

4. [Relationship Validation] 관계 매핑 검증
   - @OneToOne, @OneToMany, @ManyToOne 관계 검증
   - Foreign Key 제약조건 확인

5. [Response] 검증 결과 반환
   - 유효한 Entity 목록
   - 문제가 있는 Entity 및 이슈 상세
```

---

## 📊 2. 인덱스 상태 조회 (Index Status)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/admin/schema/indexes` |
| **설명** | 데이터베이스 인덱스 상태 및 사용률 조회 |
| **인증** | `ADMIN` 역할 필수 |
| **난이도** | **중 (Medium)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Query Parameters

| 파라미터명 | 타입 | 필수 | 설명 |
|:---|:---|:---:|:---|
| `table` | `String` | ❌ | 특정 테이블 필터링 |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "인덱스 상태 조회 완료",
  "data": {
    "indexes": [
      {
        "tableName": "users",
        "indexName": "idx_users_email",
        "columns": ["email"],
        "type": "UNIQUE",
        "cardinality": 1250,
        "sizeKB": 64
      },
      {
        "tableName": "health_reports",
        "indexName": "idx_reports_user_period",
        "columns": ["user_id", "period_start", "period_end"],
        "type": "NON_UNIQUE",
        "cardinality": 850,
        "sizeKB": 128
      }
    ],
    "summary": {
      "totalIndexes": 25,
      "totalSizeKB": 2048,
      "unusedIndexes": 2
    }
  }
}
```

### Logic Steps

```
1. [Query Metadata] INFORMATION_SCHEMA에서 인덱스 정보 조회
   - SHOW INDEX FROM {table} 또는 INFORMATION_SCHEMA.STATISTICS

2. [Calculate Statistics] 인덱스 사용률 계산
   - Cardinality, Size 정보 수집

3. [Response] 인덱스 목록 및 통계 반환
```

---

## 🔧 3. 데이터베이스 마이그레이션 상태 조회 (Migration Status)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/admin/schema/migrations` |
| **설명** | Flyway/Liquibase 마이그레이션 상태 조회 |
| **인증** | `ADMIN` 역할 필수 |
| **난이도** | **하 (Low)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "마이그레이션 상태 조회 완료",
  "data": {
    "currentVersion": "1.0.5",
    "migrations": [
      {
        "version": "1.0.1",
        "description": "Create users table",
        "executedAt": "2025-01-10T09:00:00",
        "executionTime": 1250,
        "status": "SUCCESS"
      },
      {
        "version": "1.0.5",
        "description": "Add indexes to health_reports",
        "executedAt": "2025-01-15T14:30:00",
        "executionTime": 320,
        "status": "SUCCESS"
      }
    ],
    "pendingMigrations": []
  }
}
```

### Logic Steps

```
1. [Query Flyway] Flyway 메타데이터 테이블 조회
   - flyway_schema_history 테이블에서 마이그레이션 이력 조회

2. [Parse Results] 마이그레이션 정보 파싱
   - 버전, 설명, 실행 시간, 상태 추출

3. [Response] 마이그레이션 목록 반환
```

---

## 📊 API 요약 테이블

| # | Method | Endpoint | 설명 | 인증 | 난이도 |
|:---:|:---|:---|:---|:---:|:---:|
| 1 | `GET` | `/api/v1/admin/schema/validate` | 스키마 검증 | ADMIN | 중 |
| 2 | `GET` | `/api/v1/admin/schema/indexes` | 인덱스 상태 조회 | ADMIN | 중 |
| 3 | `GET` | `/api/v1/admin/schema/migrations` | 마이그레이션 상태 조회 | ADMIN | 하 |

---

## 🏗️ 구현 참고사항

### 1. 스키마 검증 서비스 예시

```java
@Service
@RequiredArgsConstructor
public class SchemaValidationService {
    
    private final EntityManager entityManager;
    private final JpaMetamodel jpaMetamodel;
    
    public SchemaValidationResult validateSchema() {
        // Entity 메타데이터 수집
        Set<EntityType<?>> entities = jpaMetamodel.getEntities();
        
        // DB 메타데이터 조회
        DatabaseMetaData metaData = entityManager.unwrap(Connection.class)
            .getMetaData();
        
        // 매핑 검증 로직
        List<EntityValidationResult> results = entities.stream()
            .map(entity -> validateEntity(entity, metaData))
            .toList();
        
        return SchemaValidationResult.builder()
            .validationResults(results)
            .build();
    }
}
```

---

## 📝 참고 자료

- [BE] Issue-02: 통합 데이터 모델링 (ERD) 설계 (`studio/Tasks/BE_issue/issue-02-db-design.md`)
- SRS 6.2 Entity & Data Model
- Java Spring Boot 3.x Cursor Rules (`.cursor/rules/300-java-spring-cursor-rules.mdc`)


# [BE] Issue-06: 행동 카드(Action Card) 및 코칭 도메인 API 명세서

## 📋 개요

**SRS REQ-FUNC-011~014**에 따라 매일 1~3개의 행동 카드를 생성하고 수행 결과를 추적하는 API 명세서입니다.

**기술 스택**: Java 17+, Spring Boot 3.4+, MySQL 9.x, Jakarta EE 10

---

## 📅 1. 오늘의 행동 카드 조회 (Get Today Cards)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/actions/today` |
| **설명** | 오늘 날짜 기준 사용자의 행동 카드 목록 조회 |
| **인증** | `Bearer Token` 필수 |
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
  "message": "조회 성공",
  "data": [
    {
      "cardId": "550e8400-e29b-41d4-a716-446655440000",
      "targetDate": "2025-01-15",
      "category": "EXERCISE",
      "title": "10분 가볍게 걷기",
      "description": "어제 걸음수가 부족했습니다. 오늘은 10분 정도 가볍게 걸어보세요.",
      "status": "PENDING",
      "priority": 1,
      "completedAt": null
    },
    {
      "cardId": "660e8400-e29b-41d4-a716-446655440001",
      "targetDate": "2025-01-15",
      "category": "MEDICATION",
      "title": "약 복용 확인",
      "description": "오전 약을 복용하셨나요?",
      "status": "PENDING",
      "priority": 1,
      "completedAt": null
    },
    {
      "cardId": "770e8400-e29b-41d4-a716-446655440002",
      "targetDate": "2025-01-15",
      "category": "CHECKUP",
      "title": "혈압 측정하기",
      "description": "3일 연속 혈압을 측정하지 않았습니다.",
      "status": "PENDING",
      "priority": 2,
      "completedAt": null
    }
  ]
}
```

### Logic Steps

```
1. [Get Today] 오늘 날짜 확인
   - LocalDate.now()

2. [Find Cards] 오늘 날짜 카드 조회
   - ActionCardRepository.findByUserIdAndTargetDateOrderByPriority()
   - 우선순위 정렬

3. [Response] 카드 목록 반환
   - ActionCardRes.from() 변환
```

---

## 📆 2. 특정 날짜 행동 카드 조회 (Get Cards By Date)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/actions` |
| **설명** | 특정 날짜의 행동 카드 목록 조회 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **하 (Low)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Query Parameters

| 파라미터명 | 타입 | 필수 | 설명 |
|:---|:---|:---:|:---|
| `date` | `LocalDate` | ❌ | 조회할 날짜 (기본값: 오늘) |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "조회 성공",
  "data": [
    {
      "cardId": "550e8400-e29b-41d4-a716-446655440000",
      "targetDate": "2025-01-14",
      "category": "EXERCISE",
      "title": "10분 가볍게 걷기",
      "status": "COMPLETED",
      "priority": 1,
      "completedAt": "2025-01-14T14:30:00"
    }
  ]
}
```

### Logic Steps

```
1. [Parse Date] 날짜 파라미터 파싱
   - date가 없으면 오늘 날짜 사용

2. [Find Cards] 특정 날짜 카드 조회
   - ActionCardRepository.findByUserIdAndTargetDateOrderByPriority()

3. [Response] 카드 목록 반환
```

---

## ✅ 3. 행동 카드 완료 처리 (Complete Card)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/actions/{cardId}/complete` |
| **설명** | 행동 카드 완료 처리 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **하 (Low)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Path Parameters

| 파라미터명 | 타입 | 필수 | 설명 |
|:---|:---|:---:|:---|
| `cardId` | `UUID` | ✅ | 카드 ID |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "카드가 완료되었습니다.",
  "data": {
    "cardId": "550e8400-e29b-41d4-a716-446655440000",
    "targetDate": "2025-01-15",
    "category": "EXERCISE",
    "title": "10분 가볍게 걷기",
    "status": "COMPLETED",
    "priority": 1,
    "completedAt": "2025-01-15T14:30:00"
  }
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `404` | `/errors/card-not-found` | 카드 없음 | 카드를 찾을 수 없음 |
| `400` | `/errors/card-not-completable` | 완료 불가 | 이미 완료/스킵/만료된 카드 |

### Logic Steps

```
1. [Find Card] 카드 조회
   - ActionCardRepository.findByUserIdAndId()
   - 사용자 소유 여부 확인

2. [Validate Status] 완료 가능 여부 확인
   - card.isCompletable() 확인
   - PENDING 상태만 완료 가능

3. [Complete Card] 카드 완료 처리
   - card.complete()
   - completedAt = now()

4. [Update Statistics] 일별 통계 갱신
   - ActionStatisticsService.updateDailyStatistics()
   - 완료율 재계산

5. [Save] 카드 저장
   - ActionCardRepository.save(card)

6. [Response] 완료된 카드 정보 반환
```

---

## ⏭️ 4. 행동 카드 스킵 처리 (Skip Card)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/actions/{cardId}/skip` |
| **설명** | 행동 카드 스킵 처리 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **하 (Low)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Path Parameters

| 파라미터명 | 타입 | 필수 | 설명 |
|:---|:---|:---:|:---|
| `cardId` | `UUID` | ✅ | 카드 ID |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "카드를 건너뛰었습니다.",
  "data": {
    "cardId": "550e8400-e29b-41d4-a716-446655440000",
    "targetDate": "2025-01-15",
    "category": "EXERCISE",
    "title": "10분 가볍게 걷기",
    "status": "SKIPPED",
    "priority": 1,
    "completedAt": null
  }
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `404` | `/errors/card-not-found` | 카드 없음 | 카드를 찾을 수 없음 |
| `400` | `/errors/card-not-completable` | 스킵 불가 | 이미 완료/스킵/만료된 카드 |

### Logic Steps

```
1. [Find Card] 카드 조회

2. [Validate Status] 스킵 가능 여부 확인
   - card.isCompletable() 확인

3. [Skip Card] 카드 스킵 처리
   - card.skip()
   - status = SKIPPED

4. [Update Statistics] 일별 통계 갱신
   - ActionStatisticsService.updateDailyStatistics()

5. [Save] 카드 저장

6. [Response] 스킵된 카드 정보 반환
```

---

## 📊 5. 행동 카드 통계 조회 (Get Statistics)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/actions/statistics` |
| **설명** | 행동 카드 완료 통계 조회 (D1, W1 완료율 포함) |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **중 (Medium)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Query Parameters

| 파라미터명 | 타입 | 필수 | 설명 |
|:---|:---|:---:|:---|
| `period` | `Enum` | ❌ | 통계 기간 | `DAY`, `WEEK`, `MONTH` (기본값: WEEK) |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "통계 조회 성공",
  "data": {
    "d1CompletionRate": 0.85,
    "w1CompletionRate": 0.72,
    "overallCompletionRate": 0.68,
    "categoryStats": {
      "EXERCISE": {
        "total": 45,
        "completed": 32,
        "skipped": 8,
        "completionRate": 0.71
      },
      "MEDICATION": {
        "total": 30,
        "completed": 28,
        "skipped": 1,
        "completionRate": 0.93
      },
      "CHECKUP": {
        "total": 15,
        "completed": 10,
        "skipped": 3,
        "completionRate": 0.67
      }
    },
    "dailyTrend": [
      {
        "date": "2025-01-08",
        "totalCards": 3,
        "completedCards": 2,
        "completionRate": 0.67
      },
      {
        "date": "2025-01-09",
        "totalCards": 3,
        "completedCards": 3,
        "completionRate": 1.0
      }
    ]
  }
}
```

### Logic Steps

```
1. [Calculate D1] D1 완료율 계산
   - 첫날 완료 비율
   - ActionStatisticsService.calculateD1CompletionRate()

2. [Calculate W1] W1 완료율 계산
   - 첫 주 3일 이상 완료 비율
   - ActionStatisticsService.calculateW1CompletionRate()

3. [Calculate Overall] 전체 완료율 계산
   - 기간별 평균 완료율
   - ActionStatisticsRepository.calculateAverageCompletionRate()

4. [Category Stats] 카테고리별 통계 계산
   - ActionStatisticsService.calculateCategoryStats()

5. [Daily Trend] 일별 추이 조회
   - ActionStatisticsRepository.findByUserIdAndStatDateBetween()

6. [Response] 통계 정보 반환
   - ActionStatsRes 생성
```

---

## 📊 API 요약 테이블

| # | Method | Endpoint | 설명 | 인증 | 난이도 |
|:---:|:---|:---|:---|:---:|:---:|
| 1 | `GET` | `/api/v1/actions/today` | 오늘의 카드 조회 | ✅ | 하 |
| 2 | `GET` | `/api/v1/actions` | 특정 날짜 카드 조회 | ✅ | 하 |
| 3 | `POST` | `/api/v1/actions/{id}/complete` | 카드 완료 처리 | ✅ | 하 |
| 4 | `POST` | `/api/v1/actions/{id}/skip` | 카드 스킵 처리 | ✅ | 하 |
| 5 | `GET` | `/api/v1/actions/statistics` | 완료 통계 조회 | ✅ | 중 |

---

## 🏗️ 구현 참고사항

### 1. 일일 카드 생성 스케줄러

```java
@Component
@RequiredArgsConstructor
public class ActionGenerationScheduler {
    
    private final ActionGenerationService generationService;
    
    /**
     * 일일 행동 카드 생성 (매일 06:00)
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void generateDailyCards() {
        int totalCards = generationService.generateCardsForAllUsers();
        log.info("일일 행동 카드 생성 완료: 총 {}개 카드 생성", totalCards);
    }
}
```

### 2. DTO 예시

```java
public record ActionCardRes(
    UUID cardId,
    LocalDate targetDate,
    ActionCategory category,
    String title,
    String description,
    ActionStatus status,
    int priority,
    LocalDateTime completedAt
) {
    public static ActionCardRes from(ActionCard card) {
        return new ActionCardRes(
            card.getId(),
            card.getTargetDate(),
            card.getCategory(),
            card.getTitle(),
            card.getDescription(),
            card.getStatus(),
            card.getPriority(),
            card.getCompletedAt()
        );
    }
}

public record ActionStatsRes(
    float d1CompletionRate,
    float w1CompletionRate,
    float overallCompletionRate,
    Map<ActionCategory, CategoryStats> categoryStats,
    List<DailyStats> dailyTrend
) {}
```

---

## 📝 참고 자료

- [BE] Issue-06: 행동 카드(Action Card) 및 코칭 도메인 구현 (`studio/Tasks/BE_issue/issue-06-action-domain.md`)
- SRS REQ-FUNC-011~014
- SRS 6.2.4 ActionCard
- Java Spring Boot 3.x Cursor Rules (`.cursor/rules/300-java-spring-cursor-rules.mdc`)


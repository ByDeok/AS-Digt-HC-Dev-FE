# [BE] Issue-05: 1장 요약 리포트(Health Report) 도메인 API 명세서

## 📋 개요

**SRS REQ-FUNC-007~010**에 따라 건강 데이터를 집계하여 표준화된 리포트를 생성하고 조회하는 API 명세서입니다.

**기술 스택**: Java 17+, Spring Boot 3.4+, MySQL 9.x, Jakarta EE 10

---

## 📊 1. 리포트 생성 (Generate Report)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/reports/generate` |
| **설명** | 건강 데이터 집계하여 1장 의사용 요약 리포트 생성 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **상 (High)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Request Body

| 필드명 | 타입 | 필수 | 설명 | Validation |
|:---|:---|:---:|:---|:---|
| `periodStart` | `LocalDate` | ❌ | 리포트 기간 시작일 | 기본값: 3개월 전 |
| `periodEnd` | `LocalDate` | ❌ | 리포트 기간 종료일 | 기본값: 오늘 |
| `reportType` | `Enum` | ❌ | 리포트 타입 | `REGULAR`(기본), `CLINIC_VISIT`, `CUSTOM` |

#### Request Body 예시

```json
{
  "periodStart": "2024-09-01",
  "periodEnd": "2024-11-30",
  "reportType": "REGULAR"
}
```

### Response Body

**✅ 성공 (201 Created)**

```json
{
  "success": true,
  "message": "리포트가 생성되었습니다.",
  "data": {
    "reportId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "GENERATED",
    "message": "리포트 생성이 완료되었습니다."
  }
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `400` | `/errors/insufficient-data` | 데이터 부족 | 리포트 생성에 필요한 최소 데이터 없음 |
| `400` | `/errors/invalid-period` | 잘못된 기간 | 기간이 너무 짧거나 미래 날짜 포함 |

### Logic Steps

```
1. [Validate Period] 리포트 기간 유효성 검증
   - periodStart < periodEnd 확인
   - 기간이 1일 이상인지 확인
   - 미래 날짜 포함 여부 확인

2. [Create Report] HealthReport Entity 생성
   - status = DRAFT
   - periodStart, periodEnd 설정

3. [Aggregate Metrics] 건강 지표 집계
   - ReportAggregationService.aggregateMetrics()
   - 활동, 심박, 혈압, 체중, 수면 데이터 집계
   - 평균, 최소, 최대 값 계산

4. [Calculate Context] 맥락 정보 생성
   - 사용 기기 정보 수집
   - 데이터 완결성 계산
   - 결측 구간 탐지

5. [Analyze Trends] 추세 분석
   - 월별/주별 집계
   - 방향성 분석 (IMPROVING, STABLE, DECLINING)

6. [Save Report] 리포트 저장
   - metrics, context, trends JSON 저장
   - status = GENERATED
   - generatedAt = now()

7. [Generate PDF] PDF 생성 (비동기 권장)
   - PdfGeneratorService.generatePdf()
   - pdfUrl 저장

8. [Response] 리포트 생성 결과 반환
```

---

## 📋 2. 리포트 목록 조회 (Get Report List)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/reports` |
| **설명** | 사용자의 리포트 목록 조회 (페이징) |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **하 (Low)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Query Parameters

| 파라미터명 | 타입 | 필수 | 설명 |
|:---|:---|:---:|:---|
| `page` | `Integer` | ❌ | 페이지 번호 (기본값: 0) |
| `size` | `Integer` | ❌ | 페이지 크기 (기본값: 10) |
| `status` | `Enum` | ❌ | 리포트 상태 필터링 |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "조회 성공",
  "data": {
    "content": [
      {
        "reportId": "550e8400-e29b-41d4-a716-446655440000",
        "periodStart": "2024-09-01",
        "periodEnd": "2024-11-30",
        "reportType": "REGULAR",
        "status": "GENERATED",
        "generatedAt": "2024-12-01T10:00:00",
        "hasPdf": true
      }
    ],
    "page": {
      "number": 0,
      "size": 10,
      "totalElements": 5,
      "totalPages": 1
    }
  }
}
```

### Logic Steps

```
1. [Find Reports] 사용자의 리포트 목록 조회
   - HealthReportRepository.findAllByUserIdOrderByGeneratedAtDesc()
   - Pageable 적용

2. [Response] 리포트 목록 반환
   - ReportSummaryRes.from() 변환
   - 페이징 정보 포함
```

---

## 📄 3. 리포트 상세 조회 (Get Report Detail)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/reports/{reportId}` |
| **설명** | 리포트 상세 정보 조회 (지표, 맥락, 추세 포함) |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **하 (Low)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Path Parameters

| 파라미터명 | 타입 | 필수 | 설명 |
|:---|:---|:---:|:---|
| `reportId` | `UUID` | ✅ | 리포트 ID |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "조회 성공",
  "data": {
    "reportId": "550e8400-e29b-41d4-a716-446655440000",
    "periodStart": "2024-09-01",
    "periodEnd": "2024-11-30",
    "reportType": "REGULAR",
    "metrics": {
      "activity": {
        "dailySteps": {
          "average": 6500,
          "min": 2100,
          "max": 12400,
          "unit": "steps",
          "dataPoints": 85
        },
        "activeMinutes": {
          "average": 42,
          "unit": "minutes"
        }
      },
      "heartRate": {
        "restingHR": {
          "average": 68,
          "min": 58,
          "max": 72,
          "unit": "bpm"
        }
      },
      "bloodPressure": {
        "systolic": {
          "average": 128,
          "min": 118,
          "max": 142,
          "unit": "mmHg"
        },
        "diastolic": {
          "average": 82,
          "min": 72,
          "max": 95,
          "unit": "mmHg"
        }
      },
      "weight": {
        "current": 72.5,
        "periodStart": 74.2,
        "change": -1.7,
        "bmi": 24.2,
        "unit": "kg"
      }
    },
    "context": {
      "devices": [
        {
          "vendor": "samsung",
          "type": "watch",
          "model": "Galaxy Watch 6",
          "metrics": ["steps", "heartRate", "sleep"]
        }
      ],
      "completeness": {
        "steps": { "rate": 95, "missingDays": 4 },
        "bloodPressure": { "rate": 78, "missingDays": 20 }
      },
      "missingPeriods": [
        {
          "metric": "bloodPressure",
          "start": "2024-11-15",
          "end": "2024-11-20",
          "reason": "기기 미연동"
        }
      ]
    },
    "trends": {
      "monthly": {
        "steps": [
          { "month": "2024-09", "average": 5800 },
          { "month": "2024-10", "average": 6200 },
          { "month": "2024-11", "average": 7500 }
        ]
      },
      "direction": {
        "steps": "IMPROVING",
        "bloodPressure": "IMPROVING",
        "weight": "STABLE"
      }
    },
    "pdfUrl": "https://storage.example.com/reports/550e8400...",
    "generatedAt": "2024-12-01T10:00:00",
    "viewedAt": null
  }
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `404` | `/errors/report-not-found` | 리포트 없음 | 리포트를 찾을 수 없음 |
| `403` | `/errors/access-denied` | 접근 거부 | 다른 사용자의 리포트 |

### Logic Steps

```
1. [Find Report] 리포트 조회
   - HealthReportRepository.findByUserIdAndId()
   - 사용자 소유 여부 확인

2. [Record View] 열람 기록
   - ReportView.create(report, userId, WEB)
   - report.markViewed()

3. [Response] 리포트 상세 정보 반환
   - ReportDetailRes.from(report)
   - metrics, context, trends JSON 포함
```

---

## 📥 4. PDF 다운로드 (Download PDF)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/reports/{reportId}/pdf` |
| **설명** | 리포트 PDF 파일 다운로드 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **중 (Medium)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Path Parameters

| 파라미터명 | 타입 | 필수 | 설명 |
|:---|:---|:---:|:---|
| `reportId` | `UUID` | ✅ | 리포트 ID |

### Response Body

**✅ 성공 (200 OK)**

- Content-Type: `application/pdf`
- Content-Disposition: `attachment; filename="health-report-2024-12-01.pdf"`
- Body: PDF 바이너리 데이터

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `404` | `/errors/report-not-found` | 리포트 없음 | 리포트를 찾을 수 없음 |
| `404` | `/errors/pdf-not-found` | PDF 없음 | PDF 파일이 아직 생성되지 않음 |

### Logic Steps

```
1. [Find Report] 리포트 조회
   - HealthReportRepository.findByUserIdAndId()

2. [Check PDF] PDF 존재 여부 확인
   - report.hasPdf() 확인
   - 없으면 PDF 생성 후 반환

3. [Load PDF] PDF 파일 로드
   - PdfGeneratorService.loadPdf(pdfUrl)

4. [Record View] 열람 기록
   - ReportView.create(report, userId, PDF)

5. [Response] PDF 파일 반환
   - ResponseEntity<Resource>
   - Content-Type: application/pdf
```

---

## 🗑️ 5. 리포트 삭제 (Delete Report)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `DELETE /api/v1/reports/{reportId}` |
| **설명** | 리포트 삭제 (PDF 파일 포함) |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **하 (Low)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Path Parameters

| 파라미터명 | 타입 | 필수 | 설명 |
|:---|:---|:---:|:---|
| `reportId` | `UUID` | ✅ | 리포트 ID |

### Response Body

**✅ 성공 (204 No Content)**

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `404` | `/errors/report-not-found` | 리포트 없음 | 리포트를 찾을 수 없음 |
| `403` | `/errors/access-denied` | 접근 거부 | 다른 사용자의 리포트 |

### Logic Steps

```
1. [Find Report] 리포트 조회
   - HealthReportRepository.findByUserIdAndId()

2. [Delete PDF] PDF 파일 삭제
   - PdfGeneratorService.deletePdf(pdfUrl)

3. [Delete Report] 리포트 삭제
   - HealthReportRepository.delete(report)
```

---

## 📊 API 요약 테이블

| # | Method | Endpoint | 설명 | 인증 | 난이도 |
|:---:|:---|:---|:---|:---:|:---:|
| 1 | `POST` | `/api/v1/reports/generate` | 리포트 생성 | ✅ | 상 |
| 2 | `GET` | `/api/v1/reports` | 리포트 목록 조회 | ✅ | 하 |
| 3 | `GET` | `/api/v1/reports/{id}` | 리포트 상세 조회 | ✅ | 하 |
| 4 | `GET` | `/api/v1/reports/{id}/pdf` | PDF 다운로드 | ✅ | 중 |
| 5 | `DELETE` | `/api/v1/reports/{id}` | 리포트 삭제 | ✅ | 하 |

---

## 🏗️ 구현 참고사항

### 1. 리포트 집계 서비스 예시

```java
@Service
@RequiredArgsConstructor
public class ReportAggregationService {
    
    private final HealthDataDailyRepository dataRepository;
    private final MetricCalculatorFactory calculatorFactory;
    
    public Map<String, Object> aggregateMetrics(UUID userId, 
                                                 LocalDate start, 
                                                 LocalDate end) {
        Map<String, Object> metrics = new HashMap<>();
        
        // 각 지표 타입별 집계
        for (MetricType type : MetricType.values()) {
            List<HealthDataDaily> data = dataRepository
                .findByUserIdAndMetricTypeAndRecordDateBetween(
                    userId, type, start, end);
            
            MetricCalculator calculator = calculatorFactory.getCalculator(type);
            MetricResult result = calculator.calculate(data);
            
            metrics.put(type.name().toLowerCase(), result.toMap());
        }
        
        return metrics;
    }
}
```

### 2. DTO 예시

```java
public record GenerateReportReq(
    @Past LocalDate periodStart,
    @PastOrPresent LocalDate periodEnd,
    ReportType reportType
) {}

public record ReportDetailRes(
    UUID reportId,
    LocalDate periodStart,
    LocalDate periodEnd,
    ReportType reportType,
    Map<String, Object> metrics,
    Map<String, Object> context,
    Map<String, Object> trends,
    String pdfUrl,
    LocalDateTime generatedAt
) {
    public static ReportDetailRes from(HealthReport report) {
        return new ReportDetailRes(
            report.getId(),
            report.getPeriodStart(),
            report.getPeriodEnd(),
            report.getReportType(),
            report.getMetrics(),
            report.getContext(),
            report.getTrends(),
            report.getPdfUrl(),
            report.getGeneratedAt()
        );
    }
}
```

---

## 📝 참고 자료

- [BE] Issue-05: 1장 요약 리포트(Health Report) 도메인 구현 (`studio/Tasks/BE_issue/issue-05-report-domain.md`)
- SRS REQ-FUNC-007~010
- SRS 6.2.3 HealthReport
- Java Spring Boot 3.x Cursor Rules (`.cursor/rules/300-java-spring-cursor-rules.mdc`)


# 오류 처리 및 로깅 규칙
설명: 관찰 가능성 및 디버깅을 위한 표준화된 오류 처리 및 로깅입니다.

## 예외 처리
- **표준**: RFC 7807 (Problem Details) 사용
- **전역 핸들러**: `@RestControllerAdvice` 사용
- **계층 구조**:
  - `BusinessException` (기본 런타임 예외)
  - `EntityNotFoundException`, `InvalidStateException`, `AccessDeniedException`

```java
@ExceptionHandler(BusinessException.class)
public ProblemDetail handleBusinessException(BusinessException e) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(e.getStatus(), e.getMessage());
    problem.setProperty("code", e.getErrorCode());
    return problem;
}
```

## 로깅 (SLF4J + Logback)
- **레벨**:
  - `ERROR`: 시스템 장애, 복구 불가능한 오류 (알림 트리거)
  - `WARN`: 복구 가능한 비즈니스 오류, 사용 중단
  - `INFO`: 주요 생명주기 이벤트 (시작, 스케줄러 작업 완료)
  - `DEBUG`: 상세한 흐름 정보 (개발/테스트 전용)
- **형식**: 파싱을 쉽게 하기 위해 프로덕션에서 JSON 형식 (Logstash/ECS) 사용
- **컨텍스트**: 분산 추적을 위해 모든 로그에 `traceId` 및 `spanId` (MDC) 포함

## 모니터링 통합 (REQ-NF-010)
- **메트릭**: 스크래핑을 위해 `/actuator/prometheus` 노출
- **헬스 체크**: 외부 종속성(포털 API, 디바이스 SDK)에 대한 사용자 정의 `HealthIndicator` 구현

## 참고
- [300-java-spring-cursor-rules.md](300-java-spring-cursor-rules.md)

# Java Spring Boot 3.x Cursor 규칙

## 핵심 원칙
- **버전**: Java 17+, Spring Boot 3.4+
- **표준**: `javax.*` 대신 `jakarta.*` 패키지 사용 (Jakarta EE 10)
- **아키텍처**: 계층형 아키텍처 (Controller -> Service -> Repository)
- **패러다임**: 가능한 경우 함수형 스타일과 불변성 선호

## 코드 스타일 및 규칙

### 클래스 및 인터페이스
- **컨트롤러**: 버전이 있는 `RequestMapping`을 가진 `RestController` (v1)
- **서비스**: 여러 구현이 존재하지 않는 한 인터페이스 기반 서비스는 선택사항
- **DTO**: 모든 데이터 전송 객체(Request/Response)에 Java `record` 사용
  ```java
  public record UserResponse(String id, String name) {}
  ```
- **엔티티**: Lombok `@Getter`, `@NoArgsConstructor(access = AccessLevel.PROTECTED)` 사용. 일관성을 유지하기 위해 엔티티에 `@Data` 또는 `@Setter` 피하기

### Spring Boot 3.x 특수사항
- **관찰**: Spring Cloud Sleuth 대신 Micrometer Tracing 사용
- **검증**: `jakarta.validation.constraints` 사용 (@NotNull, @Size 등)
- **HTTP 클라이언트**: `RestTemplate` 대신 `RestClient` (동기) 또는 `WebClient` (반응형) 사용
- **오류 처리**: `ResponseEntityExceptionHandler`를 확장하는 `@ControllerAdvice`를 통해 `ProblemDetails` (RFC 7807) 구현

### 데이터베이스 및 트랜잭션
- **트랜잭션**: 클래스 레벨에서 `@Transactional(readOnly = true)` 사용, 쓰기 메서드에서 `@Transactional`로 오버라이드
- **JPA**: 모든 관계(`@ManyToOne`, `@OneToMany`)에 `FetchType.LAZY` 사용
- **감사**: `createdAt`, `updatedAt`을 위한 `BaseTimeEntity` (@MappedSuperclass) 확장

## 명명 규칙
- **클래스**: PascalCase (예: `OnboardingService`)
- **메서드/변수**: camelCase (예: `findUserById`)
- **상수**: UPPER_SNAKE_CASE
- **테이블**: lower_snake_case (예: `users`, `onboarding_sessions`)

## 참고
- 테스트 표준은 [310-testing-strategy-rules.md](310-testing-strategy-rules.md) 참조
- OpenAPI/Swagger는 [311-api-documentation-rules.md](311-api-documentation-rules.md) 참조

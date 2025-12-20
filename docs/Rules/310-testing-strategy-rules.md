# 테스트 전략 규칙
설명: Spring Boot 3.x에서 단위, 통합, 슬라이스 테스트에 대한 표준을 정의합니다.

## 일반 원칙
- **라이브러리**: JUnit 5 (Jupiter), AssertJ, Mockito
- **목표**: Service 및 Utils에서 >80% 라인 커버리지 목표
- **명명**: `TargetClass` + `Test` (예: `UserServiceTest`). 메서드 이름은 설명적이어야 함 (BDD 스타일: `given_when_then`)

## 단위 테스트 (빠름)
- **범위**: 도메인 로직, 유틸리티 클래스
- **주석**: `@ExtendWith(MockitoExtension.class)`
- **모킹**: `@Mock` 및 `@InjectMocks` 사용. 단위 테스트의 경우 Spring Context 로딩(`@SpringBootTest`) 피하기

```java
@Test
void givenValidUser_whenRegister_thenReturnsUser() {
    // given
    // when
    // then
}
```

## 통합 테스트 (신뢰성)
- **범위**: 저장소 계층, 복잡한 서비스 워크플로우, 외부 통합
- **컨테이너**: MySQL, Redis, Kafka에 **TestContainers** 사용. 프로덕션이 MySQL인 경우 방언 호환성을 보장하기 위해 H2(인메모리)에 의존하지 않기
- **주석**: `@SpringBootTest`, `@Testcontainers`

```java
@Container
static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:9.0");
```

## 슬라이스 테스트
- **컨트롤러**: `@WebMvcTest(UserController.class)`. 서비스 계층 모킹
- **저장소**: `@DataJpaTest`. 쿼리 정확성 및 매핑에 집중

## 참고
- [300-java-spring-cursor-rules.md](300-java-spring-cursor-rules.md)

# 보안 및 감사 규칙
설명: Spring Security 6.x, OAuth2, 감사 로깅(의료 규정 준수)에 대한 가이드라인입니다.

## Spring Security 6.x
- **구성**: Lambda DSL(함수형 스타일) 사용
- **상태**: 무상태 세션 관리 (`SessionCreationPolicy.STATELESS`)
- **인증**: JWT (JSON Web Token) 또는 불투명 토큰 내부 검사
- **CSRF**: REST API의 경우 비활성화하지만 CORS가 엄격하게 구성되어 있는지 확인

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/public/**").permitAll()
            .anyRequest().authenticated()
        );
    return http.build();
}
```

## 감사 로깅 (SRS REQ-NF-006)
- **목표**: 누가(WHO) 무엇을(WHAT) 어떤 리소스에(WHICH) 언제(WHEN) 했는지 추적
- **엔티티 감사**: `createdAt`, `updatedAt`에 대해 `@EntityListeners(AuditingEntityListener.class)` 사용
- **비즈니스 감사**:
  - 전용 `AuditLog` 엔티티 생성 (SRS 6.2.7 참조)
  - 비즈니스 로직에서 감사 로직을 분리하기 위해 AOP(`@Aspect`) 또는 ApplicationEvents 사용
  - 기록해야 함: `userId`, `actionType` (VIEW, CREATE), `resourceId`, `ipAddress`

## 민감한 데이터
- **PII/PHI**: 데이터베이스 암호화가 불충분한 경우 JPA AttributeConverter와 `@Convert`를 사용하여 민감한 열 암호화 (AES-256)
- **마스킹**: 로그에서 이름/전화번호 마스킹 (전체 PII를 로그에 기록하지 않기)

## 참고
- [300-java-spring-cursor-rules.md](300-java-spring-cursor-rules.md)

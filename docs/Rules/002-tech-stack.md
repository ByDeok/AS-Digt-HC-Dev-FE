# 기술 스택 (백엔드 중심)
참조: [001-project-overview.md](001-project-overview.md)

## 핵심 백엔드
- **언어**: Java 17 LTS
- **프레임워크**: Spring Boot 3.4+
- **빌드 도구**: Gradle (Groovy DSL)
- **ORM**: Spring Data JPA (Hibernate 6.x), QueryDSL 5.x

## 데이터베이스 및 스토리지
- **RDBMS**: MySQL 9.x (InnoDB, utf8mb4)
- **캐시/세션**: Redis (Lettuce Client)
- **메시징**: Apache Kafka (이벤트 기반 아키텍처)

## 아키텍처 컴포넌트
- **API 패턴**: RESTful API (OpenAPI 3.x), gRPC (내부, 향후)
- **보안**: Spring Security 6.x, OAuth2/OIDC (Google, Kakao)
- **로깅/모니터링**: SLF4J, Logback, Micrometer

## 외부 서비스 연동
- **AI/ML**: OpenAI API (RestClient를 통한), LangChain (Python 서비스 연동)
- **디바이스**: 벤더 OAuth2 API (Samsung Health 등)

## 참고
- 구현 세부사항은 [300-java-spring-cursor-rules.md](300-java-spring-cursor-rules.md) 참조

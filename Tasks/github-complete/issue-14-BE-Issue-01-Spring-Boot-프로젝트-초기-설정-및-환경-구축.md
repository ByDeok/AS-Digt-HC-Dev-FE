# [BE] Issue-01: Spring Boot 프로젝트 초기 설정 및 환경 구축

## 1. 개요
**SRS 1.5 Assumptions & Constraints** (C-TEC-002, C-TEC-003)에 따라 백엔드 개발 환경을 구축합니다.

## 2. 작업 워크플로우 (설계 및 구현)

| 단계 | 입력(Input) | 도구(Tool) | 출력(Output) |
| --- | --- | --- | --- |
| **Plan** | 기술 스택 요구사항(Java 17, Spring Boot 3.x) | Cursor | build.gradle, 패키지 구조 설계 |
| **Data Schema Design** | DB 연결 정보 | IntelliJ/DBeaver | Local MySQL/H2 연결 설정 |
| **Implementation** | 설정 파일 | Cursor | application.yml, Global Config Code |
| **Review** | 실행 로그 | IDE Console | 서버 정상 부트스트랩 확인 |

## 3. 상세 요구사항 (To-Do)

- [ ] **Project Initialization**
    - Java 17, Spring Boot 3.4.x 기반 프로젝트 생성
    - 의존성 추가: Web, JPA, Security, Validation, Lombok, MySQL Driver, H2(Test용)
- [ ] **Database Configuration**
    - `application.yml` 설정 (Profile 분리: local, dev)
    - MySQL 9.x 연동 설정 (Unicode/UTF-8mb4 지원)
    - JPA Hibernate 설정 (`ddl-auto: validate` or `update` for local)
- [ ] **Standard Package Structure**
    - Layered Architecture 기반 패키지 구조화 (`domain`, `application`, `infrastructure`, `interfaces`)
    - 공통 Response/Error Handling 클래스 작성 (`ApiResponse`, `GlobalExceptionHandler`)
- [ ] **Git & CI Basic**
    - `.gitignore` 설정 (IntelliJ, Java, Gradle, OS Files)

## 4. 참고 자료
- SRS 1.5 Assumptions & Constraints
- `studio/300-java-spring-cursor-rules.mdc`


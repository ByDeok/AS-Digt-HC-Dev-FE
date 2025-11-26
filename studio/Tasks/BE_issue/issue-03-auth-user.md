# [BE] Issue-03: 사용자 인증(Auth) 및 회원 도메인 구현

## 1. 개요
**SRS 4.1 REQ-FUNC-001, 002** 및 보안 요구사항에 따라 사용자 가입, 로그인, JWT 인증, 역할(Role) 관리를 구현합니다.

## 2. 작업 워크플로우 (설계 및 구현)

| 단계 | 입력(Input) | 도구(Tool) | 출력(Output) |
| --- | --- | --- | --- |
| **Plan** | REQ-FUNC-001~002 | Cursor | API 명세서 (Swagger/Markdown) |
| **Data Schema Design** | User, Profile Table | Mermaid.js | ERD Update (필요 시) |
| **Interaction Design** | Auth Flow | Cursor | **Member/Auth JPA Entity & Security Config** |
| **Review** | 구현 코드 | GitHub PR | Security Filter Chain 동작 확인 |

## 3. 상세 요구사항 (To-Do)

- [ ] **Domain: User & Profile**
    - `User` Entity 구현 (UUID PK, Role Enum)
    - `Profile` 정보(나이, 질환 등) 처리 로직
- [ ] **Security Configuration**
    - Spring Security 6.x 설정
    - Password Encoder (BCrypt)
    - CORS 설정 (Frontend 연동 고려)
- [ ] **Authentication (JWT)**
    - JWT Token Provider (Access/Refresh Token) 생성
    - OAuth2 Login (Google/Kakao 등 - Mock or Real) 또는 간편 가입 API
    - `/api/auth/login`, `/api/auth/refresh` 구현
- [ ] **Authorization**
    - Role 기반 접근 제어 (`SENIOR`, `CAREGIVER`, `ADMIN`)
    - Custom Annotation (`@CurrentUserId`) 구현

## 4. 참고 자료
- SRS 4.1.1 (REQ-FUNC-001, 002)
- SRS 6.2.1 User & Profile
- `studio/300-java-spring-cursor-rules.mdc`


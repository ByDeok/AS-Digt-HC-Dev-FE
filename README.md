# AS-Digt-HC Backend

> **AI 기반 시니어 웰니스 및 가족 건강 관리 플랫폼 - 백엔드 API 서버**

Spring Boot 기반의 RESTful API 서버로, 사용자 인증, 건강 데이터 관리, AI 코칭 기능을 제공합니다.

---

## 📋 목차

- [기술 스택](#-기술-스택)
- [프로젝트 구조](#-프로젝트-구조)
- [시작하기](#-시작하기)
- [환경변수 설정](#-환경변수-설정)
- [보안 관리 정책](#-보안-관리-정책)
- [API 문서](#-api-문서)

---

## 🛠 기술 스택

| 구분 | 기술 |
|------|------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 4.0 |
| **ORM** | Spring Data JPA + Hibernate |
| **Database** | H2 (local default) / MySQL 9.x (prod) |
| **Security** | Spring Security + JWT |
| **Build Tool** | Gradle (Groovy) |
| **AI Integration** | Google AI (Gemini) via REST API |

---

## 📂 프로젝트 구조

```
src/main/
├── java/vibe/digthc/as_digt_hc_dev_fe/
│   ├── domain/                    # 도메인 레이어
│   │   ├── common/                # 공통 엔티티 (BaseTimeEntity)
│   │   └── user/                  # 사용자 도메인
│   │       ├── entity/            # JPA 엔티티
│   │       └── repository/        # Repository 인터페이스
│   ├── application/               # 비즈니스 로직 (Service)
│   ├── infrastructure/            # 외부 연동 (AI, Storage)
│   └── interfaces/                # Controller, DTO
└── resources/
    ├── application.yml            # 공통 설정
    ├── application-local.yml      # 로컬 개발 설정
    ├── application-dev.yml        # 개발 서버 설정
    └── application-prod.yml       # 프로덕션 설정
```

---

## 🚀 시작하기

### 사전 요구사항

- **Java 21** (JDK)
- **Gradle 8.x** (Wrapper 포함)
- **MySQL 9.x** (선택: 로컬에서 MySQL 사용 시)

### 설치 및 실행

```bash
# 1. 저장소 클론
git clone https://github.com/ByDeok/AS-Digt-HC-Dev-FE.git
cd AS-Digt-HC-Dev-FE

# 2. (선택) MySQL 데이터베이스 생성
# - 로컬 기본값은 H2 인메모리 DB입니다.
# - MySQL을 사용할 경우 아래 스크립트를 실행하세요.
mysql -u root -p < scripts/init-local-db.sql

# 3. 환경변수 설정 (아래 섹션 참조)

# 4. 애플리케이션 실행
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 빌드

```bash
# JAR 파일 빌드
./gradlew build

# 실행
java -jar build/libs/as-digt-hc-dev-fe-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

---

## 🔐 환경변수 설정

### 프로필 기반 설정

| 프로필 | 용도 | 파일 |
|--------|------|------|
| `local` | 로컬 개발 | `application-local.yml` |
| `dev` | 개발 서버 | `application-dev.yml` |
| `prod` | 프로덕션 | `application-prod.yml` |

### 필수 환경변수

`application-local.yml` 파일에서 아래 항목을 실제 값으로 변경하세요:

```yaml
spring:
  datasource:
    password: YOUR_MYSQL_PASSWORD_HERE    # ← MySQL 비밀번호

external:
  ai:
    google:
      api-key: YOUR_GOOGLE_AI_API_KEY_HERE  # ← Google AI API 키
```

### 환경변수 목록

| 변수명 | 필수 | 설명 | 기본값 |
|--------|------|------|--------|
| `SPRING_PROFILES_ACTIVE` | ✅ | 활성 프로필 | `local` |
| `DB_URL` | ⚠️ | JDBC URL (MySQL 사용 시 설정) | `jdbc:h2:mem:testdb` |
| `DB_DRIVER` | ⚠️ | JDBC Driver (MySQL 사용 시 설정) | `org.h2.Driver` |
| `DB_USERNAME` | ⚠️ | DB 사용자 | `sa` |
| `DB_PASSWORD` | ⚠️ | DB 비밀번호 | - |
| `JWT_SECRET` | ✅ | JWT 서명 키 (256비트+) | - |
| `GOOGLE_AI_API_KEY` | ⚠️ | Google AI API 키 | - |

---

## 🔒 보안 관리 정책

### 핵심 원칙

| 원칙 | 설명 |
|------|------|
| **분리 (Separation)** | 민감 정보는 코드와 분리하여 환경변수로 관리 |
| **계층화 (Layering)** | 환경별(local/dev/prod) 설정 파일 분리 |
| **최소 권한 (Least Privilege)** | 각 환경에 필요한 최소한의 정보만 제공 |
| **암호화 (Encryption)** | 프로덕션 시크릿은 암호화된 형태로 저장 |

### Git에서 제외되는 파일들

`.gitignore`에 의해 다음 파일들은 Git 추적에서 제외됩니다:

```
# 환경변수 파일
.env
.env.local
.env.*.local

# 프로필별 설정 (민감정보 포함)
application-local.yml
application-dev.yml
application-prod.yml

# 시크릿/키 파일
*.pem
*.key
*.p12
*.jks
secrets/
credentials/
```

### 프로덕션 배포 시 권장사항

1. **시크릿 매니저 사용**
   - AWS Secrets Manager
   - GCP Secret Manager
   - Azure Key Vault

2. **환경변수 주입**
   ```bash
   # Kubernetes Secret 예시
   kubectl create secret generic as-digt-hc-secrets \
     --from-literal=DB_PASSWORD=xxx \
     --from-literal=JWT_SECRET=xxx
   ```

3. **JWT 시크릿 생성** (256비트 이상)
   ```bash
   # OpenSSL로 안전한 키 생성
   openssl rand -base64 32
   ```

### 사고 대응 절차

환경변수가 실수로 노출된 경우:

1. ⚡ 즉시 해당 키/비밀번호 무효화 및 재발급
2. 🔍 Git 히스토리에서 민감 정보 제거
3. 📋 영향받은 시스템 접근 로그 검토
4. 📢 관련 팀에 사고 보고

---

## 📖 API 문서

### 기본 정보

| 항목 | 값 |
|------|-----|
| Base URL | `http://localhost:8080/api` |
| Content-Type | `application/json` |
| 인증 방식 | Bearer Token (JWT) |

### 도메인별 API

| 도메인 | 설명 | 상세 문서 |
|--------|------|----------|
| Auth | 인증/회원가입 | `studio/Tasks/API/issue-03-auth-user_API.md` |
| User | 사용자 관리 | TBD |
| Report | 건강 리포트 | TBD |
| Action | 행동 카드 | TBD |

---

## 📚 관련 문서

- [로컬 개발 환경 설정 가이드](scripts/LOCAL_SETUP_GUIDE.md)
- [환경변수 관리 상세 가이드](studio/docs/ENV_MANAGEMENT_GUIDE.md)
- [프론트엔드 README](studio/README.md)
- [랜딩페이지 원고(강력 버전)](docs/Landing_advanced.md)
- [랜딩페이지 결과 검수 체크리스트](docs/Landing_advanced_checklist.md)
- [랜딩페이지 고도화 전략(레퍼런스)](docs/landing_page.md)

---

## 📄 라이선스

This project is proprietary and confidential.


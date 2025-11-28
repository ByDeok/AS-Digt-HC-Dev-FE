# 🔐 환경변수 관리 가이드

## 1. 개요

이 문서는 AS-Digt-HC 프로젝트의 환경변수를 안전하게 관리하는 방법을 설명합니다.
DB 연결 정보, API 키, JWT 시크릿 등 민감한 정보는 절대 Git에 커밋되어서는 안 됩니다.

---

## 2. 환경변수 관리 원칙

### 2.1 핵심 원칙

| 원칙 | 설명 |
|------|------|
| **분리 (Separation)** | 민감 정보는 코드와 분리하여 환경변수로 관리 |
| **계층화 (Layering)** | 환경별(local/dev/prod) 설정 파일 분리 |
| **최소 권한 (Least Privilege)** | 각 환경에 필요한 최소한의 정보만 제공 |
| **암호화 (Encryption)** | 프로덕션 시크릿은 암호화된 형태로 저장 |

### 2.2 절대 Git에 커밋하면 안 되는 것들

```
❌ 데이터베이스 비밀번호
❌ API 키 (Google AI, AWS 등)
❌ JWT 시크릿 키
❌ OAuth 클라이언트 시크릿
❌ SSH 키, SSL 인증서
❌ 프로덕션 서버 접속 정보
```

---

## 3. 백엔드 (Spring Boot) 환경변수 설정

### 3.1 파일 구조

```
src/main/resources/
├── application.yml           # 공통 설정 (Git 추적 ✅)
├── application-local.yml     # 로컬 개발 (Git 제외 ❌)
├── application-dev.yml       # 개발 서버 (Git 제외 ❌)
└── application-prod.yml      # 프로덕션 (Git 제외 ❌)
```

### 3.2 로컬 환경 설정 방법

#### 방법 1: application-local.yml 파일 생성

```yaml
# src/main/resources/application-local.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/as_digt_hc_dev
    username: root
    password: your_password_here

external:
  ai:
    google:
      api-key: your_google_ai_api_key

jwt:
  secret: your-local-jwt-secret-key
```

#### 방법 2: 시스템 환경변수 사용

```bash
# Windows PowerShell
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_NAME="as_digt_hc_dev"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password"
$env:GOOGLE_AI_API_KEY="your_api_key"
$env:JWT_SECRET="your_jwt_secret"

# 애플리케이션 실행
./gradlew bootRun
```

```bash
# Mac/Linux
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=as_digt_hc_dev
export DB_USERNAME=root
export DB_PASSWORD=your_password
export GOOGLE_AI_API_KEY=your_api_key
export JWT_SECRET=your_jwt_secret

./gradlew bootRun
```

#### 방법 3: IntelliJ IDEA 환경변수 설정

1. Run/Debug Configurations 열기
2. Environment Variables 섹션에 추가:
   ```
   DB_HOST=localhost;DB_PORT=3306;DB_NAME=as_digt_hc_dev;DB_USERNAME=root;DB_PASSWORD=yourpassword
   ```

### 3.3 프로필 활성화 방법

```bash
# Gradle 실행 시
./gradlew bootRun --args='--spring.profiles.active=local'

# JAR 실행 시
java -jar app.jar --spring.profiles.active=dev

# 환경변수로 설정
SPRING_PROFILES_ACTIVE=prod java -jar app.jar
```

---

## 4. 프론트엔드 (Vite) 환경변수 설정

### 4.1 파일 구조

```
studio/
├── .env.example         # 템플릿 (Git 추적 ✅)
├── .env.local           # 로컬 개발 (Git 제외 ❌)
└── .env.production      # 프로덕션 빌드용 (Git 제외 ❌)
```

### 4.2 환경변수 파일 생성

```bash
# studio/.env.local 파일 생성
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_ENV=development
VITE_ENABLE_AI_FEATURES=true

# 서버 사이드 전용 (Genkit 서버에서 사용)
GOOGLE_GENAI_API_KEY=your_google_ai_api_key
```

### 4.3 Vite 환경변수 규칙

| 접두사 | 노출 범위 | 용도 |
|--------|----------|------|
| `VITE_` | 클라이언트 + 서버 | API URL, 기능 플래그 등 |
| (접두사 없음) | 서버 전용 | API 키, 시크릿 등 민감 정보 |

⚠️ **주의**: `VITE_` 접두사가 붙은 변수는 클라이언트 번들에 포함되어 브라우저에서 볼 수 있습니다!

### 4.4 코드에서 환경변수 사용

```typescript
// 클라이언트에서 사용 가능
const apiUrl = import.meta.env.VITE_API_BASE_URL;

// 서버 사이드에서만 사용 (예: Genkit 서버)
const apiKey = process.env.GOOGLE_GENAI_API_KEY;
```

---

## 5. 프로덕션 환경 시크릿 관리

### 5.1 클라우드 시크릿 매니저 권장

| 클라우드 | 서비스명 | 특징 |
|----------|---------|------|
| **AWS** | Secrets Manager | 자동 로테이션, IAM 통합 |
| **GCP** | Secret Manager | 버전 관리, 감사 로그 |
| **Azure** | Key Vault | HSM 지원, RBAC |

### 5.2 AWS Secrets Manager 예시

```yaml
# application-prod.yml
spring:
  config:
    import: aws-secretsmanager:/secret/as-digt-hc/prod
```

### 5.3 Docker/Kubernetes 환경

```yaml
# docker-compose.yml
services:
  backend:
    environment:
      - DB_HOST=${DB_HOST}
      - DB_PASSWORD=${DB_PASSWORD}
    env_file:
      - .env.production
```

```yaml
# Kubernetes Secret
apiVersion: v1
kind: Secret
metadata:
  name: as-digt-hc-secrets
type: Opaque
data:
  DB_PASSWORD: <base64-encoded-value>
  JWT_SECRET: <base64-encoded-value>
```

---

## 6. 필수 환경변수 목록

### 6.1 백엔드 (Spring Boot)

| 변수명 | 필수 | 설명 | 예시 |
|--------|------|------|------|
| `SPRING_PROFILES_ACTIVE` | ✅ | 활성 프로필 | `local`, `dev`, `prod` |
| `DB_HOST` | ✅ | DB 호스트 | `localhost` |
| `DB_PORT` | ✅ | DB 포트 | `3306` |
| `DB_NAME` | ✅ | DB 이름 | `as_digt_hc_dev` |
| `DB_USERNAME` | ✅ | DB 사용자 | `root` |
| `DB_PASSWORD` | ✅ | DB 비밀번호 | `********` |
| `JWT_SECRET` | ✅ | JWT 서명 키 (256비트+) | `base64-encoded-key` |
| `GOOGLE_AI_API_KEY` | ⚠️ | Google AI API 키 | `AIza...` |

### 6.2 프론트엔드 (Vite)

| 변수명 | 필수 | 설명 | 예시 |
|--------|------|------|------|
| `VITE_API_BASE_URL` | ✅ | 백엔드 API URL | `http://localhost:8080/api` |
| `GOOGLE_GENAI_API_KEY` | ⚠️ | Genkit AI 키 (서버용) | `AIza...` |
| `VITE_APP_ENV` | ❌ | 앱 환경 | `development` |

---

## 7. 보안 체크리스트

### 7.1 개발 시 체크리스트

- [ ] `.gitignore`에 환경변수 파일 패턴 추가됨
- [ ] 민감 정보가 코드에 하드코딩되지 않음
- [ ] 템플릿 파일(`.env.example`)에 실제 값이 없음
- [ ] 로컬 환경변수 파일이 Git에 추적되지 않음

### 7.2 배포 전 체크리스트

- [ ] 프로덕션 시크릿이 안전하게 저장됨 (시크릿 매니저 등)
- [ ] JWT 시크릿이 256비트 이상의 안전한 키로 설정됨
- [ ] DB 비밀번호가 강력한 패스워드로 설정됨
- [ ] API 키에 적절한 권한 제한이 적용됨
- [ ] 환경변수 로테이션 정책이 수립됨

### 7.3 사고 대응

환경변수가 실수로 노출된 경우:
1. 즉시 해당 키/비밀번호 무효화 및 재발급
2. Git 히스토리에서 민감 정보 제거 (`git filter-branch` 또는 BFG Repo-Cleaner)
3. 영향받은 시스템 접근 로그 검토
4. 관련 팀에 사고 보고

---

## 8. API 키 발급 가이드

### 8.1 Google AI (Genkit) API 키

1. [Google AI Studio](https://aistudio.google.com/app/apikey) 접속
2. "Get API Key" 클릭
3. 새 프로젝트 생성 또는 기존 프로젝트 선택
4. API 키 복사 후 환경변수에 설정

### 8.2 JWT 시크릿 키 생성

```bash
# OpenSSL로 안전한 랜덤 키 생성
openssl rand -base64 32

# 또는 Node.js로 생성
node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"
```

---

## 9. 참고 자료

- [Spring Boot 외부 설정 가이드](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Vite 환경변수 문서](https://vitejs.dev/guide/env-and-mode.html)
- [12-Factor App: Config](https://12factor.net/config)
- [OWASP 시크릿 관리 가이드](https://cheatsheetseries.owasp.org/cheatsheets/Secrets_Management_Cheat_Sheet.html)


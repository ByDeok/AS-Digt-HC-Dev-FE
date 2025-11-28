# 🚀 로컬 개발 환경 설정 가이드

## 1. 사전 요구사항

- **Java 21** (JDK)
- **MySQL 9.x** (로컬 설치 또는 Docker)
- **Node.js 18+** (프론트엔드용)
- **Google AI API 키** (Genkit 사용)

---

## 2. MySQL 데이터베이스 설정

### 2.1 MySQL 설치 확인

```powershell
mysql --version
# 출력 예: mysql  Ver 9.0.1 for Win64 on x86_64
```

### 2.2 데이터베이스 생성

```powershell
# MySQL 접속
mysql -u root -p

# 데이터베이스 생성 (MySQL 콘솔에서)
CREATE DATABASE as_digt_hc_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
exit;
```

또는 스크립트 사용:

```powershell
mysql -u root -p < scripts/init-local-db.sql
```

---

## 3. 백엔드 환경변수 설정

### 3.1 application-local.yml 수정

`src/main/resources/application-local.yml` 파일을 열고 아래 항목들을 실제 값으로 변경하세요:

```yaml
spring:
  datasource:
    password: YOUR_MYSQL_PASSWORD_HERE  # ← 실제 MySQL 비밀번호

external:
  ai:
    google:
      api-key: YOUR_GOOGLE_AI_API_KEY_HERE  # ← 실제 API 키
```

### 3.2 백엔드 실행

```powershell
# 프로젝트 루트에서
./gradlew bootRun --args='--spring.profiles.active=local'
```

---

## 4. 프론트엔드 환경변수 설정

### 4.1 .env.local 파일 생성

`studio/` 폴더에 `.env.local` 파일을 직접 생성하세요:

```powershell
cd studio
New-Item -Path ".env.local" -ItemType File
```

### 4.2 .env.local 내용 작성

아래 내용을 `studio/.env.local` 파일에 붙여넣고 실제 값으로 수정하세요:

```properties
# =================================================================
# AS-Digt-HC Frontend 로컬 환경변수
# =================================================================

# 백엔드 API 서버 주소
VITE_API_BASE_URL=http://localhost:8080/api

# 앱 환경 설정
VITE_APP_NAME=AS-Digt-HC
VITE_APP_ENV=development

# Google AI (Genkit) - 서버 사이드 전용
# TODO: 실제 Google AI API 키로 변경
GOOGLE_GENAI_API_KEY=YOUR_GOOGLE_AI_API_KEY_HERE

# 기능 플래그
VITE_ENABLE_AI_FEATURES=true
VITE_ENABLE_MOCK_DATA=false

# 디버그 모드
VITE_DEBUG_MODE=true
```

### 4.3 프론트엔드 실행

```powershell
cd studio
npm install
npm run dev
```

---

## 5. Google AI API 키 발급 방법

1. [Google AI Studio](https://aistudio.google.com/app/apikey) 접속
2. Google 계정으로 로그인
3. "Get API Key" 또는 "Create API Key" 클릭
4. 새 프로젝트 생성 또는 기존 프로젝트 선택
5. 생성된 API 키 복사
6. `application-local.yml`과 `studio/.env.local`에 입력

---

## 6. 전체 실행 순서

```powershell
# 1. MySQL 서비스 시작 (Windows)
net start mysql

# 2. 데이터베이스 생성 (최초 1회)
mysql -u root -p < scripts/init-local-db.sql

# 3. 백엔드 실행 (터미널 1)
./gradlew bootRun --args='--spring.profiles.active=local'

# 4. 프론트엔드 실행 (터미널 2)
cd studio
npm run dev
```

---

## 7. 설정 체크리스트

- [ ] MySQL 9.x 설치 및 실행 중
- [ ] `as_digt_hc_dev` 데이터베이스 생성됨
- [ ] `application-local.yml`에 MySQL 비밀번호 입력됨
- [ ] `application-local.yml`에 Google AI API 키 입력됨
- [ ] `studio/.env.local` 파일 생성됨
- [ ] `studio/.env.local`에 API 키 입력됨
- [ ] 백엔드 서버 정상 구동 (http://localhost:8080)
- [ ] 프론트엔드 서버 정상 구동 (http://localhost:5173)

---

## 8. 문제 해결

### MySQL 연결 오류

```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
```

**해결방법:**
1. MySQL 서비스가 실행 중인지 확인
2. 포트 3306이 사용 중인지 확인: `netstat -an | findstr 3306`
3. 방화벽에서 3306 포트 허용

### Google AI API 오류

```
Error: API key not valid
```

**해결방법:**
1. API 키가 올바르게 복사되었는지 확인
2. [Google AI Studio](https://aistudio.google.com/app/apikey)에서 키 상태 확인
3. API 키에 사용량 제한이 걸렸는지 확인

### JPA 스키마 오류

```
Schema-validation: missing table
```

**해결방법:**
- `application-local.yml`에서 `ddl-auto: update` 확인
- 또는 수동으로 테이블 생성 스크립트 실행


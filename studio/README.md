# AS-Digt-HC Frontend (Golden Wellness)

> **AI 기반 시니어 웰니스 및 가족 건강 관리 플랫폼 - 프론트엔드**

React + Vite 기반의 웹 애플리케이션으로, 시니어 사용자를 위한 친화적인 UI/UX와 AI 건강 코칭 기능을 제공합니다.

---

## 📋 목차

- [기술 스택](#-기술-스택)
- [주요 기능](#-주요-기능)
- [시작하기](#-시작하기)
- [환경변수 설정](#-환경변수-설정)
- [보안 관리 정책](#-보안-관리-정책)
- [프로젝트 구조](#-프로젝트-구조)
- [문서](#-문서)

---

## 🛠 기술 스택

### Frontend Core
| 구분 | 기술 |
|------|------|
| **Framework** | React 18 + Vite |
| **Language** | TypeScript |
| **Routing** | React Router v6 |
| **State** | TanStack Query + Context API |

### UI & Styling
| 구분 | 기술 |
|------|------|
| **Styling** | Tailwind CSS |
| **Components** | shadcn/ui (Radix UI 기반) |
| **Icons** | Lucide React |
| **Charts** | Recharts |
| **Forms** | React Hook Form + Zod |

### AI Integration
| 구분 | 기술 |
|------|------|
| **AI SDK** | Google Genkit |
| **Model** | Gemini 2.5 Flash |

---

## 🌟 주요 기능

- **간편한 온보딩**: 시니어 맞춤형 단계별 설정 (약 3분 소요)
- **일일 미션 카드**: 매일 1~3개의 건강 미션 제공 및 완료 시 축하 효과
- **건강 리포트 요약**: 혈압, 혈당 등 주요 데이터 요약 및 의료진 공유
- **가족 연결**: 가족 구성원 간 활동 피드 공유 및 중요 알림

---

## 🚀 시작하기

### 사전 요구사항

- **Node.js 18+**
- **npm** 또는 **yarn**
- **Google AI API 키** (Genkit 사용 시)

### 설치 및 실행

```bash
# 1. studio 디렉토리로 이동
cd studio

# 2. 의존성 설치
npm install

# 3. 환경변수 설정 (아래 섹션 참조)

# 4. 개발 서버 실행
npm run dev
```

### AI 개발 도구 실행

```bash
# Genkit 개발 UI 실행
npm run genkit:dev
```

### 빌드

```bash
# 프로덕션 빌드
npm run build

# 빌드 결과물 미리보기
npm run preview
```

---

## 🔐 환경변수 설정

### 환경변수 파일 생성

`studio/` 폴더에 `.env.local` 파일을 생성하세요:

```bash
# PowerShell
New-Item -Path ".env.local" -ItemType File
notepad .env.local
```

### .env.local 내용

```properties
# =================================================================
# AS-Digt-HC Frontend 환경변수
# =================================================================

# 백엔드 API 서버 주소
# - 권장: VITE_API_URL
# - 호환: VITE_API_BASE_URL (코드에서 함께 지원)
VITE_API_URL=http://localhost:8081/api

# 앱 환경 설정
VITE_APP_NAME=AS-Digt-HC
VITE_APP_ENV=development

# Google AI (Genkit) - 서버 사이드 전용
# ⚠️ VITE_ 접두사 없음 = 클라이언트에 노출되지 않음
GOOGLE_GENAI_API_KEY=YOUR_GOOGLE_AI_API_KEY_HERE

# 기능 플래그
VITE_ENABLE_AI_FEATURES=true
VITE_ENABLE_MOCK_DATA=false

# 디버그 모드
VITE_DEBUG_MODE=true
```

### 환경변수 목록

| 변수명 | 필수 | 클라이언트 노출 | 설명 |
|--------|------|----------------|------|
| `VITE_API_BASE_URL` | ✅ | ✅ | 백엔드 API URL |
| `VITE_APP_ENV` | ❌ | ✅ | 앱 환경 (development/production) |
| `GOOGLE_GENAI_API_KEY` | ⚠️ | ❌ | Google AI API 키 |
| `VITE_ENABLE_AI_FEATURES` | ❌ | ✅ | AI 기능 활성화 여부 |
| `VITE_ENABLE_MOCK_DATA` | ❌ | ✅ | 목업 데이터 사용 여부 |

---

## 🔒 보안 관리 정책

### Vite 환경변수 규칙

| 접두사 | 노출 범위 | 용도 |
|--------|----------|------|
| `VITE_` | 클라이언트 + 서버 | API URL, 기능 플래그 등 공개 가능한 정보 |
| (접두사 없음) | 서버 전용 | API 키, 시크릿 등 민감 정보 |

⚠️ **중요**: `VITE_` 접두사가 붙은 변수는 클라이언트 번들에 포함되어 **브라우저에서 볼 수 있습니다!**

### Git에서 제외되는 파일들

`.gitignore`에 의해 다음 파일들은 Git 추적에서 제외됩니다:

```
# 환경변수 파일
.env
.env.local
.env.*.local
.env.development.local
.env.production.local
```

### 코드에서 환경변수 사용

```typescript
// ✅ 클라이언트에서 사용 가능 (공개 정보만)
const apiUrl = import.meta.env.VITE_API_BASE_URL;

// ✅ 서버 사이드에서만 사용 (Genkit 서버 등)
const apiKey = process.env.GOOGLE_GENAI_API_KEY;

// ❌ 절대 금지: API 키에 VITE_ 접두사 사용
// const apiKey = import.meta.env.VITE_GOOGLE_API_KEY; // 보안 위험!
```

### API 키 발급 방법

1. [Google AI Studio](https://aistudio.google.com/app/apikey) 접속
2. Google 계정으로 로그인
3. "Create API Key" 클릭
4. 생성된 키를 `.env.local`에 입력

### 프로덕션 배포 체크리스트

- [ ] `.env.local` 파일이 Git에 커밋되지 않았는지 확인
- [ ] `VITE_` 변수에 민감 정보가 없는지 확인
- [ ] API 키에 사용량 제한 및 도메인 제한 설정
- [ ] 프로덕션 환경변수가 CI/CD 시크릿으로 관리되는지 확인

---

## 📂 프로젝트 구조

```
studio/
├── src/
│   ├── ai/              # Genkit AI 관련 로직
│   │   ├── genkit.ts    # AI 클라이언트 초기화
│   │   ├── genkit-service.ts
│   │   └── mock-service.ts
│   ├── app/             # 페이지 컴포넌트
│   │   ├── (main)/      # 대시보드, 리포트 등
│   │   └── onboarding/  # 온보딩 플로우
│   ├── components/      # UI 컴포넌트
│   │   ├── ui/          # shadcn/ui 기본 컴포넌트
│   │   └── common/      # 공통 컴포넌트
│   ├── hooks/           # 커스텀 React Hooks
│   ├── lib/             # 유틸리티 함수
│   └── services/        # API 서비스 레이어
├── docs/                # 프로젝트 문서
├── Tasks/               # 개발 태스크 문서
└── package.json
```

---

## 📚 문서

### 프로젝트 문서

| 문서 | 설명 |
|------|------|
| [종합 문서](docs/README.md) | 프로젝트 개요 및 품질 분석 |
| [컴포넌트 구조](docs/01-component-structure-analysis.md) | 컴포넌트 트리 시각화 |
| [코드 품질 평가](docs/02-code-quality-assessment.md) | 코드 품질 분석 결과 |
| [문서화 가이드](docs/03-code-documentation-guide.md) | 주석/문서화 표준 |
| [함수 호출 구조](docs/04-function-call-hierarchy.md) | 데이터 흐름 분석 |

### 환경 설정 문서

| 문서 | 설명 |
|------|------|
| [환경변수 관리 가이드](docs/ENV_MANAGEMENT_GUIDE.md) | 환경변수 상세 관리 방법 |
| [로컬 설정 가이드](../scripts/LOCAL_SETUP_GUIDE.md) | 로컬 개발 환경 구축 |

---

## 🏆 코드 품질

### 종합 점수: **90/100 (A)**

| 평가 항목 | 점수 | 등급 |
|----------|------|------|
| 가독성 | 92 | A |
| 재사용성 | 95 | A+ |
| 유지보수성 | 88 | B+ |
| 일관성 | 95 | A+ |
| 성능 | 80 | B |

자세한 내용은 [코드 품질 평가](docs/02-code-quality-assessment.md) 문서를 참조하세요.

---

## 📄 라이선스

This project is proprietary and confidential.


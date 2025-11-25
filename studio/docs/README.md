# Golden Wellness (AS-Digt-HC-Dev-FE) - 프로젝트 종합 문서

> **AI 기반 시니어 웰니스 및 가족 건강 관리 플랫폼**  
> 본 문서는 프로젝트의 개요, 기술 스택, 코드 품질 분석 및 개발 가이드를 종합적으로 다룹니다.

---

## 📖 1. 프로젝트 개요

**Golden Wellness**는 시니어 사용자를 위한 친화적인 UI/UX와 가족 연결 기능을 제공하는 React 기반 웹 애플리케이션입니다. Firebase Studio 환경에서 개발되었으며, AI(Google Genkit)를 활용한 맞춤형 건강 관리를 지원합니다.

### 🌟 주요 기능
- **간편한 온보딩 (Onboarding Flow)**: 시니어 맞춤형 단계별 설정 (약 3분 소요).
- **일일 미션 카드 (Daily Mission Card)**: 매일 1~3개의 건강 미션 제공 및 완료 시 축하 효과(Confetti).
- **건강 리포트 요약 (Health Report Summary)**: 혈압, 혈당 등 주요 데이터 요약 및 의료진 공유 기능.
- **가족 연결 (Family Connection)**: 가족 구성원 간 활동 피드 공유 및 중요 알림 제공.

---

## 📚 2. 문서 및 분석 리포트 목차

프로젝트의 상세 분석 문서는 `docs/` 디렉토리에서 확인할 수 있습니다.

1. [**컴포넌트 구조 분석**](./01-component-structure-analysis.md)
   - Mermaid 차트를 이용한 컴포넌트 트리 시각화
   - 아키텍처 효율성 및 개선 가능성 분석
2. [**코드 품질 평가**](./02-code-quality-assessment.md)
   - 가독성, 재사용성, 유지보수성 등 5개 항목 정량 평가
   - 상세 평가 내역 및 점수
3. [**코드 주석 및 문서화 가이드**](./03-code-documentation-guide.md)
   - React/TypeScript 주석 표준
   - AI 모듈 문서화 규칙
4. [**함수 호출 구조 (Function Call Hierarchy)**](./04-function-call-hierarchy.md)
   - 페이지 라우팅 및 데이터 흐름 분석
   - 주요 시나리오별 호출 체인

---

## 📊 3. 코드 품질 종합 평가

현재 코드베이스(`src`)에 대한 종합적인 품질 분석 결과입니다. 상세 내용은 [코드 품질 평가](./02-code-quality-assessment.md) 문서를 참조하세요.

### 🏆 종합 점수: **90/100 (A)**
> **평가**: 최신 기술 스택과 높은 일관성을 갖춘 고품질 코드베이스

| 평가 항목 | 점수 | 등급 | 핵심 요약 |
|:---:|:---:|:---:|:---|
| **가독성** | **92** | A | 명확한 폴더 구조(Next.js 스타일)와 직관적인 컴포넌트 명명 |
| **재사용성** | **95** | A+ | Radix UI + CVA 기반의 완벽한 UI 모듈화 (shadcn/ui 패턴) |
| **유지보수성** | **88** | B+ | TypeScript & Zod로 타입 안전성 확보, 테스트 코드는 부족 |
| **일관성** | **95** | A+ | Tailwind CSS 및 엄격한 파일 컨벤션 준수 |
| **성능** | **80** | B | Vite 기반 빠른 빌드, 심화 최적화(Memoization, Code Splitting) 여지 있음 |

### ✅ 핵심 강점
1.  **현대적인 UI 아키텍처**: `components/ui` 내의 컴포넌트들이 비즈니스 로직 없이 순수하게 스타일/인터랙션만 담당(Atomic Design).
2.  **강력한 타입 시스템**: TypeScript와 Zod를 활용하여 런타임 및 정적 타입 안정성을 모두 보장.
3.  **직관적인 프로젝트 구조**: `src/app` 하위에 기능별(dashboard, onboarding)로 폴더가 정리되어 있어 유지보수가 용이함.

### ⚠️ 개선 필요 영역
1.  **테스트 코드 부재**: 주요 유틸리티(`lib/utils.ts`) 및 컴포넌트 테스트 파일이 없어 리팩토링 리스크 존재.
2.  **AI 로직 분리**: UI 컴포넌트와 AI 호출 로직의 결합도가 높아, 이를 별도 서비스 레이어로 추상화 필요.

---

## 🛠 4. 기술 스택 (Tech Stack)

### Frontend Core
- **Framework**: React (Vite 기반)
- **Language**: TypeScript
- **Routing**: React Router (App Router 스타일 폴더 구조 사용)
- **State Management**: Context API, React Hooks

### UI & Styling
- **Styling**: Tailwind CSS
- **Components**: shadcn/ui (Radix UI 기반), Lucide React
- **Visualization**: Recharts
- **Forms**: React Hook Form + Zod Validation

### Backend & AI
- **Platform**: Firebase (Backend-as-a-Service)
- **AI**: Google Genkit (Generative AI SDK)

### Design System
- **Primary Color**: Teal (`#20c997`) - 신뢰/안정
- **Background**: Very Light Teal (`#f0f9f5`)
- **Font**: PT Sans

---

## 🚀 5. 시작하기 (Getting Started)

### 설치 및 실행
```bash
# 1. 저장소 클론
git clone <repository-url>
cd as-digt-hc-dev-fe

# 2. 의존성 설치
npm install

# 3. 개발 서버 실행 (Port: 9002)
npm run dev
```

### AI 개발 도구 실행
Genkit 개발 UI를 실행하여 AI 흐름을 테스트할 수 있습니다.
```bash
npm run genkit:dev
```

---

## 📂 6. 프로젝트 구조

```
src/
├── ai/              # Genkit AI 관련 로직 (dev.ts, genkit.ts)
├── app/             # 페이지 컴포넌트 (Next.js 스타일 폴더 구조 차용)
│   ├── (main)/      # 대시보드, 리포트 등 메인 기능
│   ├── onboarding/  # 온보딩 프로세스
│   └── ...
├── components/      # 재사용 가능한 UI 컴포넌트
│   ├── ui/          # shadcn/ui 기본 컴포넌트 (Button, Card 등)
│   ├── common/      # 범용 컴포넌트
│   └── ...
├── hooks/           # 커스텀 React Hooks
├── lib/             # 유틸리티(utils.ts) 및 목업 데이터
├── App.tsx          # 메인 라우터 설정
└── main.tsx         # 앱 진입점
```

---

## 🛣️ 7. 개선 로드맵

프로젝트 품질 향상을 위한 단계별 제언입니다.

### Phase 1: 테스트 환경 구축 (Critical)
> **목표**: 안정적인 리팩토링을 위한 최소한의 안전장치 마련
- [ ] **Unit Test**: `lib/utils.ts`의 유틸리티 함수 테스트 작성 (Vitest 권장)
- [ ] **Component Test**: `components/common`의 공통 컴포넌트 테스트
- [ ] **Integration Test**: 주요 사용자 플로우(온보딩 완료 등) 검증

### Phase 2: AI 모듈 고도화
> **목표**: AI 기능의 신뢰성 및 유지보수성 확보
- [ ] **Service Layer 추상화**: `src/ai` 폴더를 별도 서비스 레이어로 정의 및 에러 핸들링 강화
- [ ] **Fallback UI**: AI 응답 지연/실패 시 Skeleton UI 및 에러 메시지 처리

### Phase 3: 개발 경험(DX) 및 문서화
> **목표**: 팀 생산성 향상
- [ ] **TSDoc 도입**: 주요 컴포넌트 및 훅에 주석 추가 (IDE 툴팁 지원)
- [ ] **Storybook 도입**: UI 컴포넌트 독립 개발 환경 구축 고려

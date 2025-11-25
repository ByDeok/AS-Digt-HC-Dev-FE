---
title: "[Refactor] 컴포넌트 분리 및 커스텀 훅 도입"
assignees: []
labels: ["refactor", "clean-code", "medium-priority"]
milestone: "Phase 1: Core Optimization"
---

## 🚀 개요
현재 `FinancialSimulation` 등 일부 컴포넌트의 복잡도가 높고, 폼 검증이나 비동기 처리 로직이 여러 곳에 분산되어 있습니다. 이를 개선하기 위해 컴포넌트 분리와 커스텀 훅 추출을 진행합니다.

## 📋 상세 작업 내용

### 1. 대형 컴포넌트 분리 (`FinancialSimulation`)
- [ ] `FinancialSimulation` (300+ lines) 컴포넌트 분할
  - `FinancialInputForm.tsx`: 입력 폼 영역
  - `MetricsSummary.tsx`: 핵심 지표 요약 영역
  - `BEPChart.tsx`: 손익분기점 차트
  - `UnitEconomicsChart.tsx`: 유닛 이코노믹스 차트
- [ ] `index.tsx`에서 분리된 컴포넌트 조합

### 2. 커스텀 훅 (Custom Hooks) 추출
- [ ] `useFormValidation.ts`: Zod 기반 폼 검증 로직 중앙화
- [ ] `useDebounce.ts`: `useAutoSave` 등에서 사용되는 디바운스 로직 공통화
- [ ] `useChartData.ts`: 재무 데이터 시각화를 위한 데이터 가공 로직 분리
- [ ] `useAsyncAction.ts`: 로딩(`isLoading`), 에러(`error`) 상태를 포함한 비동기 액션 처리 훅 구현

### 3. 중복 코드 제거 및 패턴 추상화
- [ ] `FeatureIcon` 컴포넌트 생성 (ProjectCreate 페이지의 중복 아이콘 마크업 대체)
- [ ] 매직 넘버(Magic Numbers) 상수로 추출 (Step ID, 타임아웃 시간 등)
  - `const FINANCIAL_STEP = 4;`
  - `const AI_GENERATION_DELAY = 3000;`

## ✅ 인수 조건 (Acceptance Criteria)
- [ ] `FinancialSimulation` 파일 크기가 150라인 이하로 줄어들 것
- [ ] 공통 로직이 Hooks로 분리되어 중복 코드가 제거될 것
- [ ] 주요 상수가 별도 파일(constants) 또는 상단에 정의되어 관리될 것

## 📚 참고 자료
- [01-component-structure-analysis.md](../docs/01-component-structure-analysis.md) - 개선 가능성 섹션
- [02-code-quality-assessment.md](../docs/02-code-quality-assessment.md) - 2. 재사용성 및 3. 유지보수성 섹션


---
title: "[Refactor] 성능 최적화 (Performance Optimization)"
assignees: []
labels: ["enhancement", "performance", "high-priority"]
milestone: "Phase 1: Core Optimization"
---

## 🚀 개요
현재 코드 품질 평가 결과 성능 최적화 점수가 70/100(C+)으로, 주요 컴포넌트의 불필요한 리렌더링과 계산 비용 최적화가 필요합니다. 특히 `QuestionForm`과 같은 빈번한 입력이 발생하는 컴포넌트의 최적화가 시급합니다.

## 📋 상세 작업 내용

### 1. 컴포넌트 메모이제이션 (`React.memo`)
- [ ] `QuestionForm` 컴포넌트에 `React.memo` 적용
- [ ] `WizardStep` 하위의 주요 UI 컴포넌트(Input, Button 등) 리렌더링 점검 및 최적화
- [ ] **목표**: Props 변경이 없을 때 불필요한 리렌더링 방지

### 2. 핸들러 및 계산 최적화 (`useCallback`, `useMemo`)
- [ ] `QuestionForm` 내 `handleChange` 등 이벤트 핸들러에 `useCallback` 적용
- [ ] `Layout` 및 `WizardStep`의 진척률 계산 로직에 `useMemo` 적용
  ```typescript
  const completedSteps = useMemo(
    () => steps.filter((step) => isStepCompleted(step.id)).length,
    [steps, isStepCompleted]
  );
  ```
- [ ] `FinancialSimulation` 내 복잡한 재무 계산 로직 메모이제이션

### 3. 리스트 렌더링 최적화
- [ ] `PMFSurvey` 컴포넌트의 문항 리스트 렌더링에 가상화(Virtualization) 적용 검토
  - `react-window` 또는 `react-virtuoso` 도입 고려 (문항 수가 늘어날 경우 대비)

### 4. 이미지 및 에셋 최적화
- [ ] `lucide-react` 아이콘 import 방식 점검 (Tree-shaking 확인)
- [ ] 대용량 차트 라이브러리(`recharts`) Code Splitting 검토

## ✅ 인수 조건 (Acceptance Criteria)
- [ ] `QuestionForm` 입력 시 타이핑 지연(Lag)이 없어야 함
- [ ] React DevTools Profiler 기준 불필요한 리렌더링이 제거되어야 함
- [ ] 대규모 리스트 렌더링 시 프레임 저하가 없어야 함

## 📚 참고 자료
- [01-component-structure-analysis.md](../docs/01-component-structure-analysis.md) - 성능 최적화 섹션
- [02-code-quality-assessment.md](../docs/02-code-quality-assessment.md) - 5. 성능 섹션


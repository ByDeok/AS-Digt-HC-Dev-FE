---
title: "[Chore] 접근성 개선 및 번들 최적화"
assignees: []
labels: ["enhancement", "a11y", "optimization", "low-priority"]
milestone: "Phase 3: Polish"
---

## 🚀 개요
애플리케이션의 접근성을 웹 표준 수준으로 높이고, 초기 로딩 속도 개선을 위해 번들 사이즈 최적화 및 Code Splitting을 적용합니다.

## 📋 상세 작업 내용

### 1. Code Splitting (라우트 기반)
- [ ] `React.lazy`와 `Suspense`를 사용하여 페이지 단위 코드 스플리팅 적용
  ```typescript
  const ProjectCreate = lazy(() => import('./pages/ProjectCreate'));
  const WizardStep = lazy(() => import('./pages/WizardStep'));
  // ...
  ```
- [ ] 로딩 중 보여줄 `Skeleton` 또는 `Spinner` Fallback UI 적용

### 2. 상태 관리 최적화 (Zustand Selector)
- [ ] `useWizardStore` 등 스토어 사용 시 Selector 패턴 적용하여 리렌더링 최소화
  ```typescript
  // 변경 전
  const { steps, currentStep } = useWizardStore();
  
  // 변경 후
  const steps = useWizardStore((state) => state.steps);
  ```

### 3. 접근성(A11y) 개선
- [ ] 시맨틱 태그 사용 점검 (`div` 대신 `main`, `section`, `header` 등 적절히 사용)
- [ ] 폼 요소에 `aria-label`, `aria-describedby` 등 접근성 속성 추가
- [ ] 키보드 네비게이션(Tab키 이동) 흐름 점검 및 수정

### 4. 번들 분석 및 최적화
- [ ] `vite-bundle-analyzer` 등을 통한 번들 사이즈 분석
- [ ] 불필요하게 큰 라이브러리 대체 또는 최적화 (예: Markdown 렌더러, 차트 라이브러리 등)

## ✅ 인수 조건 (Acceptance Criteria)
- [ ] Lighthouse 접근성 점수 90점 이상 달성
- [ ] 초기 로딩 시 필요한 리소스만 로드되어야 함 (Network 탭 확인)
- [ ] 키보드만으로 주요 기능(위자드 진행 등) 사용이 가능해야 함

## 📚 참고 자료
- [01-component-structure-analysis.md](../docs/01-component-structure-analysis.md) - 개선 가능성 > Code Splitting, 접근성 개선
- [02-code-quality-assessment.md](../docs/02-code-quality-assessment.md) - 5. 성능 > 번들 크기 최적화


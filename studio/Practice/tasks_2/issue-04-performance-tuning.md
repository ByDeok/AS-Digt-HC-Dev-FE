---
title: "[Perf] 렌더링 최적화 및 번들 사이즈 관리"
assignees: []
labels: ["performance", "optimization", "low-priority"]
milestone: "Phase 3: Optimization"
---

## 🚀 개요
코드 품질 평가에서 성능 점수가 80점으로 준수하나, 심화 최적화를 통해 사용자 경험을 더욱 향상시킬 여지가 있습니다. 특히 Code Splitting과 메모이제이션을 중점적으로 개선합니다.

## 📋 상세 작업 내용

### 1. Code Splitting 적용
- [ ] 라우트 레벨에서 `React.lazy`와 `Suspense` 적용 (Dashboard, Report 등 무거운 페이지 대상)
- [ ] `recharts` 등 무거운 라이브러리를 사용하는 컴포넌트 비동기 로딩

### 2. 리렌더링 방지 (Memoization)
- [ ] 리스트 아이템(`ListItem`) 등 반복 렌더링되는 컴포넌트에 `React.memo` 적용
- [ ] 무거운 계산 로직에 `useMemo`, 콜백 함수에 `useCallback` 적절히 활용

### 3. 이미지 및 에셋 최적화
- [ ] 아이콘 및 이미지 로딩 전략 점검
- [ ] 불필요한 라이브러리 import 정리

## ✅ 인수 조건 (Acceptance Criteria)
- [ ] 초기 로딩(FCP, LCP) 속도가 개선되어야 함
- [ ] React DevTools Profiler 확인 시 불필요한 리렌더링이 감지되지 않아야 함

## 📚 참고 자료
- [02-code-quality-assessment.md](../docs/02-code-quality-assessment.md) - 5. 성능 및 개선 제언


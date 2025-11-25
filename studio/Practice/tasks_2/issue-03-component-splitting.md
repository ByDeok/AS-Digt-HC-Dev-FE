---
title: "[Refactor] 페이지 컴포넌트 분할 및 로직 분리"
assignees: []
labels: ["refactor", "cleanup", "medium-priority"]
milestone: "Phase 2: Refactoring"
---

## 🚀 개요
`DashboardPage`와 `ReportPage` 등 주요 페이지 컴포넌트의 크기가 비대해지고 있어, 유지보수성과 가독성을 위해 하위 컴포넌트로 분할이 필요합니다.

## 📋 상세 작업 내용

### 1. 대시보드 페이지 리팩토링
- [ ] `DashboardPage`의 미션 리스트 렌더링 로직을 `MissionList.tsx`로 분리
- [ ] 사용자 정보 헤더를 `DashboardHeader.tsx`로 분리

### 2. 리포트 페이지 리팩토링
- [ ] `ReportPage`의 차트 섹션을 `ChartSection.tsx` 등으로 분리
- [ ] 데이터 필터링 로직을 커스텀 훅(`useReportFilter`)으로 추출

### 3. 컨테이너-프레젠터 패턴 적용 검토
- [ ] 데이터 페칭 로직(Container)과 UI 렌더링(Presenter)의 역할 분리 명확화

## ✅ 인수 조건 (Acceptance Criteria)
- [ ] 주요 페이지 컴포넌트(`page.tsx`)의 코드 라인 수가 현저히 감소해야 함 (예: 100라인 이내)
- [ ] 분리된 컴포넌트들이 `components/feature` 또는 해당 페이지 디렉토리 하위에 구조적으로 배치되어야 함

## 📚 참고 자료
- [01-component-structure-analysis.md](../docs/01-component-structure-analysis.md) - 개선 가능성 > 컴포넌트 분할


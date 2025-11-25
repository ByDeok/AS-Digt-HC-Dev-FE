# [Refactor] 라우트 기반 Code Splitting 적용

## 1. 개요
현재 `App.tsx`에서 모든 페이지 컴포넌트(`DashboardPage`, `OnboardingPage` 등)를 최상단에서 정적으로 import 하고 있습니다.
이는 초기 로딩 시 사용하지 않는 페이지의 자바스크립트까지 모두 다운로드하게 하여 **초기 로딩 속도(FCP/LCP)**를 저하시킵니다.

## 2. 작업 상세
- [ ] **React.lazy 적용**
    - `App.tsx`의 상단 import 문을 동적 import로 변경
    ```typescript
    const DashboardPage = lazy(() => import('./app/(main)/dashboard/page'));
    ```
- [ ] **Suspense 적용**
    - 라우트 전체 또는 주요 섹션을 `<Suspense fallback={<LoadingSpinner />}>`로 감싸기
    - 로딩 중 보여줄 적절한 `LoadingSpinner` 또는 스켈레톤 UI 구현
- [ ] **청크 분리 확인**
    - `npm run build` 실행 후 `dist/assets` 폴더에 페이지별로 JS 파일이 분리되었는지 확인

## 3. 기대 효과
- 초기 번들 사이즈 감소
- 앱 초기 구동 속도 향상 및 사용자 경험 개선


# [Chore] Vitest 기반 단위 테스트 환경 구축

## 1. 개요
현재 프로젝트에 테스트 라이브러리가 전무하여 로직 검증이 불가능합니다.
Vite 환경에 최적화되어 있고 속도가 빠른 **Vitest**를 도입하여 테스트 환경을 구축해야 합니다.

## 2. 작업 상세
- [ ] **패키지 설치**
    - `vitest`
    - `@testing-library/react`, `@testing-library/dom`
    - `@testing-library/jest-dom` (DOM 매처 확장)
    - `jsdom` (브라우저 환경 모킹)
- [ ] **설정 파일 수정**
    - `vite.config.ts`의 `test` 프로퍼티 설정
        - `environment: 'jsdom'`
        - `globals: true` (선택 사항)
        - `setupFiles: ['./src/test/setup.ts']`
- [ ] **Setup 파일 작성**
    - `src/test/setup.ts`: `@testing-library/jest-dom` import 등 전역 설정
- [ ] **샘플 테스트 작성**
    - 간단한 유틸리티 함수(`src/lib/utils.ts`)나 UI 컴포넌트(`Button.tsx`)에 대한 테스트 작성 후 `npm run test` 동작 확인

## 3. 기대 효과
- 주요 비즈니스 로직의 안정성 확보
- 리팩토링 시 사이드 이펙트 방지


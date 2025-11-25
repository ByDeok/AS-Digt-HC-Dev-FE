# [Feat] Global Error Boundary 및 에러 처리 강화

## 1. 개요
현재 전역적인 에러 핸들링 로직이 부재하여, 렌더링 중 에러가 발생하면 흰 화면(White Screen)이 노출될 위험이 있습니다.
사용자 경험을 해치지 않도록 우아하게 에러를 처리하고 복구할 수 있는 장치가 필요합니다.

## 2. 작업 상세
- [ ] **라이브러리 설치**
    - `react-error-boundary`
- [ ] **Fallback UI 컴포넌트 구현**
    - `src/components/error/ErrorFallback.tsx` 생성
    - "문제가 발생했습니다" 메시지와 "다시 시도" 버튼 제공
- [ ] **Global Error Boundary 적용**
    - `App.tsx` 또는 `main.tsx`의 최상위를 `ErrorBoundary`로 감싸기
    - `onReset` prop에 라우트 이동이나 상태 초기화 로직 연결
- [ ] **(Optional) 로깅 연동**
    - 에러 발생 시 콘솔 로깅 또는 외부 로깅 서비스(Sentry 등) 연동 포인트 마련

## 3. 기대 효과
- 예기치 못한 에러 발생 시에도 앱이 멈추지 않음
- 사용자에게 명확한 피드백 제공 및 복구 기회 제공


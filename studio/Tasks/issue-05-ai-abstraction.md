# [Refactor] AI Genkit 서비스 계층 추상화

## 1. 개요
현재 `src/ai/genkit.ts`에 Genkit 초기화 로직이 있으나, 이를 UI 컴포넌트에서 직접 호출하여 사용하면 결합도가 높아지고 테스트가 어려워집니다.
React Hook 패턴을 사용하여 AI 서비스를 추상화해야 합니다.

## 2. 작업 상세
- [ ] **Custom Hook 개발 (`useGenkit` 또는 `useAI`)**
    - 입력값(prompt)을 받아 AI 응답을 반환하는 훅 구현
    - `loading`, `error`, `data` 상태 관리 내장
- [ ] **스트리밍 응답 처리**
    - Genkit의 스트리밍 응답을 처리하여 UI에 실시간으로 텍스트가 타이핑되는 효과 지원
- [ ] **Mocking 지원**
    - 개발 모드이거나 API 키가 없는 경우를 대비한 Mock 모드 지원 (테스트 용이성 확보)

## 3. 기대 효과
- UI 컴포넌트와 AI 로직의 의존성 분리
- 재사용성 향상 및 테스트 용이성 확보


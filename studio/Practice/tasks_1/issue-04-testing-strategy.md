---
title: "[Infra] 테스트 환경 구축 및 주요 테스트 작성"
assignees: []
labels: ["test", "infrastructure", "medium-priority"]
milestone: "Phase 2: Stability"
---

## 🚀 개요
현재 테스트 코드가 전무하여 리팩토링이나 기능 추가 시 회귀(Regression) 버그 발생 위험이 있습니다. Jest와 React Testing Library를 도입하여 테스트 기반을 마련합니다.

## 📋 상세 작업 내용

### 1. 테스트 환경 설정
- [ ] Jest 및 React Testing Library (RTL) 설치 및 설정
- [ ] `setupTests.ts` 구성 (jest-dom 등)
- [ ] CI 파이프라인에 테스트 스크립트 추가 (선택 사항)

### 2. 유틸리티 및 UI 컴포넌트 단위 테스트 (Unit Test)
- [ ] `components/ui/Button.tsx`: Variant별 렌더링, 클릭 이벤트, 로딩 상태 테스트
- [ ] `components/ui/Input.tsx`: 입력 값 변경, 에러 메시지 표시 테스트
- [ ] `lib/utils.ts`: 유틸리티 함수 테스트

### 3. 스토어 로직 테스트
- [ ] `useWizardStore`: 단계 이동, 데이터 업데이트, 초기화 로직 테스트
- [ ] `useProjectStore`: 프로젝트 생성 및 선택 로직 테스트

### 4. 통합 테스트 (Integration Test) 예시 작성
- [ ] `QuestionForm`: Props 전달 및 입력 시 스토어 업데이트 연동 테스트

## ✅ 인수 조건 (Acceptance Criteria)
- [ ] `npm test` 실행 시 설정된 테스트가 모두 통과해야 함
- [ ] 주요 UI 컴포넌트(`Button`, `Input`)에 대한 테스트 커버리지 확보
- [ ] 핵심 비즈니스 로직(Store)에 대한 테스트 케이스 작성 완료

## 📚 참고 자료
- [02-code-quality-assessment.md](../docs/02-code-quality-assessment.md) - 3. 유지보수성 > 테스트 코드 부재


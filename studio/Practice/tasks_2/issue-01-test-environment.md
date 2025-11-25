---
title: "[Infra] 테스트 환경 구축 및 커버리지 확보 (Critical)"
assignees: []
labels: ["infrastructure", "test", "high-priority"]
milestone: "Phase 1: Foundation"
---

## 🚀 개요
현재 코드 품질 평가에서 **테스트 코드 부재**가 가장 큰 취약점으로 지적되었습니다. 리팩토링과 기능 확장을 위해 Jest와 React Testing Library 기반의 테스트 환경을 시급히 구축해야 합니다.

## 📋 상세 작업 내용

### 1. 테스트 환경 설정
- [ ] `Jest` 및 `React Testing Library` 설치
- [ ] `ts-jest` 설정 및 `jest.config.js` 구성 (Path Alias 등 처리)
- [ ] `setupTests.ts` 작성 (DOM 매처 확장)

### 2. 유틸리티 함수 단위 테스트 (Unit Test)
- [ ] `lib/utils.ts` 내 `cn` 함수 등 헬퍼 함수 테스트 작성
- [ ] `lib/mockData.ts` 데이터 무결성 검증 테스트 (선택 사항)

### 3. 공통 컴포넌트 테스트 (Component Test)
- [ ] `components/common` 및 `components/ui` 주요 컴포넌트 테스트
  - `Button`, `Input`, `Card` 등의 렌더링 및 인터랙션 테스트

### 4. 주요 비즈니스 로직 테스트
- [ ] 온보딩 프로세스 상태 관리 로직 테스트
- [ ] 리포트 데이터 계산 로직 테스트

## ✅ 인수 조건 (Acceptance Criteria)
- [ ] `npm test` 명령어로 전체 테스트가 실행되어야 함
- [ ] CI 파이프라인(GitHub Actions 등)에서 빌드 시 테스트가 수행되도록 설정
- [ ] 유틸리티 함수 커버리지 80% 이상 달성

## 📚 참고 자료
- [02-code-quality-assessment.md](../docs/02-code-quality-assessment.md) - 우선순위 1: 테스트 환경 구축


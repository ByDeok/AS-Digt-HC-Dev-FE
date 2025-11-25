# [Chore] ESLint & Prettier 설정 및 CI 연동 준비

## 1. 개요
현재 프로젝트(`package.json`)에 린터(Linter)와 포매터(Formatter)가 설정되어 있지 않습니다.
코드의 일관성을 유지하고 잠재적인 버그를 사전에 방지하기 위해 ESLint와 Prettier를 도입해야 합니다.

## 2. 작업 상세
- [ ] **패키지 설치**
    - `eslint`, `prettier`
    - `eslint-plugin-react`, `eslint-plugin-react-hooks`
    - `@typescript-eslint/parser`, `@typescript-eslint/eslint-plugin`
    - `eslint-config-prettier` (충돌 방지)
- [ ] **설정 파일 생성**
    - `.eslintrc.cjs` (또는 `.eslintrc.json`): React 및 TypeScript 권장 규칙 적용
    - `.prettierrc`: 팀 컨벤션에 맞게 설정 (예: singleQuote: true, trailingComma: all)
- [ ] **NPM 스크립트 추가**
    - `"lint": "eslint . --ext ts,tsx --report-unused-disable-directives --max-warnings 0"`
    - `"format": "prettier --write ."`
- [ ] **기존 코드 일괄 수정**
    - 초기 설정 후 전체 파일에 대해 포맷팅 및 린트 에러 수정 진행

## 3. 기대 효과
- 코드 스타일 통일로 협업 효율 증대
- 문법 오류 및 안티 패턴 조기 발견


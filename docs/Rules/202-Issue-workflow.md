# 이슈 워크플로우 표준

이 규칙은 AI 에이전트와 개발자 협업을 위한 표준 운영 절차를 정의하며, 작업 정의, 이슈 생성, 개발, 풀 리퀘스트 자동화를 다룹니다.

## 1. 작업 정의 및 이슈 생성

### 작업 정의
- `studio/Tasks/` 아래에 마크다운 형식으로 작업 설명 생성
- 파일 명명 규칙: `issue-{number}-{short-description}.md`
- 내용에는 명확한 제목(H1)과 본문(설명, 요구사항, 체크리스트)이 포함되어야 함

### 이슈 생성
- 워크스페이스 루트에 있는 `create_issues.ps1` 스크립트 사용
- 스크립트는 `gh` CLI를 사용하여:
  - 원격 저장소에 이슈 생성
  - 중복 이슈 생성 방지
  - 자동으로 `Issue Automation` 레이블 적용
- **명령어**: `.\create_issues.ps1`

## 2. 개발 사이클

### 브랜칭
- 각 이슈에 대해 새 브랜치 생성
- 작업 유형에 따라 접두사 사용: `feat/`, `fix/`, `refactor/`, `chore/`
- 브랜치 이름에 이슈 번호 또는 키워드 포함 (예: `fix/header-issue-1`)

### 구현 및 검증
1. **분석**: 변경하기 전에 코드베이스를 분석하여 맥락 이해
2. **구현**: 작업 요구사항을 만족하도록 코드 수정
3. **정적 검증**:
   - 코드 스타일과 오류 확인을 위해 `npm run lint` 실행
   - 포맷팅 확인을 위해 `npm run format` 또는 `npm run format:check` 실행
4. **런타임 검증**:
   - 개발 서버 시작 (`npm run dev`)
   - `@Browser` 도구를 사용하여 수정사항이나 기능을 시각적으로 확인

## 3. 풀 리퀘스트 자동화

### 커밋 전략
- Conventional Commits 형식 준수 (참조: [200-git-commit-push-pr.md](200-git-commit-push-pr.md))
- `git add`를 사용하여 관련 파일만 스테이징

### PR 생성
- `gh pr create`를 사용하여 Pull Request 생성
- **필수 옵션**:
  - `--title`: Conventional Commits 형식: `<type>(<scope>): <subject>`
  - `--body`: 이슈를 연결하고 자동으로 닫기 위해 "Closes #IssueNumber" 포함해야 함
  - `--reviewer`: 적절한 리뷰어 할당 (예: `ByDeok`)
- **PR 설명 요구사항** (세부사항은 [200-git-commit-push-pr.md](200-git-commit-push-pr.md) 참조):
  - 의미적 의미를 분석하는 변경사항 요약
  - 관련 이슈 링크
- **예제**:
  ```bash
  gh pr create --title "fix(header): 반응형 레이아웃 버그 해결" --body "Closes #5" --reviewer ByDeok
  ```

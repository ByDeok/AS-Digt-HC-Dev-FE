# GitHub 이슈 및 프로젝트 관리

## 맥락
- 작업 문서에서 이슈 생성 시 적용
- GitHub CLI를 통한 프로젝트 로드맵 관리 시 적용
- **Pull Request 생성 시 적용 (내용 요구사항은 규칙 200 참조)**

## 규칙
1. **이슈 생성**:
   - `tasks/priority-N/` 마크다운 파일(또는 [202-Issue-workflow.md](202-Issue-workflow.md)에 따라 `studio/Tasks/`)에서 작업 소스
   - 제목 `[#NNN] Title`, 파일에서 본문, 적절한 레이블로 `gh issue create` 사용
   - 레이블에는 우선순위(`priority-N`)와 유형(`refactoring`, `feature` 등)이 포함되어야 함

2. **프로젝트 및 로드맵**:
   - 항목 조작을 위해 Project V2 Node ID 사용 (정수 ID 아님)
   - 필수 필드: `Start Date` 및 `End Date` (타입: `DATE`)
   - `gh project item-edit --project-id <NodeID>`를 통해 날짜 설정

3. **자동화**:
   - 편집 전에 `gh project field-list`를 통해 필드 ID 확인
   - 범위(`project`, `read:project`)가 누락된 경우 인증 새로고침 재시도

4. **이슈 종료 및 상태 관리**:
   - **자동**: PR 설명에 `Closes #NNN` 포함해야 함 (참조: [200-git-commit-push-pr.md](200-git-commit-push-pr.md)) 병합 시 이슈 자동 종료
   - **수동**: 자동 종료가 실패하면 병합 직후 `gh issue close <issue-number>` 사용
   - **댓글**: 수동으로 종료할 때 PR을 참조하는 댓글 추가: `--comment "PR #<pr-number>를 통해 종료됨"`
   - **확인**: 작업 완료 후 항상 이슈 상태 확인 (`gh issue view <issue-number>`)

## 예제
<example>
# 이슈 생성 및 로드맵 날짜 설정
gh issue create -t "[#001] 리팩토링" -F tasks/001.md -l "priority-1"
gh project item-edit --id <ItemID> --field-id <StartFieldID> --date 2025-11-22
</example>

<example>
# PR 병합 후 수동으로 이슈 종료
gh issue close 123 --comment "병합된 PR #124를 통해 종료됨"
</example>

<bad-example>
# 프로젝트 편집에 정수 ID 사용
gh project item-edit --project-id 7 ... (실패함, Node ID 사용)
</bad-example>

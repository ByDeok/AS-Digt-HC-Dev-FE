# Git Flow를 위한 Git CLI 사용
- 변경사항 검토를 위해 `git status` 및 `git diff` 사용
- 원자적 커밋을 위해 `git add -p`로 스테이징, 관련 없는 혼합 커밋 피하기
- Conventional Commits 스타일로 커밋 (설명적인 커밋 메시지)
- 커밋 제목 형식: `[<변경사항 카테고리명>] <메시지>`
- 커밋당 하나의 목적(원자적 커밋), 혼합된 의도 변경이나 컴파일 불가능한 커밋 지점 피하기

# 브랜치 및 원격 저장소
- 현재 브랜치가 변경 의도와 일치하는지 확인 (release/, feat/, fix/, doc/ 등)
- 일치하지 않으면 적절한 접두사(release/, feat/, fix/, doc/ 등)로 적절한 브랜치 생성
- 브랜치당 하나의 목적, 브랜치당 많은 모듈 변경 피하기
- `git push -u origin <branch>`로 원격에 푸시
- `git ls-remote`로 GitHub 자격 증명 확인; 누락된 경우 프롬프트
- 브랜치 푸시 후 `gh pr create --draft`로 `main`에 초안 PR 생성

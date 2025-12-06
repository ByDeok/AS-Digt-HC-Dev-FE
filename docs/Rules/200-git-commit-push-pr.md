# Git 커밋, 푸시 및 PR 규칙

## 원자적 커밋
- 커밋당 하나의 논리적 변경 (단일 목적, 완전히 기능적)
- 각 커밋이 컴파일되고 테스트를 통과하는지 확인
- 선택적 스테이징을 위해 `git add -p` 사용, 관련 없는 변경 혼합 피하기
- WHAT뿐만 아니라 WHY를 설명하는 설명적인 커밋 메시지 작성

## 커밋 메시지 형식
- Conventional Commits 사용: `<type>(<scope>): <subject>`
- 타입: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`, `style`, `perf`
- 제목: 명령형, 소문자, 마침표 없음, 최대 50자
- 본문 (선택사항): 상세 설명, 72자에서 줄바꿈
- 푸터 (선택사항): `Closes #123` 또는 `Refs #456`로 이슈 참조

## 브랜치 전략
- 작업 시작 전 항상 이슈별 브랜치 생성
- 브랜치 명명: `<type>/<issue-number>-<short-description>`
  - 예: `feat/123-user-auth`, `fix/456-payment-bug`, `docs/789-api-guide`
- 즉시 원격에 브랜치 푸시: `git push -u origin <branch-name>`
- `main`, `master`, 또는 `dev` 브랜치에 직접 커밋하지 않기

## AI 에이전트 안전 규칙
- 커밋 전에 `git status` 및 `git diff` 실행
- `git branch --show-current`로 올바른 브랜치 확인
- 사용자가 명시적으로 요청한 경우에만 `--no-verify` 사용
- 사용자 명시적 확인 없이 강제 푸시(`--force`, `-f`) 절대 사용하지 않기
- 푸시 전 원격 연결 확인: `git ls-remote`
- 푸시 작업 시도 전 자격 증명이 유효한지 확인

## 팀 협업
- 시작 전 최신 변경사항 가져오기: `git pull origin <branch>`
- 충돌을 피하기 위해 main에서 기능 브랜치를 정기적으로 리베이스
- 첫 푸시 후 즉시 초안 PR 생성: `gh pr create --draft`
- **PR 설명에 다음을 포함해야 함:**
  - 변경사항 요약: **브랜치 간 전체 git diff를 분석**하고 모든 변경사항의 *의미적 의미*를 요약
  - 요약이 모든 주요 수정사항을 다루지만 간결하게 유지 (장황한 파일 목록 피하기)
  - 관련 이슈 링크
- 병합 전 리뷰 요청; 승인 없이 자체 병합하지 않기

## 예제
<example>
# 좋은 예: 명확한 메시지를 가진 원자적 커밋
git add src/auth/login.ts src/auth/login.test.ts
git commit -m "feat(auth): JWT 토큰 검증 구현

- 토큰 만료 확인 로직 추가
- 리프레시 토큰 회전 포함
- 엣지 케이스에 대한 단위 테스트 추가

Closes #234"
</example>

<bad-example>
# 나쁜 예: 혼합된 변경사항, 모호한 메시지
git add .
git commit -m "업데이트"
git push origin main  # ❌ main에 직접 푸시
</bad-example>

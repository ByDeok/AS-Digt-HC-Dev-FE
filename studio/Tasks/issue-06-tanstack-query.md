# [Feat] TanStack Query 도입 및 서버 상태 관리

## 1. 개요
현재 API 호출 방식이 명시적이지 않으나, `useEffect`와 `useState`로 데이터를 가져오는 패턴은 보일러플레이트가 많고 캐싱, 중복 호출 방지, 백그라운드 업데이트 등의 기능을 직접 구현하기 어렵습니다.
React 생태계의 표준인 TanStack Query를 도입하여 서버 상태 관리를 체계화합니다.

## 2. 작업 상세
- [ ] **패키지 설치**
    - `@tanstack/react-query`
    - `@tanstack/react-query-devtools` (개발 생산성 향상)
- [ ] **Provider 설정**
    - `App.tsx` 또는 `main.tsx`에 `QueryClientProvider` 설정
- [ ] **API 클라이언트 분리**
    - `src/lib/api.ts` 또는 `src/services` 폴더에 Axios/Fetch 기반의 API 요청 함수 분리
- [ ] **Custom Hook 적용**
    - 주요 데이터 조회 로직을 `useQuery` 훅으로 전환
    - 데이터 변경(생성/수정/삭제) 로직을 `useMutation` 훅으로 전환

## 3. 기대 효과
- 서버 데이터 캐싱 및 자동 갱신
- 로딩/에러 상태 관리 코드 간소화
- API 요청 로직의 중앙 집중화


/**
 * 스크립트 용도: 모든 페이지에서 공통으로 노출되는 브랜드 대표 아이콘(고정 위치)
 *
 * 적용 방식:
 * - App 루트에서 한 번만 렌더링하여 모든 라우트/페이지에 자동 적용
 * - 사용자가 아이콘을 클릭하면 홈("/")으로 이동
 */

import { Link } from 'react-router-dom';

/**
 * 프로그램 단위 용도: 화면 좌상단에 고정 표시되는 브랜드 아이콘
 */
export function BrandCornerIcon() {
  return (
    <Link
      to="/"
      aria-label="골든 웰니스 홈으로 이동"
      className="fixed left-2 top-3 z-[60] inline-flex h-10 w-10 items-center justify-center"
    >
      {/*
        public/ 아래 정적 자원으로 제공됩니다.
        - dev: /brand-icon.png
        - build: dist에 복사되어 동일 경로로 제공
      */}
      <img
        src="/brand-icon.png"
        alt="골든 웰니스"
        className="h-10 w-10 rounded-full bg-white/80 shadow-md ring-1 ring-black/10 backdrop-blur"
        loading="eager"
        decoding="async"
      />
    </Link>
  );
}

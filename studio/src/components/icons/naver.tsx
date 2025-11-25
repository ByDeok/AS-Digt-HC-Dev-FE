import React from 'react';

/**
 * 프로그램 단위 용도: 네이버 로고 아이콘 컴포넌트
 */
export const NaverIcon = (props: React.SVGProps<SVGSVGElement>) => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 24 24"
    {...props}
  >
    <path
      fill="currentColor"
      d="M16.273 12.845h-4.34L8.14 7H11.8l3.05 5.845zM8.14 18h7.72V7h-7.72v11z"
    />
  </svg>
);

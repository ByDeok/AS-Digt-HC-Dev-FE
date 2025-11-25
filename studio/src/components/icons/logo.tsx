import React from 'react';

/**
 * 프로그램 단위 용도: 애플리케이션 로고 아이콘 컴포넌트
 */
export const Logo = (props: React.SVGProps<SVGSVGElement>) => (
  <svg
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    {...props}
  >
    <path
      d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"
      fill="currentColor"
    />
    <path
      d="M16 8.5h-3.5V5h-1v3.5H8v1h3.5V13h1V9.5H16v-1z"
      fill="var(--background-hsl, #fff)"
    />
  </svg>
);

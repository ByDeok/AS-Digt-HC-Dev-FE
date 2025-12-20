import type { ReactNode } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { isAuthenticated } from '@/lib/auth';

/**
 * 인증이 필요한 화면을 감싸는 간단한 가드 컴포넌트
 * - accessToken이 없으면 /login 으로 이동
 */
export function RequireAuth({ children }: { children: ReactNode }) {
  const location = useLocation();

  if (!isAuthenticated()) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return children;
}


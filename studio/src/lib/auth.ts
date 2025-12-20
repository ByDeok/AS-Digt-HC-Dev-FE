/**
 * 인증 토큰 저장/조회 유틸
 * - 최소 구현: accessToken / refreshToken을 localStorage에 저장
 * - FE 인증 가드 및 axios interceptor에서 사용
 */

export type BackendRole = 'USER' | 'ADMIN' | 'SENIOR' | 'CAREGIVER';

export type UserResponse = {
  id: string;
  email: string;
  name: string;
  role: BackendRole;
  createdAt: string | null;
};

export type TokenResponse = {
  accessToken: string;
  refreshToken: string;
  tokenType: string; // "Bearer"
  expiresIn: number; // seconds
  user: UserResponse;
};

const ACCESS_TOKEN_KEY = 'auth.accessToken';
const REFRESH_TOKEN_KEY = 'auth.refreshToken';
const USER_KEY = 'auth.user';

export function setAuthSession(token: TokenResponse) {
  localStorage.setItem(ACCESS_TOKEN_KEY, token.accessToken);
  localStorage.setItem(REFRESH_TOKEN_KEY, token.refreshToken);
  localStorage.setItem(USER_KEY, JSON.stringify(token.user));
}

export function clearAuthSession() {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}

export function getAccessToken(): string | null {
  return localStorage.getItem(ACCESS_TOKEN_KEY);
}

export function getRefreshToken(): string | null {
  return localStorage.getItem(REFRESH_TOKEN_KEY);
}

export function getStoredUser(): UserResponse | null {
  const raw = localStorage.getItem(USER_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as UserResponse;
  } catch {
    return null;
  }
}

export function isAuthenticated(): boolean {
  return !!getAccessToken();
}


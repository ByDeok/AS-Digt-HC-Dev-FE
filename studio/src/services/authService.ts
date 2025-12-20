import api from '@/lib/api';
import type { TokenResponse } from '@/lib/auth';

export type ApiResponse<T> = {
  success: boolean;
  message: string;
  data?: T;
  timestamp: string;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export type SignupRequest = {
  email: string;
  password: string;
  name: string;
  role?: 'USER' | 'ADMIN' | 'SENIOR' | 'CAREGIVER';
  agreements: {
    termsService: boolean;
    privacyPolicy: boolean;
    marketingConsent: boolean;
  };
};

export async function login(req: LoginRequest): Promise<TokenResponse> {
  const res = await api.post<ApiResponse<TokenResponse>>('/v1/auth/login', req);
  if (!res.data?.success || !res.data.data) {
    throw new Error(res.data?.message || '로그인에 실패했습니다.');
  }
  return res.data.data;
}

export async function signup(req: SignupRequest) {
  // UserResponse를 반환하지만, 로그인 플로우에 직접 쓰진 않아서 any로 최소화
  const res = await api.post<ApiResponse<unknown>>('/v1/auth/signup', req);
  if (!res.data?.success) {
    throw new Error(res.data?.message || '회원가입에 실패했습니다.');
  }
  return res.data.data;
}


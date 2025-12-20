import api from '@/lib/api';
import type { TokenResponse } from '@/lib/auth';
import axios from 'axios';

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
  try {
    const res = await api.post<ApiResponse<TokenResponse>>('/v1/auth/login', req);
    if (!res.data?.success || !res.data.data) {
      throw new Error(res.data?.message || '로그인에 실패했습니다.');
    }
    return res.data.data;
  } catch (e) {
    // 백엔드가 email을 @Email로 강제하는 환경에서도 테스트 계정 로그인이 되도록
    // - 사용자가 'admin'을 입력했는데 400(VALIDATION_ERROR)로 튕기면
    // - 이메일 형식의 별칭(admin@local.test)으로 1회 재시도한다.
    if (req.email === 'admin' && axios.isAxiosError(e) && e.response?.status === 400) {
      try {
        const retry = await api.post<ApiResponse<TokenResponse>>('/v1/auth/login', {
          ...req,
          email: 'admin@local.test',
        });
        if (!retry.data?.success || !retry.data.data) {
          throw new Error(retry.data?.message || '로그인에 실패했습니다.');
        }
        return retry.data.data;
      } catch (e2) {
        throw e2;
      }
    }
    throw e;
  }
}

export async function signup(req: SignupRequest) {
  // UserResponse를 반환하지만, 로그인 플로우에 직접 쓰진 않아서 any로 최소화
  const res = await api.post<ApiResponse<unknown>>('/v1/auth/signup', req);
  if (!res.data?.success) {
    throw new Error(res.data?.message || '회원가입에 실패했습니다.');
  }
  return res.data.data;
}


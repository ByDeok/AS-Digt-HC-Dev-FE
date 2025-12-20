import api from '@/lib/api';
import { unwrapApiResponse } from '@/services/common';

export type Gender = 'MALE' | 'FEMALE' | 'OTHER';
export type Role = 'USER' | 'ADMIN' | 'SENIOR' | 'CAREGIVER';

export type ProfileResponse = {
  userId: string;
  email: string;
  name: string | null;
  phoneNumber: string | null;
  profileImageUrl: string | null;
  bio: string | null;
  birthDate: string | null; // YYYY-MM-DD
  age: number | null;
  gender: Gender | null;
  role: Role;
  primaryConditions: string | null;
  accessibilityPrefs: string | null;
  createdAt: string | null;
  updatedAt: string | null;
};

export type ProfileUpdateRequest = {
  name?: string | null;
  phoneNumber?: string | null;
  birthDate?: string | null; // YYYY-MM-DD
  gender?: Gender | null;
  bio?: string | null;
  primaryConditions?: string | null;
  accessibilityPrefs?: string | null;
};

export const userService = {
  getMe: async (): Promise<ProfileResponse> => {
    const res = await api.get('/v1/users/me');
    return unwrapApiResponse<ProfileResponse>(res, '내 프로필을 불러오지 못했습니다.');
  },

  updateMe: async (req: ProfileUpdateRequest): Promise<ProfileResponse> => {
    const res = await api.put('/v1/users/me', req);
    return unwrapApiResponse<ProfileResponse>(res, '프로필 저장에 실패했습니다.');
  },
};



import api from '@/lib/api';
import { unwrapApiResponse } from '@/services/common';

export type OnboardingStep =
  | 'TERMS_AGREEMENT'
  | 'PROFILE_BASIC'
  | 'PROFILE_DETAILS'
  | 'HOSPITAL_SELECTION'
  | 'COMPLETED';

export type Gender = 'MALE' | 'FEMALE' | 'OTHER';

export type OnboardingStepResponse = {
  currentStep: OnboardingStep;
  progressPercent: number;
  estimatedMinutesLeft: number;
  completed: boolean;
};

export type OnboardingStepRequest = {
  currentStep: OnboardingStep;
  nextStep: OnboardingStep;
  // PROFILE_BASIC
  name?: string;
  phoneNumber?: string;
  birthDate?: string; // YYYY-MM-DD
  gender?: Gender;
  // PROFILE_DETAILS
  primaryConditions?: string;
  accessibilityPrefs?: string;
  // HOSPITAL_SELECTION
  hospitalId?: string;
  regionCode?: string;
};

export const onboardingService = {
  start: async (): Promise<OnboardingStepResponse> => {
    const res = await api.post('/onboarding/start');
    return unwrapApiResponse<OnboardingStepResponse>(res, '온보딩 시작에 실패했습니다.');
  },

  get: async (): Promise<OnboardingStepResponse> => {
    const res = await api.get('/onboarding');
    return unwrapApiResponse<OnboardingStepResponse>(res, '온보딩 상태 조회에 실패했습니다.');
  },

  step: async (req: OnboardingStepRequest): Promise<OnboardingStepResponse> => {
    const res = await api.post('/onboarding/step', req);
    return unwrapApiResponse<OnboardingStepResponse>(res, '온보딩 진행 저장에 실패했습니다.');
  },

  complete: async (): Promise<void> => {
    await api.post('/onboarding/complete');
  },
};



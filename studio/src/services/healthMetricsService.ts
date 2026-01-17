import api from '@/lib/api';
import { unwrapApiResponse } from '@/services/common';

export type DailyHealthMetrics = {
  recordDate: string;
  steps?: number | null;
  heartRate?: number | null;
  weight?: number | null;
  systolic?: number | null;
  diastolic?: number | null;
};

export type DailyHealthMetricsResponse = DailyHealthMetrics & {
  userId: string;
};

export const healthMetricsService = {
  getDaily: async (date: string): Promise<DailyHealthMetricsResponse> => {
    const res = await api.get('/metrics/daily', { params: { date } });
    return unwrapApiResponse<DailyHealthMetricsResponse>(
      res,
      '일별 건강 기록을 불러오지 못했습니다.'
    );
  },

  upsertDaily: async (payload: DailyHealthMetrics): Promise<DailyHealthMetricsResponse> => {
    const res = await api.post('/metrics/daily', payload);
    return unwrapApiResponse<DailyHealthMetricsResponse>(
      res,
      '일별 건강 기록 저장에 실패했습니다.'
    );
  },
};

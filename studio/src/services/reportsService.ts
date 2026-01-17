import api from '@/lib/api';
import { unwrapApiResponse } from '@/services/common';

export type ReportMetrics = {
  activity?: {
    steps?: number;
    activeMinutes?: number;
    caloriesBurned?: number;
  };
  heartRate?: {
    avgBpm?: number;
    minBpm?: number;
    maxBpm?: number;
  };
  bloodPressure?: {
    systolic?: number;
    diastolic?: number;
  };
  weight?: {
    value?: number;
    unit?: string;
  };
};

export type ReportContext = {
  deviceId?: string;
  deviceType?: string;
  isMissingData?: boolean;
  missingDataFields?: string[];
  metadata?: string;
};

export type PeriodType = 'WEEKLY' | 'MONTHLY';

export type HealthReport = {
  reportId: string;
  userId: string;
  startDate: string; // YYYY-MM-DD
  endDate: string; // YYYY-MM-DD
  periodType?: PeriodType | null;
  metrics: ReportMetrics | null;
  context: ReportContext | null;
  createdAt: string | null;
  updatedAt: string | null;
};

export const reportsService = {
  list: async (periodType?: PeriodType): Promise<HealthReport[]> => {
    const res = await api.get('/reports', {
      params: periodType ? { periodType } : undefined,
    });
    return unwrapApiResponse<HealthReport[]>(res, '리포트를 불러오지 못했습니다.');
  },

  generate: async (periodType?: PeriodType): Promise<HealthReport> => {
    const res = await api.post('/reports/generate', undefined, {
      params: periodType ? { periodType } : undefined,
    });
    return unwrapApiResponse<HealthReport>(res, '리포트 생성에 실패했습니다.');
  },


  getById: async (reportId: string): Promise<HealthReport> => {
    const res = await api.get(`/reports/${reportId}`);
    return unwrapApiResponse<HealthReport>(res, '리포트를 불러오지 못했습니다.');
  },

  delete: async (reportId: string): Promise<void> => {
    const res = await api.delete(`/reports/${reportId}`);
    // 삭제 API는 ApiResponse<Void> 형태 (200)로 내려오도록 되어있어 success만 확인
    unwrapApiResponse<unknown>(res, '리포트 삭제에 실패했습니다.');
  },
};



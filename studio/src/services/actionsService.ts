import api from '@/lib/api';
import { unwrapApiResponse } from '@/services/common';

export type ActionStatus = 'PENDING' | 'COMPLETED' | 'SKIPPED';

export type ActionCard = {
  id: number;
  title: string;
  description: string | null;
  actionDate: string; // YYYY-MM-DD
  status: ActionStatus;
  ruleId: string | null;
};

export type ActionStats = {
  dailyCompletionRate: number;
  weeklyCompletionRate: number;
};

export const actionsService = {
  getTodayActions: async (): Promise<ActionCard[]> => {
    const res = await api.get('/actions/today');
    return unwrapApiResponse<ActionCard[]>(res, '오늘의 행동 카드를 불러오지 못했습니다.');
  },

  completeAction: async (id: number): Promise<ActionCard> => {
    const res = await api.post(`/actions/${id}/complete`);
    return unwrapApiResponse<ActionCard>(res, '행동 카드 완료 처리에 실패했습니다.');
  },

  skipAction: async (id: number): Promise<ActionCard> => {
    const res = await api.post(`/actions/${id}/skip`);
    return unwrapApiResponse<ActionCard>(res, '행동 카드 스킵 처리에 실패했습니다.');
  },

  getStats: async (): Promise<ActionStats> => {
    const res = await api.get('/actions/stats');
    return unwrapApiResponse<ActionStats>(res, '통계를 불러오지 못했습니다.');
  },
};



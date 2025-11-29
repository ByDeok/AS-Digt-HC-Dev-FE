import { mockMissions, Mission } from '@/lib/mockData';
import api from '@/lib/api';

// 실제 API 호출이 준비되면 아래 주석을 해제하고 mock 리턴을 대체합니다.
// const BASE_URL = '/missions';

export const missionService = {
  getMissions: async (): Promise<Mission[]> => {
    // const { data } = await api.get<Mission[]>(BASE_URL);
    // return data;

    // Simulate API delay
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve([...mockMissions]);
      }, 500);
    });
  },

  completeMission: async (missionId: string): Promise<void> => {
    // await api.patch(`${BASE_URL}/${missionId}/complete`);

    // Simulate API delay
    return new Promise((resolve) => {
      setTimeout(() => {
        console.log(`Mission ${missionId} completed`);
        resolve();
      }, 300);
    });
  },
};


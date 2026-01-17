import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { healthMetricsService, type DailyHealthMetrics } from '@/services/healthMetricsService';

export const healthMetricsKeys = {
  daily: (date: string) => ['healthMetrics', 'daily', date] as const,
};

export function useDailyHealthMetrics(date: string) {
  return useQuery({
    queryKey: healthMetricsKeys.daily(date),
    queryFn: () => healthMetricsService.getDaily(date),
    enabled: Boolean(date),
  });
}

export function useUpsertDailyHealthMetrics(date: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: DailyHealthMetrics) => healthMetricsService.upsertDaily(payload),
    onSuccess: (data) => {
      queryClient.setQueryData(healthMetricsKeys.daily(date), data);
    },
  });
}

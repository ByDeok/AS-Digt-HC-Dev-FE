import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { reportsService, type HealthReport, type PeriodType } from '@/services/reportsService';

export const reportKeys = {
  list: (periodType?: PeriodType) => ['reports', 'list', periodType ?? 'ALL'] as const,
  detail: (id: string) => ['reports', 'detail', id] as const,
};

export function useReports(periodType?: PeriodType) {
  return useQuery({
    queryKey: reportKeys.list(periodType),
    queryFn: () => reportsService.list(periodType),
  });
}

export function useGenerateReport(periodType?: PeriodType) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => reportsService.generate(periodType),
    onSuccess: (created) => {
      // list 갱신
      queryClient.setQueryData<HealthReport[]>(reportKeys.list(periodType), (old) =>
        old ? [created, ...old] : [created],
      );
    },
  });
}


export function useDeleteReport(periodType?: PeriodType) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => reportsService.delete(id),
    onSuccess: (_, id) => {
      queryClient.setQueryData<HealthReport[]>(reportKeys.list(periodType), (old) =>
        old ? old.filter((r) => r.reportId !== id) : old,
      );
    },
  });
}




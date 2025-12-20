import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { reportsService, type HealthReport } from '@/services/reportsService';

export const reportKeys = {
  list: ['reports', 'list'] as const,
  detail: (id: string) => ['reports', 'detail', id] as const,
};

export function useReports() {
  return useQuery({
    queryKey: reportKeys.list,
    queryFn: reportsService.list,
  });
}

export function useGenerateReport() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: reportsService.generate,
    onSuccess: (created) => {
      // list 갱신
      queryClient.setQueryData<HealthReport[]>(reportKeys.list, (old) =>
        old ? [created, ...old] : [created],
      );
    },
  });
}

export function useDeleteReport() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => reportsService.delete(id),
    onSuccess: (_, id) => {
      queryClient.setQueryData<HealthReport[]>(reportKeys.list, (old) =>
        old ? old.filter((r) => r.reportId !== id) : old,
      );
    },
  });
}



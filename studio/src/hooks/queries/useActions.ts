import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { actionsService, type ActionCard } from '@/services/actionsService';

export const actionKeys = {
  today: ['actions', 'today'] as const,
};

export function useTodayActions() {
  return useQuery({
    queryKey: actionKeys.today,
    queryFn: actionsService.getTodayActions,
  });
}

export function useCompleteAction() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => actionsService.completeAction(id),
    onSuccess: (updated) => {
      queryClient.setQueryData<ActionCard[]>(actionKeys.today, (old) => {
        if (!old) return [updated];
        return old.map((a) => (a.id === updated.id ? updated : a));
      });
    },
  });
}

export function useSkipAction() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => actionsService.skipAction(id),
    onSuccess: (updated) => {
      queryClient.setQueryData<ActionCard[]>(actionKeys.today, (old) => {
        if (!old) return [updated];
        return old.map((a) => (a.id === updated.id ? updated : a));
      });
    },
  });
}



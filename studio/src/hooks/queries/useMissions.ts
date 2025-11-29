import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { missionService } from '@/services/missionService';
import { Mission } from '@/lib/mockData';

export const missionKeys = {
  all: ['missions'] as const,
};

export const useMissions = () => {
  return useQuery({
    queryKey: missionKeys.all,
    queryFn: missionService.getMissions,
  });
};

export const useCompleteMission = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (missionId: string) => missionService.completeMission(missionId),
    onSuccess: (_, missionId) => {
      // Optimistic update or invalidation could go here
      // For now, we'll just invalidate the query to refetch
      
      // In a real app, we might want to update the cache optimistically
      // to avoid a loading spinner for a simple checkmark toggle.
      // But since our mock service resets data on fetch, simple invalidation might reset state unexpectedly if we are not careful with the mock data source persistence.
      // Since mockMissions in mockData.ts is a constant, refetching will always return the initial state unless we modify the in-memory array.
      // For this example, let's update the cache manually to simulate persistence.
      
      queryClient.setQueryData<Mission[]>(missionKeys.all, (oldData) => {
        if (!oldData) return [];
        return oldData.map((mission) =>
          mission.id === missionId ? { ...mission, isCompleted: true } : mission
        );
      });
    },
  });
};


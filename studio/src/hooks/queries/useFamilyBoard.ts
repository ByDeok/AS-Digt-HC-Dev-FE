import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  familyBoardService,
  type BoardRole,
} from '@/services/familyBoardService';

export const familyBoardKeys = {
  board: ['familyBoard', 'board'] as const,
  members: ['familyBoard', 'members'] as const,
};

export function useMyFamilyBoard() {
  return useQuery({
    queryKey: familyBoardKeys.board,
    queryFn: familyBoardService.getMyBoard,
  });
}

export function useFamilyMembers() {
  return useQuery({
    queryKey: familyBoardKeys.members,
    queryFn: familyBoardService.getMembers,
  });
}

export function useInviteFamilyMember() {
  return useMutation({
    mutationFn: (req: { inviteeEmail: string; intendedRole: BoardRole }) => familyBoardService.invite(req),
  });
}

export function useAcceptFamilyInvite() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (inviteCode: string) => familyBoardService.accept(inviteCode),
    onSuccess: () => {
      // 멤버 목록/보드 정보 갱신
      queryClient.invalidateQueries({ queryKey: familyBoardKeys.members });
      queryClient.invalidateQueries({ queryKey: familyBoardKeys.board });
    },
  });
}



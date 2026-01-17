import api from '@/lib/api';
import { unwrapApiResponse } from '@/services/common';

export type BoardRole = 'ADMIN' | 'EDITOR' | 'VIEWER';
export type MemberStatus = 'ACTIVE' | 'INVITED' | 'REMOVED' | 'LEFT';

export type BackendRole = 'USER' | 'ADMIN' | 'SENIOR' | 'CAREGIVER';

export type UserResponse = {
  id: string;
  email: string;
  name: string;
  role: BackendRole;
  createdAt: string | null;
};

export type FamilyBoard = {
  boardId: string;
  name: string | null;
  description: string | null;
  senior: UserResponse;
  memberCount: number;
  settings: Record<string, unknown> | null;
  lastActivityAt: string | null;
};

export type BoardSettingsUpdate = Record<string, unknown>;

export type FamilyMember = {
  membershipId: number;
  user: UserResponse;
  role: BoardRole;
  status: MemberStatus;
  joinedAt: string | null;
};

export type Invitation = {
  invitationId: string;
  inviteCode: string;
  inviteeEmail: string | null;
  intendedRole: BoardRole;
  status: 'PENDING' | 'ACCEPTED' | 'DECLINED' | 'EXPIRED';
  expiresAt: string;
};

export const familyBoardService = {
  getMyBoard: async (): Promise<FamilyBoard> => {
    const res = await api.get('/v1/family-board');
    return unwrapApiResponse<FamilyBoard>(res, '가족 보드를 불러오지 못했습니다.');
  },

  getMembers: async (): Promise<FamilyMember[]> => {
    const res = await api.get('/v1/family-board/members');
    return unwrapApiResponse<FamilyMember[]>(res, '가족 멤버를 불러오지 못했습니다.');
  },

  invite: async (req: { inviteeEmail: string; intendedRole: BoardRole }): Promise<Invitation> => {
    const res = await api.post('/v1/family-board/invite', req);
    return unwrapApiResponse<Invitation>(res, '가족 초대 생성에 실패했습니다.');
  },

  accept: async (inviteCode: string): Promise<FamilyMember> => {
    const res = await api.post('/v1/family-board/accept', { inviteCode });
    return unwrapApiResponse<FamilyMember>(res, '가족 초대 수락에 실패했습니다.');
  },

  updateMemberRole: async (memberId: string, newRole: BoardRole): Promise<FamilyMember> => {
    const res = await api.put(`/v1/family-board/members/${memberId}/role`, { newRole });
    return unwrapApiResponse<FamilyMember>(res, '멤버 역할 변경에 실패했습니다.');
  },

  removeMember: async (memberId: string): Promise<void> => {
    await api.delete(`/v1/family-board/members/${memberId}`);
  },

  updateSettings: async (settings: BoardSettingsUpdate): Promise<FamilyBoard> => {
    const res = await api.put('/v1/family-board/settings', { settings });
    return unwrapApiResponse<FamilyBoard>(res, '보드 설정 변경에 실패했습니다.');
  },
};



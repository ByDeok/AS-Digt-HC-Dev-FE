// src/app/(main)/family/page.tsx
/**
 * 스크립트 용도: 가족 정보 및 활동 피드 페이지
 *
 * 함수 호출 구조:
 * FamilyPage
 * ├── Header
 * ├── Card (Family Members)
 * │   ├── Avatar
 * │   └── Badge
 * └── Card (Activity Feed)
 *     └── Icons (HeartPulse, Pill)
 */

'use client';

import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Mail } from 'lucide-react';
import { useToast } from '@/hooks/use-toast';
import { PageHeader } from '@/components/common/PageHeader';
import { Section } from '@/components/common/Section';
import { ListItem } from '@/components/common/ListItem';
import {
  useAcceptFamilyInvite,
  useFamilyMembers,
  useInviteFamilyMember,
  useMyFamilyBoard,
} from '@/hooks/queries/useFamilyBoard';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { useState, useEffect, useRef } from 'react';
import {
  trackFamilyBoardView,
  trackFamilyInviteOpen,
  trackFamilyInviteSend,
  trackFamilyInviteCopyCode,
  trackFamilyJoinAttempt,
  trackFamilyJoinSuccess,
} from '@/lib/analytics';

/**
 * 프로그램 단위 용도: 연결된 가족 멤버 목록과 그들의 최근 활동 내역을 표시
 * 기능:
 * - 가족 멤버 목록 렌더링 (역할 표시)
 * - 활동 피드 타임라인 표시
 * - 가족 초대 링크 복사 기능
 */
export default function FamilyPage() {
  const { toast } = useToast();
  const { data: board, isLoading: boardLoading, isError: boardError } = useMyFamilyBoard();
  const { data: members = [], isLoading: membersLoading, isError: membersError } = useFamilyMembers();
  const inviteMutation = useInviteFamilyMember();
  const acceptMutation = useAcceptFamilyInvite();
  const [inviteCode, setInviteCode] = useState('');
  // 페이지뷰 추적 여부 (중복 방지)
  const hasPageViewTracked = useRef(false);

  // GA4: 가족 보드 페이지 조회 이벤트 (한 번만)
  useEffect(() => {
    if (!boardLoading && !membersLoading && !hasPageViewTracked.current) {
      hasPageViewTracked.current = true;
      // 현재 사용자의 역할 찾기 (members 배열에서)
      const currentUserRole = members.find((m) => m.role === 'ADMIN')?.role || 'VIEWER';
      trackFamilyBoardView(members.length, currentUserRole);
    }
  }, [board, members, boardLoading, membersLoading]);

  const handleInvite = async () => {
    // GA4: 초대 모달 열기 이벤트
    trackFamilyInviteOpen();

    const email = window.prompt('초대할 가족의 이메일을 입력해주세요.');
    if (!email) return;

    try {
      const inv = await inviteMutation.mutateAsync({ inviteeEmail: email, intendedRole: 'VIEWER' });
      await navigator.clipboard.writeText(inv.inviteCode);

      // GA4: 초대 발송 이벤트
      trackFamilyInviteSend('VIEWER');
      // GA4: 초대 코드 복사 이벤트
      trackFamilyInviteCopyCode();

      toast({
        title: '초대 코드가 복사되었습니다.',
        description: `초대 코드: ${inv.inviteCode}`,
      });
    } catch (e) {
      const message = e instanceof Error ? e.message : '가족 초대에 실패했습니다.';
      toast({ title: '초대 실패', description: message, variant: 'destructive' });
    }
  };

  const handleAccept = async () => {
    const code = inviteCode.trim();
    if (!code) return;

    // GA4: 참여 시도 이벤트
    trackFamilyJoinAttempt();

    try {
      await acceptMutation.mutateAsync(code);
      setInviteCode('');

      // GA4: 참여 성공 이벤트
      trackFamilyJoinSuccess('invite_code');

      toast({ title: '참여 완료', description: '가족 보드에 참여했습니다.' });
    } catch (e) {
      const message = e instanceof Error ? e.message : '초대 수락에 실패했습니다.';
      toast({ title: '참여 실패', description: message, variant: 'destructive' });
    }
  };

  if (boardLoading || membersLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-50">
        <LoadingSpinner />
      </div>
    );
  }

  if (boardError || membersError) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-50">
        <p className="text-red-500">가족 정보를 불러오는 중 오류가 발생했습니다.</p>
      </div>
    );
  }

  return (
    <div className="bg-gray-50 min-h-screen">
      <PageHeader title="가족" className="justify-center" />

      <main className="p-4 space-y-6">
        <Section
          title={board?.name || '우리 가족'}
          description={board?.description || '가족의 건강 상태를 함께 확인해요.'}
          action={
            <Button onClick={handleInvite} size="sm" withIcon>
              <Mail className="w-4 h-4" />
              가족 초대
            </Button>
          }
        >
          <div className="space-y-4">
            {members.map((member) => (
              <ListItem
                key={member.user.id}
                className="p-0 hover:bg-transparent"
                start={
                  <Avatar>
                    <AvatarImage src={`https://i.pravatar.cc/150?u=${member.user.id}`} />
                    <AvatarFallback>{(member.user.name || 'U').charAt(0)}</AvatarFallback>
                  </Avatar>
                }
                middle={
                  <div>
                    <p className="font-semibold">{member.user.name}</p>
                    <p className="text-sm text-muted-foreground">{member.user.email}</p>
                  </div>
                }
                end={
                  <Badge variant={member.role === 'ADMIN' ? 'default' : 'secondary'}>
                    {member.role === 'ADMIN' ? '관리자' : member.role === 'EDITOR' ? '편집자' : '뷰어'}
                  </Badge>
                }
              />
            ))}
          </div>
        </Section>

        <Section title="초대 코드로 참여" description="가족에게 받은 초대 코드를 입력해 참여할 수 있어요.">
          <div className="flex gap-2">
            <Input
              value={inviteCode}
              onChange={(e) => setInviteCode(e.target.value)}
              placeholder="초대 코드 입력"
              autoComplete="off"
            />
            <Button onClick={handleAccept} disabled={acceptMutation.isPending}>
              {acceptMutation.isPending ? '처리 중...' : '참여'}
            </Button>
          </div>
        </Section>

        <Section
          title="활동 피드"
          description={
            board?.lastActivityAt
              ? `최근 활동 시간: ${board.lastActivityAt}`
              : '가족의 최근 활동 내역입니다. (준비중)'
          }
        >
          <div className="rounded-md border bg-card p-4 text-sm text-muted-foreground">
            활동 피드는 현재 BE API가 준비되는 단계입니다. (연동 시, 행동 카드/리포트/측정 데이터와 함께 제공 예정)
          </div>
        </Section>
      </main>
    </div>
  );
}

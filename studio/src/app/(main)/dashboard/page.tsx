// src/app/(main)/dashboard/page.tsx
/**
 * 스크립트 용도: 사용자 대시보드 페이지
 * 
 * 함수 호출 구조:
 * DashboardPage
 * ├── Confetti (Mission Completion Effect)
 * ├── Header (User Info & Notifications)
 * └── Card (Mission List)
 *     └── Button (Toggle Mission)
 */

import { useState, useEffect } from 'react';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Bell, Check, Circle, RefreshCw } from 'lucide-react';
import { mockUser, mockMissions, Mission } from '@/lib/mockData';
import { cn } from '@/lib/utils';
import { useToast } from '@/hooks/use-toast';
import Confetti from 'react-confetti';
import { PageHeader } from '@/components/common/PageHeader';
import { Section } from '@/components/common/Section';
import { ListItem } from '@/components/common/ListItem';

/**
 * 프로그램 단위 용도: 사용자의 오늘의 미션을 관리하고, 알림을 확인하는 메인 화면
 * 기능:
 * - 미션 목록 표시 및 완료 처리
 * - 미션 완료 시 폭죽 효과
 * - 사용자 정보 및 알림 버튼 표시
 */
export default function DashboardPage() {
  const [missions, setMissions] = useState<Mission[]>(mockMissions);
  const [completedMissionId, setCompletedMissionId] = useState<string | null>(null);
  const [showConfetti, setShowConfetti] = useState(false);
  const [windowSize, setWindowSize] = useState<{ width: number; height: number; }>({ width: 0, height: 0 });

  const { toast } = useToast();

  // Window resize handler for Confetti
  useEffect(() => {
    const handleResize = () => {
      setWindowSize({ width: window.innerWidth, height: window.innerHeight });
    };
    window.addEventListener('resize', handleResize);
    handleResize();
    return () => window.removeEventListener('resize', handleResize);
  }, []);


  const remainingMissions = missions.filter(m => !m.isCompleted).length;

  const handleMissionToggle = (missionId: string) => {
    const mission = missions.find(m => m.id === missionId);
    if (!mission || mission.isCompleted) return;

    setMissions(prevMissions =>
      prevMissions.map(m =>
        m.id === missionId ? { ...m, isCompleted: true } : m
      )
    );
    
    setCompletedMissionId(missionId);
    setShowConfetti(true);
    toast({
      title: "참 잘했어요!",
      description: `'${mission.title}' 미션을 완료했어요.`,
    });

    setTimeout(() => {
      setShowConfetti(false);
      setCompletedMissionId(null);
    }, 4000);
  };

  const handleNotificationClick = () => {
    toast({
      title: "알림",
      description: "지금 혈압 잴 시간입니다.",
    });
  };

  const handleRefresh = () => {
    setMissions(mockMissions);
    toast({
      title: "새로고침 완료",
      description: "미션 목록이 업데이트되었습니다.",
    });
  };

  return (
    <div className="flex flex-col min-h-screen bg-gray-50 pb-20">
      {showConfetti && <Confetti width={windowSize.width} height={windowSize.height} recycle={false} numberOfPieces={200} />}
      <PageHeader
        title={`안녕하세요, ${mockUser.name}님!`}
        leftContent={
          <Avatar>
            <AvatarImage src={`https://i.pravatar.cc/150?u=${mockUser.uid}`} />
            <AvatarFallback>{mockUser.name.charAt(0)}</AvatarFallback>
          </Avatar>
        }
        rightContent={
          <div className="flex items-center gap-1">
            <Button variant="ghost" size="icon" onClick={handleRefresh}>
              <RefreshCw className="w-6 h-6" />
            </Button>
            <Button variant="ghost" size="icon" onClick={handleNotificationClick}>
              <Bell className="w-6 h-6" />
            </Button>
          </div>
        }
      />
      
      <main className="flex-1 p-4 space-y-6 max-w-3xl mx-auto w-full">
        <Section title={`오늘 할 일 ${remainingMissions > 0 ? `${remainingMissions}개 남았어요` : '모두 완료!'}`}>
          <div className="space-y-3">
            {missions.map(mission => (
              <ListItem
                key={mission.id}
                onClick={() => handleMissionToggle(mission.id)}
                className={cn(
                  "shadow-sm",
                  mission.isCompleted ? "bg-secondary/50 text-muted-foreground border-transparent" : "bg-card border-border hover:border-primary/50",
                  completedMissionId === mission.id && "bg-primary/10 border-primary ring-1 ring-primary/20"
                )}
                middle={
                  <span className={cn(
                    "font-medium transition-all",
                    mission.isCompleted && "line-through text-muted-foreground decoration-slate-400"
                  )}>
                    {mission.title}
                  </span>
                }
                end={
                  mission.isCompleted ? (
                    <Check className="w-6 h-6 text-primary" />
                  ) : (
                    <Circle className="w-6 h-6 text-muted-foreground" />
                  )
                }
              />
            ))}
          </div>
        </Section>
      </main>
    </div>
  );
}

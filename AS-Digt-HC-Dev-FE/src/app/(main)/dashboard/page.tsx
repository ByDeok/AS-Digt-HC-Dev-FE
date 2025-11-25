"use client";

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Bell, Check, Circle } from 'lucide-react';
import { mockUser, mockMissions, Mission } from '@/lib/mockData';
import { cn } from '@/lib/utils';
import { useToast } from '@/hooks/use-toast';
import Confetti from 'react-confetti';

export default function DashboardPage() {
  const [missions, setMissions] = useState<Mission[]>(mockMissions);
  const [completedMissionId, setCompletedMissionId] = useState<string | null>(null);
  const [showConfetti, setShowConfetti] = useState(false);
  const [windowSize, setWindowSize] = useState<{ width: number; height: number; }>({ width: 0, height: 0 });

  const { toast } = useToast();
  const router = useRouter();

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

  return (
    <div className="flex flex-col min-h-screen bg-gray-50 pb-20">
      {showConfetti && <Confetti width={windowSize.width} height={windowSize.height} recycle={false} numberOfPieces={200} />}
      <header className="flex items-center justify-between p-4 bg-background border-b">
        <div className="flex items-center gap-3">
          <Avatar>
            <AvatarImage src={`https://i.pravatar.cc/150?u=${mockUser.uid}`} />
            <AvatarFallback>{mockUser.name.charAt(0)}</AvatarFallback>
          </Avatar>
          <h1 className="text-lg font-semibold">안녕하세요, {mockUser.name}님!</h1>
        </div>
        <Button variant="ghost" size="icon" onClick={handleNotificationClick}>
          <Bell className="w-6 h-6" />
        </Button>
      </header>
      
      <main className="flex-1 p-4 space-y-6">
        <Card>
          <CardHeader>
            <CardTitle>오늘 할 일 {remainingMissions > 0 ? `${remainingMissions}개 남았어요` : '모두 완료!'}</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {missions.map(mission => (
                <button
                  key={mission.id}
                  onClick={() => handleMissionToggle(mission.id)}
                  className={cn(
                    "w-full flex items-center justify-between p-4 rounded-lg transition-all",
                    mission.isCompleted ? "bg-secondary text-muted-foreground" : "bg-background border",
                    completedMissionId === mission.id && "bg-primary/20"
                  )}
                >
                  <span className="font-medium">{mission.title}</span>
                  {mission.isCompleted ? (
                    <Check className="w-6 h-6 text-primary" />
                  ) : (
                    <Circle className="w-6 h-6 text-muted-foreground" />
                  )}
                </button>
              ))}
            </div>
          </CardContent>
        </Card>
      </main>
    </div>
  );
}
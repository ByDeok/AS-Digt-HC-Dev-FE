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

import { useState, useEffect, useRef } from 'react';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Bell, Check, Circle, RefreshCw, RotateCcw } from 'lucide-react';
import { cn } from '@/lib/utils';
import { useToast } from '@/hooks/use-toast';
import Confetti from 'react-confetti';
import { PageHeader } from '@/components/common/PageHeader';
import { Section } from '@/components/common/Section';
import { ListItem } from '@/components/common/ListItem';
import { useCompleteAction, useTodayActions, actionKeys } from '@/hooks/queries/useActions';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { useQueryClient } from '@tanstack/react-query';
import { getStoredUser } from '@/lib/auth';
import { useDailyHealthMetrics, useUpsertDailyHealthMetrics } from '@/hooks/queries/useHealthMetrics';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  trackMissionView,
  trackMissionComplete,
  trackHealthRecordSubmit,
  trackEvent,
} from '@/lib/analytics';

/**
 * 프로그램 단위 용도: 사용자의 오늘의 미션을 관리하고, 알림을 확인하는 메인 화면
 * 기능:
 * - 미션 목록 표시 및 완료 처리
 * - 미션 완료 시 폭죽 효과
 * - 사용자 정보 및 알림 버튼 표시
 */
export default function DashboardPage() {
  const { data: actions = [], isLoading, isError } = useTodayActions();
  const completeActionMutation = useCompleteAction();
  const queryClient = useQueryClient();
  const storedUser = getStoredUser();

  const [completedMissionId, setCompletedMissionId] = useState<string | null>(null);
  const [showConfetti, setShowConfetti] = useState(false);
  const [windowSize, setWindowSize] = useState<{ width: number; height: number }>({
    width: 0,
    height: 0,
  });

  const { toast } = useToast();

  // Manual health input state
  const today = new Date().toISOString().split('T')[0];
  const { data: todayMetrics } = useDailyHealthMetrics(today);
  const upsertMutation = useUpsertDailyHealthMetrics(today);

  const [healthData, setHealthData] = useState({
    systolic: '',
    diastolic: '',
    weight: '',
    heartRate: '',
    steps: '',
  });

  // 미션 목록 추적 여부 (중복 방지)
  const hasMissionViewTracked = useRef(false);

  // Sync form data with fetched metrics
  useEffect(() => {
    if (todayMetrics) {
      setHealthData({
        systolic: todayMetrics.systolic?.toString() || '',
        diastolic: todayMetrics.diastolic?.toString() || '',
        weight: todayMetrics.weight?.toString() || '',
        heartRate: todayMetrics.heartRate?.toString() || '',
        steps: todayMetrics.steps?.toString() || '',
      });
    }
  }, [todayMetrics]);

  // GA4: 미션 목록 조회 이벤트 (한 번만)
  useEffect(() => {
    if (!isLoading && actions.length > 0 && !hasMissionViewTracked.current) {
      hasMissionViewTracked.current = true;
      const completedCount = actions.filter((a) => a.status === 'COMPLETED').length;
      trackMissionView(actions.length, completedCount);
    }
  }, [actions, isLoading]);

  // Window resize handler for Confetti
  useEffect(() => {
    const handleResize = () => {
      setWindowSize({ width: window.innerWidth, height: window.innerHeight });
    };
    window.addEventListener('resize', handleResize);
    handleResize();
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  // 날짜 변경 감지 및 자동 새로고침
  useEffect(() => {
    const getTodayString = () => new Date().toDateString();
    let currentDate = getTodayString();

    const checkDateChange = () => {
      const newDate = getTodayString();
      if (newDate !== currentDate) {
        currentDate = newDate;
        // 날짜가 바뀌면 미션 재조회
        queryClient.invalidateQueries({ queryKey: actionKeys.today });
      }
    };

    // 1분마다 날짜 체크 (자정 근처에서 날짜 변경 감지)
    const intervalId = setInterval(checkDateChange, 60000); // 1분

    // 페이지 visibility 변경 시에도 체크 (다른 탭에서 돌아올 때)
    const handleVisibilityChange = () => {
      if (!document.hidden) {
        checkDateChange();
      }
    };
    document.addEventListener('visibilitychange', handleVisibilityChange);

    // 초기 체크
    checkDateChange();

    return () => {
      clearInterval(intervalId);
      document.removeEventListener('visibilitychange', handleVisibilityChange);
    };
  }, [queryClient]);

  const remainingActions = actions.filter((a) => a.status === 'PENDING').length;

  const handleActionComplete = (actionId: number) => {
    const action = actions.find((a) => a.id === actionId);
    if (!action || action.status !== 'PENDING') return;

    completeActionMutation.mutate(actionId, {
      onSuccess: () => {
        setCompletedMissionId(String(actionId));
        setShowConfetti(true);

        // GA4: 미션 완료 이벤트
        const isFirstMissionToday = actions.filter((a) => a.status === 'COMPLETED').length === 0;
        trackMissionComplete(
          String(actionId),
          action.title,
          'health', // 카테고리 (action에 category 필드가 있으면 사용)
          isFirstMissionToday
        );

        // GA4: 폭죽 효과 트리거 이벤트
        trackEvent('confetti_trigger', { trigger_type: 'mission_complete' });

        toast({
          title: '참 잘했어요!',
          description: `'${action.title}' 행동을 완료했어요.`,
        });

        // TODO: 백엔드 API 추가 필요 - 활동 피드에 게시글 생성
        // 예: POST /api/family-board/activity-feed
        // 활동 피드 게시글 생성 API 호출 (백엔드에서 completeAction 시 자동으로 처리할 수도 있음)
        // try {
        //   await familyBoardService.createActivityPost({
        //     type: 'ACTION_COMPLETED',
        //     actionId: actionId,
        //     title: action.title,
        //   });
        // } catch (error) {
        //   console.error('활동 피드 게시글 생성 실패:', error);
        // }

        setTimeout(() => {
          setShowConfetti(false);
          setCompletedMissionId(null);
        }, 4000);
      },
    });
  };

  const handleNotificationClick = () => {
    toast({
      title: '알림',
      description: '지금 혈압 잴 시간입니다.',
    });
  };

  const handleRefresh = () => {
    queryClient.invalidateQueries({ queryKey: actionKeys.today });
    toast({
      title: '새로고침 완료',
      description: '오늘의 행동 카드가 업데이트되었습니다.',
    });
  };

  const handleReset = async () => {
    // TODO: 백엔드 API 추가 필요 - 완료된 오늘의 할 일들을 모두 미완료로 되돌리는 API
    // 예: POST /api/actions/today/reset
    const completedActions = actions.filter((a) => a.status === 'COMPLETED');
    if (completedActions.length === 0) {
      toast({
        title: '초기화할 항목이 없습니다',
        description: '완료된 할 일이 없습니다.',
      });
      return;
    }

    // 임시: 프론트엔드에서만 처리 (백엔드 API 추가 후 제거)
    toast({
      title: '초기화 기능 준비중',
      description: '백엔드 API가 추가되면 완료된 할 일을 초기화할 수 있습니다.',
      variant: 'destructive',
    });

    // 백엔드 API 추가 시 아래 코드 사용:
    // try {
    //   await actionsService.resetTodayActions();
    //   queryClient.invalidateQueries({ queryKey: actionKeys.today });
    //   toast({
    //     title: '초기화 완료',
    //     description: '완료된 할 일이 모두 미완료로 되돌아갔습니다.',
    //   });
    // } catch (error) {
    //   toast({
    //     title: '초기화 실패',
    //     description: '할 일 초기화에 실패했습니다.',
    //     variant: 'destructive',
    //   });
    // }
  };

  const handleHealthSave = async () => {
    try {
      await upsertMutation.mutateAsync({
        recordDate: today,
        systolic: healthData.systolic ? Number(healthData.systolic) : null,
        diastolic: healthData.diastolic ? Number(healthData.diastolic) : null,
        weight: healthData.weight ? Number(healthData.weight) : null,
        heartRate: healthData.heartRate ? Number(healthData.heartRate) : null,
        steps: healthData.steps ? Number(healthData.steps) : null,
      });

      // GA4: 건강 기록 제출 이벤트
      const metricsTypes: string[] = [];
      if (healthData.systolic || healthData.diastolic) metricsTypes.push('bloodPressure');
      if (healthData.weight) metricsTypes.push('weight');
      if (healthData.heartRate) metricsTypes.push('heartRate');
      if (healthData.steps) metricsTypes.push('steps');
      trackHealthRecordSubmit(today, metricsTypes, metricsTypes.length);

      toast({
        title: '건강 기록 저장 완료',
        description: `${today}의 건강 데이터가 저장되었습니다.`,
      });
    } catch (e) {
      const message = e instanceof Error ? e.message : '건강 기록 저장에 실패했습니다.';
      toast({
        title: '저장 실패',
        description: message,
        variant: 'destructive',
      });
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-50">
        <LoadingSpinner />
      </div>
    );
  }

  if (isError) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-50">
        <p className="text-red-500">데이터를 불러오는 중 오류가 발생했습니다.</p>
      </div>
    );
  }

  return (
    <div className="flex flex-col min-h-screen bg-gray-50 pb-20">
      {showConfetti && (
        <Confetti
          width={windowSize.width}
          height={windowSize.height}
          recycle={false}
          numberOfPieces={200}
        />
      )}
      <PageHeader
        title={`안녕하세요, ${storedUser?.name || '사용자'}님!`}
        leftContent={
          <Avatar>
            <AvatarImage src={`https://i.pravatar.cc/150?u=${storedUser?.id || 'user'}`} />
            <AvatarFallback>{(storedUser?.name || 'U').charAt(0)}</AvatarFallback>
          </Avatar>
        }
        rightContent={
          <div className="flex items-center gap-1">
            <Button variant="ghost" size="icon" onClick={handleReset} title="할 일 초기화">
              <RotateCcw className="w-6 h-6" />
            </Button>
            <Button variant="ghost" size="icon" onClick={handleRefresh} title="새로고침">
              <RefreshCw className="w-6 h-6" />
            </Button>
            <Button variant="ghost" size="icon" onClick={handleNotificationClick} title="알림">
              <Bell className="w-6 h-6" />
            </Button>
          </div>
        }
      />

      <main className="flex-1 p-4 space-y-6 max-w-3xl mx-auto w-full">
        <Section
          title={`오늘의 건강 기록 (${today})`}
          description="혈압, 체중, 심박수, 걸음 수를 입력하세요."
        >
          <div className="space-y-4">
            {/* Blood Pressure */}
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-2">
                <Label htmlFor="systolic" className="text-base font-semibold">
                  수축기 혈압 (mmHg)
                </Label>
                <Input
                  id="systolic"
                  type="number"
                  placeholder="120"
                  value={healthData.systolic}
                  onChange={(e) => setHealthData({ ...healthData, systolic: e.target.value })}
                  min="60"
                  max="250"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="diastolic" className="text-base font-semibold">
                  이완기 혈압 (mmHg)
                </Label>
                <Input
                  id="diastolic"
                  type="number"
                  placeholder="80"
                  value={healthData.diastolic}
                  onChange={(e) => setHealthData({ ...healthData, diastolic: e.target.value })}
                  min="40"
                  max="150"
                />
              </div>
            </div>

            {/* Weight, Heart Rate, Steps */}
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-2">
                <Label htmlFor="weight" className="text-base font-semibold">
                  체중 (kg)
                </Label>
                <Input
                  id="weight"
                  type="number"
                  placeholder="70"
                  value={healthData.weight}
                  onChange={(e) => setHealthData({ ...healthData, weight: e.target.value })}
                  min="20"
                  max="300"
                  step="0.1"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="heartRate" className="text-base font-semibold">
                  심박수 (bpm)
                </Label>
                <Input
                  id="heartRate"
                  type="number"
                  placeholder="72"
                  value={healthData.heartRate}
                  onChange={(e) => setHealthData({ ...healthData, heartRate: e.target.value })}
                  min="30"
                  max="200"
                />
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="steps" className="text-base font-semibold">
                걸음 수
              </Label>
              <Input
                id="steps"
                type="number"
                placeholder="5000"
                value={healthData.steps}
                onChange={(e) => setHealthData({ ...healthData, steps: e.target.value })}
                min="0"
                max="100000"
              />
            </div>

            <Button
              onClick={handleHealthSave}
              disabled={upsertMutation.isPending}
              className="w-full h-12 text-base font-semibold"
            >
              {upsertMutation.isPending ? '저장 중...' : '건강 기록 저장'}
            </Button>
          </div>
        </Section>

        <Section
          title={`오늘 할 일 ${remainingActions > 0 ? `${remainingActions}개 남았어요` : '모두 완료!'}`}
        >
          <div className="space-y-3">
            {actions.map((action) => (
              <ListItem
                key={action.id}
                onClick={() => handleActionComplete(action.id)}
                className={cn(
                  'shadow-sm',
                  action.status === 'COMPLETED'
                    ? 'bg-secondary/50 text-muted-foreground border-transparent'
                    : 'bg-card border-border hover:border-primary/50',
                  completedMissionId === String(action.id) &&
                    'bg-primary/10 border-primary ring-1 ring-primary/20',
                )}
                middle={
                  <span
                    className={cn(
                      'font-medium transition-all',
                      action.status === 'COMPLETED' &&
                        'line-through text-muted-foreground decoration-slate-400',
                    )}
                  >
                    {action.title}
                  </span>
                }
                end={
                  action.status === 'COMPLETED' ? (
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

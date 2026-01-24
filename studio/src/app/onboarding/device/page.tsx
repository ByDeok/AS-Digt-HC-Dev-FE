// src/app/onboarding/device/page.tsx
/**
 * 스크립트 용도: 온보딩 - 기기 연결 설정 페이지
 *
 * 함수 호출 구조:
 * OnboardingDevicePage
 * └── Card (Device Selection)
 *     ├── Button (Device Item - Watch/BP Monitor)
 *     └── Button (Next/Skip)
 */

'use client';

import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { CheckCircle2, HeartPulse, Loader2, Watch } from 'lucide-react';
import { cn } from '@/lib/utils';
import { integrationService } from '@/services/integrationService';
import { useToast } from '@/hooks/use-toast';
import {
  trackOnboardingStepView,
  trackOnboardingStepComplete,
  trackDeviceConnectAttempt,
  trackDeviceConnectSuccess,
  trackDeviceConnectFail,
  trackDeviceStepSkip,
} from '@/lib/analytics';

type DeviceStatus = 'idle' | 'connecting' | 'connected';
const devices = [
  { id: 'watch', name: '스마트 워치', icon: Watch },
  { id: 'bp', name: '스마트 혈압계', icon: HeartPulse },
];

/**
 * 프로그램 단위 용도: 사용자가 보유한 헬스케어 기기(워치, 혈압계 등)를 앱과 연동
 * 기능:
 * - 기기 선택 및 연결 시뮬레이션
 * - 연결 상태 시각적 피드백 (로딩, 완료)
 * - 다음 단계 이동 또는 건너뛰기
 */
export default function OnboardingDevicePage() {
  const [deviceStatuses, setDeviceStatuses] = useState<Record<string, DeviceStatus>>({});
  const navigate = useNavigate();
  const { toast } = useToast();
  // 단계 시작 시간 기록 (소요 시간 계산용)
  const stepStartTimeRef = useRef<number>(Date.now());

  // GA4: 기기 연동 단계 시작 이벤트
  useEffect(() => {
    trackOnboardingStepView('device', 4);
    stepStartTimeRef.current = Date.now();
  }, []);

  const handleConnect = async (deviceId: string) => {
    setDeviceStatuses((prev) => ({ ...prev, [deviceId]: 'connecting' }));

    const deviceType = deviceId === 'watch' ? 'WATCH' : 'BP_MONITOR';
    const deviceName = deviceId === 'watch' ? '스마트 워치' : '스마트 혈압계';
    const dataTypes = deviceId === 'watch' ? ['STEPS', 'HEART_RATE', 'SLEEP'] : ['BLOOD_PRESSURE'];

    // GA4: 기기 연동 시도 이벤트
    trackDeviceConnectAttempt(deviceType, 'mock');

    try {
      await integrationService.connectDevice({
        vendor: 'mock',
        deviceType,
        authCode: 'local',
        consentScope: {
          dataTypes,
          frequency: 'daily',
          retentionPeriod: '30d',
          sharingAllowed: { family: true },
        },
      });
      setDeviceStatuses((prev) => ({ ...prev, [deviceId]: 'connected' }));

      // GA4: 기기 연동 성공 이벤트
      trackDeviceConnectSuccess(deviceType, 'mock');

      toast({ title: '연동 완료', description: `${deviceName}가 연동되었습니다.` });
    } catch (e) {
      const message = e instanceof Error ? e.message : '기기 연동에 실패했습니다.';
      setDeviceStatuses((prev) => ({ ...prev, [deviceId]: 'idle' }));

      // GA4: 기기 연동 실패 이벤트
      trackDeviceConnectFail(deviceType, 'mock', message);

      toast({ title: '연동 실패', description: message, variant: 'destructive' });
    }
  };

  const handleNext = (isSkip: boolean = false) => {
    // GA4: 기기 연동 단계 완료 이벤트
    const durationSec = Math.round((Date.now() - stepStartTimeRef.current) / 1000);
    const connectedCount = Object.values(deviceStatuses).filter((s) => s === 'connected').length;
    trackOnboardingStepComplete('device', 4, durationSec, connectedCount, 2);

    if (isSkip) {
      // GA4: 기기 연동 건너뛰기 이벤트
      trackDeviceStepSkip();
    }

    navigate('/onboarding/complete');
  };

  return (
    <Card className="w-full">
      <CardHeader>
        <CardTitle>사용 중인 기기를 연결해요</CardTitle>
        <CardDescription>
          자동으로 건강 데이터를 기록할 수 있어 편리해요. 나중에 언제든지 추가할 수 있어요.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-2 gap-4 mb-8">
          {devices.map((device) => {
            const status = deviceStatuses[device.id] || 'idle';
            const Icon = device.icon;
            return (
              <button
                key={device.id}
                onClick={() => handleConnect(device.id)}
                disabled={status !== 'idle'}
                className={cn(
                  'flex flex-col items-center justify-center gap-3 p-4 border rounded-lg aspect-square text-center transition-colors',
                  'focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2',
                  status === 'idle' && 'hover:bg-accent',
                  status === 'connected' && 'bg-primary/10 border-primary text-primary',
                  status === 'connecting' && 'cursor-wait',
                )}
              >
                {status === 'connecting' && (
                  <Loader2 className="w-10 h-10 animate-spin text-primary" />
                )}
                {status === 'connected' && <CheckCircle2 className="w-10 h-10 text-primary" />}
                {status === 'idle' && <Icon className="w-10 h-10 text-muted-foreground" />}
                <span className="font-semibold text-base">{device.name}</span>
                {status === 'connecting' && (
                  <span className="text-sm text-muted-foreground">연동 중...</span>
                )}
              </button>
            );
          })}
        </div>
        <div className="flex flex-col gap-3">
          <Button size="xl" onClick={() => handleNext(false)} className="w-full">
            연결 완료
          </Button>
          <Button size="xl" variant="ghost" onClick={() => handleNext(true)} className="w-full">
            건너뛰기
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}

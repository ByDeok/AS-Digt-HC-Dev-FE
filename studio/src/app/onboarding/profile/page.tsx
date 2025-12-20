// src/app/onboarding/profile/page.tsx
/**
 * 스크립트 용도: 온보딩 - 사용자 프로필 입력 페이지
 *
 * 함수 호출 구조:
 * OnboardingProfilePage
 * └── Card (Form Container)
 *     ├── Progress (Step Indicator)
 *     └── Input / RadioGroup / Checkbox (Form Fields)
 */

'use client';

import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { Checkbox } from '@/components/ui/checkbox';
import { WizardLayout } from '@/components/layout/WizardLayout';
import { onboardingService } from '@/services/onboardingService';
import { userService } from '@/services/userService';
import { useToast } from '@/hooks/use-toast';

type Step = 'name' | 'birthdate' | 'conditions';

const conditionsList = ['고혈압', '당뇨', '고지혈증', '관절염'];

/**
 * 프로그램 단위 용도: 사용자 기본 정보(이름, 생년월일, 성별, 기저질환)를 수집하는 다단계 양식
 * 기능:
 * - 단계별 입력 폼 제공 (이름 -> 생년월일 -> 건강정보)
 * - 진행률(Progress Bar) 표시
 * - 이전/다음 네비게이션
 */
export default function OnboardingProfilePage() {
  const [step, setStep] = useState<Step>('name');
  const [formData, setFormData] = useState({
    name: '',
    birthdate: '',
    gender: '',
    conditions: [] as string[],
  });
  const navigate = useNavigate();
  const { toast } = useToast();

  useEffect(() => {
    // 온보딩 세션 시작(best-effort). 인증이 없으면 401이므로 무시 가능.
    onboardingService.start().catch(() => undefined);
  }, []);

  const toIsoDate = (yyyymmdd: string) => {
    if (!/^\d{8}$/.test(yyyymmdd)) return null;
    return `${yyyymmdd.slice(0, 4)}-${yyyymmdd.slice(4, 6)}-${yyyymmdd.slice(6, 8)}`;
  };

  const handleNext = async () => {
    if (step === 'name') {
      setStep('birthdate');
      return;
    }

    if (step === 'birthdate') {
      // PROFILE_BASIC 저장
      const birthDate = toIsoDate(formData.birthdate);
      if (!formData.name.trim()) {
        toast({ title: '입력 필요', description: '이름을 입력해주세요.', variant: 'destructive' });
        return;
      }
      if (!birthDate) {
        toast({ title: '입력 오류', description: '생년월일 8자리(예: 19501024)를 입력해주세요.', variant: 'destructive' });
        return;
      }
      const gender = formData.gender === 'male' ? 'MALE' : formData.gender === 'female' ? 'FEMALE' : undefined;

      try {
        await onboardingService.step({
          currentStep: 'PROFILE_BASIC',
          nextStep: 'PROFILE_DETAILS',
          name: formData.name.trim(),
          birthDate,
          gender,
        });
      } catch {
        // 온보딩 저장은 best-effort (서버 미연동/권한 문제로 실패해도 UX를 막지 않음)
      }
      setStep('conditions');
      return;
    }

    if (step === 'conditions') {
      const birthDate = toIsoDate(formData.birthdate);
      const gender = formData.gender === 'male' ? 'MALE' : formData.gender === 'female' ? 'FEMALE' : undefined;
      const primaryConditions = JSON.stringify(formData.conditions);

      try {
        // PROFILE_DETAILS 저장(best-effort)
        await onboardingService.step({
          currentStep: 'PROFILE_DETAILS',
          nextStep: 'HOSPITAL_SELECTION',
          primaryConditions,
        });
      } catch {
        // best-effort
      }

      try {
        // 실제 프로필 저장(핵심)
        await userService.updateMe({
          name: formData.name.trim(),
          birthDate: birthDate ?? undefined,
          gender: gender ?? undefined,
          primaryConditions,
        });
      } catch (e) {
        const message = e instanceof Error ? e.message : '프로필 저장에 실패했습니다.';
        toast({ title: '저장 실패', description: message, variant: 'destructive' });
        return;
      }

      navigate('/onboarding/device');
    }
  };

  const handleBack = () => {
    if (step === 'birthdate') setStep('name');
    else if (step === 'conditions') setStep('birthdate');
  };

  const handleConditionChange = (condition: string) => {
    setFormData((prev) => ({
      ...prev,
      conditions: prev.conditions.includes(condition)
        ? prev.conditions.filter((c) => c !== condition)
        : [...prev.conditions, condition],
    }));
  };

  const progressValue = step === 'name' ? 33 : step === 'birthdate' ? 66 : 100;

  const getStepTitle = () => {
    switch (step) {
      case 'name':
        return '이름을 알려주세요';
      case 'birthdate':
        return '생년월일을 알려주세요';
      case 'conditions':
        return '건강 정보를 알려주세요';
    }
  };

  return (
    <WizardLayout
      title={getStepTitle()}
      description="맞춤 관리를 위해 필요해요."
      progress={progressValue}
      footer={
        <>
          {step !== 'name' && (
            <Button size="xl" variant="outline" onClick={handleBack} className="w-1/3">
              이전
            </Button>
          )}
          <Button size="xl" onClick={handleNext} className="w-full">
            {step === 'conditions' ? '거의 다 왔어요!' : '다음'}
          </Button>
        </>
      }
    >
      <div className="space-y-6">
        {step === 'name' && (
          <div className="space-y-2">
            <Label htmlFor="name" className="text-base">
              이름
            </Label>
            <Input
              id="name"
              placeholder="김철수"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              autoComplete="name"
            />
          </div>
        )}
        {step === 'birthdate' && (
          <div className="space-y-2">
            <Label htmlFor="birthdate" className="text-base">
              생년월일 8자리
            </Label>
            <Input
              id="birthdate"
              placeholder="19501024"
              value={formData.birthdate}
              onChange={(e) => setFormData({ ...formData, birthdate: e.target.value })}
              type="tel"
              maxLength={8}
            />
          </div>
        )}
        {step === 'conditions' && (
          <div className="space-y-6">
            <div className="space-y-3">
              <Label className="text-base">성별</Label>
              <RadioGroup
                defaultValue={formData.gender}
                onValueChange={(value) => setFormData({ ...formData, gender: value })}
                className="flex gap-4"
              >
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="male" id="male" />
                  <Label htmlFor="male" className="text-base">
                    남성
                  </Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="female" id="female" />
                  <Label htmlFor="female" className="text-base">
                    여성
                  </Label>
                </div>
              </RadioGroup>
            </div>
            <div className="space-y-3">
              <Label className="text-base">가지고 있는 질환을 모두 선택해주세요.</Label>
              <div className="grid grid-cols-2 gap-4">
                {conditionsList.map((condition) => (
                  <div
                    key={condition}
                    className="flex items-center space-x-3 rounded-md border p-4"
                  >
                    <Checkbox
                      id={condition}
                      checked={formData.conditions.includes(condition)}
                      onCheckedChange={() => handleConditionChange(condition)}
                    />
                    <Label htmlFor={condition} className="text-base font-normal">
                      {condition}
                    </Label>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>
    </WizardLayout>
  );
}

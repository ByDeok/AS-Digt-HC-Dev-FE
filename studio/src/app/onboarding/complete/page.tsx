// src/app/onboarding/complete/page.tsx
/**
 * 스크립트 용도: 온보딩 - 완료 및 환영 페이지
 * 
 * 함수 호출 구조:
 * OnboardingCompletePage
 * └── Card
 *     └── PartyPopper (Icon)
 */

"use client";

import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { PartyPopper } from "lucide-react";
import { Card, CardContent, CardDescription, CardTitle } from "@/components/ui/card";

/**
 * 프로그램 단위 용도: 온보딩 완료 메시지를 표시하고 대시보드로 자동 이동
 * 기능:
 * - 환영 메시지 및 아이콘 표시
 * - 2.5초 후 대시보드로 자동 리다이렉트
 */
export default function OnboardingCompletePage() {
  const navigate = useNavigate();

  useEffect(() => {
    const timer = setTimeout(() => {
      navigate("/dashboard", { replace: true });
    }, 2500);

    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <Card className="w-full border-0 shadow-none bg-transparent sm:border sm:shadow-sm sm:bg-card">
        <CardContent className="flex flex-col items-center justify-center text-center p-8 gap-6">
          <PartyPopper className="w-16 h-16 text-primary" />
          <div className="space-y-2">
            <CardTitle className="text-3xl font-headline">준비 완료!</CardTitle>
            <CardDescription className="text-lg">
              이제 골든 웰니스와 함께<br />매일 건강을 챙겨봐요.
            </CardDescription>
          </div>
        </CardContent>
    </Card>
  );
}

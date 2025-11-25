"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Loader2 } from "lucide-react";
import { KakaoIcon } from "@/components/icons/kakao";
import { NaverIcon } from "@/components/icons/naver";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";

export default function OnboardingAuthPage() {
  const [isLoading, setIsLoading] = useState<null | "kakao" | "naver">(null);
  const router = useRouter();

  const handleLogin = (provider: "kakao" | "naver") => {
    setIsLoading(provider);
    setTimeout(() => {
      router.push("/onboarding/profile");
    }, 1000);
  };

  return (
    <Card className="w-full">
      <CardHeader className="text-center">
        <CardTitle className="text-2xl font-headline">시작하기</CardTitle>
        <CardDescription>
          간편하게 로그인하고<br />맞춤 건강 관리를 시작해보세요.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          <Button
            size="xl"
            className="w-full bg-[#FEE500] text-black hover:bg-[#FEE500]/90"
            onClick={() => handleLogin("kakao")}
            disabled={!!isLoading}
          >
            {isLoading === 'kakao' ? (
              <Loader2 className="animate-spin" />
            ) : (
              <>
                <KakaoIcon className="w-6 h-6" />
                <span>카카오로 시작하기</span>
              </>
            )}
          </Button>
          <Button
            size="xl"
            className="w-full bg-[#03C75A] text-white hover:bg-[#03C75A]/90"
            onClick={() => handleLogin("naver")}
            disabled={!!isLoading}
          >
            {isLoading === 'naver' ? (
              <Loader2 className="animate-spin" />
            ) : (
              <>
                <NaverIcon className="w-5 h-5" />
                <span>네이버로 시작하기</span>
              </>
            )}
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}

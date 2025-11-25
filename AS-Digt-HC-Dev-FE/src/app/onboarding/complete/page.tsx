"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { PartyPopper } from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";

export default function OnboardingCompletePage() {
  const router = useRouter();

  useEffect(() => {
    const timer = setTimeout(() => {
      router.replace("/dashboard");
    }, 2500);

    return () => clearTimeout(timer);
  }, [router]);

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

import React from "react";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";

interface WizardLayoutProps {
  /** 현재 단계 제목 */
  title: string;
  /** 현재 단계 설명 (선택 사항) */
  description?: string;
  /** 진행률 (0 ~ 100) */
  progress: number;
  /** 메인 콘텐츠 영역 */
  children: React.ReactNode;
  /** 하단 영역 (이전/다음 버튼 등) */
  footer?: React.ReactNode;
}

/**
 * 프로그램 단위 용도: 위자드/단계별 프로세스 레이아웃 컴포넌트
 * 
 * 로직 및 데이터 흐름:
 * - 진행 상태(Progress Bar)를 상단에 표시하여 사용자의 현재 위치를 알려줍니다.
 * - Card 컴포넌트 내부에 제목, 설명, 콘텐츠, 하단 버튼을 배치합니다.
 * - 온보딩 프로세스나 설문조사와 같은 단계별 작업에 사용됩니다.
 */
export function WizardLayout({
  title,
  description,
  progress,
  children,
  footer,
}: WizardLayoutProps) {
  return (
    <Card className="w-full max-w-md mx-auto">
      <CardHeader>
        <Progress value={progress} className="mb-4 h-2" />
        <CardTitle>{title}</CardTitle>
        {description && <CardDescription>{description}</CardDescription>}
      </CardHeader>
      <CardContent>
        <div className="space-y-6">{children}</div>
        {footer && <div className="mt-8 flex gap-4">{footer}</div>}
      </CardContent>
    </Card>
  );
}


import React from "react";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { cn } from "@/lib/utils";

interface SectionProps {
  /** 섹션 제목 */
  title: string;
  /** 섹션 설명 (선택 사항) */
  description?: string;
  /** 헤더 우측 액션 버튼 (선택 사항) */
  action?: React.ReactNode;
  /** 섹션 내부 콘텐츠 */
  children: React.ReactNode;
  /** 추가 스타일 클래스 */
  className?: string;
}

/**
 * 프로그램 단위 용도: 콘텐츠 섹션 컴포넌트
 * 
 * 로직 및 데이터 흐름:
 * - Card 컴포넌트를 기반으로 섹션을 구분합니다.
 * - 제목과 설명을 포함한 헤더 영역과 콘텐츠 영역으로 나뉩니다.
 * - action prop이 있으면 헤더 우측에 배치합니다.
 */
export function Section({
  title,
  description,
  action,
  children,
  className,
}: SectionProps) {
  return (
    <Card className={cn(className)}>
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <div className="space-y-1">
          <CardTitle>{title}</CardTitle>
          {description && <CardDescription>{description}</CardDescription>}
        </div>
        {action}
      </CardHeader>
      <CardContent className="pt-4">{children}</CardContent>
    </Card>
  );
}


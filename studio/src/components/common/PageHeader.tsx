import React from "react";
import { cn } from "@/lib/utils";

interface PageHeaderProps {
  /** 페이지 제목 */
  title?: React.ReactNode;
  /** 좌측 콘텐츠 (뒤로가기 버튼, 아바타 등) */
  leftContent?: React.ReactNode;
  /** 우측 콘텐츠 (알림 아이콘, 액션 버튼 등) */
  rightContent?: React.ReactNode;
  /** 추가 스타일 클래스 */
  className?: string;
}

/**
 * 프로그램 단위 용도: 페이지 상단 헤더 컴포넌트
 * 
 * 로직 및 데이터 흐름:
 * - sticky top-0 포지셔닝을 사용하여 스크롤 시 상단에 고정됩니다.
 * - backdrop-filter를 사용하여 배경 블러 효과를 제공합니다.
 * - 좌측, 중앙(제목), 우측 영역으로 구분되어 콘텐츠를 배치합니다.
 */
export function PageHeader({
  title,
  leftContent,
  rightContent,
  className,
}: PageHeaderProps) {
  return (
    <header
      className={cn(
        "flex items-center justify-between px-4 py-3 bg-background/80 backdrop-blur-md border-b sticky top-0 z-50 transition-all supports-[backdrop-filter]:bg-background/60",
        className
      )}
    >
      <div className="flex items-center gap-3">
        {leftContent}
        {title && <h1 className="text-xl font-bold tracking-tight text-foreground/90">{title}</h1>}
      </div>
      {rightContent && <div>{rightContent}</div>}
    </header>
  );
}


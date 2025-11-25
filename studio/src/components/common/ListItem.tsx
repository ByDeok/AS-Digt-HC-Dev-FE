import React from "react";
import { cn } from "@/lib/utils";

interface ListItemProps extends React.HTMLAttributes<HTMLDivElement> {
  /** 좌측 콘텐츠 (Avatar, Icon 등) */
  start?: React.ReactNode;
  /** 중앙 콘텐츠 (Title, Description 등 - flex-1) */
  middle: React.ReactNode;
  /** 우측 콘텐츠 (Action button, Badge, Checkbox 등) */
  end?: React.ReactNode;
  /** 클릭 핸들러 (제공 시 button 태그로 렌더링됨) */
  onClick?: () => void;
}

/**
 * 프로그램 단위 용도: 리스트 아이템 컴포넌트
 * 
 * 로직 및 데이터 흐름:
 * - start, middle, end prop을 받아 좌-중-우 레이아웃을 구성합니다.
 * - onClick prop이 존재하면 button 태그로, 없으면 div 태그로 렌더링합니다.
 * - 클릭 가능한 상태일 경우 hover/active 스타일을 적용합니다.
 */
export function ListItem({
  start,
  middle,
  end,
  className,
  onClick,
  ...props
}: ListItemProps) {
  const Component = onClick ? "button" : "div";
  
  return (
    <Component
      className={cn(
        "w-full flex items-center justify-between p-4 rounded-xl transition-all duration-200 text-left border border-transparent",
        onClick 
          ? "hover:bg-secondary/80 hover:shadow-sm hover:border-border active:scale-[0.98] cursor-pointer" 
          : "",
        className
      )}
      onClick={onClick}
      {...props}
    >
      <div className="flex items-center gap-3">
        {start}
        <div className="flex-1">{middle}</div>
      </div>
      {end}
    </Component>
  );
}


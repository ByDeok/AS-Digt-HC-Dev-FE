// src/app/(main)/layout.tsx
/**
 * 스크립트 용도: 메인 앱의 공통 레이아웃 (네비게이션 바 포함)
 * 
 * 함수 호출 구조:
 * MainAppLayout
 * ├── <main> (Page Content)
 * └── <nav> (Bottom Navigation)
 *     ├── Link (Tab Item)
 *     └── Popover (Settings Menu)
 */

import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Home, BarChart2, Users, Settings } from 'lucide-react';
import { cn } from '@/lib/utils';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"
import { Button } from '@/components/ui/button';

const navItems = [
  { href: '/dashboard', label: '홈', icon: Home },
  { href: '/report', label: '리포트', icon: BarChart2 },
  { href: '/family', label: '가족', icon: Users },
];

const settingsNav = {
  href: '/settings', label: '설정', icon: Settings
}

/**
 * 프로그램 단위 용도: 하단 탭 네비게이션 바를 포함한 메인 레이아웃 래퍼
 * 기능:
 * - 페이지 콘텐츠 렌더링
 * - 하단 고정 네비게이션 바 제공
 * - 현재 활성화된 탭 강조
 * - 설정 팝오버 메뉴 제공
 */
export default function MainAppLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const location = useLocation();
  const pathname = location.pathname;

  const isSettingsActive = pathname.startsWith(settingsNav.href);

  return (
    <div className="min-h-screen bg-background">
      <main className="pb-20">{children}</main>
      <nav className="fixed bottom-0 left-0 right-0 bg-background border-t shadow-t-lg">
        <div className="flex justify-around max-w-lg mx-auto">
          {navItems.map(({ href, label, icon: Icon }) => (
            <Link
              key={href}
              to={href}
              className={cn(
                'flex flex-col items-center justify-center w-full pt-2 pb-1 text-sm transition-colors',
                pathname === href
                  ? 'text-primary font-semibold'
                  : 'text-muted-foreground hover:text-primary'
              )}
            >
              <Icon className="w-6 h-6 mb-1" />
              <span>{label}</span>
            </Link>
          ))}
          <Popover>
            <PopoverTrigger asChild>
                <button
                className={cn(
                    'flex flex-col items-center justify-center w-full pt-2 pb-1 text-sm transition-colors',
                    isSettingsActive
                    ? 'text-primary font-semibold'
                    : 'text-muted-foreground hover:text-primary'
                )}
                >
                <Settings className="w-6 h-6 mb-1" />
                <span>{settingsNav.label}</span>
                </button>
            </PopoverTrigger>
            <PopoverContent className="w-56 p-2 mb-2">
                <div className="grid">
                    <div className="p-2">
                        <h4 className="font-medium leading-none">설정</h4>
                        <p className="text-sm text-muted-foreground mt-1">
                            앱 관련 설정을 변경할 수 있습니다.
                        </p>
                    </div>
                    <Link to="/settings/profile">
                        <Button variant="ghost" className="w-full justify-start">내 정보 수정</Button>
                    </Link>
                    <Link to="/settings/notifications">
                        <Button variant="ghost" className="w-full justify-start">알림 설정</Button>
                    </Link>
                    <Link to="/">
                        <Button variant="ghost" className="w-full justify-start text-red-500 hover:text-red-600">로그아웃</Button>
                    </Link>
                </div>
            </PopoverContent>
          </Popover>
        </div>
      </nav>
    </div>
  );
}

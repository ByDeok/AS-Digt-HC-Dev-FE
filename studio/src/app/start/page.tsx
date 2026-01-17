// src/app/start/page.tsx
/**
 * 스크립트 용도: 기존 시작 화면(서비스 진입 게이트)
 *
 * 배경:
 * - 전면(/)에는 Hook 랜딩을 배치하여 “사용 의향”을 먼저 만들고,
 * - 실제 가입/로그인/소셜 시작은 CTA를 통해서만 이 화면(/start)으로 유도합니다.
 *
 * 이 페이지는 ‘기존 홈 화면’의 역할(로그인/회원가입/온보딩 진입)을 그대로 유지합니다.
 */

import { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { isAuthenticated } from '@/lib/auth';
import logoSrc from '@/assets/resource/Logo_wint_name-Photoroom.png';

/**
 * 프로그램 단위 용도: 인증 상태에 따라 서비스 진입 동선을 분기하는 시작 화면
 */
export default function StartPage() {
  const navigate = useNavigate();

  // 이미 로그인한 사용자는 시작 화면을 거치지 않고 바로 대시보드로 이동합니다.
  useEffect(() => {
    if (isAuthenticated()) {
      navigate('/dashboard', { replace: true });
    }
  }, [navigate]);

  return (
    <main className="flex min-h-screen w-full flex-col items-center justify-center bg-background p-8">
      <div className="flex flex-col items-center justify-center gap-6 text-center">
        {/* 로고 이미지 */}
        <div className="w-full flex justify-center items-center">
          <img 
            src={logoSrc} 
            alt="골든 웰니스" 
            className="w-auto h-auto max-w-[70vw] sm:max-w-[60vw] md:max-w-[50vw] lg:max-w-[450px] object-contain"
            style={{ 
              maxHeight: 'min(25vh, 200px)',
              width: 'auto',
              height: 'auto'
            }}
          />
        </div>
        <div className="w-full max-w-sm space-y-3">
          <Link to="/login" className="block">
            <Button size="xl" className="w-full">
              로그인
            </Button>
          </Link>
          <Link to="/signup" className="block">
            <Button size="xl" variant="outline" className="w-full">
              회원가입
            </Button>
          </Link>
          <Link to="/onboarding" className="block">
            <Button size="xl" variant="ghost" className="w-full">
              소셜 로그인으로 시작하기
            </Button>
          </Link>
          <Link to="/" className="block">
            <Button size="sm" variant="link" className="w-full">
              ← 랜딩으로 돌아가기
            </Button>
          </Link>
        </div>
      </div>
    </main>
  );
}


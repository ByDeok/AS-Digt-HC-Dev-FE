// src/app/page.tsx
/**
 * 스크립트 용도: 랜딩 페이지 (홈 화면)
 * 
 * 함수 호출 구조:
 * Home
 * ├── Logo (Icon Component)
 * ├── Link (Router Navigation)
 * └── Button (UI Component)
 */

import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Logo } from '@/components/icons/logo';

/**
 * 프로그램 단위 용도: 앱의 첫 진입 화면 렌더링. '시작하기' 버튼을 통해 온보딩으로 유도.
 */
export default function Home() {
  return (
    <main className="flex min-h-screen w-full flex-col items-center justify-center bg-background p-8">
      <div className="flex flex-col items-center justify-center text-center gap-6">
        <Logo className="h-24 w-24 text-primary" />
        <div className="space-y-2">
          <h1 className="text-4xl font-bold tracking-tight text-foreground font-headline">
            골든 웰니스
          </h1>
          <p className="text-lg text-muted-foreground">
            건강한 오늘, 활기찬 내일
          </p>
        </div>
        <Link to="/onboarding" className="w-full max-w-sm">
          <Button size="xl" className="w-full">
            시작하기
          </Button>
        </Link>
      </div>
    </main>
  );
}

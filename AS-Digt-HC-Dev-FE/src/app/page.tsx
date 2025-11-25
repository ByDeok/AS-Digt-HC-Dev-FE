import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Logo } from '@/components/icons/logo';

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
        <Link href="/onboarding" className="w-full max-w-sm">
          <Button size="xl" className="w-full">
            시작하기
          </Button>
        </Link>
      </div>
    </main>
  );
}

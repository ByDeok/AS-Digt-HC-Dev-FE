// src/app/page.tsx
/**
 * 스크립트 용도: Hook 랜딩 페이지 (전면 배치용)
 *
 * 목표:
 * - 방문자의 “사용 의향”을 먼저 끌어올리는 마케팅형 랜딩을 제공
 * - 기존 시작 화면(로그인/회원가입/소셜 시작)은 CTA를 통해서만 진입(/start)
 * - 로그인 사용자는 랜딩을 건너뛰고 곧바로 대시보드로 이동
 *
 * 설계 근거:
 * - docs/landing_page.md 의 Core Essentials(히어로/CTA/신뢰/가치 제안) 기반
 * - C 유형(결과 지향형) 강화:
 *   - Input-Output 다이어그램(“넣으면 나온다”)
 *   - 결과물(Outcome) 갤러리
 *   - Before & After / ROI(시간·노력 절감) 비교
 */

import { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  CheckCircle2,
  ArrowRight,
  AlertTriangle,
  FileText,
  ShieldCheck,
  Sparkles,
  TrendingDown,
  Activity,
  Lock,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from '@/components/ui/accordion';
import { ReviewSection } from '@/components/landing/ReviewSection';
import { ScrollAnimate } from '@/components/landing/ScrollAnimate';
import { isAuthenticated } from '@/lib/auth';
import videoSrc from '@/assets/resource/goden wellness.mp4';
import logoSrc from '@/assets/resource/Logo_wint_name-Photoroom.png';

type TrackProps = Record<string, string | number | boolean | null | undefined>;

/**
 * 프로그램 단위 용도: 랜딩 전환 이벤트 트래킹(준비)
 * - GTM(dataLayer) 또는 gtag(GA4)가 붙어있으면 그대로 이벤트가 흘러갑니다.
 * - 붙어있지 않으면 no-op(런타임 안전)입니다.
 */
function track(event: string, props: TrackProps = {}) {
  try {
    const w = window as unknown as {
      dataLayer?: unknown;
      gtag?: unknown;
    };

    if (typeof w.gtag === 'function') {
      (w.gtag as (...args: unknown[]) => void)('event', event, props);
    }
    if (Array.isArray(w.dataLayer)) {
      (w.dataLayer as Array<Record<string, unknown>>).push({ event, ...props });
    }
  } catch {
    // analytics는 “실패해도 앱이 깨지지 않도록” 설계합니다.
  }
}

/**
 * 프로그램 단위 용도: 랜딩 페이지 렌더링 및 리다이렉션 처리
 */
export default function LandingPage() {
  const navigate = useNavigate();

  // 이미 로그인한 사용자는 Hook 랜딩을 스킵하고 바로 서비스로 진입합니다.
  useEffect(() => {
    if (isAuthenticated()) {
      navigate('/dashboard', { replace: true });
    }
  }, [navigate]);

  // 스크롤 깊이 트래킹(25/50/75/100%)
  useEffect(() => {
    const thresholds = [25, 50, 75, 100] as const;
    const seen = new Set<number>();
    let ticking = false;

    const onScroll = () => {
      if (ticking) return;
      ticking = true;

      window.requestAnimationFrame(() => {
        const doc = document.documentElement;
        const maxScroll = Math.max(1, doc.scrollHeight - doc.clientHeight);
        const ratio = Math.min(1, Math.max(0, window.scrollY / maxScroll));
        const percent = Math.round(ratio * 100);

        thresholds.forEach((t) => {
          if (percent >= t && !seen.has(t)) {
            seen.add(t);
            track('landing_scroll_depth', { percent: t });
          }
        });

        ticking = false;
      });
    };

    window.addEventListener('scroll', onScroll, { passive: true });
    onScroll();
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  return (
    <main className="min-h-screen w-full bg-background selection:bg-primary/10">
      {/* Top bar: 네비게이션 메뉴와 CTA가 포함된 헤더 */}
      <header className="sticky top-0 z-50 w-full border-b bg-background/80 backdrop-blur-md supports-[backdrop-filter]:bg-background/60">
        <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">
          {/* Left: 로고 및 브랜드명 (전역 아이콘 위치 고려하여 좌측 여백 조정) */}
          <div className="flex items-center gap-2">
            {/* 전역 BrandCornerIcon과의 간격 조정을 위해 ml-10 정도를 유지하거나, 
                전역 아이콘 위치가 수정된다면 이 값을 줄일 수 있습니다. 
                사용자 요청("왼쪽으로 몰아넣고")을 반영하여 기존 ml-12보다 약간 줄인 ml-10을 적용합니다. */}
            <Link 
              to="/" 
              className="flex items-center gap-2 ml-10 sm:ml-12"
              onClick={(e) => {
                e.preventDefault();
                window.scrollTo({ top: 0, behavior: 'smooth' });
              }}
            >
               <span className="text-lg font-bold tracking-tight text-foreground/90 absolute left-[51px]">골든웰니스</span>
            </Link>
          </div>

          {/* Center: 주요 섹션 네비게이션 (데스크탑) */}
          <nav className="hidden md:flex items-center gap-4 lg:gap-8 absolute left-1/2 -translate-x-1/2 whitespace-nowrap">
            <a 
              href="#problem" 
              className="text-sm font-medium text-muted-foreground hover:text-primary transition-colors"
              onClick={() => track('landing_nav_click', { to: '#problem' })}
            >
              왜 필요한가요?
            </a>
            <a 
              href="#how" 
              className="text-sm font-medium text-muted-foreground hover:text-primary transition-colors"
              onClick={() => track('landing_nav_click', { to: '#how' })}
            >
              어떻게 동작하나요?
            </a>
            <a 
              href="#reviews" 
              className="text-sm font-medium text-muted-foreground hover:text-primary transition-colors"
              onClick={() => track('landing_nav_click', { to: '#reviews' })}
            >
              사용자 후기
            </a>
            <a 
              href="#trust" 
              className="text-sm font-medium text-muted-foreground hover:text-primary transition-colors"
              onClick={() => track('landing_nav_click', { to: '#trust' })}
            >
              신뢰와 안전
            </a>
            <a 
              href="#faq" 
              className="text-sm font-medium text-muted-foreground hover:text-primary transition-colors"
              onClick={() => track('landing_nav_click', { to: '#faq' })}
            >
              자주 묻는 질문
            </a>
          </nav>

          {/* Right: CTA 및 로그인 */}
          <div className="flex items-center gap-3">
            <Link to="/login" className="hidden sm:inline-flex">
              <Button
                variant="ghost"
                size="sm"
                className="text-muted-foreground hover:text-foreground"
                onClick={() => track('landing_cta_click', { location: 'header_login', to: '/login' })}
              >
                로그인
              </Button>
            </Link>
            <Link to="/start">
              <Button
                size="sm"
                className="font-medium shadow-sm"
                onClick={() => track('landing_cta_click', { location: 'header_primary', to: '/start' })}
              >
                무료로 시작하기 <ArrowRight className="ml-1 h-3.5 w-3.5" />
              </Button>
            </Link>
          </div>
        </div>
      </header>

      {/* Hero (추가, 최상단): “공유 = 감시” 프레임을 먼저 해소 */}
      {/* 고급스러운 그라데이션과 넉넉한 여백 적용 */}
      <section className="relative mx-auto w-full max-w-7xl px-4 pt-12 pb-8 sm:px-6 lg:px-8 lg:pt-20">
        <div className="group relative overflow-hidden rounded-3xl border p-8 shadow-sm transition-all hover:shadow-md md:p-12 lg:p-16">
          {/* 장식용 배경 효과 */}
          <div className="pointer-events-none absolute -right-20 -top-20 h-96 w-96 rounded-full bg-primary/5 blur-3xl transition-all duration-1000 group-hover:bg-primary/10" />
          <div className="pointer-events-none absolute -bottom-20 -left-20 h-64 w-64 rounded-full bg-blue-500/5 blur-3xl transition-all duration-1000 group-hover:bg-blue-500/10" />
          
          <div className="relative z-10 flex flex-col items-center text-center space-y-6 max-w-3xl mx-auto">
            <div className="inline-flex items-center gap-2 rounded-full border bg-background/50 backdrop-blur px-4 py-1.5 text-xs font-medium text-primary shadow-sm ring-1 ring-primary/10">
              <ShieldCheck className="h-3.5 w-3.5" />
              <span>기본값은 최소 공유 · 범위는 직접 선택</span>
            </div>
            
            <div className="flex flex-col items-center gap-4 w-full">
              {/* 로고 이미지 - 브라우저 창 크기에 맞게 반응형 조절, 중심 정렬, 꽉 차게 */}
              <div className="w-full flex justify-center items-center">
                <img 
                  src={logoSrc} 
                  alt="골든 웰니스" 
                  className="w-auto h-auto max-w-[85vw] sm:max-w-[75vw] md:max-w-[70vw] lg:max-w-[65vw] xl:max-w-[60vw] object-contain"
                  style={{ 
                    maxHeight: 'min(30vh, 300px)',
                    width: 'auto',
                    height: 'auto'
                  }}
                />
              </div>
              <h1 className="text-3xl font-extrabold tracking-tight text-foreground sm:text-5xl lg:text-6xl break-keep">
                “감시”가 아니라<br className="sm:hidden" /> <span className="text-primary">“안심”</span>을 위한 공유
              </h1>
              <p className="mx-auto max-w-2xl text-base sm:text-lg text-muted-foreground leading-relaxed break-keep">
                가족이 함께 이해하되, 필요한 정보만.<br className="hidden sm:inline" /> 
                동의와 권한을 기반으로 공유 범위를 정교하게 조절해<br className="hidden sm:inline" /> 
                사생활은 안전하게 지키고, 가족의 걱정은 덜어드립니다.
              </p>
            </div>

            <div className="flex flex-col w-full sm:w-auto sm:flex-row gap-3 pt-4">
              <Link to="/start" className="w-full sm:w-auto">
                <Button
                  size="lg"
                  className="w-full h-12 px-8 text-base shadow-lg shadow-primary/20 transition-all hover:scale-105 whitespace-nowrap"
                  onClick={() => track('landing_cta_click', { location: 'top_hero_primary', to: '/start' })}
                >
                  무료로 시작하기 <ArrowRight className="ml-2 h-4 w-4" />
                </Button>
              </Link>
              <a href="#trust" className="w-full sm:w-auto">
                <Button
                  size="lg"
                  variant="outline"
                  className="w-full h-12 px-8 text-base bg-background/50 backdrop-blur hover:bg-background/80 whitespace-nowrap"
                  onClick={() => track('landing_cta_click', { location: 'top_hero_secondary', to: '#trust' })}
                >
                  공유 원칙 살펴보기
                </Button>
              </a>
            </div>
          </div>
        </div>
      </section>

      {/* 영상 섹션: 상단 히어로(가치 제안)와 다음 히어로(결과 제시) 사이의 감정 전환용 브릿지 */}
      <section className="mx-auto w-full max-w-7xl px-4 pb-8 sm:px-6 lg:px-8">
        <ScrollAnimate animationType="fade-up" delay={100}>
          <div className="overflow-hidden rounded-3xl border bg-muted/10 shadow-sm">
          <div className="px-6 pt-6 sm:px-8 sm:pt-8">
            <p className="text-sm font-medium text-muted-foreground">소개 영상</p>
            <p className="mt-1 text-base font-semibold text-foreground break-keep">
              골든웰니스가 “안심 공유”를 만드는 흐름을 한 번에 확인하세요.
            </p>
          </div>

          <div className="p-4 sm:p-6">
            <div className="relative overflow-hidden rounded-2xl ring-1 ring-border/60 bg-black/5">
              <video
                className="w-full h-auto"
                autoPlay
                muted
                loop
                playsInline
                controls
              >
                <source src={videoSrc} type="video/mp4" />
                브라우저가 비디오 태그를 지원하지 않습니다.
              </video>
            </div>
          </div>
        </div>
        </ScrollAnimate>
      </section>

      {/* Hero: 3초 안에 '얻는 이득'을 전달 */}
      <section className="mx-auto w-full max-w-7xl px-4 py-16 sm:px-6 lg:px-8 lg:py-24">
        {/* 반응형: 중간 폭(태블릿)에서는 1열로 내려 잘림/오버플로우를 예방합니다. */}
        <div className="grid gap-12 lg:grid-cols-2 lg:items-center">
          <ScrollAnimate animationType="slide-right" delay={0}>
            <div className="space-y-8 min-w-0">
            <div className="space-y-4">
              <h2 className="text-2xl font-bold tracking-tight text-foreground sm:text-4xl lg:text-5xl break-keep">
                오늘 건강 상태,<br />
                <span className="text-primary relative inline-block">
                  한 장으로
                  <span className="absolute bottom-1 left-0 h-3 w-full bg-primary/20 -z-10 rounded-sm"></span>
                </span> 끝내세요.
              </h2>
              <p className="text-base sm:text-lg leading-relaxed text-muted-foreground break-keep">
                3분만 설정하면 <strong className="font-semibold text-foreground">한 장 요약 리포트</strong>와
                <strong className="font-semibold text-foreground"> 오늘의 미션 1~3개</strong>, 그리고
                <strong className="font-semibold text-foreground"> 가족 안심 공유</strong>까지 한 번에 정리됩니다.
              </p>
            </div>

            {/* 초단기 설득(배지 3개) - 디자인 개선 */}
            <div className="grid gap-4 sm:grid-cols-3">
              {[
                { title: '3분 온보딩', desc: '큰 버튼·단계형 흐름', icon: <Activity className="h-4 w-4 text-blue-500" /> },
                { title: 'A4 한 장 리포트', desc: 'PDF 저장·A4 인쇄', icon: <FileText className="h-4 w-4 text-green-500" /> },
                { title: '미션 1~3개', desc: '과부하 없이 실천', icon: <CheckCircle2 className="h-4 w-4 text-orange-500" /> }
              ].map((item, i) => (
                <ScrollAnimate key={i} animationType="fade-up" delay={100 + i * 100}>
                  <div className="flex flex-col gap-2 rounded-xl border bg-background/50 p-4 transition-all hover:border-primary/30 hover:shadow-sm">
                  <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-muted">
                    {item.icon}
                  </div>
                  <div>
                    <p className="font-semibold text-foreground">{item.title}</p>
                    <p className="text-sm text-muted-foreground">{item.desc}</p>
                  </div>
                  </div>
                </ScrollAnimate>
              ))}
            </div>

            {/* CTA: 상단/중단/하단 반복 노출 (전환 장치) */}
            <div className="flex flex-col gap-3 sm:flex-row">
              <Link to="/start" className="w-full sm:w-auto">
                <Button
                  size="xl"
                  className="w-full min-w-[200px] shadow-lg shadow-primary/10"
                  onClick={() => track('landing_cta_click', { location: 'hero_primary', to: '/start' })}
                >
                  3분만에 시작하기 <ArrowRight className="ml-2 h-5 w-5" />
                </Button>
              </Link>
              <a href="#how" className="w-full sm:w-auto">
                <Button
                  size="xl"
                  variant="ghost"
                  className="w-full"
                  onClick={() => track('landing_cta_click', { location: 'hero_secondary', to: '#how' })}
                >
                  넣으면 뭐가 나오나요?
                </Button>
              </a>
            </div>
          </div>
          </ScrollAnimate>

          {/* Hero media: C 유형은 "최종 결과물"을 먼저 보여줘 기대감을 만듭니다. */}
          {/* Glassmorphism 적용 */}
          <ScrollAnimate animationType="slide-left" delay={150}>
            <div className="relative min-w-0 w-full max-w-lg mx-auto lg:max-w-none">
            <div className="pointer-events-none absolute -inset-4 rounded-[2rem] bg-gradient-to-tr from-primary/20 via-primary/5 to-transparent blur-2xl opacity-70" />
            <Card className="relative overflow-hidden border-0 shadow-2xl shadow-primary/10 ring-1 ring-border/50 bg-background/80 backdrop-blur-sm">
              <div className="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-primary/40 via-primary to-primary/40" />
              <CardHeader className="border-b bg-muted/30 pb-4">
                <CardTitle className="flex items-center gap-2 text-base font-medium text-muted-foreground">
                  <Sparkles className="h-4 w-4 text-primary" />
                  결과물 미리보기 (샘플)
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-6 pt-6">
                <div className="grid gap-4 sm:grid-cols-2">
                  <div className="group rounded-xl border bg-background p-4 transition-all hover:border-primary/50 hover:shadow-md">
                    <div className="mb-3 flex h-8 w-8 items-center justify-center rounded-full bg-primary/10 text-primary">
                      <FileText className="h-4 w-4" />
                    </div>
                    <p className="text-xs font-medium text-muted-foreground">A4 한 장 요약 리포트</p>
                    <p className="mt-2 text-sm font-semibold text-foreground leading-snug">
                      “오늘은 활동량이 줄었어요.<br/>10분 산책부터 시작해요.”
                    </p>
                    <div className="mt-3 flex flex-wrap gap-2 text-[10px] text-muted-foreground">
                      <span className="inline-flex items-center gap-1 rounded-full bg-muted px-2 py-1">
                        PDF/인쇄
                      </span>
                      <span className="inline-flex items-center gap-1 rounded-full bg-muted px-2 py-1">
                        안심 공유
                      </span>
                    </div>
                  </div>
                  <div className="group rounded-xl border bg-background p-4 transition-all hover:border-primary/50 hover:shadow-md">
                    <div className="mb-3 flex h-8 w-8 items-center justify-center rounded-full bg-orange-500/10 text-orange-500">
                      <CheckCircle2 className="h-4 w-4" />
                    </div>
                    <p className="text-xs font-medium text-muted-foreground">오늘의 미션 (1~3개)</p>
                    <p className="mt-2 text-sm font-semibold text-foreground leading-snug">
                      “수분 섭취 체크하기”<br/>“가벼운 스트레칭 5분”
                    </p>
                    <div className="mt-3 flex items-center gap-1.5 text-[11px] text-primary">
                      <Sparkles className="h-3 w-3" />
                      완료 시 작은 축하
                    </div>
                  </div>
                </div>
                
                <div className="rounded-xl border bg-muted/30 p-4">
                  <div className="flex items-start gap-3">
                    <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-blue-500/10 text-blue-500">
                      <Lock className="h-4 w-4" />
                    </div>
                    <div>
                      <p className="text-sm font-semibold text-foreground">안심 공유 모드</p>
                      <p className="text-xs text-muted-foreground mt-1">
                        “자녀분에게는 요약 점수와 응급 알림만 공유됩니다.”
                      </p>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
            </div>
          </ScrollAnimate>
        </div>
      </section>

      {/* 문제 → 해결 구조: 숨은 비용을 정확히 찌르기 */}
      <section id="problem" className="mx-auto w-full max-w-7xl px-4 py-16 sm:px-6 lg:px-8 scroll-mt-20">
        <ScrollAnimate animationType="fade-up" delay={0}>
          <div className="text-center space-y-4 mb-12">
          <h2 className="text-2xl font-bold tracking-tight text-foreground sm:text-3xl lg:text-4xl break-keep">기록은 있는데, 결론이 없습니다</h2>
          <p className="text-base sm:text-lg text-muted-foreground max-w-2xl mx-auto break-keep">
            더 많은 데이터가 아니라, 더 빠른 결론이 필요합니다.<br/>
            복잡함은 우리가 처리하고, 당신은 결과만 확인하세요.
          </p>
        </div>
        </ScrollAnimate>

        <div className="grid gap-6 md:grid-cols-2 lg:gap-8">
          <ScrollAnimate animationType="fade-up" delay={100}>
            <Card className="bg-muted/30 border-0 shadow-none">
              <CardHeader>
                <CardTitle className="text-lg">시니어의 어려움</CardTitle>
              </CardHeader>
              <CardContent className="space-y-3 text-muted-foreground">
                <p className="flex items-start gap-2">
                  <span className="text-red-400 mt-1">✕</span>
                  앱이 복잡하고 용어가 어려워서 금방 포기하게 됩니다.
                </p>
                <p className="flex items-start gap-2">
                  <span className="text-red-400 mt-1">✕</span>
                  수치는 쌓이는데, &ldquo;오늘 당장 뭘 해야 하는지&rdquo; 모릅니다.
                </p>
              </CardContent>
            </Card>
          </ScrollAnimate>
          <ScrollAnimate animationType="fade-up" delay={200}>
            <Card className="bg-muted/30 border-0 shadow-none">
            <CardHeader>
              <CardTitle className="text-lg">가족의 어려움</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3 text-muted-foreground">
              <p className="flex items-start gap-2">
                <span className="text-red-400 mt-1">✕</span>
                매번 전화로 물어보고 설명 듣는 과정이 서로 피로합니다.
              </p>
              <p className="flex items-start gap-2">
                <span className="text-red-400 mt-1">✕</span>
                과잉정보는 불안만 키우고, 정작 중요한 변화는 놓칩니다.
              </p>
            </CardContent>
            </Card>
          </ScrollAnimate>
        </div>

        <ScrollAnimate animationType="scale" delay={300}>
          <div className="mt-8 rounded-2xl bg-primary/5 p-8 text-center border border-primary/10">
          <p className="text-lg font-medium text-foreground">
            골든 웰니스는 <span className="text-primary font-bold">&ldquo;데이터 나열&rdquo;</span>이 아닌{' '}
            <span className="text-primary font-bold">&ldquo;명쾌한 결론&rdquo;</span>을 제공합니다.
          </p>
          </div>
        </ScrollAnimate>
      </section>

      {/* C 유형 핵심: Input-Output 다이어그램 (복잡한 과정은 숨기고 "넣으면 나온다") */}
      <section id="how" className="mx-auto w-full max-w-7xl px-4 py-16 sm:px-6 lg:px-8 bg-muted/10 scroll-mt-20">
        <div className="space-y-12">
          <ScrollAnimate animationType="fade-up" delay={0}>
            <div className="text-center space-y-4">
            <div className="inline-flex items-center rounded-full border px-3 py-1 text-xs font-medium bg-background">
              Process
            </div>
            <h2 className="text-2xl font-bold tracking-tight text-foreground sm:text-3xl lg:text-4xl break-keep">입력하면, 이렇게 나옵니다</h2>
            <p className="text-base sm:text-lg text-muted-foreground max-w-2xl mx-auto break-keep">
              내부의 복잡한 로직은 몰라도 됩니다.<br/>
              &ldquo;입력 → 엔진 → 결과&rdquo;의 단순한 흐름만 기억하세요.
            </p>
            </div>
          </ScrollAnimate>

          <div className="relative grid gap-8 md:grid-cols-3 md:items-stretch">
            {/* 연결선 (데스크탑) */}
            <div className="hidden md:block absolute top-1/2 left-0 w-full h-0.5 bg-gradient-to-r from-transparent via-border to-transparent -translate-y-1/2 z-0" />

            <ScrollAnimate animationType="fade-up" delay={100}>
              <Card className="relative z-10 bg-background transition-all hover:-translate-y-1 hover:shadow-lg">
              <CardHeader>
                <CardTitle className="flex items-center gap-3 text-lg">
                  <div className="flex h-10 w-10 items-center justify-center rounded-full bg-muted text-muted-foreground">1</div>
                  Input (입력)
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-3 text-sm text-muted-foreground">
                <p>• 기본 프로필 · 건강 목표</p>
                <p>• (선택) 삼성헬스 등 기기 연동</p>
                <p>• (선택) 가족 구성원 연결</p>
              </CardContent>
              </Card>
            </ScrollAnimate>

            <ScrollAnimate animationType="scale" delay={200}>
              <Card className="relative z-10 bg-background border-primary/50 shadow-lg shadow-primary/5 transition-all hover:-translate-y-1 hover:shadow-xl">
              <div className="absolute -top-3 left-1/2 -translate-x-1/2 bg-primary text-primary-foreground text-[10px] font-bold px-2 py-0.5 rounded-full">
                AUTO
              </div>
              <CardHeader>
                <CardTitle className="flex items-center gap-3 text-lg text-primary">
                  <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary/10 text-primary">
                    <Sparkles className="h-5 w-5" />
                  </div>
                  ⚡ AI 엔진
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-3 text-sm text-muted-foreground">
                <p>• 6가지 영역(H.E.A.L.T.H) 분석</p>
                <p>• 이상 징후 & 패턴 감지</p>
                <p>• 개인화된 우선순위 도출</p>
              </CardContent>
              </Card>
            </ScrollAnimate>

            <ScrollAnimate animationType="fade-up" delay={300}>
              <Card className="relative z-10 bg-background transition-all hover:-translate-y-1 hover:shadow-lg">
              <CardHeader>
                <CardTitle className="flex items-center gap-3 text-lg">
                  <div className="flex h-10 w-10 items-center justify-center rounded-full bg-muted text-muted-foreground">3</div>
                  Output (결과)
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-3 text-sm text-muted-foreground">
                <p className="font-medium text-foreground">• 한 장 요약 리포트 (PDF)</p>
                <p>• 오늘의 미션 카드 (1~3개)</p>
                <p>• 안심 공유 알림</p>
              </CardContent>
              </Card>
            </ScrollAnimate>
          </div>
        </div>

        {/* CTA 반복 배치 (중단) */}
        <ScrollAnimate animationType="fade-up" delay={400}>
          <div className="mt-16 text-center">
          <Link to="/start">
            <Button
              size="lg"
              className="h-14 px-8 text-lg shadow-lg shadow-primary/20 rounded-full"
              onClick={() => track('landing_cta_click', { location: 'mid', to: '/start' })}
            >
              내 결과물 미리보기 <ArrowRight className="ml-2 h-5 w-5" />
            </Button>
          </Link>
          <p className="mt-4 text-sm text-muted-foreground">
            3분이면 설정이 끝납니다. 카드 등록 없이 무료로 시작하세요.
          </p>
          </div>
        </ScrollAnimate>
      </section>

      {/* 사용자 리뷰 섹션 */}
      <ReviewSection />

      {/* ROI / Before & After */}
      <section className="mx-auto w-full max-w-7xl px-4 py-16 sm:px-6 lg:px-8">
        <ScrollAnimate animationType="fade-up" delay={0}>
          <Card className="overflow-hidden border-0 bg-gradient-to-br from-muted/50 via-background to-muted/50 shadow-sm">
            <CardHeader className="pb-8 text-center">
            <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-primary/10 text-primary">
              <TrendingDown className="h-6 w-6" />
            </div>
            <CardTitle className="text-xl sm:text-2xl font-bold break-keep">시간과 걱정을 얼마나 줄일 수 있나요?</CardTitle>
            <p className="text-muted-foreground mt-2 text-sm sm:text-base break-keep">
              “건강 관리를 잘 하고 싶지만 복잡해서 못 하는” 문제를,<br/>
              결과물 중심으로 단순화하여 효율을 높입니다.
            </p>
            </CardHeader>
            <CardContent className="px-6 pb-12 md:px-12">
              <div className="grid gap-8 md:grid-cols-2">
                <ScrollAnimate animationType="slide-right" delay={100}>
                  <div className="relative rounded-2xl border bg-background p-6 shadow-sm">
                <div className="absolute top-0 right-0 rounded-bl-xl bg-muted px-3 py-1 text-xs font-medium text-muted-foreground">BEFORE</div>
                <h3 className="font-bold text-foreground mb-4 text-lg">기존의 방식</h3>
                <ul className="space-y-4">
                  <li className="flex gap-3 text-muted-foreground text-sm sm:text-base break-keep">
                    <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-muted text-xs">1</span>
                    <span>앱 3~4개를 켜서 데이터를 확인하고 해석해야 함</span>
                  </li>
                  <li className="flex gap-3 text-muted-foreground text-sm sm:text-base break-keep">
                    <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-muted text-xs">2</span>
                    <span>가족에게 전화해서 &ldquo;오늘 어때?&rdquo; 묻고 답답해함</span>
                  </li>
                  <li className="flex gap-3 text-muted-foreground text-sm sm:text-base break-keep">
                    <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-muted text-xs">3</span>
                    <span>&ldquo;그래서 오늘 뭘 조심해야 하지?&rdquo; 결론 없이 끝남</span>
                  </li>
                  </ul>
                  </div>
                </ScrollAnimate>
              
                <ScrollAnimate animationType="slide-left" delay={200}>
                  <div className="relative rounded-2xl border-2 border-primary/20 bg-primary/5 p-6 shadow-sm">
                <div className="absolute top-0 right-0 rounded-bl-xl bg-primary text-primary-foreground px-3 py-1 text-xs font-bold">AFTER</div>
                <h3 className="font-bold text-foreground mb-4 text-lg">골든 웰니스</h3>
                <ul className="space-y-4">
                  <li className="flex gap-3 text-foreground text-sm sm:text-base break-keep">
                    <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-primary text-primary-foreground text-xs">1</span>
                    <span><span className="font-bold">한 장의 리포트</span>로 모든 흐름을 30초 만에 파악</span>
                  </li>
                  <li className="flex gap-3 text-foreground text-sm sm:text-base break-keep">
                    <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-primary text-primary-foreground text-xs">2</span>
                    <span><span className="font-bold">공유 카드</span>를 보며 가족이 같은 정보를 이해함</span>
                  </li>
                  <li className="flex gap-3 text-foreground text-sm sm:text-base break-keep">
                    <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-primary text-primary-foreground text-xs">3</span>
                    <span><span className="font-bold">오늘의 미션 1개</span>만 실천하면 관리 끝</span>
                  </li>
                  </ul>
                  </div>
                </ScrollAnimate>
              </div>
            </CardContent>
          </Card>
        </ScrollAnimate>
      </section>

      {/* 신뢰/안전 — 의심(개인정보/민감정보)을 먼저 처리 */}
      <section id="trust" className="mx-auto w-full max-w-7xl px-4 py-16 sm:px-6 lg:px-8 scroll-mt-20">
        <div className="grid gap-12 lg:grid-cols-3">
          <ScrollAnimate animationType="fade-up" delay={0}>
            <div className="lg:col-span-1 space-y-4">
            <h2 className="text-2xl font-bold tracking-tight text-foreground sm:text-3xl lg:text-4xl break-keep">
              신뢰와 안전을<br/>최우선으로 합니다
            </h2>
            <p className="text-muted-foreground text-sm sm:text-base break-keep">
              건강정보는 가장 민감한 자산입니다.<br/>
              그래서 우리는 “최소 수집”과 “철저한 권한 관리”를 원칙으로 삼습니다.
            </p>
            <div className="pt-4">
              <Button variant="outline" className="gap-2">
                <ShieldCheck className="h-4 w-4" /> 보안 백서 보기 (준비중)
              </Button>
            </div>
            </div>
          </ScrollAnimate>
          
          <div className="lg:col-span-2 grid gap-6 sm:grid-cols-2">
            {[
              { title: '최소 수집 원칙', desc: '서비스 제공에 꼭 필요한 정보만 요청합니다. 불필요한 정보는 묻지 않습니다.' },
              { title: '선택적 연동', desc: '기기/서비스 연동은 100% 사용자의 선택입니다. 연동 없이도 기본 기능을 사용할 수 있습니다.' },
              { title: '권한 기반 공유', desc: '가족이라도 모든 정보를 볼 수 없습니다. 사용자가 허용한 항목만 공유됩니다.' },
              { title: '투명한 의료 고지', desc: '본 서비스는 의료 진단/치료가 아닙니다. AI의 제안은 참고용 가이드임을 명확히 합니다.' }
            ].map((item, i) => (
              <ScrollAnimate key={i} animationType="fade-up" delay={100 + i * 100}>
                <Card className="border bg-background transition-colors hover:border-primary/40">
                <CardHeader>
                  <CardTitle className="text-base font-semibold">{item.title}</CardTitle>
                </CardHeader>
                <CardContent>
                  <p className="text-sm text-muted-foreground">{item.desc}</p>
                </CardContent>
                </Card>
              </ScrollAnimate>
            ))}
          </div>
        </div>
      </section>

      {/* FAQ: 불안 해소 */}
      <section id="faq" className="mx-auto w-full max-w-4xl px-4 py-16 sm:px-6 lg:px-8 scroll-mt-20">
        <ScrollAnimate animationType="fade-up" delay={0}>
          <div className="text-center mb-10">
            <h2 className="text-2xl font-bold">자주 묻는 질문</h2>
          </div>
        </ScrollAnimate>
        <Accordion type="single" collapsible className="w-full space-y-2">
          {[
            { q: '스마트폰을 잘 못해도 쓸 수 있나요?', a: '네. 큰 글씨와 단순한 버튼으로 설계했습니다. 초기 설정이 어려우면 가족이 대신 설정해줄 수도 있습니다.' },
            { q: '가족에게는 어디까지 보이나요?', a: '기본 설정은 ‘안심 점수’와 ‘응급 알림’만 공유됩니다. 상세 데이터 공유 여부는 본인이 직접 켜고 끌 수 있습니다.' },
            { q: '혈압/혈당 기기는 꼭 연동해야 하나요?', a: '아니요, 필수가 아닙니다. 가지고 계신 기기가 없으면 수기 입력을 하거나, 입력 없이 기본 생활 습관 관리만 하셔도 됩니다.' },
            { q: '이용 요금은 얼마인가요?', a: '기본적인 리포트 생성과 미션 기능은 무료입니다. 추후 전문가 상담 등 일부 프리미엄 기능만 유료로 제공될 예정입니다.' }
          ].map((item, i) => (
            <AccordionItem key={i} value={`item-${i}`} className="border rounded-lg px-4 bg-background">
              <AccordionTrigger className="text-left font-medium hover:no-underline hover:text-primary break-keep text-sm sm:text-base">
                {item.q}
              </AccordionTrigger>
              <AccordionContent className="text-muted-foreground break-keep text-sm sm:text-base">
                {item.a}
              </AccordionContent>
            </AccordionItem>
          ))}
        </Accordion>
      </section>

      {/* Bottom CTA: 마지막 결심 포인트 */}
      <section className="border-t bg-muted/30">
        <ScrollAnimate animationType="fade-up" delay={0}>
          <div className="mx-auto w-full max-w-7xl px-4 py-20 sm:px-6 lg:px-8 text-center">
          <h2 className="text-2xl font-bold tracking-tight text-foreground sm:text-3xl lg:text-4xl mb-6 break-keep">
            오늘부터, “설명” 대신 “요약”을 공유하세요.
          </h2>
          <p className="text-base sm:text-lg text-muted-foreground max-w-2xl mx-auto mb-10 break-keep">
            3분만 투자하면 매일 아침 따뜻한 안심을 선물할 수 있습니다.<br/>
            복잡한 건강 관리, 이제 골든 웰니스가 쉽게 정리해드립니다.
          </p>
          
          <div className="flex flex-col gap-4 sm:flex-row justify-center">
            <Link to="/start">
              <Button
                size="xl"
                className="h-14 px-10 text-lg shadow-xl shadow-primary/20 rounded-full transition-transform hover:scale-105"
                onClick={() => track('landing_cta_click', { location: 'bottom_primary', to: '/start' })}
              >
                지금 결과물 받아보기 <ArrowRight className="ml-2 h-5 w-5" />
              </Button>
            </Link>
            <Link to="/login">
              <Button size="xl" variant="outline" className="h-14 px-10 text-lg rounded-full bg-background/50 backdrop-blur">
                기존 계정으로 로그인
              </Button>
            </Link>
          </div>

          <div className="mt-8 inline-flex items-center gap-2 rounded-full border bg-background/50 px-4 py-2 text-xs text-muted-foreground backdrop-blur">
            <AlertTriangle className="h-3.5 w-3.5 text-primary" />
            <p>의료적 진단이 아닌 건강 관리 보조 서비스입니다.            </p>
          </div>
          </div>
        </ScrollAnimate>
      </section>

      {/* Footer */}
      <footer className="border-t bg-background">
        <div className="mx-auto w-full max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
          <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-4">
            <div className="space-y-4">
              <span className="text-lg font-bold text-foreground">골든웰니스</span>
              <p className="text-sm text-muted-foreground">
                시니어와 가족을 잇는<br/>
                가장 쉬운 건강 소통 플랫폼
              </p>
            </div>
            <div>
              <h3 className="font-semibold mb-4">서비스</h3>
              <ul className="space-y-2 text-sm text-muted-foreground">
                <li><Link to="#" className="hover:text-foreground">기능 소개</Link></li>
                <li><Link to="#" className="hover:text-foreground">요금 안내</Link></li>
                <li><Link to="#" className="hover:text-foreground">자주 묻는 질문</Link></li>
              </ul>
            </div>
            <div>
              <h3 className="font-semibold mb-4">회사</h3>
              <ul className="space-y-2 text-sm text-muted-foreground">
                <li><Link to="#" className="hover:text-foreground">팀 소개</Link></li>
                <li><Link to="#" className="hover:text-foreground">채용</Link></li>
                <li><Link to="#" className="hover:text-foreground">문의하기</Link></li>
              </ul>
            </div>
            <div>
              <h3 className="font-semibold mb-4">약관 및 정책</h3>
              <ul className="space-y-2 text-sm text-muted-foreground">
                <li><Link to="#" className="hover:text-foreground">서비스 이용약관</Link></li>
                <li><Link to="#" className="hover:text-foreground">개인정보 처리방침</Link></li>
              </ul>
            </div>
          </div>
          <div className="mt-12 border-t pt-8 text-center text-xs text-muted-foreground">
            <p>&copy; {new Date().getFullYear()} Golden Wellness. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </main>
  );
}

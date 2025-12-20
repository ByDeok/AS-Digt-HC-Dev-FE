// src/App.tsx
/**
 * 스크립트 용도: 메인 애플리케이션 컴포넌트 및 라우팅 설정
 *
 * 함수 호출 구조:
 * App
 * ├── BrowserRouter (Router Provider)
 * ├── Suspense (Async Loading Boundary)
 * │   └── Routes (Route Matcher)
 * │       ├── Route (/) -> LandingPage (Hook 랜딩)
 * │       ├── Route (/start) -> StartPage (기존 시작 화면)
 * │       ├── Route (/onboarding/*) -> OnboardingLayout -> [Pages]
 * │       └── Route (/dashboard, etc) -> MainLayout -> [Pages]
 * └── Toaster (Global Toast Provider)
 */

import { Suspense, lazy } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from '@/components/ui/toaster';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { queryClient } from '@/lib/react-query';
import { RequireAuth } from '@/components/common/RequireAuth';
import { BrandCornerIcon } from '@/components/common/BrandCornerIcon';

// Layouts
import MainLayout from './app/(main)/layout';
import OnboardingLayout from './app/onboarding/layout';

// Pages (Lazy Loading)
const LandingPage = lazy(() => import('./app/page'));
const StartPage = lazy(() => import('./app/start/page'));
const LoginPage = lazy(() => import('./app/auth/login/page'));
const SignupPage = lazy(() => import('./app/auth/signup/page'));
const DashboardPage = lazy(() => import('./app/(main)/dashboard/page'));
const ReportPage = lazy(() => import('./app/(main)/report/page'));
const FamilyPage = lazy(() => import('./app/(main)/family/page'));
const OnboardingPage = lazy(() => import('./app/onboarding/page'));
const DevicePage = lazy(() => import('./app/onboarding/device/page'));
const ProfilePage = lazy(() => import('./app/onboarding/profile/page'));
const CompletePage = lazy(() => import('./app/onboarding/complete/page'));

/**
 * 프로그램 단위 용도: 전체 앱의 라우팅 구조 정의 및 공통 레이아웃 적용
 * @returns JSX.Element - 라우터가 적용된 앱 컴포넌트
 */
function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        {/* 브랜드 대표 아이콘: 라우트와 무관하게 모든 페이지에 고정 노출 */}
        <BrandCornerIcon />
        <Suspense fallback={<LoadingSpinner />}>
          <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route path="/start" element={<StartPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/signup" element={<SignupPage />} />

          <Route
            path="/onboarding"
            element={
              <RequireAuth>
                <OnboardingLayout>
                  <OnboardingPage />
                </OnboardingLayout>
              </RequireAuth>
            }
          />
          <Route
            path="/onboarding/device"
            element={
              <RequireAuth>
                <OnboardingLayout>
                  <DevicePage />
                </OnboardingLayout>
              </RequireAuth>
            }
          />
          <Route
            path="/onboarding/profile"
            element={
              <RequireAuth>
                <OnboardingLayout>
                  <ProfilePage />
                </OnboardingLayout>
              </RequireAuth>
            }
          />
          <Route
            path="/onboarding/complete"
            element={
              <RequireAuth>
                <OnboardingLayout>
                  <CompletePage />
                </OnboardingLayout>
              </RequireAuth>
            }
          />

          <Route
            path="/dashboard"
            element={
              <RequireAuth>
                <MainLayout>
                  <DashboardPage />
                </MainLayout>
              </RequireAuth>
            }
          />
          <Route
            path="/report"
            element={
              <RequireAuth>
                <MainLayout>
                  <ReportPage />
                </MainLayout>
              </RequireAuth>
            }
          />
          <Route
            path="/family"
            element={
              <RequireAuth>
                <MainLayout>
                  <FamilyPage />
                </MainLayout>
              </RequireAuth>
            }
          />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Suspense>
      <Toaster />
    </BrowserRouter>
    <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  );
}

export default App;

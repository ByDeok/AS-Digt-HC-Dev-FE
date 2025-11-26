// src/App.tsx
/**
 * 스크립트 용도: 메인 애플리케이션 컴포넌트 및 라우팅 설정
 *
 * 함수 호출 구조:
 * App
 * ├── BrowserRouter (Router Provider)
 * ├── Suspense (Async Loading Boundary)
 * │   └── Routes (Route Matcher)
 * │       ├── Route (/) -> LandingPage
 * │       ├── Route (/onboarding/*) -> OnboardingLayout -> [Pages]
 * │       └── Route (/dashboard, etc) -> MainLayout -> [Pages]
 * └── Toaster (Global Toast Provider)
 */

import { Suspense, lazy } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from '@/components/ui/toaster';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';

// Layouts
import MainLayout from './app/(main)/layout';
import OnboardingLayout from './app/onboarding/layout';

// Pages (Lazy Loading)
const LandingPage = lazy(() => import('./app/page'));
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
    <BrowserRouter>
      <Suspense fallback={<LoadingSpinner />}>
        <Routes>
          <Route path="/" element={<LandingPage />} />

          <Route
            path="/onboarding"
            element={
              <OnboardingLayout>
                <OnboardingPage />
              </OnboardingLayout>
            }
          />
          <Route
            path="/onboarding/device"
            element={
              <OnboardingLayout>
                <DevicePage />
              </OnboardingLayout>
            }
          />
          <Route
            path="/onboarding/profile"
            element={
              <OnboardingLayout>
                <ProfilePage />
              </OnboardingLayout>
            }
          />
          <Route
            path="/onboarding/complete"
            element={
              <OnboardingLayout>
                <CompletePage />
              </OnboardingLayout>
            }
          />

          <Route
            path="/dashboard"
            element={
              <MainLayout>
                <DashboardPage />
              </MainLayout>
            }
          />
          <Route
            path="/report"
            element={
              <MainLayout>
                <ReportPage />
              </MainLayout>
            }
          />
          <Route
            path="/family"
            element={
              <MainLayout>
                <FamilyPage />
              </MainLayout>
            }
          />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Suspense>
      <Toaster />
    </BrowserRouter>
  );
}

export default App;

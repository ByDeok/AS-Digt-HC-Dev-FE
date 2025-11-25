// src/app/onboarding/layout.tsx
/**
 * 스크립트 용도: 온보딩 프로세스 공통 레이아웃
 * 
 * 함수 호출 구조:
 * OnboardingLayout
 * └── main (Center Wrapper)
 *     └── children (Page Content)
 */

/**
 * 프로그램 단위 용도: 온보딩 페이지들을 중앙 정렬된 카드 형태로 감싸는 래퍼
 */
export default function OnboardingLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="flex min-h-screen w-full items-center justify-center bg-background p-4">
      <main className="w-full max-w-md">{children}</main>
    </div>
  );
}

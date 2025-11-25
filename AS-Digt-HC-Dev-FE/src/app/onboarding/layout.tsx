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

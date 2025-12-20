import { useMemo, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { useToast } from '@/hooks/use-toast';
import { login } from '@/services/authService';
import { setAuthSession } from '@/lib/auth';

const schema = z.object({
  email: z.string().email('이메일 형식이 올바르지 않습니다.'),
  password: z.string().min(1, '비밀번호를 입력해주세요.'),
});

type FormValues = z.infer<typeof schema>;

export default function LoginPage() {
  const { toast } = useToast();
  const navigate = useNavigate();
  const location = useLocation();
  const [submitting, setSubmitting] = useState(false);

  const fromPath = useMemo(() => {
    const state = location.state as { from?: { pathname?: string } } | null;
    return state?.from?.pathname || '/dashboard';
  }, [location.state]);

  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { email: '', password: '' },
    mode: 'onSubmit',
  });

  const onSubmit = async (values: FormValues) => {
    setSubmitting(true);
    try {
      const token = await login(values);
      setAuthSession(token);
      toast({ title: '로그인 성공', description: `${token.user.name}님, 환영합니다.` });
      navigate(fromPath, { replace: true });
    } catch (e) {
      const message = e instanceof Error ? e.message : '로그인에 실패했습니다.';
      toast({ title: '로그인 실패', description: message, variant: 'destructive' });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main className="flex min-h-screen w-full items-center justify-center bg-background p-6">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <CardTitle className="text-2xl font-headline">로그인</CardTitle>
          <CardDescription>기존 계정으로 로그인하세요.</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
              <FormField
                control={form.control}
                name="email"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>이메일</FormLabel>
                    <FormControl>
                      <Input placeholder="name@example.com" autoComplete="email" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>비밀번호</FormLabel>
                    <FormControl>
                      <Input type="password" autoComplete="current-password" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <Button type="submit" className="w-full" size="xl" disabled={submitting}>
                {submitting ? '로그인 중...' : '로그인'}
              </Button>
            </form>
          </Form>

          <div className="text-center text-sm text-muted-foreground">
            계정이 없으신가요?{' '}
            <Link className="text-primary underline underline-offset-4" to="/signup">
              회원가입
            </Link>
          </div>
        </CardContent>
      </Card>
    </main>
  );
}


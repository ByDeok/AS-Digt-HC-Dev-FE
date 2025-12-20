import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { Checkbox } from '@/components/ui/checkbox';
import { useToast } from '@/hooks/use-toast';
import { signup } from '@/services/authService';

const passwordRule =
  /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,20}$/;

const schema = z
  .object({
    email: z.string().email('이메일 형식이 올바르지 않습니다.'),
    name: z.string().min(2, '이름은 2자 이상 입력해주세요.').max(50, '이름은 50자 이하로 입력해주세요.'),
    password: z
      .string()
      .regex(passwordRule, '비밀번호는 8~20자, 영문/숫자/특수문자를 포함해야 합니다.'),
    passwordConfirm: z.string().min(1, '비밀번호 확인을 입력해주세요.'),
    termsService: z.boolean().refine((v) => v === true, '서비스 이용약관 동의가 필요합니다.'),
    privacyPolicy: z.boolean().refine((v) => v === true, '개인정보 처리방침 동의가 필요합니다.'),
    marketingConsent: z.boolean().optional().default(false),
  })
  .refine((val) => val.password === val.passwordConfirm, {
    message: '비밀번호가 일치하지 않습니다.',
    path: ['passwordConfirm'],
  });

type FormValues = z.infer<typeof schema>;

export default function SignupPage() {
  const { toast } = useToast();
  const navigate = useNavigate();
  const [submitting, setSubmitting] = useState(false);

  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      email: '',
      name: '',
      password: '',
      passwordConfirm: '',
      termsService: false,
      privacyPolicy: false,
      marketingConsent: false,
    },
  });

  const onSubmit = async (values: FormValues) => {
    setSubmitting(true);
    try {
      await signup({
        email: values.email,
        password: values.password,
        name: values.name,
        role: 'SENIOR',
        agreements: {
          termsService: values.termsService,
          privacyPolicy: values.privacyPolicy,
          marketingConsent: !!values.marketingConsent,
        },
      });

      toast({
        title: '회원가입 완료',
        description: '이제 로그인할 수 있습니다.',
      });
      navigate('/login', { replace: true });
    } catch (e) {
      const message = e instanceof Error ? e.message : '회원가입에 실패했습니다.';
      toast({ title: '회원가입 실패', description: message, variant: 'destructive' });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main className="flex min-h-screen w-full items-center justify-center bg-background p-6">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <CardTitle className="text-2xl font-headline">회원가입</CardTitle>
          <CardDescription>이메일 계정을 생성합니다.</CardDescription>
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
                name="name"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>이름</FormLabel>
                    <FormControl>
                      <Input placeholder="홍길동" autoComplete="name" {...field} />
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
                      <Input type="password" autoComplete="new-password" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="passwordConfirm"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>비밀번호 확인</FormLabel>
                    <FormControl>
                      <Input type="password" autoComplete="new-password" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <div className="space-y-3 rounded-md border p-3">
                <FormField
                  control={form.control}
                  name="termsService"
                  render={({ field }) => (
                    <FormItem>
                      <div className="flex items-center gap-2">
                        <Checkbox checked={field.value} onCheckedChange={(v) => field.onChange(!!v)} />
                        <FormLabel className="cursor-pointer">서비스 이용약관 동의(필수)</FormLabel>
                      </div>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="privacyPolicy"
                  render={({ field }) => (
                    <FormItem>
                      <div className="flex items-center gap-2">
                        <Checkbox checked={field.value} onCheckedChange={(v) => field.onChange(!!v)} />
                        <FormLabel className="cursor-pointer">개인정보 처리방침 동의(필수)</FormLabel>
                      </div>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="marketingConsent"
                  render={({ field }) => (
                    <FormItem>
                      <div className="flex items-center gap-2">
                        <Checkbox checked={field.value} onCheckedChange={(v) => field.onChange(!!v)} />
                        <FormLabel className="cursor-pointer">마케팅 수신 동의(선택)</FormLabel>
                      </div>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>

              <Button type="submit" className="w-full" size="xl" disabled={submitting}>
                {submitting ? '가입 중...' : '회원가입'}
              </Button>
            </form>
          </Form>

          <div className="text-center text-sm text-muted-foreground">
            이미 계정이 있으신가요?{' '}
            <Link className="text-primary underline underline-offset-4" to="/login">
              로그인
            </Link>
          </div>
        </CardContent>
      </Card>
    </main>
  );
}


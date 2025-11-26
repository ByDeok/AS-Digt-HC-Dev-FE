import { FallbackProps } from 'react-error-boundary';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { AlertCircle } from 'lucide-react';

export function ErrorFallback({ error, resetErrorBoundary }: FallbackProps) {
  return (
    <div className="flex min-h-screen items-center justify-center bg-background p-4">
      <Card className="w-full max-w-md shadow-lg">
        <CardHeader className="space-y-1 text-center">
          <div className="flex justify-center mb-4">
            <div className="rounded-full bg-destructive/10 p-3">
              <AlertCircle className="h-10 w-10 text-destructive" />
            </div>
          </div>
          <CardTitle className="text-2xl font-bold">문제가 발생했습니다</CardTitle>
        </CardHeader>
        <CardContent className="text-center space-y-2">
          <p className="text-muted-foreground">
            예기치 못한 오류가 발생하여 페이지를 표시할 수 없습니다.
          </p>
          <div className="rounded-md bg-muted p-4 mt-4 text-left overflow-auto max-h-40 text-xs font-mono text-muted-foreground">
            {error.message}
          </div>
        </CardContent>
        <CardFooter className="flex justify-center pb-6">
          <Button onClick={resetErrorBoundary} size="lg" className="w-full">
            다시 시도하기
          </Button>
        </CardFooter>
      </Card>
    </div>
  );
}


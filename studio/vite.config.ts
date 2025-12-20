/// <reference types="vitest" />
import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  // Vite config에서 .env.* 를 안전하게 읽기 위해 loadEnv 사용
  const env = loadEnv(mode, process.cwd(), '');

  const beTarget = env.VITE_BE_URL || process.env.VITE_BE_URL || 'http://localhost:8081';

  return {
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 9002,
    /**
     * 개발 환경 통합: FE에서 /api로 호출하면 Vite가 BE로 프록시
     * - CORS 없이 브라우저에서 API 호출 확인 가능
     * - 예: http://localhost:9002/api/health  ->  http://localhost:8080/api/health
     *
     * 이 프로젝트는 로컬에서 BE를 8081로 띄워 쓰는 경우가 많아 기본 target을 8081로 둔다.
     * (예: `.\gradlew bootRun --args='--spring.profiles.active=local --server.port=8081'`)
     *
     * 또한 환경에 따라 BE 포트를 바꾸는 경우
     * 환경에 따라 BE 포트를 바꾸는 경우 VITE_BE_URL로 오버라이드 할 수 있게 한다.
     */
    proxy: {
      '/api': {
        target: beTarget,
        changeOrigin: true,
        secure: false,
      },
    },
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
  },
  };
});

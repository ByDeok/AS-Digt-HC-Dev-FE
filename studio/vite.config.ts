/// <reference types="vitest" />
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

// https://vitejs.dev/config/
export default defineConfig({
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
     * 로컬에서 BE를 8081로 띄운 경우에도 동작하도록 기본 target을 8081로 둠.
     */
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
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
});

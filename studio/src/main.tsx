// src/main.tsx
/**
 * 스크립트 용도: React 애플리케이션의 진입점(Entry Point)
 * 기능:
 * - DOM에 React Root 생성
 * - App 컴포넌트 렌더링
 * - 전역 CSS 임포트
 */

import React from 'react';
import ReactDOM from 'react-dom/client';
import { ErrorBoundary } from 'react-error-boundary';
import App from './App';
import { ErrorFallback } from './components/error/ErrorFallback';
import './app/globals.css';

/**
 * 프로그램 단위 용도: React DOM 렌더링 및 StrictMode 적용
 */
ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ErrorBoundary
      FallbackComponent={ErrorFallback}
      onReset={() => {
        // 에러 발생 시 리셋 로직 (예: 홈으로 이동)
        window.location.href = '/';
      }}
      onError={(error, info) => {
        console.error('Global Error:', error);
        console.error('Component Stack:', info.componentStack);
      }}
    >
      <App />
    </ErrorBoundary>
  </React.StrictMode>,
);

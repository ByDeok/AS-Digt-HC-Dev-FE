// src/main.tsx
/**
 * 스크립트 용도: React 애플리케이션의 진입점(Entry Point)
 * 기능:
 * - DOM에 React Root 생성
 * - App 컴포넌트 렌더링
 * - 전역 CSS 임포트
 */

import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import './app/globals.css'

/**
 * 프로그램 단위 용도: React DOM 렌더링 및 StrictMode 적용
 */
ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)


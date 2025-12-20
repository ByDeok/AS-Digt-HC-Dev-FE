/// <reference types="vite/client" />

/**
 * 스크립트 용도: Vite 정적 자원 import 타입 선언
 *
 * 배경:
 * - 랜딩에서 mp4를 `import videoSrc from '...mp4'` 형태로 사용하기 위해 필요합니다.
 * - Vite 런타임은 문제 없이 처리하지만, TS 타입 체커가 모듈을 인식하지 못하면 에러가 납니다.
 */
declare module '*.mp4' {
  const src: string;
  export default src;
}
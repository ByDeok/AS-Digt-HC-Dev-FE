import { genkit } from 'genkit';
import { googleAI } from '@genkit-ai/google-genai';

/**
 * Google AI (Genkit) 클라이언트 초기화
 * 
 * ⚠️ 환경변수 설정 필요:
 *   - GOOGLE_GENAI_API_KEY: Google AI Studio에서 발급받은 API 키
 *   - API 키는 서버 사이드에서만 사용 (클라이언트 노출 금지)
 * 
 * @see https://aistudio.google.com/app/apikey - API 키 발급
 * @see studio/docs/ENV_MANAGEMENT_GUIDE.md - 환경변수 관리 가이드
 */
export const ai = genkit({
  plugins: [
    googleAI({
      // Google AI API 키는 환경변수에서 자동으로 읽어옴 (GOOGLE_GENAI_API_KEY)
      // 명시적으로 설정하려면: apiKey: process.env.GOOGLE_GENAI_API_KEY
    }),
  ],
  // 기본 모델 설정 (환경변수로 오버라이드 가능)
  model: process.env.GOOGLE_AI_MODEL || 'googleai/gemini-2.5-flash',
});

import { ai } from './genkit';
import { AIService, AIRequest, AIResponse } from './types';

export const genkitService: AIService = {
  async generateContent(request: AIRequest): Promise<AIResponse> {
    try {
      const result = await ai.generate({
        prompt: request.prompt,
        config: {
          temperature: request.temperature,
        },
      });

      return {
        text: result.text,
        usage: {
          promptTokens: result.usage?.inputTokens || 0,
          completionTokens: result.usage?.outputTokens || 0,
          totalTokens: result.usage?.totalTokens || 0,
        },
      };
    } catch (error) {
      // 콘솔 로그 대신(ESLint no-console), 필요 시 상위에서 에러를 관측할 수 있게 이벤트만 남깁니다.
      if (typeof window !== 'undefined') {
        window.dispatchEvent(
          new CustomEvent('genkit_error', {
            detail: { kind: 'generateContent', error },
          }),
        );
      }
      throw error;
    }
  },

  async generateStream(request: AIRequest, onChunk: (chunk: string) => void): Promise<void> {
    try {
      const { stream } = await ai.generateStream({
        prompt: request.prompt,
        config: {
          temperature: request.temperature,
        },
      });

      for await (const chunk of stream) {
        if (chunk.text) {
          onChunk(chunk.text);
        }
      }
    } catch (error) {
      // 콘솔 로그 대신(ESLint no-console), 필요 시 상위에서 에러를 관측할 수 있게 이벤트만 남깁니다.
      if (typeof window !== 'undefined') {
        window.dispatchEvent(
          new CustomEvent('genkit_error', {
            detail: { kind: 'generateStream', error },
          }),
        );
      }
      throw error;
    }
  },
};


import { AIRequest, AIResponse, AIService } from './types';

export const mockAIService: AIService = {
  async generateContent(request: AIRequest): Promise<AIResponse> {
    // 1초 지연 시뮬레이션
    await new Promise((resolve) => setTimeout(resolve, 1000));

    return {
      text: `[Mock AI Response] 입력하신 "${request.prompt}"에 대한 답변입니다.\n이것은 테스트용 가짜 응답입니다. 실제 AI 모델을 연결하면 더 의미 있는 답변을 받을 수 있습니다.`,
      usage: {
        promptTokens: 10,
        completionTokens: 20,
        totalTokens: 30,
      },
    };
  },

  async generateStream(request: AIRequest, onChunk: (chunk: string) => void): Promise<void> {
    const fullText = `[Mock Stream] "${request.prompt}"에 대한 스트리밍 응답 시뮬레이션입니다... 한 글자씩... 타이핑... 효과를... 확인해보세요.`;
    const chars = fullText.split('');

    for (const char of chars) {
      await new Promise((resolve) => setTimeout(resolve, 50)); // 50ms 딜레이
      onChunk(char);
    }
  },
};


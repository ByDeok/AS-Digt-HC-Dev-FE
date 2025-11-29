export interface AIRequest {
  prompt: string;
  model?: string;
  temperature?: number;
}

export interface AIResponse {
  text: string;
  usage?: {
    promptTokens: number;
    completionTokens: number;
    totalTokens: number;
  };
}

export interface AIService {
  generateContent(request: AIRequest): Promise<AIResponse>;
  generateStream?(request: AIRequest, onChunk: (chunk: string) => void): Promise<void>;
}


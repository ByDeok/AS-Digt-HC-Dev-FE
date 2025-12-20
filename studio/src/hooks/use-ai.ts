import { useState, useCallback } from 'react';
import { mockAIService } from '../ai/mock-service';
import { genkitService } from '../ai/genkit-service';
import { AIRequest, AIResponse } from '../ai/types';

const USE_MOCK = import.meta.env.VITE_USE_MOCK_AI === 'true';

export function useAI() {
  const [data, setData] = useState<AIResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);
  const [streamData, setStreamData] = useState<string>('');

  const getService = () => {
    return USE_MOCK ? mockAIService : genkitService;
  };

  const generate = useCallback(async (prompt: string) => {
    setIsLoading(true);
    setError(null);
    setData(null);
    setStreamData('');

    try {
      const request: AIRequest = { prompt };
      const service = getService();

      const response = await service.generateContent(request);
      
      setData(response);
      return response;
    } catch (err) {
      const errorObj = err instanceof Error ? err : new Error('Unknown AI Error');
      setError(errorObj);
      throw errorObj;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const generateStream = useCallback(async (prompt: string) => {
    setIsLoading(true);
    setError(null);
    setStreamData('');
    
    try {
       const request: AIRequest = { prompt };
       const service = getService();

       if (service.generateStream) {
         await service.generateStream(request, (chunk) => {
            setStreamData((prev) => prev + chunk);
         });
       } else {
         // 스트리밍 미지원 시 폴백
         const response = await service.generateContent(request);
         setStreamData(response.text);
       }
    } catch (err) {
      const errorObj = err instanceof Error ? err : new Error('Unknown AI Stream Error');
      setError(errorObj);
      throw errorObj;
    } finally {
      setIsLoading(false);
    }
  }, []);

  return {
    generate,
    generateStream,
    data,
    streamData,
    isLoading,
    error,
  };
}


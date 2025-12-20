export type ApiResponse<T> = {
  success: boolean;
  message: string;
  data?: T;
  timestamp: string;
};

/**
 * 백엔드의 표준 ApiResponse 언랩 유틸
 * - success=false 또는 data 누락 시 예외로 처리해 화면/훅 레이어에서 일관된 에러 처리가 가능하게 함
 */
export function unwrapApiResponse<T>(res: { data: ApiResponse<T> }, fallbackMessage: string): T {
  const body = res?.data;
  if (!body?.success) {
    throw new Error(body?.message || fallbackMessage);
  }
  if (body.data === undefined || body.data === null) {
    throw new Error(body?.message || fallbackMessage);
  }
  return body.data;
}



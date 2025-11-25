// src/lib/utils.ts
/**
 * 스크립트 용도: 공통 유틸리티 함수 모음
 */

import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"

/**
 * 프로그램 단위 용도: Tailwind CSS 클래스 병합 유틸리티
 * 기능:
 * - clsx를 사용하여 조건부 클래스 적용
 * - tailwind-merge를 사용하여 중복 클래스 충돌 해결
 */
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

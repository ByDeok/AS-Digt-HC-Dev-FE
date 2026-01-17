/**
 * 스크롤 트리거 애니메이션 훅
 * 요소가 뷰포트에 진입할 때 애니메이션을 트리거합니다.
 */

import { useEffect, useRef, useState } from 'react';

interface UseScrollAnimationOptions {
  threshold?: number; // 요소가 얼마나 보여야 트리거할지 (0-1)
  rootMargin?: string; // 루트 마진 (예: "0px 0px -100px 0px")
  triggerOnce?: boolean; // 한 번만 트리거할지 여부
  delay?: number; // 애니메이션 지연 시간 (ms)
}

/**
 * 요소가 뷰포트에 진입했는지 추적하는 훅
 * @param options Intersection Observer 옵션
 * @returns ref와 isVisible 상태
 */
export function useScrollAnimation(options: UseScrollAnimationOptions = {}) {
  const {
    threshold = 0.1,
    rootMargin = '0px 0px -50px 0px',
    triggerOnce = true,
    delay = 0,
  } = options;

  const [isVisible, setIsVisible] = useState(false);
  const elementRef = useRef<HTMLElement>(null);
  const hasTriggeredRef = useRef(false);

  useEffect(() => {
    const element = elementRef.current;
    if (!element) return;

    // 이미 트리거되었고 triggerOnce가 true면 더 이상 관찰하지 않음
    if (hasTriggeredRef.current && triggerOnce) return;

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            if (delay > 0) {
              setTimeout(() => {
                setIsVisible(true);
                hasTriggeredRef.current = true;
              }, delay);
            } else {
              setIsVisible(true);
              hasTriggeredRef.current = true;
            }
          } else if (!triggerOnce) {
            setIsVisible(false);
          }
        });
      },
      {
        threshold,
        rootMargin,
      }
    );

    observer.observe(element);

    return () => {
      observer.unobserve(element);
    };
  }, [threshold, rootMargin, triggerOnce, delay]);

  return { ref: elementRef, isVisible };
}

/**
 * 여러 요소에 순차적으로(stagger) 애니메이션을 적용하는 훅
 * @param count 애니메이션을 적용할 요소의 개수
 * @param staggerDelay 각 요소 간 지연 시간 (ms)
 * @param options Intersection Observer 옵션
 * @returns ref와 각 요소의 visible 상태 배열
 */
export function useStaggerAnimation(
  count: number,
  staggerDelay: number = 100,
  options: Omit<UseScrollAnimationOptions, 'delay'> = {}
) {
  const {
    threshold = 0.1,
    rootMargin = '0px 0px -50px 0px',
    triggerOnce = true,
  } = options;

  const [visibleStates, setVisibleStates] = useState<boolean[]>(
    new Array(count).fill(false)
  );
  const containerRef = useRef<HTMLElement>(null);
  const hasTriggeredRef = useRef(false);

  useEffect(() => {
    const container = containerRef.current;
    if (!container) return;

    if (hasTriggeredRef.current && triggerOnce) return;

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting && !hasTriggeredRef.current) {
            hasTriggeredRef.current = true;

            // 순차적으로 상태를 true로 변경
            const newStates = new Array(count).fill(false);
            setVisibleStates(newStates);

            for (let i = 0; i < count; i++) {
              setTimeout(() => {
                setVisibleStates((prev) => {
                  const next = [...prev];
                  next[i] = true;
                  return next;
                });
              }, i * staggerDelay);
            }
          } else if (!triggerOnce && !entry.isIntersecting) {
            setVisibleStates(new Array(count).fill(false));
            hasTriggeredRef.current = false;
          }
        });
      },
      {
        threshold,
        rootMargin,
      }
    );

    observer.observe(container);

    return () => {
      observer.unobserve(container);
    };
  }, [count, staggerDelay, threshold, rootMargin, triggerOnce]);

  return { ref: containerRef, visibleStates };
}


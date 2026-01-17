/**
 * 스크롤 트리거 애니메이션 래퍼 컴포넌트
 * 요소가 뷰포트에 진입할 때 애니메이션을 적용합니다.
 */

import { useEffect, useRef, useState, ReactNode } from 'react';
import { cn } from '@/lib/utils';

interface ScrollAnimateProps {
  children: ReactNode;
  className?: string;
  animationType?: 'fade-up' | 'fade-in' | 'slide-left' | 'slide-right' | 'scale';
  delay?: number; // ms
  threshold?: number;
  rootMargin?: string;
}

export function ScrollAnimate({
  children,
  className,
  animationType = 'fade-up',
  delay = 0,
  threshold = 0.1,
  rootMargin = '0px 0px -50px 0px',
}: ScrollAnimateProps) {
  const [isVisible, setIsVisible] = useState(false);
  const elementRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const element = elementRef.current;
    if (!element) return;

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            if (delay > 0) {
              setTimeout(() => setIsVisible(true), delay);
            } else {
              setIsVisible(true);
            }
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
  }, [delay, threshold, rootMargin]);

  const animationClassMap = {
    'fade-up': 'scroll-animate',
    'fade-in': 'scroll-animate-scale',
    'slide-left': 'scroll-animate-left',
    'slide-right': 'scroll-animate-right',
    'scale': 'scroll-animate-scale',
  };

  return (
    <div
      ref={elementRef}
      className={cn(
        animationClassMap[animationType],
        isVisible && 'active',
        className
      )}
    >
      {children}
    </div>
  );
}


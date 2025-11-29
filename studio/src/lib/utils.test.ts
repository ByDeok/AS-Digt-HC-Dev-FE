import { describe, it, expect } from 'vitest';
import { cn } from './utils';

describe('cn utility', () => {
  it('merges class names correctly', () => {
    const result = cn('p-4', 'bg-red-500', 'p-2');
    // tailwind-merge should resolve p-4 and p-2 to p-2 (last wins)
    // Note: The exact output order depends on tailwind-merge implementation,
    // but typically it preserves non-conflicting and resolves conflicting.
    // 'bg-red-500' is non-conflicting. 'p-4' vs 'p-2' -> 'p-2'.
    expect(result).toContain('bg-red-500');
    expect(result).toContain('p-2');
    expect(result).not.toContain('p-4');
  });

  it('handles conditional classes', () => {
    const result = cn('text-red-500', true && 'text-lg', false && 'text-sm');
    expect(result).toBe('text-red-500 text-lg');
  });
});




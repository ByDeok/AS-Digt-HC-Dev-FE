# 코드 주석 및 문서화 가이드

본 가이드는 **AS-Digt-HC-Dev-FE** 프로젝트의 코드 가독성과 유지보수성을 높이기 위한 문서화 표준을 정의합니다.

---

## 1. 기본 원칙 (General Principles)

1.  **코드가 문서다 (Self-documenting Code)**: 변수명, 함수명을 명확하게 짓는 것이 최우선입니다. 주석은 "무엇(What)"이 아닌 **"왜(Why)"**를 설명해야 합니다.
2.  **TSDoc 준수**: TypeScript를 사용하므로, IDE(VS Code)에서 인텔리센스 지원을 받을 수 있도록 JSDoc/TSDoc 표준을 따릅니다.
3.  **한글 작성**: 팀 내 소통을 위해 주석은 **한글**로 작성하는 것을 원칙으로 합니다.

---

## 2. 컴포넌트 주석 가이드

React 컴포넌트 상단에는 해당 컴포넌트의 역할, 주요 Props, 사용 예시를 명시합니다.

### 템플릿

```typescript
/**
 * [컴포넌트 이름]
 * 
 * [컴포넌트의 역할 및 설명]
 * 
 * @component
 * @example
 * return (
 *   <ComponentName prop1="value" />
 * )
 */
```

### 실전 예시 (`components/common/ListItem.tsx`)

```typescript
import React from 'react';

interface ListItemProps {
  title: string;
  description?: string;
  icon?: React.ReactNode;
}

/**
 * ListItem
 * 
 * 리스트 형태의 UI에서 각 항목을 표시하는 공통 컴포넌트입니다.
 * 좌측 아이콘, 제목, 설명 텍스트로 구성됩니다.
 * 
 * @component
 * @example
 * <ListItem 
 *   title="건강 검진 결과" 
 *   description="2024-03-20 업데이트"
 *   icon={<FileTextIcon />} 
 * />
 */
export const ListItem: React.FC<ListItemProps> = ({ title, description, icon }) => {
  // ...
};
```

---

## 3. 함수 및 Hooks 주석 가이드

복잡한 비즈니스 로직이나 커스텀 훅에는 매개변수, 반환값, 주의사항을 명시합니다.

### 템플릿

```typescript
/**
 * [함수 설명]
 *
 * @param {Type} paramName - [파라미터 설명]
 * @returns {Type} [반환값 설명]
 * @throws {ErrorType} [에러 발생 조건]
 */
```

### 실전 예시 (`lib/utils.ts`)

```typescript
import { type ClassValue, clsx } from "clsx"
import { twMerge } from "tailwind-merge"

/**
 * Tailwind CSS 클래스를 조건부로 병합하는 유틸리티 함수
 * clsx로 조건부 클래스를 처리하고, twMerge로 중복되는 Tailwind 클래스를 정리합니다.
 * 
 * @param inputs - 병합할 클래스 목록
 * @returns 병합된 클래스 문자열
 */
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}
```

---

## 4. AI 모듈 문서화 가이드 (`src/ai`)

AI 관련 코드는 불확실성이 높고 로직이 복잡하므로, 프롬프트 의도와 입출력 예시를 상세히 적습니다.

### 예시 (`src/ai/genkit.ts`)

```typescript
/**
 * 건강 데이터 분석 AI 플로우
 * 
 * 사용자의 활동 데이터를 기반으로 맞춤형 건강 조언을 생성합니다.
 * 
 * @model gemini-pro
 * @input {HealthData} - 사용자의 일일 걸음 수, 수면 시간 등
 * @output {Advice} - AI가 생성한 3줄 요약 조언
 * 
 * @note 프롬프트 수정 시 'system_instruction' 부분만 변경하세요.
 */
export const analyzeHealthFlow = defineFlow(
  {
    name: 'analyzeHealth',
    inputSchema: z.object({ steps: z.number(), sleep: z.number() }),
    outputSchema: z.string(),
  },
  async (input) => {
    // ...
  }
);
```

---

## 5. 파일 구조별 주석 정책

| 경로 | 주석 필수 여부 | 내용 |
|-----|---------------|------|
| `src/components/ui/*` | 선택 | Shadcn/ui 자동 생성 파일은 주석 생략 가능 (커스텀 시 이유 명시) |
| `src/components/common/*` | **필수** | 재사용 컴포넌트이므로 사용법(Example) 필수 |
| `src/hooks/*` | **필수** | 훅의 목적과 의존성(Dependency) 설명 |
| `src/app/**/*` | 권장 | 페이지의 주요 비즈니스 로직 설명 |
| `src/lib/*` | **필수** | 유틸리티 함수의 입력/출력 명확화 |

---

## 6. TODO 및 FIXME

코드 내에 개선이 필요하거나 임시로 작성된 부분은 반드시 태그를 남깁니다.

- `// TODO: [설명]`: 추후 구현해야 할 기능
- `// FIXME: [설명]`: 버그가 있거나 수정이 시급한 로직
- `// NOTE: [설명]`: 특별한 주의가 필요한 로직 설명

```typescript
// TODO: 실제 API 연동 후 mockData 제거 필요
const data = mockData.users;

// FIXME: 모바일에서 차트 툴팁이 짤리는 현상 수정 필요
<LineChart ... />
```


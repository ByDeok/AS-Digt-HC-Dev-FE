# 코드 주석 및 문서화 가이드

> **StartupPlan** - 코드 주석 작성 규칙 및 AI 프롬프팅 최적화  
> 생성일: 2025년 11월 21일

---

## 📋 목차

1. [개요](#개요)
2. [주석 작성 규칙](#주석-작성-규칙)
3. [파일별 주석 구조](#파일별-주석-구조)
4. [함수 호출 구조 문서화](#함수-호출-구조-문서화)
5. [AI 프롬프팅 최적화](#ai-프롬프팅-최적화)

---

## 개요

이 프로젝트는 AI 기반 개발을 염두에 두고 체계적인 주석 시스템을 구축했습니다.

### 주석 작성의 목적

1. **맥락 유지**: 파일 전체를 읽지 않고도 내용 파악 가능
2. **트러블슈팅 효율화**: 함수 호출 구조를 통한 원인 파악 용이
3. **개발자 지원**: 신규 개발자 온보딩 및 유지보수 편의성
4. **AI 프롬프팅**: AI가 코드를 이해하고 작업하기 쉽도록 최적화

---

## 주석 작성 규칙

### 1. 파일 최상단 주석

모든 TypeScript/TSX 파일은 다음 구조의 주석으로 시작합니다:

```typescript
/**
 * 파일명: ComponentName.tsx
 * 
 * 파일 용도:
 * 컴포넌트/모듈의 역할 간단 설명
 * - 주요 기능 1
 * - 주요 기능 2
 * 
 * 호출 구조:
 * 이 파일이 호출하는 주요 함수/컴포넌트
 *   ├─> 의존성 1
 *   └─> 의존성 2
 * 
 * 데이터 흐름:
 * 데이터가 어떻게 흐르는지 설명
 * 
 * 사용하는 Store/Hook:
 * - useXXXStore: 설명
 */
```

### 2. 함수/컴포넌트 주석

```typescript
/**
 * 함수/컴포넌트 이름
 * 
 * 역할:
 * - 이 함수가 하는 일
 * 
 * 주요 기능:
 * 1. 기능 1
 * 2. 기능 2
 * 
 * @param {Type} paramName - 파라미터 설명
 * @returns {Type} 반환값 설명
 */
```

### 3. 인라인 주석

```typescript
// 짧은 설명은 한 줄 주석 사용
const result = calculate(); // 계산 실행

// 복잡한 로직은 상세 설명
/**
 * 손익분기점 계산
 * - 고정비를 공헌이익으로 나눔
 * - 결과를 올림하여 정수로 반환
 */
const breakEvenPoint = Math.ceil(fixedCosts / contributionMargin);
```

---

## 파일별 주석 구조

### 컴포넌트 파일 (.tsx)

```typescript
/**
 * 파일명: QuestionForm.tsx
 * 
 * 파일 용도:
 * 마법사 단계별 질문 폼 렌더링 컴포넌트
 * 
 * 호출 구조:
 * QuestionForm (이 컴포넌트)
 *   ├─> useWizardStore
 *   │   ├─> updateStepData() - 답변 저장
 *   │   └─> getStepData() - 데이터 조회
 *   └─> useAutoSave() - 자동 저장
 */

/**
 * QuestionForm 컴포넌트
 * 
 * @param {Question[]} questions - 질문 목록
 * @param {number} stepId - 현재 단계 ID
 * @returns {JSX.Element} 질문 폼
 */
export const QuestionForm: React.FC<Props> = ({ questions, stepId }) => {
  // ...
}
```

### Store 파일 (.ts)

```typescript
/**
 * 파일명: useWizardStore.ts
 * 
 * 파일 용도:
 * 마법사 진행 상태 및 데이터 관리를 위한 Zustand Store
 * 
 * 호출 구조:
 * useWizardStore
 *   ├─> WizardStep 페이지
 *   ├─> QuestionForm 컴포넌트
 *   └─> Layout 컴포넌트
 * 
 * 영속화:
 * - localStorage 키: 'wizard-storage'
 */

export const useWizardStore = create<WizardState>()(
  persist((set, get) => ({
    /**
     * 현재 단계 설정
     * @param {number} step - 설정할 단계 번호
     */
    setCurrentStep: (step: number) => {
      set({ currentStep: step });
    },
  }))
);
```

### Hook 파일 (.ts)

```typescript
/**
 * 파일명: useAutoSave.ts
 * 
 * 파일 용도:
 * 자동 저장 기능을 제공하는 Custom Hook
 * 
 * 호출 구조:
 * useAutoSave
 *   └─> useProjectStore.setSaveStatus()
 *       └─> SaveIndicator에서 표시
 * 
 * 최적화:
 * - Debounce로 연속 입력 시 저장 요청 방지
 */

/**
 * useAutoSave Hook
 * 
 * @param {any} data - 저장할 데이터
 * @param {number} delay - Debounce 지연 시간 (기본 1000ms)
 */
export const useAutoSave = (data: any, delay: number = 1000) => {
  // ...
}
```

---

## 함수 호출 구조 문서화

### 트리 형태 표기법

```
ComponentA (진입점)
  ├─> useStoreA
  │   ├─> action1() - 데이터 업데이트
  │   └─> action2() - 상태 변경
  │
  ├─> ComponentB (하위 컴포넌트)
  │   └─> useStoreA - 상태 구독
  │
  └─> utilFunction() - 유틸리티 호출
```

### 실제 예시: WizardStep

```
WizardStep.tsx
  ├─> useWizardStore
  │   ├─> setCurrentStep() - URL 동기화
  │   ├─> isStepCompleted() - 진행 가능 여부 확인
  │   ├─> goToNextStep() - 다음 단계 이동
  │   └─> goToPreviousStep() - 이전 단계 이동
  │
  └─> 단계별 컴포넌트 렌더링
      ├─> QuestionForm (1-3단계)
      │   ├─> useWizardStore.updateStepData()
      │   └─> useAutoSave()
      │
      ├─> FinancialSimulation (4단계)
      │   └─> useFinancialStore
      │
      └─> PMFSurvey (5단계)
          └─> usePMFStore
```

### 데이터 흐름 표기

```
사용자 입력
  ↓
QuestionForm.handleChange()
  ↓
useWizardStore.updateStepData()
  ↓
localStorage 자동 저장 (persist middleware)
  ↓
useAutoSave hook 트리거
  ↓
useProjectStore.setSaveStatus('saving')
  ↓
SaveIndicator 컴포넌트 업데이트
```

---

## AI 프롬프팅 최적화

### 파일 구조가 AI에게 주는 이점

1. **맥락 파악 용이**
   - 파일명과 용도를 상단에 명시
   - 전체 파일을 읽지 않아도 역할 이해 가능

2. **의존성 추적**
   - 호출 구조를 통해 연관 파일 파악
   - 변경 영향 범위 예측 가능

3. **트러블슈팅 효율화**
   - 에러 발생 시 호출 체인 추적
   - 원인 파악에 필요한 컴퓨팅 파워 감소

### AI 프롬프팅 예시

#### ❌ 비효율적 프롬프트
```
"QuestionForm 컴포넌트의 버그를 수정해줘"
```

#### ✅ 효율적 프롬프트
```
"QuestionForm 컴포넌트에서 답변이 저장되지 않는 문제를 수정해줘.

관련 파일:
- QuestionForm.tsx: 사용자 입력 처리
- useWizardStore.ts: updateStepData() 함수 확인
- useAutoSave.ts: 자동 저장 로직 확인

호출 구조:
QuestionForm → useWizardStore.updateStepData() → localStorage

예상 원인:
1. updateStepData 파라미터 오류
2. Debounce 타이밍 문제
3. localStorage 권한 문제"
```

### 주석 기반 컨텍스트 제공

AI에게 파일을 제공할 때:

```typescript
// 파일 최상단 주석을 먼저 읽고
/**
 * 파일명: useWizardStore.ts
 * 호출 구조: ...
 * 데이터 흐름: ...
 */

// 관련 함수만 선택적으로 제공
export const useWizardStore = create<WizardState>()(
  persist((set, get) => ({
    updateStepData: (stepId, questionId, value) => {
      // 이 함수만 집중적으로 분석
    }
  }))
);
```

---

## 주석 작성 체크리스트

### 필수 항목

- [ ] 파일명을 최상단 주석에 명시
- [ ] 파일 용도를 간단명료하게 설명
- [ ] 주요 함수 호출 구조를 트리 형태로 표기
- [ ] 데이터 흐름을 명확히 설명
- [ ] 각 함수에 역할 및 파라미터 설명 추가

### 권장 항목

- [ ] 사용하는 Store/Hook 목록
- [ ] 주요 의존성 라이브러리
- [ ] 알려진 제약사항이나 주의사항
- [ ] 최적화 기법 설명
- [ ] 관련 문서 링크

### 피해야 할 것

- ❌ 코드를 그대로 반복하는 주석
- ❌ 너무 장황한 설명
- ❌ 오래된/부정확한 주석 방치
- ❌ 주석 없이 복잡한 로직 작성

---

## 실전 예시

### Before (주석 없음)

```typescript
export const useAutoSave = (data: any, delay: number = 1000) => {
  const { setSaveStatus } = useProjectStore();
  const previousDataRef = useRef<string>();

  useEffect(() => {
    const currentData = JSON.stringify(data);
    if (previousDataRef.current === currentData) return;
    
    previousDataRef.current = currentData;
    setSaveStatus('saving');
    
    const debouncedSave = debounce(() => {
      setTimeout(() => {
        setSaveStatus('saved');
        setTimeout(() => setSaveStatus('idle'), 2000);
      }, 500);
    }, delay);

    debouncedSave();
  }, [data, delay, setSaveStatus]);
};
```

### After (주석 추가)

```typescript
/**
 * 파일명: useAutoSave.ts
 * 
 * 파일 용도:
 * 자동 저장 기능을 제공하는 Custom Hook
 * - 데이터 변경 감지 및 자동 저장
 * - Debounce를 통한 저장 요청 최적화
 * 
 * 호출 구조:
 * useAutoSave
 *   └─> useProjectStore.setSaveStatus()
 *       └─> SaveIndicator 컴포넌트에서 표시
 * 
 * 동작 과정:
 * 1. data 변경 감지 (useEffect)
 * 2. 변경되지 않았으면 스킵
 * 3. 'saving' 상태 설정
 * 4. delay 후 저장 시뮬레이션
 * 5. 'saved' 상태 설정
 * 6. 2초 후 'idle' 복귀
 */

/**
 * useAutoSave Hook
 * 
 * 역할:
 * - 데이터 변경 시 자동으로 저장
 * - 불필요한 저장 요청 방지 (Debounce)
 * 
 * @param {any} data - 저장할 데이터
 * @param {number} delay - Debounce 지연 시간 (기본 1000ms)
 */
export const useAutoSave = (data: any, delay: number = 1000) => {
  const { setSaveStatus } = useProjectStore();
  const previousDataRef = useRef<string>();

  useEffect(() => {
    // 현재 데이터를 JSON 문자열로 변환하여 비교
    const currentData = JSON.stringify(data);
    
    // 데이터가 변경되지 않았으면 저장하지 않음
    if (previousDataRef.current === currentData) return;
    
    // 이전 데이터 업데이트
    previousDataRef.current = currentData;
    
    // 저장 중 상태로 변경
    setSaveStatus('saving');
    
    // Debounce를 적용한 저장 시뮬레이션
    const debouncedSave = debounce(() => {
      // 실제로는 API 호출
      setTimeout(() => {
        setSaveStatus('saved');
        
        // 2초 후 idle 상태로 복귀
        setTimeout(() => setSaveStatus('idle'), 2000);
      }, 500);
    }, delay);

    debouncedSave();
  }, [data, delay, setSaveStatus]);
};
```

---

## 결론

체계적인 주석 시스템은:

1. ✅ **개발 효율성 향상**: 코드 이해 시간 단축
2. ✅ **유지보수성 강화**: 변경 영향 범위 파악 용이
3. ✅ **AI 협업 최적화**: 프롬프팅 효율성 극대화
4. ✅ **온보딩 시간 단축**: 신규 개발자 빠른 적응

이 가이드를 따라 일관된 주석을 작성하면, 사람과 AI 모두에게 친화적인 코드베이스를 유지할 수 있습니다.


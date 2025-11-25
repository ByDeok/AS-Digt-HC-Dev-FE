# 함수 호출 구조 (Function Call Hierarchy)

> **StartupPlan** - 전체 애플리케이션의 함수 호출 흐름  
> 생성일: 2025년 11월 21일

---

## 📋 목차

1. [전체 구조 개요](#전체-구조-개요)
2. [페이지별 호출 구조](#페이지별-호출-구조)
3. [Store 간 상호작용](#store-간-상호작용)
4. [데이터 흐름](#데이터-흐름)
5. [주요 시나리오별 호출 체인](#주요-시나리오별-호출-체인)

---

## 전체 구조 개요

### 애플리케이션 진입점

```
main.tsx (진입점)
  └─> App.tsx (라우팅)
      ├─> ProjectCreate (/)
      ├─> Layout
      │   ├─> WizardStep (/wizard/:stepId)
      │   └─> BusinessPlanViewer (/business-plan)
      └─> Navigate (404)
```

### 아키텍처 레이어

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Components, Pages)                    │
├─────────────────────────────────────────┤
│         Business Logic Layer            │
│  (Hooks, Utils)                         │
├─────────────────────────────────────────┤
│         State Management Layer          │
│  (Zustand Stores + Persist)             │
├─────────────────────────────────────────┤
│         Data Layer                      │
│  (localStorage, Types)                  │
└─────────────────────────────────────────┘
```

---

## 페이지별 호출 구조

### 1. ProjectCreate 페이지

```
ProjectCreate.tsx
  │
  ├─> useState
  │   ├─> projectName
  │   ├─> selectedTemplate
  │   └─> error
  │
  ├─> useProjectStore
  │   └─> createProject(name, templateId)
  │       └─> set({ currentProject: newProject })
  │           └─> localStorage 저장
  │
  ├─> useWizardStore
  │   └─> resetWizard()
  │       └─> set({ currentStep: 1, wizardData: {} })
  │           └─> localStorage 저장
  │
  └─> useNavigate
      └─> navigate('/wizard/1')
          └─> React Router 라우팅
```

### 2. WizardStep 페이지

```
WizardStep.tsx
  │
  ├─> useParams<{ stepId: string }>()
  │   └─> URL에서 stepId 추출
  │
  ├─> useNavigate()
  │
  ├─> useWizardStore
  │   ├─> currentStep
  │   ├─> steps
  │   ├─> setCurrentStep(stepNumber)
  │   ├─> isStepCompleted(stepNumber)
  │   ├─> goToNextStep()
  │   └─> goToPreviousStep()
  │
  └─> 조건부 렌더링
      ├─> stepNumber === 4
      │   └─> <FinancialSimulation />
      │       └─> useFinancialStore
      │           ├─> input
      │           ├─> metrics
      │           ├─> chartData
      │           └─> updateInput()
      │               ├─> calculateMetrics()
      │               └─> generateChartData()
      │
      ├─> stepNumber === 5
      │   └─> <PMFSurvey />
      │       └─> usePMFStore
      │           ├─> answers
      │           ├─> report
      │           ├─> updateAnswer()
      │           └─> generateReport()
      │
      └─> 기타 (1-3)
          └─> <QuestionForm />
              ├─> useWizardStore
              │   ├─> updateStepData(stepId, questionId, value)
              │   └─> getStepData(stepId)
              │
              └─> useAutoSave(stepData, 1000)
                  └─> useProjectStore
                      └─> setSaveStatus(status)
```

### 3. BusinessPlanViewer 페이지

```
BusinessPlanViewer.tsx
  │
  ├─> useState
  │   ├─> isGenerating
  │   ├─> isGenerated
  │   ├─> sections
  │   └─> regeneratingSection
  │
  ├─> handleGenerate()
  │   └─> setTimeout(() => {
  │         setIsGenerated(true)
  │       }, 3000)
  │
  ├─> handleRegenerate(sectionId)
  │   └─> setTimeout(() => {
  │         setSections(updated)
  │       }, 2000)
  │
  └─> handleExport(format)
      └─> window.alert(...)
```

### 4. Layout 컴포넌트

```
Layout.tsx
  │
  ├─> useLocation()
  │   └─> location.pathname
  │
  ├─> useWizardStore
  │   ├─> currentStep
  │   ├─> steps
  │   └─> isStepCompleted(step.id)
  │
  ├─> useProjectStore
  │   └─> currentProject
  │
  └─> 하위 컴포넌트
      ├─> <SaveIndicator />
      │   └─> useProjectStore
      │       └─> saveStatus
      │
      ├─> <Progress />
      │   └─> value={progressPercentage}
      │
      └─> <Outlet />
          └─> 현재 경로의 페이지 렌더링
```

---

## Store 간 상호작용

### Store 호출 맵

```
useProjectStore
  ├─> 호출하는 곳
  │   ├─> ProjectCreate
  │   │   └─> createProject()
  │   ├─> Layout
  │   │   └─> currentProject 조회
  │   └─> SaveIndicator
  │       └─> saveStatus 조회
  │
  └─> 호출되는 곳
      └─> useAutoSave
          └─> setSaveStatus()

useWizardStore
  ├─> 호출하는 곳
  │   ├─> ProjectCreate
  │   │   └─> resetWizard()
  │   ├─> WizardStep
  │   │   ├─> setCurrentStep()
  │   │   ├─> isStepCompleted()
  │   │   ├─> goToNextStep()
  │   │   └─> goToPreviousStep()
  │   ├─> Layout
  │   │   ├─> currentStep 조회
  │   │   └─> isStepCompleted()
  │   └─> QuestionForm
  │       ├─> updateStepData()
  │       └─> getStepData()
  │
  └─> 의존성: 없음 (독립적)

useFinancialStore
  ├─> 호출하는 곳
  │   └─> FinancialSimulation
  │       ├─> input 조회
  │       ├─> metrics 조회
  │       ├─> chartData 조회
  │       └─> updateInput()
  │
  └─> 내부 호출
      └─> updateInput()
          ├─> calculateMetrics()
          └─> generateChartData()

usePMFStore
  ├─> 호출하는 곳
  │   └─> PMFSurvey
  │       ├─> answers 조회
  │       ├─> report 조회
  │       ├─> updateAnswer()
  │       └─> generateReport()
  │
  └─> 의존성: mockData
      ├─> mockRisks
      └─> mockRecommendations
```

---

## 데이터 흐름

### 1. 프로젝트 생성 흐름

```
사용자 입력
  ↓
ProjectCreate.handleSubmit()
  ↓
useProjectStore.createProject()
  ├─> newProject 생성
  ├─> set({ currentProject: newProject })
  └─> persist middleware
      └─> localStorage.setItem('project-storage', ...)
  ↓
useWizardStore.resetWizard()
  ├─> set({ currentStep: 1, wizardData: {} })
  └─> persist middleware
      └─> localStorage.setItem('wizard-storage', ...)
  ↓
navigate('/wizard/1')
  ↓
WizardStep 페이지 렌더링
```

### 2. 질문 답변 저장 흐름

```
사용자 입력 (Input/Textarea)
  ↓
QuestionForm.handleChange(questionId, value)
  ↓
useWizardStore.updateStepData(stepId, questionId, value)
  ├─> set({ wizardData: updated })
  └─> persist middleware
      └─> localStorage.setItem('wizard-storage', ...)
  ↓
useAutoSave hook 트리거 (1초 debounce)
  ├─> useProjectStore.setSaveStatus('saving')
  │   ↓
  │   SaveIndicator 업데이트
  ↓
setTimeout (500ms)
  ├─> useProjectStore.setSaveStatus('saved')
  │   ↓
  │   SaveIndicator 업데이트
  └─> setTimeout (2000ms)
      └─> useProjectStore.setSaveStatus('idle')
          ↓
          SaveIndicator 숨김
```

### 3. 재무 계산 흐름

```
사용자 입력 (숫자 변경)
  ↓
FinancialSimulation.handleInputChange(field, value)
  ↓
useFinancialStore.updateInput({ [field]: value })
  ├─> set({ input: updated })
  └─> persist middleware
      └─> localStorage.setItem('financial-storage', ...)
  ↓
useFinancialStore.calculateMetrics() (자동 호출)
  ├─> 매출 계산: customers × pricePerCustomer
  ├─> 비용 계산: fixedCosts + variableCosts
  ├─> 이익 계산: revenue - totalCosts
  ├─> LTV 계산: pricePerCustomer × avgLifetimeMonths
  ├─> LTV/CAC 비율 계산
  └─> 손익분기점 계산
  ↓
useFinancialStore.generateChartData() (자동 호출)
  └─> 12개월 데이터 생성 (15% 성장률 가정)
  ↓
컴포넌트 리렌더링
  ├─> 지표 카드 업데이트
  └─> 차트 업데이트
```

### 4. PMF 진단 흐름

```
사용자 답변 선택
  ↓
PMFSurvey.handleAnswerChange(questionId, value)
  ↓
usePMFStore.updateAnswer(questionId, value)
  ├─> answers 배열 업데이트
  └─> persist middleware
      └─> localStorage.setItem('pmf-storage', ...)
  ↓
모든 질문 답변 완료 시
  ↓
PMFSurvey.handleSubmit()
  ↓
usePMFStore.generateReport()
  ├─> 평균 점수 계산
  ├─> 100점 환산
  ├─> 등급 판정 (excellent/high/medium/low)
  ├─> 리스크 필터링 (점수 기반)
  └─> 개선 제언 로드
  ↓
set({ report: newReport })
  ↓
컴포넌트 리렌더링
  └─> 진단 결과 화면 표시
```

---

## 주요 시나리오별 호출 체인

### 시나리오 1: 신규 프로젝트 시작

```
1. 메인 페이지 접속
   main.tsx → App.tsx → ProjectCreate

2. 프로젝트 정보 입력
   ProjectCreate
     ├─> projectName 입력
     └─> template 선택

3. 프로젝트 생성
   handleSubmit()
     ├─> useProjectStore.createProject()
     └─> useWizardStore.resetWizard()

4. 마법사 1단계로 이동
   navigate('/wizard/1')
     └─> WizardStep 렌더링
```

### 시나리오 2: 마법사 진행

```
1. 단계 진입
   WizardStep
     ├─> useParams() - stepId 추출
     └─> useWizardStore.setCurrentStep()

2. 질문 답변
   QuestionForm
     ├─> handleChange()
     └─> useWizardStore.updateStepData()
         └─> useAutoSave() 트리거

3. 다음 단계 이동
   WizardStep.handleNext()
     ├─> useWizardStore.goToNextStep()
     └─> navigate(`/wizard/${stepNumber + 1}`)

4. 특수 단계 (4, 5)
   stepNumber === 4
     └─> FinancialSimulation
         └─> 재무 계산 실행
   
   stepNumber === 5
     └─> PMFSurvey
         └─> PMF 진단 실행
```

### 시나리오 3: 사업계획서 생성

```
1. 마지막 단계 완료
   WizardStep.handleNext()
     └─> navigate('/business-plan')

2. 생성 버튼 클릭
   BusinessPlanViewer.handleGenerate()
     └─> setIsGenerating(true)

3. AI 생성 시뮬레이션
   setTimeout(3000)
     └─> setIsGenerated(true)

4. 결과 표시
   sections.map((section) => ...)
     ├─> 섹션별 내용 표시
     └─> "다시 쓰기" 버튼

5. 섹션 재생성
   handleRegenerate(sectionId)
     └─> 해당 섹션만 업데이트

6. 내보내기
   handleExport('pdf' | 'hwp')
     └─> 다운로드 시뮬레이션
```

### 시나리오 4: 자동 저장

```
1. 데이터 변경
   사용자 입력
     └─> 컴포넌트 state 업데이트

2. AutoSave Hook 트리거
   useAutoSave(data, 1000)
     ├─> useEffect 실행
     └─> 데이터 비교 (JSON.stringify)

3. 변경 감지 시
   previousDataRef !== currentData
     ├─> setSaveStatus('saving')
     └─> debounce(1000ms)

4. 저장 실행
   debouncedSave()
     ├─> 500ms 대기
     ├─> setSaveStatus('saved')
     └─> 2000ms 후 idle 복귀

5. UI 피드백
   SaveIndicator
     ├─> 'saving': 구름 아이콘
     ├─> 'saved': 체크 아이콘
     └─> 'idle': 숨김
```

---

## 성능 최적화 포인트

### 현재 구현

```
❌ React.memo 미사용
  → 불필요한 리렌더링 발생

❌ useCallback 미사용
  → 매 렌더링마다 함수 재생성

❌ useMemo 미사용
  → 매 렌더링마다 계산 재실행
```

### 개선 방향

```typescript
// Before
export const QuestionForm: React.FC<Props> = ({ questions, stepId }) => {
  const handleChange = (id: string, value: any) => {
    updateStepData(stepId, id, value);
  };
  // ...
}

// After
export const QuestionForm = React.memo<Props>(({ questions, stepId }) => {
  const handleChange = useCallback((id: string, value: any) => {
    updateStepData(stepId, id, value);
  }, [stepId, updateStepData]);
  // ...
});
```

---

## 결론

이 문서는 전체 애플리케이션의 함수 호출 구조를 시각화하여:

1. ✅ **트러블슈팅 효율화**: 에러 발생 시 호출 체인 추적
2. ✅ **영향 범위 파악**: 변경 시 영향받는 컴포넌트 파악
3. ✅ **최적화 기회 발견**: 불필요한 호출 및 리렌더링 식별
4. ✅ **신규 개발자 온보딩**: 전체 구조 빠른 이해

코드 변경 시 이 문서를 함께 업데이트하여 최신 상태를 유지하세요.


# 🔍 Java 프로젝트 인식 문제 진단

## 현재 상태 확인

VS Code가 프로젝트를 인식하지 못하고 있습니다. 다음을 단계별로 확인하세요.

---

## 📋 1단계: Java Extension 확인

### Extensions 패널 확인
1. `Ctrl+Shift+X` (Extensions 열기)
2. 검색: `Extension Pack for Java`
3. **설치되어 있는지 확인**
4. **설치되어 있다면:**
   - 우클릭 → **Disable**
   - VS Code 완전 종료
   - VS Code 재시작
   - Extensions에서 다시 **Enable**

---

## 📋 2단계: Output 패널에서 오류 확인

1. **`Ctrl+Shift+U`** (Output 패널 열기)
2. 우측 상단 **드롭다운** 클릭
3. 다음 항목들을 각각 선택하고 오류 메시지 확인:
   - `Language Support for Java`
   - `Test Runner for Java`
   - `Gradle for Java`

**오류 메시지를 복사해서 알려주세요!**

---

## 📋 3단계: Java Projects 뷰 상태 확인

1. 좌측 사이드바 **"Java Projects"** 아이콘 클릭
2. 다음 중 어떤 상태인지 확인:

### 상태 A: 완전히 비어있음 (아무것도 안 보임)
→ 프로젝트가 전혀 로드되지 않음

### 상태 B: "No Java projects found" 메시지
→ Java Extension이 작동하지만 프로젝트를 찾지 못함

### 상태 C: 프로젝트가 보이지만 "..." 또는 "Loading..." 상태
→ 로딩 중이거나 오류 발생

### 상태 D: 프로젝트가 보이지만 테스트가 안 보임
→ 프로젝트는 인식되었지만 테스트 인식 실패

---

## 📋 4단계: 수동으로 프로젝트 로드 시도

### 방법 A: Import Java Projects
1. `Ctrl+Shift+P`
2. `Java: Import Java Projects...` 입력
3. `build.gradle` 파일 선택 또는 프로젝트 루트 선택

### 방법 B: Gradle 프로젝트 강제 인식
1. `Ctrl+Shift+P`
2. `Java: Build Workspace` 입력
3. 완료될 때까지 대기

---

## 📋 5단계: Java 런타임 확인

VS Code가 Java를 찾지 못하면 프로젝트를 로드할 수 없습니다.

1. **Command Palette** (`Ctrl+Shift+P`)
2. `Java: Configure Java Runtime` 입력
3. Java 버전 확인:
   - Java 17 이상이 설정되어 있어야 함
   - "Download JDK" 옵션이 보이면 클릭하여 자동 설치

---

## 🆘 최후의 수단

위 방법들이 모두 실패하면:

### 방법 1: IntelliJ IDEA 사용 (권장)
- IntelliJ IDEA Community Edition (무료) 다운로드
- File → Open → `build.gradle` 선택
- 자동으로 Gradle 프로젝트 인식
- 테스트 실행 버튼 클릭

### 방법 2: 터미널에서 테스트 (Java 설치 필요)
```powershell
./gradlew test --tests "OnboardingServiceTest"
```

---

## 📝 확인 체크리스트

다음을 확인하고 알려주세요:

1. [ ] Java Extension Pack 설치 여부: ✅ / ❌
2. [ ] Output 패널 오류 메시지: (복사해서 알려주세요)
3. [ ] Java Projects 뷰 상태: (상태 A/B/C/D 중 선택)
4. [ ] Java Runtime 설정: (Java 17 이상 있음 / 없음)

위 정보를 알려주시면 더 정확한 해결책을 제시할 수 있습니다!

# Java Extension 확인 가이드

## 현재 문제
`Java: Import Java Projects...` 실행 시 반응 없음

---

## 🔍 즉시 확인 사항

### 1. Java Extension 설치 확인

1. **Extensions 패널 열기**
   - `Ctrl+Shift+X`

2. **검색:**
   ```
   Extension Pack for Java
   ```

3. **상태 확인:**
   - **설치 안 됨** → "Install" 버튼 클릭
   - **설치됨 + 비활성화** → "Enable" 클릭
   - **설치됨 + 활성화됨** → 다른 문제

---

### 2. Output 패널 확인 (중요!)

1. **`Ctrl+Shift+U`** (Output 패널)
2. 우측 상단 **드롭다운** 클릭
3. 다음 항목들 각각 확인:

#### A. "Language Support for Java"
- 오류 메시지 확인
- "Failed to activate" 같은 메시지 있는지

#### B. "Extension Host"
- Java Extension 관련 오류 확인

**오류 메시지를 복사해서 알려주세요!**

---

## 🔧 해결 방법

### 방법 1: Extension 재설치

1. Extensions (`Ctrl+Shift+X`)
2. "Extension Pack for Java" 검색
3. **Uninstall** (제거)
4. VS Code **완전 종료**
5. VS Code 재시작
6. 다시 **Install**
7. 설치 완료 후 1-2분 대기
8. `Java: Import Java Projects...` 다시 시도

---

### 방법 2: 다른 명령어 시도

`Ctrl+Shift+P` → 다음 명령어들 시도:

```
Java: Build Workspace
```

```
Java: Reload Projects
```

```
Java: Configure Java Runtime
```

이 명령어들이 반응하는지 확인!

---

### 방법 3: VS Code 완전 재시작

1. **모든 VS Code 창 닫기**
2. 작업 관리자에서 VS Code 프로세스 확인 (완전 종료)
3. VS Code 다시 실행
4. 프로젝트 폴더 열기
5. 2-3분 대기 (Extension 초기화)
6. `Java: Import Java Projects...` 시도

---

## 🆘 최후의 수단: IntelliJ IDEA

VS Code에서 계속 문제가 있으면:

### IntelliJ IDEA 사용 (권장)

**이유:**
- Gradle 프로젝트 자동 인식
- 테스트 실행이 더 쉬움
- Java 개발에 최적화

**설치 및 실행:**
1. 다운로드: https://www.jetbrains.com/idea/download/
2. Community Edition (무료) 선택
3. 설치 후:
   - File → Open
   - `build.gradle` 파일 선택
   - "Open as Project" 클릭
   - 자동으로 프로젝트 인식
   - 테스트 실행 버튼 바로 사용

---

## 📋 확인 체크리스트

다음을 확인하고 알려주세요:

1. [ ] Extension Pack for Java **설치 여부**: (✅ / ❌)
2. [ ] Extension **활성화 여부**: (✅ / ❌)
3. [ ] Output 패널 오류 메시지: (복사해서 알려주세요)
4. [ ] `Java: Build Workspace` 명령어 반응: (✅ / ❌)

---

## 💡 빠른 확인

**가장 빠른 확인 방법:**

1. Extensions (`Ctrl+Shift+X`)
2. 검색: `java`
3. 다음 확장들이 설치되어 있는지 확인:
   - ✅ Extension Pack for Java
   - ✅ Language Support for Java by Red Hat
   - ✅ Test Runner for Java
   - ✅ Gradle for Java

**모두 설치되어 있어야 합니다!**

---

위 내용을 확인한 후 결과를 알려주세요. 특히:
1. Extension 설치 여부
2. Output 패널의 오류 메시지

이 정보가 있으면 정확한 해결책을 제시할 수 있습니다!

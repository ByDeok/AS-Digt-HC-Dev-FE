# 프로젝트 Import 반응 없을 때 해결 방법

## 문제
`Java: Import Java Projects...` 실행 시 아무 반응 없음

---

## 🔍 1단계: Java Extension 확인

### Extensions 상태 확인
1. `Ctrl+Shift+X` (Extensions)
2. 검색: `Extension Pack for Java`
3. **상태 확인:**
   - ✅ 설치됨 + 활성화됨
   - ❌ 설치 안 됨 → 설치 필요
   - ⚠️ 비활성화됨 → Enable 클릭

---

## 🔧 2단계: 다른 명령어 시도

### 방법 A: Build Workspace
`Ctrl+Shift+P` → 다음 명령어:
```
Java: Build Workspace
```

### 방법 B: Clean Workspace 후 Reload
`Ctrl+Shift+P` → 다음 순서:
1. `Java: Clean Java Language Server Workspace`
2. "Reload Window?" → Reload
3. 재시작 후 `Java: Reload Projects`

### 방법 C: Gradle 프로젝트 직접 열기
1. File → Open Folder
2. 프로젝트 루트 폴더 선택 (`AS-Digt-HC-Dev-FE`)
3. `build.gradle` 파일이 보이는지 확인

---

## 🆘 3단계: Java Extension 재설치

### 완전 재설치
1. Extensions (`Ctrl+Shift+X`)
2. "Extension Pack for Java" 검색
3. **Uninstall** (제거)
4. VS Code **완전 종료** (모든 창 닫기)
5. VS Code 재시작
6. Extensions에서 다시 **Install**
7. 설치 완료 후:
   - `Ctrl+Shift+P`
   - `Java: Import Java Projects...` 다시 시도

---

## 💡 4단계: 대안 - IntelliJ IDEA 사용

VS Code에서 계속 문제가 있으면:

### IntelliJ IDEA Community Edition (무료)
1. 다운로드: https://www.jetbrains.com/idea/download/
2. 설치 후:
   - File → Open
   - `build.gradle` 파일 선택
   - "Open as Project" 클릭
   - 자동으로 Gradle 프로젝트 인식
   - 테스트 실행 버튼 바로 사용 가능

**IntelliJ는 Gradle 프로젝트를 자동으로 잘 인식합니다!**

---

## 🔍 5단계: Output 패널에서 실제 문제 확인

### Output 패널 상세 확인
1. `Ctrl+Shift+U` (Output 패널)
2. 우측 상단 드롭다운에서 다음 항목들 확인:

#### A. Language Support for Java
- 오류 메시지 확인
- "Gradle sync failed" 같은 메시지 있는지

#### B. Gradle for Java  
- Gradle 관련 오류 확인

#### C. Test Runner for Java
- 테스트 러너 상태 확인

**오류 메시지를 복사해서 알려주세요!**

---

## ✅ 체크리스트

- [ ] Extension Pack for Java 설치되어 있음
- [ ] Extension이 활성화되어 있음 (Disable 버튼이 보임)
- [ ] `Java: Build Workspace` 명령어는 작동하는가?
- [ ] Output 패널에 오류 메시지가 있는가?
- [ ] VS Code를 완전히 재시작했는가?

---

## 🎯 권장 순서

1. **Java Extension 설치/활성화 확인**
2. **VS Code 완전 재시작**
3. **`Java: Build Workspace` 시도**
4. **여전히 안 되면 IntelliJ IDEA 사용** (가장 확실한 방법)

IntelliJ IDEA는 Gradle 프로젝트를 자동으로 완벽하게 인식하므로, VS Code에서 계속 문제가 있다면 IntelliJ 사용을 권장합니다.

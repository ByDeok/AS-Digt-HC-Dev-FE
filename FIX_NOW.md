# 🚨 JUnit 5 오류 즉시 해결

## 지금 바로 실행할 단계

### ⚡ 단계 1: Command Palette 열기
**`Ctrl+Shift+P`** 키를 누르세요

### ⚡ 단계 2: 다음 명령어 실행

다음 명령어를 **정확히** 입력하고 Enter:

```
Java: Clean Java Language Server Workspace
```

→ "Reload Window?" 질문이 나오면 **"Reload"** 클릭

### ⚡ 단계 3: 프로젝트 다시 로드

VS Code가 다시 시작된 후, 다시 `Ctrl+Shift+P` → 다음 명령어:

```
Java: Reload Projects
```

### ⚡ 단계 4: 확인

1. **좌측 사이드바**에서 **"Java Projects"** 아이콘 클릭 (컵 아이콘)
2. 프로젝트 트리가 열리는지 확인
3. `as-digt-hc-dev-fe` 프로젝트가 보여야 함

---

## 🔍 여전히 오류가 나면

### 방법 A: Output 패널에서 오류 확인

1. **`Ctrl+Shift+U`** (Output 패널 열기)
2. 우측 상단 드롭다운에서 **"Language Support for Java"** 선택
3. 오류 메시지 확인
4. 오류 내용을 복사해서 알려주세요

### 방법 B: Java Extension 확인

1. **`Ctrl+Shift+X`** (Extensions)
2. 검색: `Extension Pack for Java`
3. **설치되어 있는지 확인**
4. **설치되어 있다면**:
   - 우클릭 → **Disable**
   - VS Code 재시작
   - 다시 **Enable**

### 방법 C: Java Projects 수동 새로고침

1. **Java Projects** 뷰에서
2. 프로젝트 이름(`as-digt-hc-dev-fe`) 우클릭
3. **"Reload Projects"** 선택

---

## ✅ 성공 확인

성공하면 `OnboardingServiceTest.java` 파일에서:
- ✅ 각 `@Test` 메서드 위에 **"Run Test"** 링크가 보임
- ✅ 클래스 선언 위에 **"Run All Tests"** 링크가 보임
- ✅ 빨간 밑줄 에러가 없음
- ✅ IntelliSense가 정상 작동

---

## 🆘 최후의 수단

위 방법들이 모두 실패하면:

1. **VS Code 완전 종료** (모든 창 닫기)
2. 프로젝트 폴더에서 `.vscode` 폴더 삭제 (선택사항)
3. VS Code 재시작
4. 프로젝트 다시 열기
5. `Ctrl+Shift+P` → `Java: Reload Projects`

또는 **IntelliJ IDEA 사용**을 고려하세요. (Gradle 프로젝트를 더 잘 지원합니다)

# VS Code Java 프로젝트 인식 가이드

## ❌ 별도 폴더 만들 필요 없음!

**Gradle 프로젝트는 자동으로 인식되어야 합니다.**

현재 프로젝트 구조:
```
AS-Digt-HC-Dev-FE/
├── build.gradle          ← 이게 있으면 Gradle 프로젝트
├── settings.gradle
├── gradlew.bat
├── src/
│   ├── main/java/       ← 소스 코드
│   └── test/java/       ← 테스트 코드
└── ...
```

## ✅ VS Code가 프로젝트를 인식하도록 하는 방법

### 방법 1: Command Palette에서 프로젝트 로드 (권장)

1. **`Ctrl+Shift+P`** 입력
2. 다음 명령어 실행:
   ```
   Java: Reload Projects
   ```

3. **Java Projects 뷰 확인**
   - 좌측 사이드바 "Java Projects" 아이콘 클릭
   - `as-digt-hc-dev-fe` 프로젝트가 나타나야 함

### 방법 2: Java Extension 설정 확인

1. **Command Palette** (`Ctrl+Shift+P`)
2. 다음 명령어 실행:
   ```
   Java: Clean Java Language Server Workspace
   ```
   - "Reload Window?" → **Reload** 클릭

3. VS Code 재시작 후:
   ```
   Java: Reload Projects
   ```

### 방법 3: 수동으로 Gradle 프로젝트 인식

1. **Command Palette** (`Ctrl+Shift+P`)
2. 다음 명령어:
   ```
   Java: Import Java Projects
   ```
   - 프로젝트 목록에서 `build.gradle` 선택
   - 또는 "Import Gradle Project" 선택

## 🔍 현재 상태 확인

### Java Projects 뷰에서 확인해야 할 것:

```
Java Projects
└── 📁 as-digt-hc-dev-fe
    ├── 📁 Referenced Libraries
    │   ├── junit-jupiter-api-*.jar
    │   ├── spring-boot-starter-test-*.jar
    │   └── ...
    ├── 📁 src/main/java
    └── 📁 src/test/java
        └── vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.service
            └── OnboardingServiceTest.java  ← 이게 보여야 함
```

### 테스트 파일에서 확인:

`OnboardingServiceTest.java` 파일을 열었을 때:
- ✅ 파일 상단에 에러가 없어야 함
- ✅ 각 `@Test` 위에 "▶ Run Test" 링크가 보여야 함
- ✅ 클래스 선언 위에 "▶ Run All Tests" 링크가 보여야 함

## 🐛 여전히 인식이 안 되면

### 1. Output 패널 확인

`Ctrl+Shift+U` → 드롭다운에서 "Language Support for Java" 선택
- 오류 메시지가 있으면 확인

### 2. Java Extension Pack 재설치

1. Extensions (`Ctrl+Shift+X`)
2. "Extension Pack for Java" 검색
3. **Disable** → **Enable** (재시작)

### 3. 프로젝트 루트 확인

VS Code가 올바른 폴더를 열었는지 확인:
- `AS-Digt-HC-Dev-FE` 폴더가 루트여야 함
- `build.gradle` 파일이 보여야 함

### 4. .vscode/settings.json 확인

현재 설정이 올바른지 확인:
```json
{
    "java.import.gradle.enabled": true,
    "java.import.gradle.wrapper.enabled": true
}
```

## 📋 체크리스트

- [ ] `build.gradle` 파일이 프로젝트 루트에 있음
- [ ] `Java: Reload Projects` 실행함
- [ ] Java Projects 뷰에서 프로젝트가 보임
- [ ] `src/test/java` 폴더가 인식됨
- [ ] `OnboardingServiceTest.java` 파일에 "Run Test" 링크가 보임

## 💡 핵심 정리

**❌ 별도 폴더 만들 필요 없음!**
- Gradle 프로젝트는 자동 인식
- `build.gradle`만 있으면 됨

**✅ 해야 할 일:**
1. `Java: Reload Projects` 실행
2. Java Projects 뷰 확인
3. 테스트 파일에서 "Run Test" 링크 확인

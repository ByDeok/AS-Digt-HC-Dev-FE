# JUnit 5 Build Path 오류 해결 가이드

## 오류 메시지
```
Cannot find 'org.junit.platform.commons.annotation.Testable' on project build path. 
JUnit 5 tests can only be run if JUnit 5 is on the build path.
```

## 해결 방법

### 1. build.gradle 업데이트 완료 ✅

JUnit 5 의존성을 명시적으로 추가했습니다:
```groovy
testImplementation 'org.junit.jupiter:junit-jupiter-api'
testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine'
```

### 2. VS Code에서 Gradle 프로젝트 다시 로드

#### 방법 A: 명령 팔레트 사용 (권장)

1. **`Ctrl+Shift+P` (또는 `Cmd+Shift+P`)** 입력
2. 다음 명령어들을 순서대로 실행:
   - `Java: Clean Java Language Server Workspace`
   - `Java: Reload Projects`
   - `Developer: Reload Window`

#### 방법 B: VS Code Java Extension 설정

1. **Java Extension 확인**
   - Extensions (`Ctrl+Shift+X`)에서 "Extension Pack for Java" 설치 확인
   - 필수 확장:
     - Language Support for Java by Red Hat
     - Debugger for Java
     - Test Runner for Java
     - Maven for Java
     - Project Manager for Java

2. **프로젝트 새로고침**
   - `Ctrl+Shift+P` → `Java: Reload Projects` 실행

### 3. Gradle 의존성 다운로드 강제 실행

터미널에서 다음 명령어 실행 (Java가 필요하지만, 의존성만 확인):
```powershell
# Gradle Wrapper가 있으면 실행 (Java 필요)
# ./gradlew build --refresh-dependencies

# 또는 VS Code 내부에서
# Command Palette → "Java: Build Workspace"
```

### 4. IDE 설정 파일 확인

`.vscode/settings.json` 파일이 생성되었는지 확인:
```json
{
    "java.configuration.updateBuildConfiguration": "automatic",
    "java.compile.nullAnalysis.mode": "automatic",
    "gradle.nestedProjects": true
}
```

### 5. 수동으로 프로젝트 인식 확인

1. **Java Projects 뷰 확인**
   - VS Code 좌측 사이드바에서 "Java Projects" 탭 클릭
   - 프로젝트가 "Referenced Libraries"와 함께 표시되는지 확인
   - "as-digt-hc-dev-fe" 프로젝트가 보이는지 확인

2. **JUnit 5 라이브러리 확인**
   - "Java Projects" → 프로젝트 → "Referenced Libraries" 확장
   - `junit-jupiter-api-*.jar` 파일이 있는지 확인

### 6. 캐시 정리 및 재빌드

```powershell
# Gradle 캐시 정리 (필요시)
Remove-Item -Recurse -Force .gradle -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force build -ErrorAction SilentlyContinue

# VS Code Java Language Server 캐시 정리
# Command Palette → "Java: Clean Java Language Server Workspace"
```

## 단계별 체크리스트

- [ ] `build.gradle`에 JUnit 5 의존성 추가됨
- [ ] `.vscode/settings.json` 파일 존재 확인
- [ ] Java Extension Pack 설치 확인
- [ ] `Java: Reload Projects` 실행
- [ ] Java Projects 뷰에서 프로젝트 인식 확인
- [ ] Referenced Libraries에 JUnit 5 라이브러리 표시 확인

## 여전히 작동하지 않는 경우

### IntelliJ IDEA 사용 고려

IntelliJ IDEA는 Gradle 프로젝트를 더 잘 인식합니다:
1. File → Open → `build.gradle` 선택
2. "Open as Project" 선택
3. Gradle 동기화 자동 실행

### Gradle 프로젝트 수동 빌드

```powershell
# Gradle 빌드 스크립트 직접 실행 (Java 17 필요)
# 또는 IDE에서 자동으로 실행되도록 대기
```

## 테스트 실행 확인

프로젝트가 제대로 로드되면:
- `OnboardingServiceTest.java` 파일에서 각 테스트 메서드 위에 "Run Test" 링크가 보여야 합니다
- 또는 클래스 선언 위에 "Run All Tests" 링크가 보여야 합니다

## 추가 정보

- VS Code Java Extension: https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack
- Spring Boot Test 문서: https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing

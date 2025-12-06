# JUnit 5 오류 빠른 해결 가이드

## 문제
```
Cannot find 'org.junit.platform.commons.annotation.Testable' on project build path.
```

## 즉시 해결 방법 (VS Code)

### 1단계: Command Palette 열기
`Ctrl+Shift+P` (또는 `F1`)

### 2단계: 다음 명령어들을 순서대로 실행

#### A. Java Language Server 캐시 정리
```
Java: Clean Java Language Server Workspace
```
- 실행 후 **VS Code를 재시작**하라는 메시지가 나오면 재시작

#### B. Gradle 프로젝트 다시 로드
```
Java: Reload Projects
```

#### C. Java Extension이 Gradle을 사용하도록 확인
1. Extensions (`Ctrl+Shift+X`) 열기
2. "Extension Pack for Java" 검색
3. 설치되어 있는지 확인
4. 설치되어 있다면 → Disable → Enable (재시작)

### 3단계: Java Projects 뷰 확인

1. VS Code 좌측 사이드바에서 **"Java Projects"** 아이콘 클릭
2. 프로젝트 트리에서:
   - `as-digt-hc-dev-fe` 프로젝트가 보여야 함
   - **"Referenced Libraries"** 확장
   - 다음 라이브러리들이 보여야 함:
     - `junit-jupiter-api-*.jar`
     - `spring-boot-starter-test-*.jar`
     - `mockito-*.jar`

### 4단계: 여전히 작동하지 않으면

#### 방법 1: 수동 Gradle 동기화
터미널에서 (Java가 설치되어 있다면):
```powershell
# Gradle 의존성 다운로드
./gradlew build --refresh-dependencies
```

그 다음:
```
Java: Reload Projects
```

#### 방법 2: .vscode/settings.json 확인
현재 설정 파일 확인:
- `.vscode/settings.json` 파일이 있어야 함
- `"java.import.gradle.enabled": true` 포함 확인

#### 방법 3: Java Extension 재설치
1. Extensions에서 "Extension Pack for Java" 제거
2. VS Code 재시작
3. 다시 설치

## 확인 방법

성공적으로 설정되면:
1. `OnboardingServiceTest.java` 파일을 열었을 때
2. 각 `@Test` 메서드 위에 **"Run Test"** 링크가 보임
3. 또는 클래스 선언 위에 **"Run All Tests"** 링크가 보임
4. 에러 마커(빨간 밑줄)가 없어야 함

## 추가 문제 해결

### Java Language Server 로그 확인
1. `Ctrl+Shift+P`
2. `Java: Show Server Task Status` 실행
3. 오류 메시지 확인

### Gradle 빌드 수동 실행
Java가 설치되어 있다면:
```powershell
./gradlew testClasses
```

이 명령어는 테스트 클래스를 컴파일하고 의존성을 다운로드합니다.

## 대안: IntelliJ IDEA 사용

VS Code에서 계속 문제가 발생하면:
1. IntelliJ IDEA (Community Edition 무료) 다운로드
2. File → Open → `build.gradle` 선택
3. "Open as Project" 클릭
4. Gradle 동기화 자동 실행
5. 테스트 실행 버튼 클릭

IntelliJ는 Gradle 프로젝트를 더 잘 지원합니다.

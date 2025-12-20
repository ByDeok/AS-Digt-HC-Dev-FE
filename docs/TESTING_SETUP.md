# 테스트 실행 환경 설정 가이드

## 문제: Java 17이 설치되지 않은 경우

에러 메시지:
```
Cannot find a Java installation on your machine matching: {languageVersion=17}
```

## 해결 방법

### 방법 1: IDE에서 테스트 실행 (권장)

#### IntelliJ IDEA
1. `OnboardingServiceTest.java` 파일 열기
2. 테스트 메서드 옆의 초록색 실행 버튼 클릭
3. 또는 `Ctrl+Shift+F10` (Windows/Linux) / `Cmd+Shift+R` (Mac)

#### VS Code
1. Java Extension Pack 설치
2. 테스트 메서드 위의 "Run Test" 링크 클릭
3. 또는 `Ctrl+F5`

### 방법 2: Java 17 설치 (Gradle 터미널 실행용)

#### Windows
1. **Adoptium (Eclipse Temurin) 다운로드**
   - https://adoptium.net/temurin/releases/?version=17
   - Windows x64 JDK 17 선택
   - `.msi` 설치 파일 다운로드

2. **설치 후 환경 변수 설정**
   ```powershell
   # 시스템 환경 변수 설정
   [Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot", "Machine")
   [Environment]::SetEnvironmentVariable("Path", "$env:Path;C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot\bin", "Machine")
   ```

3. **확인**
   ```powershell
   java -version
   # 출력 예: openjdk version "17.0.x" ...
   ```

### 방법 3: Gradle Wrapper로 자동 Java 다운로드 (선택사항)

`build.gradle`에 자동 다운로드 설정 추가:
```groovy
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
        vendor = JvmVendorSpec.ADOPTIUM
        implementation = JvmImplementation.VENDOR_SPECIFIC
    }
}

// Toolchain 자동 다운로드 활성화
tasks.withType(JavaCompile).configureEach {
    javaCompiler = javaToolchains.compilerFor {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
```

## 현재 상황 확인

프로젝트는 Java 17을 요구합니다:
- `build.gradle`: `languageVersion = JavaLanguageVersion.of(17)`
- Java 8, 11, 21 등 다른 버전은 호환되지 않을 수 있습니다.

## 테스트 실행 명령어

Java가 설치된 후:
```powershell
# 전체 테스트
./gradlew test

# 특정 테스트 클래스
./gradlew test --tests "vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.service.OnboardingServiceTest"

# 특정 테스트 메서드
./gradlew test --tests "OnboardingServiceTest.testStartSession_CreatesNewSession"
```

## 문제 해결 체크리스트

- [ ] Java 17 설치 여부 확인: `java -version`
- [ ] JAVA_HOME 환경 변수 설정 확인: `echo $JAVA_HOME` (Linux/Mac) / `echo %JAVA_HOME%` (Windows)
- [ ] IDE에서 테스트 실행 가능한지 확인
- [ ] Gradle Wrapper 권한 확인: `./gradlew --version`

## 참고

- 프로젝트는 Gradle Wrapper를 사용하므로, `gradlew` 명령어로 자동 Gradle 다운로드가 가능합니다.
- 하지만 Java는 별도로 설치해야 합니다.

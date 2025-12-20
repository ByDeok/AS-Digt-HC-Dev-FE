# 🔧 Java 17 미설치 오류 해결

## 📋 오류 메시지 분석

**핵심 오류:**
```
Cannot find a Java installation on your machine (Windows 11 10.0 amd64) 
matching: {languageVersion=17, vendor=any vendor, implementation=vendor-specific, nativeImageCapable=false}. 
Toolchain download repositories have not been configured.
```

**의미:**
- Java 17이 시스템에 설치되지 않았거나 PATH에 등록되지 않음
- Gradle이 Java 17을 찾지 못해서 프로젝트를 빌드할 수 없음
- 이로 인해 VS Code가 프로젝트를 인식하지 못함

---

## ✅ 해결 방법

### 방법 1: VS Code에서 Java 자동 다운로드 (가장 간단)

1. **Command Palette 열기**
   - `Ctrl+Shift+P`

2. **다음 명령어 실행:**
   ```
   Java: Configure Java Runtime
   ```

3. **JDK 17 다운로드**
   - "Download JDK..." 옵션이 보이면 클릭
   - Java 17 (LTS) 선택
   - 자동으로 다운로드 및 설정됨

4. **프로젝트 다시 로드**
   - `Ctrl+Shift+P` → `Java: Reload Projects`

---

### 방법 2: Gradle 자동 다운로드 설정 (build.gradle 수정)

`build.gradle` 파일에 자동 다운로드 설정을 추가하여 Java 17을 자동으로 받을 수 있도록 설정할 수 있습니다.

---

### 방법 3: 수동으로 Java 17 설치

1. **Eclipse Temurin (Adoptium) 다운로드**
   - https://adoptium.net/temurin/releases/?version=17
   - Windows x64 JDK 17 선택
   - `.msi` 파일 다운로드 및 설치

2. **환경 변수 설정**
   - 설치 후 자동으로 설정되는 경우가 많음

3. **확인**
   ```powershell
   java -version
   ```

---

## 🚀 즉시 시도: VS Code Java Runtime 설정

**가장 빠른 방법:**

1. `Ctrl+Shift+P`
2. `Java: Configure Java Runtime` 입력
3. "Download JDK..." 클릭
4. Java 17 선택
5. 자동 다운로드 완료 대기 (2-5분)
6. `Java: Reload Projects` 실행

---

이 방법을 시도해보세요!

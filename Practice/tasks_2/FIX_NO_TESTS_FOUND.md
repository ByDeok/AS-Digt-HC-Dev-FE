# "테스트할 게 없다" 오류 해결

## 🔍 원인

VS Code가 테스트 클래스를 인식하지 못하는 경우:
1. 클래스가 `public`이 아니어서 인식 안 됨 (수정 완료 ✅)
2. Java Extension이 프로젝트를 제대로 로드하지 못함
3. 테스트 소스 디렉토리가 빌드 경로에 포함되지 않음

## ✅ 해결 방법

### 1. 클래스 접근 제어자 수정 (완료)

`class OnboardingServiceTest` → `public class OnboardingServiceTest`로 변경했습니다.

### 2. 프로젝트 다시 로드

**`Ctrl+Shift+P`** → 다음 명령어 실행:

```
Java: Reload Projects
```

### 3. Java Projects 뷰 확인

1. 좌측 사이드바 **"Java Projects"** 아이콘 클릭
2. 프로젝트 트리에서:
   - `as-digt-hc-dev-fe` 확장
   - `src/test/java` 경로 확인
   - `OnboardingServiceTest` 클래스가 보이는지 확인

### 4. 테스트 디렉토리 확인

Java Projects 뷰에서:
- `src/test/java`가 **테스트 소스로 인식**되어야 함
- 보통 초록색 아이콘이나 "test" 표시가 있어야 함

## 🚀 테스트 실행 확인

수정 후 다음을 확인하세요:

1. **파일 저장**
   - `Ctrl+S`로 파일 저장

2. **Testing 뷰 열기**
   - `Ctrl+Shift+T` 또는 좌측 사이드바 "Testing" 아이콘

3. **테스트 확인**
   - `OnboardingServiceTest`가 목록에 나타나야 함
   - 총 14개의 테스트 메서드가 보여야 함

4. **실행**
   - `OnboardingServiceTest` 옆의 ▶ 버튼 클릭

## 🐛 여전히 문제가 있다면

### Output 패널 확인

1. **`Ctrl+Shift+U`** (Output 패널)
2. 우측 상단 드롭다운에서 선택:
   - "Language Support for Java"
   - "Test Runner for Java"
3. 오류 메시지 확인

### 수동 빌드 시도

터미널에서 (Java가 있다면):
```powershell
./gradlew testClasses
```

이 명령어는 테스트 클래스를 컴파일합니다.

### Java Extension 재시작

1. `Ctrl+Shift+P`
2. `Java: Restart Language Server`

## 📝 확인 체크리스트

- [ ] 클래스가 `public class OnboardingServiceTest`로 변경됨
- [ ] 파일 저장 완료 (`Ctrl+S`)
- [ ] `Java: Reload Projects` 실행
- [ ] Testing 뷰에서 테스트 클래스 확인
- [ ] 테스트 메서드들이 목록에 나타남

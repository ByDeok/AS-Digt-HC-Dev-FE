# 테스트 실행 스크립트
# 사용법: .\scripts\run-tests.ps1

Write-Host "=== OnboardingServiceTest 실행 ===" -ForegroundColor Cyan

# Java 버전 확인
Write-Host "`n1. Java 버전 확인 중..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    if ($javaVersion) {
        Write-Host "✓ Java 발견: $javaVersion" -ForegroundColor Green
    } else {
        Write-Host "✗ Java가 설치되어 있지 않습니다." -ForegroundColor Red
        Write-Host "  IDE에서 테스트를 실행하거나 Java 17을 설치하세요." -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "✗ Java가 PATH에 등록되어 있지 않습니다." -ForegroundColor Red
    Write-Host "  IDE에서 테스트를 실행하거나 Java 17을 설치하세요." -ForegroundColor Yellow
    exit 1
}

# Gradle Wrapper 확인
Write-Host "`n2. Gradle Wrapper 확인 중..." -ForegroundColor Yellow
if (Test-Path "gradlew.bat") {
    Write-Host "✓ Gradle Wrapper 발견" -ForegroundColor Green
} else {
    Write-Host "✗ Gradle Wrapper를 찾을 수 없습니다." -ForegroundColor Red
    exit 1
}

# 테스트 실행
Write-Host "`n3. 테스트 실행 중..." -ForegroundColor Yellow
Write-Host "   테스트 클래스: OnboardingServiceTest" -ForegroundColor Gray

& .\gradlew.bat test --tests "vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.service.OnboardingServiceTest" --console=plain

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n✓ 모든 테스트 통과!" -ForegroundColor Green
} else {
    Write-Host "`n✗ 일부 테스트 실패" -ForegroundColor Red
    Write-Host "  자세한 내용은 위의 출력을 확인하세요." -ForegroundColor Yellow
}

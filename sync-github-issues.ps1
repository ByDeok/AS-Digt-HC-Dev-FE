# GitHub 이슈를 로컬 파일로 동기화하는 스크립트
# 사용법: .\sync-github-issues.ps1

$outputDir = "Tasks\github-issue"
$repo = "ByDeok/AS-Digt-HC-Dev-FE"

# 출력 디렉토리 생성
if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
    Write-Host "Created directory: $outputDir" -ForegroundColor Green
}

# UTF-8 인코딩 설정
$OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "Fetching all issues from GitHub..." -ForegroundColor Cyan

# 이슈 번호 목록 가져오기 (간단한 방법)
$issueList = gh issue list --repo $repo --limit 100 --json number | ConvertFrom-Json

if ($null -eq $issueList -or $issueList.Count -eq 0) {
    Write-Host "No issues found." -ForegroundColor Yellow
    exit 0
}

Write-Host "Found $($issueList.Count) issues. Processing..." -ForegroundColor Cyan

foreach ($issue in $issueList) {
    $issueNumber = $issue.number
    
    Write-Host "Processing Issue #${issueNumber}..." -ForegroundColor Gray
    
    try {
        # 이슈 상세 정보를 JSON으로 가져오기
        $issueJson = gh issue view $issueNumber --repo $repo --json number,title,body,labels,state,createdAt,updatedAt
        $issueDetail = $issueJson | ConvertFrom-Json
        
        $title = $issueDetail.title
        $body = $issueDetail.body
        
        if ([string]::IsNullOrWhiteSpace($body)) {
            Write-Host "  ⚠ Skipped: Empty body" -ForegroundColor Yellow
            continue
        }
        
        # 파일명 생성 (제목에서 특수문자 제거 및 공백을 하이픈으로 변경)
        $safeTitle = $title -replace '[<>:"/\\|?*\[\]]', '' -replace '\s+', '-' -replace '-+', '-'
        $safeTitle = $safeTitle.Trim('-')
        $fileName = "issue-${issueNumber}-${safeTitle}.md"
        $filePath = Join-Path $outputDir $fileName
        
        # 파일 저장 (UTF-8, BOM 없이)
        [System.IO.File]::WriteAllText($filePath, $body, [System.Text.UTF8Encoding]::new($false))
        
        Write-Host "  ✓ Saved: $fileName" -ForegroundColor Green
    }
    catch {
        Write-Host "  ✗ Error processing issue #${issueNumber}: $_" -ForegroundColor Red
    }
}

Write-Host "`nDone! All issues have been synced to $outputDir" -ForegroundColor Green


<# 
    Create GitHub issues from markdown task files under studio/Tasks using gh CLI.
    - Extracts title from the first markdown heading line.
    - Uses the rest of the file as the issue body.
    - Skips creation if an issue with the same title already exists (any state).
    - Adds the "Issue Automation" label to created issues.

    This script is designed to run from the workspace root, regardless of whether
    it is itself a git repository. It infers the GitHub repo from the nested
    studio project (.git/config) and passes it explicitly to gh via --repo.
#>

# Force UTF-8 encoding for console output to handle non-ASCII characters (e.g., Korean) correctly
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

# Determine script directory so paths work regardless of current working directory.
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

# Resolve studio repo directory and tasks directory.
$studioDir = Join-Path $scriptDir "studio"
$tasksDir = Join-Path $studioDir "Tasks"

if (-not (Test-Path $tasksDir)) {
    Write-Error "Tasks directory not found: $tasksDir"
    exit 1
}

# Infer GitHub repo (owner/name) from studio/.git/config if available,
# otherwise default to "ByDeok/AS-Digt-HC-Dev-FE".
$gitConfigPath = Join-Path $studioDir ".git\config"
$repo = "ByDeok/AS-Digt-HC-Dev-FE"

if (Test-Path $gitConfigPath) {
    $gitConfigContent = Get-Content -Path $gitConfigPath -Raw
    if ($gitConfigContent -match "url\s*=\s*https://github\.com/([^/]+/[^\.]+)") {
        $repo = $matches[1]
    }
} else {
    Write-Warning "Git config not found at $gitConfigPath. Defaulting to repo: $repo"
}

if (-not $repo) {
    Write-Error "Could not determine GitHub repo."
    exit 1
}

Write-Host "Using GitHub repo: $repo" -ForegroundColor Cyan
Write-Host "Scanning task files in: $tasksDir" -ForegroundColor Cyan

# Ensure the automation label exists in the target repo.
$automationLabel = "Issue Automation"
$labelsJson = gh label list --repo $repo --json name --limit 200
$existingLabelNames = @()
if ($labelsJson) {
    $labels = $labelsJson | ConvertFrom-Json
    if ($labels) {
        if ($labels -is [array]) {
            $existingLabelNames = $labels.name
        } else {
            $existingLabelNames = @($labels.name)
        }
    }
}

if (-not ($existingLabelNames -contains $automationLabel)) {
    Write-Host "Creating label '$automationLabel' in $repo" -ForegroundColor Cyan
    gh label create --repo $repo $automationLabel --color "5319e7" --description "Issues created from studio/Tasks automation"
}

$files = Get-ChildItem -Path $tasksDir -Filter "*.md"

foreach ($file in $files) {
    $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8
    $lines = $content -split "\r?\n"

    if ($lines.Count -eq 0 -or [string]::IsNullOrWhiteSpace($lines[0])) {
        Write-Warning "Skipping file with empty or missing title line: $($file.Name)"
        continue
    }

    # Extract Title (remove leading '# ' and trim)
    $title = $lines[0] -replace "^#\s*", ""
    $title = $title.Trim()

    # Extract Body (rest of the file)
    if ($lines.Count -gt 1) {
        $body = $lines[1..($lines.Count - 1)] -join "`n"
    } else {
        $body = ""
    }

    Write-Host "Processing: $title"

    # Check for existing issues by exact title match in the target repo (all states)
    $existingJson = gh issue list --repo $repo --search "$title" --json title --state all
    $existing = $null
    if ($existingJson) {
        $existing = $existingJson | ConvertFrom-Json
    }

    $titles = @()
    if ($existing) {
        if ($existing -is [array]) {
            $titles = $existing.title
        } else {
            $titles = @($existing.title)
        }
    }

    if ($titles -and $titles -contains $title) {
        Write-Host "  Skipping: Issue already exists." -ForegroundColor Yellow
    } else {
        Write-Host "  Creating issue..." -ForegroundColor Green

        # Create issue using a temporary file for body to avoid shell escaping issues
        $tempBodyFile = [System.IO.Path]::GetTempFileName()
        $body | Set-Content -Path $tempBodyFile -Encoding UTF8

        gh issue create --repo $repo --title "$title" --body-file "$tempBodyFile" --label "$automationLabel"

        Remove-Item -Path $tempBodyFile -ErrorAction SilentlyContinue
    }
}
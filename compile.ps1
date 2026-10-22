# =========================================================================
#  BillWise — PowerShell Build Script
# =========================================================================

if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path", "Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path", "User")
}

if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    $javaHome = [System.Environment]::GetEnvironmentVariable("JAVA_HOME", "Machine")
    if ($javaHome -and (Test-Path "$javaHome\bin\javac.exe")) {
        $env:Path = "$javaHome\bin;" + $env:Path
    }
}

Write-Host "`n[BillWise] Checking for Java compiler (javac)..." -ForegroundColor Cyan

if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    Write-Host "[ERROR] 'javac' compiler was not found in PATH." -ForegroundColor Red
    Write-Host "Install JDK 21 LTS using: winget install EclipseAdoptium.Temurin.21.JDK" -ForegroundColor Yellow
    exit 1
}

$outputDir = Join-Path $PSScriptRoot "bin"
if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
}

Write-Host "[BillWise] Compiling Java source files..." -ForegroundColor Cyan
javac -d "$outputDir" -sourcepath "$PSScriptRoot\src" "$PSScriptRoot\src\com\billwise\*.java"

if ($LASTEXITCODE -eq 0) {
    Write-Host "[BillWise] SUCCESS: Compilation completed successfully in 'bin/'!" -ForegroundColor Green
} else {
    Write-Host "[ERROR] Compilation failed." -ForegroundColor Red
}

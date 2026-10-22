# =========================================================================
#  BillWise — PowerShell GUI Launcher
# =========================================================================

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path", "Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path", "User")
}

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    $javaHome = [System.Environment]::GetEnvironmentVariable("JAVA_HOME", "Machine")
    if ($javaHome -and (Test-Path "$javaHome\bin\java.exe")) {
        $env:Path = "$javaHome\bin;" + $env:Path
    }
}

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "[ERROR] 'java' runtime was not found in PATH." -ForegroundColor Red
    Write-Host "Install JDK 21 LTS using: winget install EclipseAdoptium.Temurin.21.JDK" -ForegroundColor Yellow
    exit 1
}

$binDir = Join-Path $PSScriptRoot "bin"
Write-Host "[BillWise] Compiling latest Java source files..." -ForegroundColor Cyan
& "$PSScriptRoot\compile.ps1"
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed." -ForegroundColor Red
    exit 1
}

Write-Host "[BillWise] Launching Swing GUI..." -ForegroundColor Green
Start-Process javaw -ArgumentList "-cp", "`"$binDir`"", "com.billwise.Main"

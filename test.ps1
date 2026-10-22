# =========================================================================
#  BillWise — PowerShell Test Runner
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
if (-not (Test-Path "$binDir\com\billwise\Main.class")) {
    Write-Host "[BillWise] Class files missing. Compiling first..." -ForegroundColor Cyan
    & "$PSScriptRoot\compile.ps1"
}

Write-Host "[BillWise] Running automated tariff verification tests...`n" -ForegroundColor Green
java -cp "$binDir" com.billwise.Main --test

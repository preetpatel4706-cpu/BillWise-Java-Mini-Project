@echo off
setlocal
REM =========================================================================
REM  BillWise — Compilation Script for Windows
REM =========================================================================

echo.
echo [BillWise] Checking for Java Development Kit (javac)...

where javac >nul 2>nul
if %errorlevel% equ 0 goto :has_javac

REM Check JAVA_HOME
if not defined JAVA_HOME goto :check_adoptium
if exist "%JAVA_HOME%\bin\javac.exe" (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
    goto :has_javac
)

:check_adoptium
for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-*") do (
    if exist "%%D\bin\javac.exe" (
        set "PATH=%%D\bin;%PATH%"
        goto :has_javac
    )
)

:check_oracle
for /d %%D in ("C:\Program Files\Java\jdk-*") do (
    if exist "%%D\bin\javac.exe" (
        set "PATH=%%D\bin;%PATH%"
        goto :has_javac
    )
)

echo.
echo [ERROR] 'javac' compiler was not found in your system PATH!
echo Please ensure Java Development Kit (JDK 17 or JDK 21 LTS) is installed.
echo You can install it using Windows Terminal:
echo     winget install EclipseAdoptium.Temurin.21.JDK
echo.
exit /b 1

:has_javac
echo [BillWise] Java compiler found!
echo [BillWise] Creating 'bin' output directory...
if not exist "bin" mkdir "bin"

echo [BillWise] Compiling Java source files...
javac -d bin -sourcepath src src/com/billwise/*.java

if %errorlevel% equ 0 (
    echo [BillWise] SUCCESS: Compilation completed without errors!
    echo Compiled class files are placed in the 'bin/' folder.
    echo.
    echo Next step: Run 'run.bat' to launch the GUI or 'test.bat' to run verification tests.
) else (
    echo.
    echo [ERROR] Compilation failed. Please inspect the error messages above.
)

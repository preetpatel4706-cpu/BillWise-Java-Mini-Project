@echo off
setlocal
REM =========================================================================
REM  BillWise — GUI Launcher Script for Windows
REM =========================================================================

echo.
echo [BillWise] Checking for Java Runtime Environment (java)...

where java >nul 2>nul
if %errorlevel% equ 0 goto :has_java

REM Check JAVA_HOME
if not defined JAVA_HOME goto :check_adoptium
if exist "%JAVA_HOME%\bin\java.exe" (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
    goto :has_java
)

:check_adoptium
for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-*") do (
    if exist "%%D\bin\java.exe" (
        set "PATH=%%D\bin;%PATH%"
        goto :has_java
    )
)

:check_oracle
for /d %%D in ("C:\Program Files\Java\jdk-*") do (
    if exist "%%D\bin\java.exe" (
        set "PATH=%%D\bin;%PATH%"
        goto :has_java
    )
)

echo.
echo [ERROR] 'java' was not found in your system PATH!
echo Please install a JDK (e.g. Eclipse Temurin 21) or add Java to your PATH.
echo.
exit /b 1

:has_java
echo [BillWise] Java runtime found!
echo [BillWise] Compiling latest Java source files...
if not exist "bin" mkdir "bin"
javac -d bin -sourcepath src src/com/billwise/*.java
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed. Please inspect the error messages above.
    pause
    exit /b 1
)

echo [BillWise] Launching Desktop GUI Application...
start "BillWise" javaw -cp bin com.billwise.Main

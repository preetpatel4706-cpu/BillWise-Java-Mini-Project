@echo off
setlocal
REM =========================================================================
REM  BillWise — Automated Test Verification Runner
REM =========================================================================

echo.
echo [BillWise] Checking for Java Runtime (java)...

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

if not exist "bin\com\billwise\Main.class" (
    echo [BillWise] Compiled classes not found. Running compilation first...
    javac -d bin -sourcepath src src/com/billwise/*.java
)

echo [BillWise] Running Automated Calculation Verification Suite...
echo.
java -cp bin com.billwise.Main --test
echo.

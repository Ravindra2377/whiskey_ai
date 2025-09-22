@echo off
echo Starting WHISKEY AI System...
echo =============================

REM Check if Maven is installed
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Maven is not installed or not in PATH
    echo Please install Maven and try again
    pause
    exit /b 1
)

REM Build and run the WHISKEY service
echo Building WHISKEY service...
mvn clean package

if %errorlevel% neq 0 (
    echo Error: Failed to build WHISKEY service
    pause
    exit /b 1
)

echo Starting WHISKEY service...
java -jar target/nexus-1.0.0.jar

if %errorlevel% neq 0 (
    echo Error: Failed to start WHISKEY service
    pause
    exit /b 1
)

echo WHISKEY service started successfully!
pause
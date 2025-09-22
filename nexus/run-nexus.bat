@echo off
echo Starting NEXUS AI System...
echo =============================

REM Check if Maven is installed
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Maven is not installed or not in PATH
    echo Please install Maven and try again
    pause
    exit /b 1
)

REM Build and run the NEXUS service
echo Building NEXUS service...
mvn clean package

if %errorlevel% neq 0 (
    echo Error: Failed to build NEXUS service
    pause
    exit /b 1
)

echo Starting NEXUS service...
java -jar target/nexus-1.0.0.jar

if %errorlevel% neq 0 (
    echo Error: Failed to start NEXUS service
    pause
    exit /b 1
)

echo NEXUS service started successfully!
pause
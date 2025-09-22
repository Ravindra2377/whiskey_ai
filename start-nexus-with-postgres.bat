@echo off
echo ===============================================
echo Starting NEXUS AI with PostgreSQL
echo ===============================================

echo Checking if PostgreSQL is running...
netstat -ano | findstr :5432 >nul
if %errorlevel% == 0 (
    echo PostgreSQL is running
) else (
    echo PostgreSQL is not running!
    echo Please start PostgreSQL before running NEXUS
    echo.
    echo Instructions:
    echo 1. Start PostgreSQL service from Services.msc
    echo 2. Or start PostgreSQL from the installation directory
    echo.
    pause
    exit /b 1
)

echo Starting NEXUS AI system...
cd nexus
call mvnw spring-boot:run

echo NEXUS AI has stopped
pause
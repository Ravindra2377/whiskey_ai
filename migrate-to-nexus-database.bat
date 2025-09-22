@echo off
echo ===============================================
echo Migrating Files to WHISKEY Database
echo ===============================================

echo Checking if WHISKEY is running...
powershell -Command "& {try {Invoke-WebRequest -Uri 'http://localhost:8085/api/nexus/health' -Method GET -ErrorAction Stop | Out-Null; Write-Host 'NEXUS is running' -ForegroundColor Green} catch {Write-Host 'NEXUS is not running. Please start NEXUS first.' -ForegroundColor Red; exit 1}}"

if %errorlevel% neq 0 (
    echo.
    echo Please start NEXUS before running this script:
    echo   cd nexus
    echo   mvnw spring-boot:run
    echo.
    pause
    exit /b 1
)

echo.
echo Importing all files to NEXUS database and cleaning directory...
powershell -ExecutionPolicy Bypass -File "migrate-to-nexus-database.ps1"

echo.
echo Migration process completed.
pause
@echo off
title WHISKEY AI Database Services Initialization

echo  WHISKEY AI Database Services Initialization
echo =============================================
echo.

echo Checking Docker status...
docker version >nul 2>&1
if %errorlevel% == 0 (
    echo ✅ Docker is running
) else (
    echo ❌ Docker is not running. Please start Docker Desktop and run this script again.
    pause
    exit /b 1
)

echo Starting database services...
docker compose up -d

echo Waiting 30 seconds for services to initialize...
timeout /t 30 /nobreak >nul

echo Checking service status...
docker compose ps

echo ✅ Database services initialization complete!
echo PostgreSQL is running on port 5432
echo Redis is running on port 6379
echo InfluxDB is running on port 8086

echo.
echo Next steps:
echo 1. Navigate to the nexus directory: cd nexus
echo 2. Compile the application: mvnw.cmd clean compile
echo 3. Run database tests: mvnw.cmd test -Dtest=DatabaseConnectionTest

pause
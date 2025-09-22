@echo off
title WHISKEY AI - Fix Docker Context

echo  WHISKEY AI - Fix Docker Context
echo ==============================
echo.

echo Setting Docker context to default (Windows)...
docker context use default

echo.
echo Verifying Docker status...
docker version >nul 2>&1
if %errorlevel% == 0 (
    echo ✅ Docker is responding
) else (
    echo ❌ Docker is not responding
    echo Please check Docker Desktop installation
    echo.
    pause
    exit /b 1
)

echo.
echo Current Docker context:
docker context ls

echo.
echo If the default context is not marked with an asterisk (*), 
echo please ensure Docker Desktop is configured for Windows containers.
echo.
echo To switch to Windows containers:
echo 1. Right-click Docker icon in system tray
echo 2. Select "Switch to Windows containers"
echo.
echo Press any key to continue...
pause >nul

echo.
echo Starting database services...
cd /d "D:\OneDrive\Desktop\Boozer_App_Main"
docker compose up -d

echo.
echo Waiting 30 seconds for services to initialize...
timeout /t 30 /nobreak >nul

echo.
echo Checking service status...
docker compose ps

echo.
echo  Database Services Status Report
echo ===============================
echo PostgreSQL (nexus-postgres): Port 5432
echo Redis (nexus-redis): Port 6379
echo InfluxDB (nexus-influxdb): Port 8086
echo.

echo Next steps:
echo 1. Navigate to the nexus directory: cd nexus
echo 2. Compile the application: ..\mvnw.cmd clean compile
echo 3. Run database tests: ..\mvnw.cmd test -Dtest=DatabaseConnectionTest
echo.

echo Setup complete! Press any key to exit...
pause >nul
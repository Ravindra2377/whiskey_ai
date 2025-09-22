@echo off
title WHISKEY AI - Start Docker and Database Services

echo  WHISKEY AI - Start Docker and Database Services
echo =================================================
echo.

echo This script will:
echo 1. Start Docker Desktop with administrator privileges
echo 2. Wait for Docker to initialize
echo 3. Start database services
echo.

echo Please follow these steps:
echo.
echo 1. When prompted, click "Yes" to allow administrator access
echo 2. Wait for Docker Desktop to fully start (system tray icon)
echo 3. Press any key to continue after Docker is running
echo.

pause

echo Starting Docker Desktop...
powershell -Command "Start-Process 'C:\Program Files\Docker\Docker\Docker Desktop.exe' -Verb RunAs"

echo.
echo Please wait for Docker Desktop to start completely.
echo Look for the Docker icon in your system tray (bottom right).
echo It should stop animating when fully initialized.
echo.
echo Press any key once Docker Desktop is running...
pause

echo Starting database services...
cd /d "D:\OneDrive\Desktop\Boozer_App_Main"
docker compose up -d

echo.
echo Waiting 30 seconds for services to initialize...
timeout /t 30 /nobreak >nul

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
echo 2. Compile the application: mvnw.cmd clean compile
echo 3. Run database tests: mvnw.cmd test -Dtest=DatabaseConnectionTest
echo.

echo Setup complete! Press any key to exit...
pause
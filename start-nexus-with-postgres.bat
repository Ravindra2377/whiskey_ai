@echo off
echo ===============================================
echo 🚀 Starting NEXUS AI with PostgreSQL
echo ===============================================

echo 📋 Checking if PostgreSQL is running...
netstat -ano | findstr :5432 >nul
if %errorlevel% == 0 (
    echo ✅ PostgreSQL is running
) else (
    echo ❌ PostgreSQL is not running!
    echo Please start PostgreSQL before running NEXUS
    echo.
    echo Instructions:
    echo 1. Start PostgreSQL service from Services.msc
    echo 2. Or start PostgreSQL from the installation directory
    echo.
    pause
    exit /b 1
)

echo.
echo 🔧 Setting environment variables for database connection...
set SPRING_PROFILES_ACTIVE=prod
set DB_HOST=localhost
set DB_PORT=5432
set DB_NAME=nexus_ai
set DB_USER=nexus_admin
set DB_PASSWORD=SecurePassword123!
set REDIS_HOST=localhost
set REDIS_PORT=6379
set INFLUXDB_URL=http://localhost:8086

echo ✅ Environment configured for production database settings
echo.

echo 🔍 Running database service check...
python check_database_services.py
echo.

echo 🚀 Starting NEXUS application...
echo    API will be available at: http://localhost:8094
echo    Health check: http://localhost:8094/actuator/health
echo.

cd nexus
mvnw.cmd spring-boot:run

pause

echo Starting NEXUS AI system...
cd nexus
call mvnw spring-boot:run

echo NEXUS AI has stopped
pause
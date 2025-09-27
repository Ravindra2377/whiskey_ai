@echo off
REM NEXUS AI Production Deployment Script for Windows
echo 🚀 Starting NEXUS AI Production Deployment...

REM Set production environment
set SPRING_PROFILES_ACTIVE=prod
set NODE_ENV=production

echo 📦 Building NEXUS AI Platform...

REM Create directories
if not exist logs mkdir logs
if not exist monitoring\grafana\dashboards mkdir monitoring\grafana\dashboards
if not exist monitoring\grafana\datasources mkdir monitoring\grafana\datasources

echo 🐳 Starting with Docker Compose...

REM Stop any existing containers
docker-compose -f docker-compose.production.yml down

REM Check if Docker is running
docker version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

REM Build and start services (excluding backend temporarily due to compilation issues)
echo 🔧 Starting infrastructure services...
docker-compose -f docker-compose.production.yml up postgres redis nginx prometheus grafana -d

echo ⏳ Waiting for services to be ready...
timeout /t 30 /nobreak >nul

echo 🏥 Performing health checks...

REM Check if PostgreSQL is ready
docker-compose -f docker-compose.production.yml exec -T postgres pg_isready -U nexus_admin -d nexus_ai_prod >nul 2>&1
if errorlevel 1 (
    echo ❌ PostgreSQL health check failed
) else (
    echo ✅ PostgreSQL is ready
)

REM Check if Redis is ready
docker-compose -f docker-compose.production.yml exec -T redis redis-cli --no-auth-warning -a RedisQuantumCache2025! ping >nul 2>&1
if errorlevel 1 (
    echo ❌ Redis health check failed
) else (
    echo ✅ Redis is ready
)

echo 📊 Deployment Status:
docker-compose -f docker-compose.production.yml ps

echo.
echo 🌐 Access URLs:
echo Frontend: http://localhost:3000 (when built)
echo Backend API: http://localhost:8080 (compilation in progress)
echo Nginx Proxy: http://localhost:80
echo PostgreSQL: localhost:5432
echo Redis: localhost:6379
echo Prometheus: http://localhost:9090
echo Grafana: http://localhost:3001 (admin/NexusGrafana2025!)

echo.
echo 🎉 NEXUS AI Platform infrastructure deployed!
echo 🔧 Note: Backend compilation needs to be fixed. Infrastructure is ready.
echo 💡 Next steps:
echo    1. Fix Java compilation issues
echo    2. Build and deploy backend service
echo    3. Build and deploy frontend application
echo    4. Run full integration tests

pause
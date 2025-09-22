# NEXUS AI - Start Docker and Database Services
Write-Host " NEXUS AI - Start Docker and Database Services" -ForegroundColor Green
Write-Host "=================================================" -ForegroundColor Green
Write-Host ""

Write-Host "This script will:" -ForegroundColor Yellow
Write-Host "1. Start Docker Desktop with administrator privileges" -ForegroundColor White
Write-Host "2. Wait for Docker to initialize" -ForegroundColor White
Write-Host "3. Start database services" -ForegroundColor White
Write-Host ""

Write-Host "Please follow these steps:" -ForegroundColor Yellow
Write-Host "1. When prompted, click 'Yes' to allow administrator access" -ForegroundColor White
Write-Host "2. Wait for Docker Desktop to fully start (system tray icon)" -ForegroundColor White
Write-Host "3. Press Enter to continue after Docker is running" -ForegroundColor White
Write-Host ""

Write-Host "Press Enter to start Docker Desktop..." -ForegroundColor Cyan
Read-Host

Write-Host "Starting Docker Desktop..." -ForegroundColor Yellow
Start-Process "C:\Program Files\Docker\Docker\Docker Desktop.exe" -Verb RunAs

Write-Host ""
Write-Host "Please wait for Docker Desktop to start completely." -ForegroundColor Yellow
Write-Host "Look for the Docker icon in your system tray (bottom right)." -ForegroundColor White
Write-Host "It should stop animating when fully initialized." -ForegroundColor White
Write-Host ""
Write-Host "Press Enter once Docker Desktop is running..." -ForegroundColor Cyan
Read-Host

Write-Host "Starting database services..." -ForegroundColor Yellow
Set-Location -Path "D:\OneDrive\Desktop\Boozer_App_Main"
docker compose up -d

Write-Host ""
Write-Host "Waiting 30 seconds for services to initialize..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

Write-Host "Checking service status..." -ForegroundColor Yellow
docker compose ps

Write-Host ""
Write-Host " Database Services Status Report" -ForegroundColor Green
Write-Host "===============================" -ForegroundColor Green
Write-Host "PostgreSQL (nexus-postgres): Port 5432" -ForegroundColor White
Write-Host "Redis (nexus-redis): Port 6379" -ForegroundColor White
Write-Host "InfluxDB (nexus-influxdb): Port 8086" -ForegroundColor White
Write-Host ""

Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Navigate to the nexus directory: cd nexus" -ForegroundColor White
Write-Host "2. Compile the application: .\mvnw.cmd clean compile" -ForegroundColor White
Write-Host "3. Run database tests: .\mvnw.cmd test -Dtest=DatabaseConnectionTest" -ForegroundColor White
Write-Host ""

Write-Host "Setup complete! Press Enter to exit..." -ForegroundColor Green
Read-Host
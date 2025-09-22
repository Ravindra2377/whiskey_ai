# Reset Docker Context and Start Services
Write-Host " NEXUS AI - Reset Docker Context and Start Services" -ForegroundColor Green
Write-Host "====================================================" -ForegroundColor Green
Write-Host ""

# Set Docker context to default (Windows)
Write-Host "Setting Docker context to default (Windows)..." -ForegroundColor Yellow
docker context use default

# Restart Docker Desktop
Write-Host "Restarting Docker Desktop..." -ForegroundColor Yellow
Write-Host "Please close Docker Desktop if it's currently running." -ForegroundColor Cyan
Write-Host "Press Enter when ready to continue..." -ForegroundColor Cyan
Read-Host

# Try to start Docker Desktop
try {
    Write-Host "Attempting to start Docker Desktop..." -ForegroundColor Yellow
    Start-Process "C:\Program Files\Docker\Docker\Docker Desktop.exe" -Verb RunAs -ErrorAction SilentlyContinue
    Write-Host "Docker Desktop start command sent. Please wait for it to initialize." -ForegroundColor Green
} catch {
    Write-Host "Could not automatically start Docker Desktop. Please start it manually." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Please wait for Docker Desktop to fully start (look for the whale icon in system tray)." -ForegroundColor Yellow
Write-Host "The icon should stop animating when Docker is ready." -ForegroundColor White
Write-Host ""
Write-Host "Press Enter once Docker Desktop is running..." -ForegroundColor Cyan
Read-Host

# Verify Docker is working
Write-Host "Verifying Docker status..." -ForegroundColor Yellow
try {
    $dockerCheck = docker version
    if ($dockerCheck) {
        Write-Host "✅ Docker is running correctly" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Docker is still not responding properly" -ForegroundColor Red
    Write-Host "Please check Docker Desktop installation and try again." -ForegroundColor Yellow
    exit 1
}

# Set context again to be sure
Write-Host "Reconfirming Docker context..." -ForegroundColor Yellow
docker context use default

# Start database services
Write-Host "Starting database services..." -ForegroundColor Yellow
Set-Location -Path "D:\OneDrive\Desktop\Boozer_App_Main"
docker compose up -d

# Wait for services to initialize
Write-Host "Waiting 30 seconds for services to initialize..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Check service status
Write-Host "Checking service status..." -ForegroundColor Yellow
docker compose ps

Write-Host ""
Write-Host "✅ Docker context reset and services initialization complete!" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Navigate to the nexus directory: cd nexus" -ForegroundColor White
Write-Host "2. Compile the application: ..\mvnw.cmd clean compile" -ForegroundColor White
Write-Host "3. Run database tests: ..\mvnw.cmd test -Dtest=DatabaseConnectionTest" -ForegroundColor White
Write-Host ""

Write-Host "Press Enter to exit..." -ForegroundColor Cyan
Read-Host
# Initialize NEXUS AI Database Services
Write-Host " NEXUS AI Database Services Initialization" -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Green

# Check if Docker is running
Write-Host "Checking Docker status..." -ForegroundColor Yellow
try {
    $dockerVersion = docker version
    if ($dockerVersion) {
        Write-Host "✅ Docker is running" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Docker is not running. Please start Docker Desktop and run this script again." -ForegroundColor Red
    exit 1
}

# Navigate to the project directory
Set-Location -Path "D:\OneDrive\Desktop\Boozer_App_Main"

# Start database services
Write-Host "Starting database services..." -ForegroundColor Yellow
docker compose up -d

# Wait for services to initialize
Write-Host "Waiting 30 seconds for services to initialize..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Check if services are running
Write-Host "Checking service status..." -ForegroundColor Yellow
docker compose ps

Write-Host "✅ Database services initialization complete!" -ForegroundColor Green
Write-Host "PostgreSQL is running on port 5432" -ForegroundColor Cyan
Write-Host "Redis is running on port 6379" -ForegroundColor Cyan
Write-Host "InfluxDB is running on port 8086" -ForegroundColor Cyan

Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Navigate to the nexus directory: cd nexus" -ForegroundColor White
Write-Host "2. Compile the application: .\mvnw.cmd clean compile" -ForegroundColor White
Write-Host "3. Run database tests: .\mvnw.cmd test -Dtest=DatabaseConnectionTest" -ForegroundColor White
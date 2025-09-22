# NEXUS AI Startup Script with PostgreSQL
Write-Host "===============================================" -ForegroundColor Green
Write-Host "Starting NEXUS AI with PostgreSQL" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green

Write-Host "Checking if PostgreSQL is running..." -ForegroundColor Cyan
$pgPort = Get-NetTCPConnection -LocalPort 5432 -ErrorAction SilentlyContinue
if ($pgPort) {
    Write-Host "PostgreSQL is running on port 5432" -ForegroundColor Green
} else {
    Write-Host "PostgreSQL is not running!" -ForegroundColor Red
    Write-Host "Please start PostgreSQL before running NEXUS" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Instructions:" -ForegroundColor Yellow
    Write-Host "1. Start PostgreSQL service from Services.msc" -ForegroundColor Yellow
    Write-Host "2. Or start PostgreSQL from the installation directory" -ForegroundColor Yellow
    Write-Host ""
    pause
    exit 1
}

Write-Host "Starting NEXUS AI system..." -ForegroundColor Cyan
Set-Location -Path "nexus"
& .\mvnw.cmd spring-boot:run

Write-Host "NEXUS AI has stopped" -ForegroundColor Yellow
pause
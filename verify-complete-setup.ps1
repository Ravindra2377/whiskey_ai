# WHISKEY AI - Complete Setup Verification
Write-Host " WHISKEY AI - Complete Setup Verification" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Check Docker status
Write-Host "1. Checking Docker Status..." -ForegroundColor Yellow
try {
    $dockerVersion = docker version
    if ($dockerVersion) {
        Write-Host "   ✅ Docker is running" -ForegroundColor Green
    }
} catch {
    Write-Host "   ❌ Docker is not running. Please start Docker Desktop." -ForegroundColor Red
    exit 1
}

# Check database services
Write-Host "2. Checking Database Services..." -ForegroundColor Yellow
try {
    $services = docker compose ps
    if ($services -like "*whiskey-postgres*Up*" -and $services -like "*whiskey-redis*Up*" -and $services -like "*whiskey-influxdb*Up*") {
        Write-Host "   ✅ All database services are running" -ForegroundColor Green
    } else {
        Write-Host "   ⚠️  Some database services may not be running properly" -ForegroundColor Yellow
        Write-Host "   Services status:" -ForegroundColor Yellow
        Write-Host $services -ForegroundColor White
    }
} catch {
    Write-Host "   ❌ Could not check database services" -ForegroundColor Red
}

# Verify database connections
Write-Host "3. Verifying Database Connections..." -ForegroundColor Yellow

# PostgreSQL verification
try {
    $postgresCheck = docker exec whiskey-postgres pg_isready -U whiskey_admin -d whiskey_ai 2>$null
    if ($postgresCheck -like "*accepting connections*") {
        Write-Host "   ✅ PostgreSQL is accepting connections" -ForegroundColor Green
    } else {
        Write-Host "   ⚠️  PostgreSQL connection check returned: $postgresCheck" -ForegroundColor Yellow
    }
} catch {
    Write-Host "   ⚠️  Could not verify PostgreSQL connection" -ForegroundColor Yellow
}

# Redis verification
try {
    $redisCheck = docker exec whiskey-redis redis-cli ping 2>$null
    if ($redisCheck -eq "PONG") {
        Write-Host "   ✅ Redis is responding" -ForegroundColor Green
    } else {
        Write-Host "   ⚠️  Redis check returned: $redisCheck" -ForegroundColor Yellow
    }
} catch {
    Write-Host "   ⚠️  Could not verify Redis connection" -ForegroundColor Yellow
}

# InfluxDB verification
try {
    $influxCheck = docker exec whiskey-influxdb influx ping 2>$null
    if ($influxCheck -like "*OK*") {
        Write-Host "   ✅ InfluxDB is responding" -ForegroundColor Green
    } else {
        Write-Host "   ⚠️  InfluxDB check returned: $influxCheck" -ForegroundColor Yellow
    }
} catch {
    Write-Host "   ⚠️  Could not verify InfluxDB connection" -ForegroundColor Yellow
}

# Check Java application compilation
Write-Host "4. Checking Java Application..." -ForegroundColor Yellow
Set-Location -Path "D:\OneDrive\Desktop\Boozer_App_Main\whiskey"

try {
    # Just check if mvnw.cmd exists
    if (Test-Path "mvnw.cmd") {
        Write-Host "   ✅ Maven wrapper found" -ForegroundColor Green
    } else {
        Write-Host "   ❌ Maven wrapper not found" -ForegroundColor Red
    }
} catch {
    Write-Host "   ⚠️  Could not check Java application files" -ForegroundColor Yellow
}

Write-Host ""
Write-Host " Verification Complete!" -ForegroundColor Green
Write-Host "======================" -ForegroundColor Green

Write-Host "Next steps if all checks passed:" -ForegroundColor Yellow
Write-Host "1. Compile the application: .\mvnw.cmd clean compile" -ForegroundColor White
Write-Host "2. Run database tests: .\mvnw.cmd test -Dtest=DatabaseConnectionTest" -ForegroundColor White
Write-Host "3. Start the application: .\mvnw.cmd spring-boot:run" -ForegroundColor White

Write-Host ""
Write-Host "Press Enter to exit..." -ForegroundColor Cyan
Read-Host
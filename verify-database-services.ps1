# Verify WHISKEY AI Database Services
Write-Host " WHISKEY AI Database Services Verification" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green

# Check if Docker is running
Write-Host "Checking Docker status..." -ForegroundColor Yellow
try {
    $dockerVersion = docker version
    if ($dockerVersion) {
        Write-Host "✅ Docker is running" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Docker is not running. Please start Docker Desktop." -ForegroundColor Red
    exit 1
}

# Check if services are running
Write-Host "Checking service status..." -ForegroundColor Yellow
docker compose ps

# Verify PostgreSQL connection
Write-Host "Verifying PostgreSQL connection..." -ForegroundColor Yellow
try {
    $postgresCheck = docker exec whiskey-postgres pg_isready -U whiskey_admin -d whiskey_ai
    if ($postgresCheck -like "*accepting connections*") {
        Write-Host "✅ PostgreSQL is accepting connections" -ForegroundColor Green
    } else {
        Write-Host "⚠️  PostgreSQL may not be ready yet" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Could not verify PostgreSQL connection" -ForegroundColor Red
}

# Verify Redis connection
Write-Host "Verifying Redis connection..." -ForegroundColor Yellow
try {
    $redisCheck = docker exec whiskey-redis redis-cli ping
    if ($redisCheck -eq "PONG") {
        Write-Host "✅ Redis is responding" -ForegroundColor Green
    } else {
        Write-Host "⚠️  Redis may not be ready yet" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Could not verify Redis connection" -ForegroundColor Red
}

# Verify InfluxDB connection
Write-Host "Verifying InfluxDB connection..." -ForegroundColor Yellow
try {
    $influxCheck = docker exec whiskey-influxdb influx ping
    if ($influxCheck -like "*OK*") {
        Write-Host "✅ InfluxDB is responding" -ForegroundColor Green
    } else {
        Write-Host "⚠️  InfluxDB may not be ready yet" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Could not verify InfluxDB connection" -ForegroundColor Red
}

Write-Host ""
Write-Host "Verification complete!" -ForegroundColor Green
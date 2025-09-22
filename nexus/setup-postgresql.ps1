# Setup PostgreSQL for WHISKEY AI System

Write-Host "Setting up PostgreSQL database for WHISKEY AI System..." -ForegroundColor Cyan

# Check if PostgreSQL is installed and running
$pgService = Get-Service | Where-Object {$_.Name -like "*postgres*"}
if ($null -eq $pgService) {
    Write-Host "Error: PostgreSQL service not found!" -ForegroundColor Red
    exit 1
}

if ($pgService.Status -ne "Running") {
    Write-Host "Starting PostgreSQL service..." -ForegroundColor Yellow
    Start-Service $pgService.Name
    Start-Sleep -Seconds 5
}

Write-Host "PostgreSQL service is running." -ForegroundColor Green

# PostgreSQL executable path
$psqlPath = "C:\Program Files\PostgreSQL\17\bin\psql.exe"

# Check if psql exists
if (-not (Test-Path $psqlPath)) {
    Write-Host "Error: Could not find psql executable at $psqlPath" -ForegroundColor Red
    exit 1
}

# Create database and user
Write-Host "Creating database and user for WHISKEY..." -ForegroundColor Cyan

# Create the boozer_db database
& $psqlPath -U postgres -c "CREATE DATABASE boozer_db;" 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Database 'boozer_db' created successfully" -ForegroundColor Green
} else {
    Write-Host "ℹ Database 'boozer_db' already exists or creation failed" -ForegroundColor Yellow
}

# Create the boozer_user
& $psqlPath -U postgres -c "CREATE USER boozer_user WITH PASSWORD 'boozer_password';" 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ User 'boozer_user' created successfully" -ForegroundColor Green
} else {
    Write-Host "ℹ User 'boozer_user' already exists or creation failed" -ForegroundColor Yellow
}

# Grant privileges
& $psqlPath -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE boozer_db TO boozer_user;" 2>$null
Write-Host "✓ Granted privileges to boozer_user" -ForegroundColor Green

Write-Host "PostgreSQL setup for WHISKEY completed!" -ForegroundColor Green
Write-Host "You can now start the WHISKEY AI System." -ForegroundColor Cyan
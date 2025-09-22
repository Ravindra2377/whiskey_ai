# Setup PostgreSQL database for WHISKEY AI System

Write-Host "Setting up PostgreSQL database for WHISKEY AI System..." -ForegroundColor Cyan

# Check if PostgreSQL is installed and running
$pgService = Get-Service | Where-Object {$_.Name -like "*postgres*"}
if ($null -eq $pgService) {
    Write-Host "Error: PostgreSQL service not found!" -ForegroundColor Red
    exit 1
}

Write-Host "PostgreSQL service status: $($pgService.Status)" -ForegroundColor Green

# PostgreSQL executable path
$psqlPath = "C:\Program Files\PostgreSQL\17\bin\psql.exe"

# Check if psql exists
if (-not (Test-Path $psqlPath)) {
    Write-Host "Error: Could not find psql executable at $psqlPath" -ForegroundColor Red
    exit 1
}

# Use the provided PostgreSQL password
$postgresPassword = "PASSWORD@11"

# Create a temporary password file
$passwordFile = [System.IO.Path]::GetTempFileName()
$passwordFileContent = "localhost:5432:*:postgres:$postgresPassword"
Set-Content -Path $passwordFile -Value $passwordFileContent

# Set the PGPASSFILE environment variable
$env:PGPASSFILE = $passwordFile

try {
    # Create database
    Write-Host "Creating database 'boozer_db'..." -ForegroundColor Yellow
    $result = & $psqlPath -U postgres -c "CREATE DATABASE boozer_db;" 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Database 'boozer_db' created successfully" -ForegroundColor Green
    } else {
        if ($result -like "*already exists*") {
            Write-Host "Database 'boozer_db' already exists" -ForegroundColor Yellow
        } else {
            Write-Host "Database creation result: $result" -ForegroundColor Yellow
        }
    }

    # Create user
    Write-Host "Creating user 'boozer_user'..." -ForegroundColor Yellow
    $result = & $psqlPath -U postgres -c "CREATE USER boozer_user WITH PASSWORD 'boozer_password';" 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "User 'boozer_user' created successfully" -ForegroundColor Green
    } else {
        if ($result -like "*already exists*") {
            Write-Host "User 'boozer_user' already exists" -ForegroundColor Yellow
        } else {
            Write-Host "User creation result: $result" -ForegroundColor Yellow
        }
    }

    # Grant privileges
    Write-Host "Granting privileges..." -ForegroundColor Yellow
    & $psqlPath -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE boozer_db TO boozer_user;" 2>$null
    Write-Host "Granted privileges to boozer_user" -ForegroundColor Green

    Write-Host "PostgreSQL setup for WHISKEY completed successfully!" -ForegroundColor Green
    Write-Host "You can now start the WHISKEY AI System." -ForegroundColor Cyan
}
finally {
    # Clean up the temporary password file
    if (Test-Path $passwordFile) {
        Remove-Item $passwordFile -Force
    }
}
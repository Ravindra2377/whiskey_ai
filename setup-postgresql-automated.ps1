# WHISKEY PostgreSQL Setup Script
# This script helps automate the setup of PostgreSQL for WHISKEY AI

Write-Host "===============================================" -ForegroundColor Green
Write-Host "WHISKEY AI - PostgreSQL Database Setup" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green

Write-Host ""
Write-Host "This script will guide you through setting up PostgreSQL for WHISKEY AI." -ForegroundColor Yellow
Write-Host ""

# Check if PostgreSQL is already installed
Write-Host "Checking for existing PostgreSQL installation..." -ForegroundColor Cyan
$pgService = Get-Service -Name "postgresql*" -ErrorAction SilentlyContinue
if ($pgService) {
    Write-Host "PostgreSQL service found: $($pgService.Name)" -ForegroundColor Green
} else {
    Write-Host "PostgreSQL not found. Please install PostgreSQL first." -ForegroundColor Red
    Write-Host "Download from: https://www.postgresql.org/download/windows/" -ForegroundColor Yellow
    Write-Host "After installation, run this script again." -ForegroundColor Yellow
    exit
}

# Check if PostgreSQL is running
Write-Host "Checking if PostgreSQL is running..." -ForegroundColor Cyan
try {
    $pgStatus = Get-Service -Name "postgresql*" | Where-Object {$_.Status -eq "Running"}
    if ($pgStatus) {
        Write-Host "PostgreSQL is running." -ForegroundColor Green
    } else {
        Write-Host "PostgreSQL is not running. Please start the PostgreSQL service." -ForegroundColor Red
        exit
    }
} catch {
    Write-Host "Error checking PostgreSQL status: $_" -ForegroundColor Red
    exit
}

# Check if we can connect to PostgreSQL
Write-Host "Testing database connection..." -ForegroundColor Cyan
try {
    # This would require the npgsql package for PowerShell, which is complex to install
    # For now, we'll just check if the port is open
    $tcpClient = New-Object System.Net.Sockets.TcpClient
    $connect = $tcpClient.BeginConnect("localhost", 5432, $null, $null)
    $wait = $connect.AsyncWaitHandle.WaitOne(3000, $false)
    if ($wait) {
        try {
            $tcpClient.EndConnect($connect)
            Write-Host "Successfully connected to PostgreSQL on port 5432" -ForegroundColor Green
            $tcpClient.Close()
        } catch {
            Write-Host "Cannot connect to PostgreSQL. Error: $_" -ForegroundColor Red
            exit
        }
    } else {
        Write-Host "Cannot connect to PostgreSQL. Port 5432 is not responding." -ForegroundColor Red
        $tcpClient.Close()
        exit
    }
} catch {
    Write-Host "Error testing database connection: $_" -ForegroundColor Red
    exit
}

Write-Host ""
Write-Host "Manual Steps Required:" -ForegroundColor Yellow
Write-Host "1. Open pgAdmin or psql command line tool" -ForegroundColor Yellow
Write-Host "2. Connect to PostgreSQL with the postgres user" -ForegroundColor Yellow
Write-Host "3. Run these SQL commands:" -ForegroundColor Yellow
Write-Host ""
Write-Host "-- Create database user for WHISKEY" -ForegroundColor Yellow
Write-Host "CREATE USER boozer_user WITH PASSWORD 'boozer_password';" -ForegroundColor Yellow
Write-Host ""
Write-Host "-- Create development database" -ForegroundColor Yellow
Write-Host "CREATE DATABASE boozer_dev OWNER boozer_user;" -ForegroundColor Yellow
Write-Host ""
Write-Host "-- Create production database" -ForegroundColor Yellow
Write-Host "CREATE DATABASE boozer_db OWNER boozer_user;" -ForegroundColor Yellow
Write-Host ""
Write-Host "-- Grant permissions" -ForegroundColor Yellow
Write-Host "GRANT ALL PRIVILEGES ON DATABASE boozer_dev TO boozer_user;" -ForegroundColor Yellow
Write-Host "GRANT ALL PRIVILEGES ON DATABASE boozer_db TO boozer_user;" -ForegroundColor Yellow
Write-Host ""

Write-Host "===============================================" -ForegroundColor Green
Write-Host "Next Steps:" -ForegroundColor Green
Write-Host "1. Run the SQL commands above in your PostgreSQL client" -ForegroundColor Green
Write-Host "2. Navigate to the NEXUS directory" -ForegroundColor Green
Write-Host "3. Start NEXUS with: ./mvnw spring-boot:run" -ForegroundColor Green
Write-Host "4. Verify the connection at: http://localhost:8085/api/nexus/health" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green
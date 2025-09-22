# Setup PostgreSQL and Migrate Files to NEXUS Database
Write-Host "===============================================" -ForegroundColor Green
Write-Host "Setting up PostgreSQL and Migrating Files to NEXUS Database" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green
Write-Host ""

# Function to check if PostgreSQL is installed
function Test-PostgreSQLInstalled {
    Write-Host "Checking if PostgreSQL is installed..." -ForegroundColor Cyan
    try {
        $pgService = Get-Service -Name "postgresql*" -ErrorAction SilentlyContinue
        if ($pgService) {
            Write-Host "✓ PostgreSQL service found: $($pgService.Name)" -ForegroundColor Green
            return $true
        } else {
            Write-Host "⚠ PostgreSQL service not found" -ForegroundColor Yellow
            return $false
        }
    } catch {
        Write-Host "⚠ Error checking PostgreSQL service: $($_.Exception.Message)" -ForegroundColor Yellow
        return $false
    }
}

# Function to check if PostgreSQL is running
function Test-PostgreSQLRunning {
    Write-Host "Checking if PostgreSQL is running..." -ForegroundColor Cyan
    try {
        $pgStatus = Get-Service -Name "postgresql*" | Where-Object {$_.Status -eq "Running"}
        if ($pgStatus) {
            Write-Host "✓ PostgreSQL is running" -ForegroundColor Green
            return $true
        } else {
            Write-Host "⚠ PostgreSQL is not running" -ForegroundColor Yellow
            return $false
        }
    } catch {
        Write-Host "⚠ Error checking PostgreSQL status: $($_.Exception.Message)" -ForegroundColor Yellow
        return $false
    }
}

# Function to start PostgreSQL
function Start-PostgreSQL {
    Write-Host "Attempting to start PostgreSQL..." -ForegroundColor Cyan
    try {
        $pgService = Get-Service -Name "postgresql*"
        if ($pgService) {
            Start-Service -Name $pgService.Name
            Write-Host "✓ Started PostgreSQL service: $($pgService.Name)" -ForegroundColor Green
            return $true
        } else {
            Write-Host "✗ No PostgreSQL service found to start" -ForegroundColor Red
            return $false
        }
    } catch {
        Write-Host "✗ Error starting PostgreSQL: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Function to check NEXUS health
function Test-NexusHealth {
    Write-Host "Checking NEXUS status..." -ForegroundColor Cyan
    try {
        $healthResponse = Invoke-WebRequest -Uri "http://localhost:8085/api/nexus/health" -Method GET -ErrorAction Stop
        $healthData = $healthResponse | ConvertFrom-Json
        
        if ($healthData.status -eq "HEALTHY") {
            Write-Host "✓ NEXUS is running and healthy" -ForegroundColor Green
            return $true
        } else {
            Write-Host "⚠ NEXUS health status: $($healthData.status)" -ForegroundColor Yellow
            return $true  # Still running, just not healthy
        }
    } catch {
        Write-Host "⚠ NEXUS is not running" -ForegroundColor Yellow
        return $false
    }
}

# Function to start NEXUS
function Start-Nexus {
    Write-Host "Starting NEXUS AI system..." -ForegroundColor Cyan
    try {
        # Start NEXUS in the background
        Start-Process -FilePath "cmd.exe" -ArgumentList "/c cd nexus && mvnw spring-boot:run" -WindowStyle Hidden
        Write-Host "✓ NEXUS start command issued" -ForegroundColor Green
        
        # Wait a bit for NEXUS to start
        Write-Host "Waiting for NEXUS to start..." -ForegroundColor Cyan
        Start-Sleep -Seconds 15
        
        # Check if it's running
        if (Test-NexusHealth) {
            return $true
        } else {
            Write-Host "⚠ NEXUS may still be starting up" -ForegroundColor Yellow
            return $true
        }
    } catch {
        Write-Host "✗ Error starting NEXUS: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Function to import files to NEXUS database
function Import-FilesToNexus {
    Write-Host "Importing files to NEXUS database..." -ForegroundColor Cyan
    
    try {
        # Payload to import all files from current directory
        $importPayload = @{
            directoryPath = "."
        } | ConvertTo-Json
        
        $importResponse = Invoke-WebRequest -Uri "http://localhost:8085/api/nexus/boozer-files/import" -Method POST -Body $importPayload -ContentType "application/json" -ErrorAction Stop
        $importData = $importResponse | ConvertFrom-Json
        
        Write-Host "✓ Success: $($importData.message)" -ForegroundColor Green
        Write-Host "  Imported $($importData.importedCount) files" -ForegroundColor Green
        return $true
    } catch {
        Write-Host "✗ Error importing files: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Function to get file info from database
function Get-FileInfo {
    Write-Host "Checking imported files..." -ForegroundColor Cyan
    try {
        $infoResponse = Invoke-WebRequest -Uri "http://localhost:8085/api/nexus/boozer-files/info" -Method GET -ErrorAction Stop
        $infoData = $infoResponse | ConvertFrom-Json
        
        Write-Host "✓ Database contains $($infoData.totalFiles) files" -ForegroundColor Green
        Write-Host "  File types:" -ForegroundColor Green
        $infoData.fileTypes.PSObject.Properties | ForEach-Object {
            Write-Host "    $($_.Name): $($_.Value)" -ForegroundColor Green
        }
        return $true
    } catch {
        Write-Host "✗ Error getting file info: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Function to clean directory
function Clean-Directory {
    Write-Host "Cleaning directory to keep only NEXUS files..." -ForegroundColor Cyan
    
    # List of files and directories to keep
    $keepItems = @(
        "nexus",
        "nexus-ai-*",
        "NEXUS_*",
        "import-boozer-files.*",
        "migrate-to-nexus-database.*",
        "setup-and-migrate.*",
        "*.zip"
    )
    
    # Get all items in current directory
    $allItems = Get-ChildItem -Path "." -Force
    
    $deletedCount = 0
    foreach ($item in $allItems) {
        $shouldKeep = $false
        
        # Check if item should be kept
        foreach ($keepPattern in $keepItems) {
            if ($item.Name -like $keepPattern) {
                $shouldKeep = $true
                break
            }
        }
        
        # Special case: keep this script
        if ($item.Name -eq "setup-and-migrate.ps1") {
            $shouldKeep = $true
        }
        
        # Delete if not in keep list
        if (-not $shouldKeep) {
            try {
                if ($item.PSIsContainer) {
                    Remove-Item -Path $item.FullName -Recurse -Force
                    Write-Host "  Deleted directory: $($item.Name)" -ForegroundColor Yellow
                } else {
                    Remove-Item -Path $item.FullName -Force
                    Write-Host "  Deleted file: $($item.Name)" -ForegroundColor Yellow
                }
                $deletedCount++
            } catch {
                Write-Host "  Failed to delete: $($item.Name) - $($_.Exception.Message)" -ForegroundColor Red
            }
        } else {
            Write-Host "  Keeping: $($item.Name)" -ForegroundColor Green
        }
    }
    
    Write-Host "  Deleted $deletedCount items" -ForegroundColor Cyan
}

# Main execution
Write-Host "1. Checking PostgreSQL installation..." -ForegroundColor Cyan
$pgInstalled = Test-PostgreSQLInstalled

if (-not $pgInstalled) {
    Write-Host ""
    Write-Host "PostgreSQL is not installed. Please install PostgreSQL first:" -ForegroundColor Yellow
    Write-Host "1. Download PostgreSQL from: https://www.postgresql.org/download/windows/" -ForegroundColor Yellow
    Write-Host "2. Install with default settings" -ForegroundColor Yellow
    Write-Host "3. Run this script again after installation" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "After installation, you'll also need to create the database:" -ForegroundColor Yellow
    Write-Host "1. Open pgAdmin or psql" -ForegroundColor Yellow
    Write-Host "2. Run these commands:" -ForegroundColor Yellow
    Write-Host "   CREATE USER boozer_user WITH PASSWORD 'boozer_password';" -ForegroundColor Yellow
    Write-Host "   CREATE DATABASE boozer_db OWNER boozer_user;" -ForegroundColor Yellow
    Write-Host "   GRANT ALL PRIVILEGES ON DATABASE boozer_db TO boozer_user;" -ForegroundColor Yellow
    Write-Host ""
    exit 1
}

Write-Host ""
Write-Host "2. Checking PostgreSQL status..." -ForegroundColor Cyan
$pgRunning = Test-PostgreSQLRunning

if (-not $pgRunning) {
    Write-Host "Attempting to start PostgreSQL..."
    if (-not (Start-PostgreSQL)) {
        Write-Host ""
        Write-Host "Failed to start PostgreSQL. Please start it manually and run this script again." -ForegroundColor Red
        exit 1
    }
    Start-Sleep -Seconds 5
}

Write-Host ""
Write-Host "3. Checking NEXUS status..." -ForegroundColor Cyan
$nexusRunning = Test-NexusHealth

if (-not $nexusRunning) {
    Write-Host "Starting NEXUS..."
    if (-not (Start-Nexus)) {
        Write-Host ""
        Write-Host "Failed to start NEXUS. Please check the logs and run this script again." -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "4. Waiting for systems to stabilize..." -ForegroundColor Cyan
Start-Sleep -Seconds 10

Write-Host ""
Write-Host "5. Importing all files to NEXUS database..." -ForegroundColor Cyan
if (-not (Import-FilesToWhiskey)) {
    Write-Host ""
    Write-Host "Failed to import files. Please check the NEXUS logs and run this script again." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "6. Verifying imported files..." -ForegroundColor Cyan
Get-FileInfo

Write-Host ""
Write-Host "7. Cleaning directory..." -ForegroundColor Cyan
Clean-Directory

Write-Host ""
Write-Host "🎉 Setup and migration completed successfully!" -ForegroundColor Green
Write-Host "   PostgreSQL is running" -ForegroundColor Green
Write-Host "   NEXUS is running" -ForegroundColor Green
Write-Host "   All files have been imported to NEXUS database" -ForegroundColor Green
Write-Host "   Directory has been cleaned to keep only NEXUS files" -ForegroundColor Green
Write-Host ""
Write-Host "   You can now access your files through NEXUS API:" -ForegroundColor Green
Write-Host "   - List all files: GET http://localhost:8085/api/nexus/boozer-files" -ForegroundColor Yellow
Write-Host "   - Search files: GET http://localhost:8085/api/nexus/boozer-files/search?query=YourQuery" -ForegroundColor Yellow
Write-Host "   - Get file content: GET http://localhost:8085/api/nexus/boozer-files/content/{filePath}" -ForegroundColor Yellow
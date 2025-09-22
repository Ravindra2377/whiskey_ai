# Migrate All Files to NEXUS Database and Clean Directory
Write-Host "===============================================" -ForegroundColor Green
Write-Host "Migrating Files to NEXUS Database" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green
Write-Host ""

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
        Write-Host "✗ NEXUS is not running" -ForegroundColor Red
        Write-Host ""
        Write-Host "Please start NEXUS before running this script:" -ForegroundColor Yellow
        Write-Host "  cd nexus" -ForegroundColor Yellow
        Write-Host "  ./mvnw spring-boot:run" -ForegroundColor Yellow
        Write-Host ""
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
        if ($item.Name -eq "migrate-to-nexus-database.ps1") {
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
Write-Host "1. Checking NEXUS status..." -ForegroundColor Cyan
if (-not (Test-NexusHealth)) {
    exit 1
}

Write-Host ""
Write-Host "2. Importing all files to NEXUS database..." -ForegroundColor Cyan
if (-not (Import-FilesToNexus)) {
    exit 1
}

Write-Host ""
Write-Host "3. Verifying imported files..." -ForegroundColor Cyan
Get-FileInfo

Write-Host ""
Write-Host "4. Cleaning directory..." -ForegroundColor Cyan
Clean-Directory

Write-Host ""
Write-Host "🎉 Migration completed successfully!" -ForegroundColor Green
Write-Host "   All files have been imported to NEXUS database" -ForegroundColor Green
Write-Host "   Directory has been cleaned to keep only NEXUS files" -ForegroundColor Green
Write-Host ""
Write-Host "   You can now access your files through NEXUS API:" -ForegroundColor Green
Write-Host "   - List all files: GET http://localhost:8085/api/nexus/boozer-files" -ForegroundColor Yellow
Write-Host "   - Search files: GET http://localhost:8085/api/nexus/boozer-files/search?query=YourQuery" -ForegroundColor Yellow
Write-Host "   - Get file content: GET http://localhost:8085/api/nexus/boozer-files/content/{filePath}" -ForegroundColor Yellow
# Boozer Files Import to NEXUS Database
Write-Host "===============================================" -ForegroundColor Green
Write-Host "Boozer Files Import to NEXUS Database" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green
Write-Host ""

# Check if NEXUS is running
Write-Host "1. Checking NEXUS status..." -ForegroundColor Cyan
try {
    $healthResponse = Invoke-WebRequest -Uri "http://localhost:8085/api/nexus/health" -Method GET -ErrorAction Stop
    $healthData = $healthResponse | ConvertFrom-Json
    
    if ($healthData.status -eq "HEALTHY") {
        Write-Host "✓ NEXUS is running and healthy" -ForegroundColor Green
    } else {
        Write-Host "⚠ NEXUS health status: $($healthData.status)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "✗ NEXUS is not running" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please start NEXUS before running this script:" -ForegroundColor Yellow
    Write-Host "  cd nexus" -ForegroundColor Yellow
    Write-Host "  ./mvnw spring-boot:run" -ForegroundColor Yellow
    Write-Host ""
    exit 1
}

Write-Host ""
Write-Host "2. Importing Boozer files..." -ForegroundColor Cyan

# Import files
try {
    $importPayload = @{
        directoryPath = "../backend"
    } | ConvertTo-Json
    
    $importResponse = Invoke-WebRequest -Uri "http://localhost:8085/api/nexus/boozer-files/import" -Method POST -Body $importPayload -ContentType "application/json" -ErrorAction Stop
    $importData = $importResponse | ConvertFrom-Json
    
    Write-Host "✓ Success: $($importData.message)" -ForegroundColor Green
    Write-Host "  Imported $($importData.importedCount) files" -ForegroundColor Green
} catch {
    Write-Host "✗ Error importing files: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "3. Checking imported files..." -ForegroundColor Cyan

# Get file information
try {
    $infoResponse = Invoke-WebRequest -Uri "http://localhost:8085/api/nexus/boozer-files/info" -Method GET -ErrorAction Stop
    $infoData = $infoResponse | ConvertFrom-Json
    
    Write-Host "✓ Database contains $($infoData.totalFiles) files" -ForegroundColor Green
    Write-Host "  File types:" -ForegroundColor Green
    $infoData.fileTypes.PSObject.Properties | ForEach-Object {
        Write-Host "    $($_.Name): $($_.Value)" -ForegroundColor Green
    }
} catch {
    Write-Host "✗ Error getting file info: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "🎉 Boozer files import completed successfully!" -ForegroundColor Green
Write-Host "   You can now access Boozer files through NEXUS API endpoints:" -ForegroundColor Green
Write-Host "   - List all files: GET http://localhost:8085/api/nexus/boozer-files" -ForegroundColor Yellow
Write-Host "   - Search files: GET http://localhost:8085/api/nexus/boozer-files/search?query=YourQuery" -ForegroundColor Yellow
Write-Host "   - Get file by path: GET http://localhost:8085/api/nexus/boozer-files/path/{filePath}" -ForegroundColor Yellow
Write-Host "   - Get file content: GET http://localhost:8085/api/nexus/boozer-files/content/{filePath}" -ForegroundColor Yellow
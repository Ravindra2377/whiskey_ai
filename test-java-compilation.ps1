# Test Java Application Compilation for WHISKEY AI
Write-Host " WHISKEY AI - Java Application Compilation Test" -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Green
Write-Host ""

# Navigate to the whiskey directory
Write-Host "Navigating to whiskey directory..." -ForegroundColor Yellow
Set-Location -Path "D:\OneDrive\Desktop\Boozer_App_Main\whiskey"

# Check if mvnw.cmd exists
Write-Host "Checking for Maven wrapper..." -ForegroundColor Yellow
if (Test-Path "mvnw.cmd") {
    Write-Host "✅ Found mvnw.cmd" -ForegroundColor Green
    
    # Try to compile the application
    Write-Host "Compiling the application..." -ForegroundColor Yellow
    try {
        $output = .\mvnw.cmd clean compile 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Compilation successful!" -ForegroundColor Green
        } else {
            Write-Host "❌ Compilation failed:" -ForegroundColor Red
            Write-Host $output -ForegroundColor White
        }
    } catch {
        Write-Host "❌ Compilation failed with error:" -ForegroundColor Red
        Write-Host $_.Exception.Message -ForegroundColor White
    }
} else {
    Write-Host "❌ mvnw.cmd not found in whiskey directory" -ForegroundColor Red
    Write-Host "Current directory: $(Get-Location)" -ForegroundColor White
    
    # List files in current directory
    Write-Host "Files in current directory:" -ForegroundColor Yellow
    Get-ChildItem | ForEach-Object { Write-Host "   $($_.Name)" -ForegroundColor White }
    
    # Try to find mvnw.cmd in parent directory
    Write-Host "Checking parent directory..." -ForegroundColor Yellow
    if (Test-Path "..\mvnw.cmd") {
        Write-Host "✅ Found mvnw.cmd in parent directory" -ForegroundColor Green
        Write-Host "Try running: ..\mvnw.cmd clean compile" -ForegroundColor Cyan
    }
}

Write-Host ""
Write-Host "Press Enter to exit..." -ForegroundColor Cyan
Read-Host
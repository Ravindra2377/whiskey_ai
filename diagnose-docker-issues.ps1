# Diagnose and Fix Docker Issues for WHISKEY AI
Write-Host " WHISKEY AI - Docker Issue Diagnosis" -ForegroundColor Green
Write-Host "===================================" -ForegroundColor Green
Write-Host ""

Write-Host "1. Checking Docker Context..." -ForegroundColor Yellow
$contexts = docker context ls
Write-Host $contexts -ForegroundColor White

# Check if desktop-linux is the current context
if ($contexts -like "*desktop-linux *") {
    Write-Host "⚠️  Docker is using Linux context. Switching to default..." -ForegroundColor Yellow
    docker context use default
    Write-Host "Context switched. Verifying..." -ForegroundColor Yellow
    $newContexts = docker context ls
    Write-Host $newContexts -ForegroundColor White
}

Write-Host ""
Write-Host "2. Checking Docker Version..." -ForegroundColor Yellow
try {
    $version = docker version
    Write-Host "✅ Docker version command successful" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker version command failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor White
}

Write-Host ""
Write-Host "3. Checking Docker Info..." -ForegroundColor Yellow
try {
    $info = docker info 2>$null
    if ($info) {
        Write-Host "✅ Docker info command successful" -ForegroundColor Green
        # Check if we're using Windows or Linux containers
        if ($info -like "*Operating System: Docker Desktop*") {
            Write-Host "✅ Using Docker Desktop (Windows containers)" -ForegroundColor Green
        } else {
            Write-Host "⚠️  May be using Linux containers" -ForegroundColor Yellow
        }
    }
} catch {
    Write-Host "❌ Docker info command failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor White
}

Write-Host ""
Write-Host "4. Checking Docker Daemon..." -ForegroundColor Yellow
$dockerProcesses = Get-Process -Name "com.docker*" -ErrorAction SilentlyContinue
if ($dockerProcesses) {
    Write-Host "✅ Docker processes are running:" -ForegroundColor Green
    $dockerProcesses | ForEach-Object { Write-Host "   $($_.ProcessName)" -ForegroundColor White }
} else {
    Write-Host "⚠️  No Docker processes found" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "5. Checking Docker Service..." -ForegroundColor Yellow
try {
    $service = Get-Service com.docker.service -ErrorAction SilentlyContinue
    if ($service) {
        Write-Host "Docker Service Status: $($service.Status)" -ForegroundColor White
        if ($service.Status -ne "Running") {
            Write-Host "⚠️  Docker service is not running" -ForegroundColor Yellow
        } else {
            Write-Host "✅ Docker service is running" -ForegroundColor Green
        }
    } else {
        Write-Host "⚠️  Docker service not found" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Could not check Docker service" -ForegroundColor Red
}

Write-Host ""
Write-Host "6. Checking Container Type Setting..." -ForegroundColor Yellow
# Check if Docker Desktop is set to use Windows or Linux containers
Write-Host "Please check Docker Desktop settings:" -ForegroundColor Cyan
Write-Host "1. Right-click Docker icon in system tray" -ForegroundColor White
Write-Host "2. Look for 'Switch to Windows containers' or 'Switch to Linux containers'" -ForegroundColor White
Write-Host "3. If 'Switch to Windows containers' is available, click it" -ForegroundColor White

Write-Host ""
Write-Host " Recommended Actions:" -ForegroundColor Yellow
Write-Host "====================" -ForegroundColor Yellow
Write-Host "1. If Docker Desktop is not running, start it manually" -ForegroundColor White
Write-Host "2. Switch to Windows containers if currently using Linux" -ForegroundColor White
Write-Host "3. Restart Docker Desktop after switching container types" -ForegroundColor White
Write-Host "4. Run the fix-docker-context.bat script after Docker is running" -ForegroundColor White

Write-Host ""
Write-Host "Press Enter to exit..." -ForegroundColor Cyan
Read-Host
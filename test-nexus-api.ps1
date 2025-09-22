# Test script for WHISKEY AI API

Write-Host "Testing WHISKEY AI API" -ForegroundColor Green

# Test 1: Get system info
Write-Host "`n1. Testing system info endpoint..." -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri "http://localhost:8085/api/whiskey/info" -Method Get
Write-Host "System Name: $($response.name)"
Write-Host "Version: $($response.version)"

# Test 2: Get system health
Write-Host "`n2. Testing health endpoint..." -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri "http://localhost:8085/api/whiskey/health" -Method Get
Write-Host "Status: $($response.status)"
Write-Host "Version: $($response.version)"

# Test 3: Get system metrics
Write-Host "`n3. Testing metrics endpoint..." -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri "http://localhost:8085/api/whiskey/metrics" -Method Get
Write-Host "CPU Usage: $($response.cpuUsage)%"
Write-Host "Memory Usage: $($response.memoryUsage)%"

# Test 4: Submit a sample task
Write-Host "`n4. Submitting a sample task..." -ForegroundColor Yellow
$task = @{
    type = "CODE_MODIFICATION"
    description = "Analyze and optimize the user authentication module"
    parameters = @{
        module = "user-auth"
        depth = "detailed"
    }
    createdBy = "test-script"
}

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8085/api/whiskey/task" -Method Post -Body ($task | ConvertTo-Json) -ContentType "application/json"
    Write-Host "Task Status: $($response.status)"
    Write-Host "Task ID: $($response.taskId)"
    Write-Host "Message: $($response.message)"
    
    # Test 5: Check task status
    if ($response.taskId) {
        Write-Host "`n5. Checking task status..." -ForegroundColor Yellow
        Start-Sleep -Seconds 2
        $statusResponse = Invoke-RestMethod -Uri "http://localhost:8085/api/whiskey/task/$($response.taskId)" -Method Get
        Write-Host "Task Status: $($statusResponse.status)"
        Write-Host "Progress: $($statusResponse.progress)%"
    }
} catch {
    Write-Host "Error submitting task: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nWHISKEY API tests completed!" -ForegroundColor Green
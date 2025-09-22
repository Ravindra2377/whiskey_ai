# NEXUS AI Client Script
# This script demonstrates how to interact with the NEXUS AI API

 param(
    [string]$BaseURL = "http://localhost:8085/api/nexus"
)

function Submit-NexusTask {
    param(
        [string]$TaskType,
        [string]$Description,
        [hashtable]$Parameters = @{},
        [string]$CreatedBy = "PowerShell_Client"
    )
    
    $task = @{
        type = $TaskType
        description = $Description
        parameters = $Parameters
        createdBy = $CreatedBy
    } | ConvertTo-Json
    
    try {
        $response = Invoke-WebRequest -Uri "$BaseURL/task" -Method POST -Body $task -ContentType "application/json"
        return $response.Content | ConvertFrom-Json
    } catch {
        Write-Error "Failed to submit task: $($_.Exception.Message)"
        return $null
    }
}

function Get-NexusTaskStatus {
    param(
        [string]$TaskId
    )
    
    try {
        $response = Invoke-WebRequest -Uri "$BaseURL/task/$TaskId" -Method GET
        return $response.Content | ConvertFrom-Json
    } catch {
        Write-Error "Failed to get task status: $($_.Exception.Message)"
        return $null
    }
}

function Get-NexusSystemHealth {
    try {
        $response = Invoke-WebRequest -Uri "$BaseURL/health" -Method GET
        return $response.Content | ConvertFrom-Json
    } catch {
        Write-Error "Failed to get system health: $($_.Exception.Message)"
        return $null
    }
}

function Get-NexusSystemInfo {
    try {
        $response = Invoke-WebRequest -Uri "$BaseURL/info" -Method GET
        return $response.Content | ConvertFrom-Json
    } catch {
        Write-Error "Failed to get system info: $($_.Exception.Message)"
        return $null
    }
}

# Example usage
Write-Host "=== NEXUS AI Client Demo ===" -ForegroundColor Green

# Check system health
Write-Host "`n1. Checking system health..." -ForegroundColor Yellow
$health = Get-NexusSystemHealth
if ($health) {
    Write-Host "System Status: $($health.status)" -ForegroundColor Cyan
    Write-Host "Version: $($health.version)" -ForegroundColor Cyan
}

# Get system info
Write-Host "`n2. Getting system information..." -ForegroundColor Yellow
$info = Get-NexusSystemInfo
if ($info) {
    Write-Host "System Name: $($info.name)" -ForegroundColor Cyan
    Write-Host "Description: $($info.description)" -ForegroundColor Cyan
    Write-Host "Available Processors: $($info.system.availableProcessors)" -ForegroundColor Cyan
}

# Submit a sample task
Write-Host "`n3. Submitting a sample task..." -ForegroundColor Yellow
$taskResponse = Submit-NexusTask -TaskType "CODE_MODIFICATION" -Description "Add new authentication feature" -Parameters @{ priority = "HIGH"; module = "auth" }
if ($taskResponse) {
    Write-Host "Task Status: $($taskResponse.status)" -ForegroundColor Cyan
    Write-Host "Task ID: $($taskResponse.taskId)" -ForegroundColor Cyan
    Write-Host "Task Type: $($taskResponse.taskType)" -ForegroundColor Cyan
    
    # Wait a moment and check task status
    Write-Host "`n4. Checking task status..." -ForegroundColor Yellow
    Start-Sleep -Seconds 2
    $taskStatus = Get-NexusTaskStatus -TaskId $taskResponse.taskId
    if ($taskStatus) {
        Write-Host "Task Status: $($taskStatus.status)" -ForegroundColor Cyan
        Write-Host "Progress: $($taskStatus.progress)%" -ForegroundColor Cyan
        Write-Host "Message: $($taskStatus.message)" -ForegroundColor Cyan
    }
}

Write-Host "`n=== Demo Complete ===" -ForegroundColor Green
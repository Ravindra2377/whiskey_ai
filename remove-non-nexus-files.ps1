# Script to remove all files and directories except NEXUS AI system
# This will preserve the standalone NEXUS AI system and its documentation

Write-Host "Preparing to remove all files except NEXUS AI system..." -ForegroundColor Yellow

# Define paths to preserve
$nexusStandalonePath = "D:\OneDrive\Desktop\NEXUS_Standalone"
$nexusEnhancementsPath = "D:\OneDrive\Desktop\Boozer_App_Main\nexus-ai-enhancements"
$nexusDocs = @(
    "NEXUS_AI_COMPLETE_DOCUMENTATION.md",
    "NEXUS_AI_COMPLETE_INVENTORY.md", 
    "NEXUS_AI_FILE_LIST.md",
    "NEXUS_AI_STANDALONE.md",
    "NEXUS_API_DOCUMENTATION.md",
    "NEXUS_DOCUMENTATION.md",
    "NEXUS_AI_COMPLETE_ENHANCEMENT_SUITE.md",
    "nexus-ai-source-code.zip",
    "nexus-ai-scripts-and-docs.zip",
    "nexus-ai-enhancements.zip"
)

# Ask for confirmation
Write-Host "This script will remove all files except the NEXUS AI system and enhancements." -ForegroundColor Red
Write-Host "The following will be preserved:" -ForegroundColor Green
Write-Host "  - $nexusStandalonePath" -ForegroundColor Green
Write-Host "  - $nexusEnhancementsPath" -ForegroundColor Green
foreach ($doc in $nexusDocs) {
    Write-Host "  - $doc" -ForegroundColor Green
}
Write-Host ""
$confirmation = Read-Host "Are you sure you want to proceed? (yes/no)"

if ($confirmation -ne "yes") {
    Write-Host "Operation cancelled." -ForegroundColor Yellow
    exit
}

# Stop any running processes that might lock files
Write-Host "Stopping any running Java processes..." -ForegroundColor Yellow
taskkill /F /IM java.exe 2>$null

# Get all items in the current directory
$items = Get-ChildItem -Path "D:\OneDrive\Desktop\Boozer_App_Main" -Force

# Remove items except those we want to preserve
foreach ($item in $items) {
    $shouldPreserve = $false
    
    # Check if item should be preserved
    if ($item.FullName -eq $nexusStandalonePath) {
        $shouldPreserve = $true
    } elseif ($item.FullName -eq $nexusEnhancementsPath) {
        $shouldPreserve = $true
    } else {
        foreach ($doc in $nexusDocs) {
            if ($item.Name -eq $doc) {
                $shouldPreserve = $true
                break
            }
        }
    }
    
    # Remove if not preserved
    if (-not $shouldPreserve) {
        Write-Host "Removing: $($item.Name)" -ForegroundColor Red
        try {
            if ($item.PSIsContainer) {
                Remove-Item -Path $item.FullName -Recurse -Force
            } else {
                Remove-Item -Path $item.FullName -Force
            }
        } catch {
            Write-Host "Failed to remove: $($item.Name)" -ForegroundColor Red
        }
    }
}

# Remove apache maven directories if they exist
$mavenDirs = @(
    "apache-maven-3.8.1",
    "apache-maven-3.9.4"
)

foreach ($dir in $mavenDirs) {
    $path = "D:\OneDrive\Desktop\Boozer_App_Main\$dir"
    if (Test-Path $path) {
        Write-Host "Removing Maven directory: $dir" -ForegroundColor Red
        Remove-Item -Path $path -Recurse -Force
    }
}

# Remove backend, frontend, and monitoring directories if they exist
$appDirs = @(
    "backend",
    "frontend",
    "monitoring"
)

foreach ($dir in $appDirs) {
    $path = "D:\OneDrive\Desktop\Boozer_App_Main\$dir"
    if (Test-Path $path) {
        Write-Host "Removing application directory: $dir" -ForegroundColor Red
        Remove-Item -Path $path -Recurse -Force
    }
}

Write-Host "Cleanup complete!" -ForegroundColor Green
Write-Host "Preserved NEXUS AI system and enhancements." -ForegroundColor Green
# Script to set up NEXUS AI as a standalone system
# This script will update the package structure and prepare NEXUS for standalone operation

Write-Host "Setting up NEXUS AI as a standalone system..." -ForegroundColor Green

# Navigate to the nexus-ai directory
Set-Location -Path "D:\OneDrive\Desktop\NEXUS_Standalone\nexus-ai"

Write-Host "1. Creating new package structure..." -ForegroundColor Yellow
# Create the new package structure
New-Item -ItemType Directory -Path "src\main\java\com\nexus\ai" -Force
New-Item -ItemType Directory -Path "src\test\java\com\nexus\ai" -Force

Write-Host "2. Moving Java files to new package structure..." -ForegroundColor Yellow
# Move the Java files from the old package structure to the new one
Copy-Item -Path "src\main\java\com\boozer\nexus\*" -Destination "src\main\java\com\nexus\ai\" -Recurse -Force
Copy-Item -Path "src\test\java\com\boozer\nexus\*" -Destination "src\test\java\com\nexus\ai\" -Recurse -Force

Write-Host "3. Updating package declarations in Java files..." -ForegroundColor Yellow
# Update package declarations in all Java files
Get-ChildItem -Path "src\main\java\com\nexus\ai" -Recurse -Include *.java | ForEach-Object {
    $content = Get-Content $_.FullName
    $content = $content -replace "package com.boozer.nexus", "package com.nexus.ai"
    $content = $content -replace "import com.boozer.nexus", "import com.nexus.ai"
    Set-Content -Path $_.FullName -Value $content
}

Get-ChildItem -Path "src\test\java\com\nexus\ai" -Recurse -Include *.java | ForEach-Object {
    $content = Get-Content $_.FullName
    $content = $content -replace "package com.boozer.nexus", "package com.nexus.ai"
    $content = $content -replace "import com.boozer.nexus", "import com.nexus.ai"
    Set-Content -Path $_.FullName -Value $content
}

Write-Host "4. Updating pom.xml..." -ForegroundColor Yellow
# Update the pom.xml file
$pomContent = Get-Content "pom.xml"
$pomContent = $pomContent -replace "<groupId>com.boozer</groupId>", "<groupId>com.nexus</groupId>"
$pomContent = $pomContent -replace "<name>NEXUS AI System</name>", "<name>NEXUS AI Standalone System</name>"
Set-Content -Path "pom.xml" -Value $pomContent

Write-Host "5. Cleaning up old package structure..." -ForegroundColor Yellow
# Remove the old package structure
Remove-Item -Path "src\main\java\com\boozer" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Path "src\test\java\com\boozer" -Recurse -Force -ErrorAction SilentlyContinue

Write-Host "6. Building the standalone NEXUS AI system..." -ForegroundColor Yellow
# Build the system
.\mvnw.cmd clean package

Write-Host "NEXUS AI standalone system setup complete!" -ForegroundColor Green
Write-Host "To run the system, execute: java -jar target/nexus-1.0.0.jar" -ForegroundColor Cyan
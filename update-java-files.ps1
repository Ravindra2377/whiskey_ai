# Script to update Java files to replace whiskey with nexus

Write-Host "Updating Java files to replace whiskey with nexus..." -ForegroundColor Green

# Update package declarations in main java files
Get-ChildItem -Path "nexus\src\main\java\com\boozer\whiskey" -Recurse -Include *.java | ForEach-Object {
    $content = Get-Content $_.FullName
    $content = $content -replace "package com.boozer.whiskey", "package com.boozer.nexus"
    $content = $content -replace "import com.boozer.whiskey", "import com.boozer.nexus"
    Set-Content -Path $_.FullName -Value $content
    Write-Host "Updated package declarations in $($_.Name)" -ForegroundColor Yellow
}

# Update package declarations in test java files
Get-ChildItem -Path "nexus\src\test\java\com\boozer\whiskey" -Recurse -Include *.java | ForEach-Object {
    $content = Get-Content $_.FullName
    $content = $content -replace "package com.boozer.whiskey", "package com.boozer.nexus"
    $content = $content -replace "import com.boozer.whiskey", "import com.boozer.nexus"
    Set-Content -Path $_.FullName -Value $content
    Write-Host "Updated package declarations in $($_.Name)" -ForegroundColor Yellow
}

# Update API endpoint annotations in all Java files
Get-ChildItem -Path "nexus\src\main\java\com\boozer\whiskey" -Recurse -Include *.java | ForEach-Object {
    $content = Get-Content $_.FullName
    $content = $content -replace '/api/whiskey/', '/api/nexus/'
    Set-Content -Path $_.FullName -Value $content
    Write-Host "Updated API endpoints in $($_.Name)" -ForegroundColor Yellow
}

Write-Host "Java files update complete!" -ForegroundColor Green
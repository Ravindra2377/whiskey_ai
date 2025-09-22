# Script to verify that all WHISKEY AI enhancement files are in place

Write-Host "🔍 Verifying WHISKEY AI Enhancement Suite..." -ForegroundColor Cyan
Write-Host ("=" * 50)

# Define expected files
$expectedFiles = @(
    "D:\OneDrive\Desktop\Boozer_App_Main\whiskey-ai-enhancements\multi_modal_enhancement.py",
    "D:\OneDrive\Desktop\Boozer_App_Main\whiskey-ai-enhancements\proactive_ai_module.py",
    "D:\OneDrive\Desktop\Boozer_App_Main\whiskey-ai-enhancements\emotional_intelligence_module.py",
    "D:\OneDrive\Desktop\Boozer_App_Main\whiskey-ai-enhancements\advanced_security_module.py",
    "D:\OneDrive\Desktop\Boozer_App_Main\whiskey-ai-enhancements\multi_agent_collaboration.py",
    "D:\OneDrive\Desktop\Boozer_App_Main\whiskey-ai-enhancements\edge_ai_module.py",
    "D:\OneDrive\Desktop\Boozer_App_Main\whiskey-ai-enhancements\enhancement_integration.py",
    "D:\OneDrive\Desktop\Boozer_App_Main\whiskey-ai-enhancements\requirements_advanced.txt",
    "D:\OneDrive\Desktop\Boozer_App_Main\whiskey-ai-enhancements\WHISKEY_AI_ENHANCEMENT_DOCUMENTATION.md",
    "D:\OneDrive\Desktop\Boozer_App_Main\whiskey-ai-enhancements\ENHANCEMENT_SUMMARY.md",
    "D:\OneDrive\Desktop\Boozer_App_Main\whiskey-ai-enhancements\INSTALL_ENHANCEMENTS.md",
    "D:\OneDrive\Desktop\Boozer_App_Main\whiskey-ai-enhancements\test_enhancements.py"
)

$expectedZipFiles = @(
    "D:\OneDrive\Desktop\Boozer_App_Main\whiskey-ai-enhancements.zip"
)

# Check individual files
$filesFound = 0
$filesMissing = 0

Write-Host "Checking individual enhancement files..." -ForegroundColor Yellow
foreach ($file in $expectedFiles) {
    if (Test-Path $file) {
        Write-Host "  ✅ $($file.Split('\')[-1])" -ForegroundColor Green
        $filesFound++
    } else {
        Write-Host "  ❌ $($file.Split('\')[-1])" -ForegroundColor Red
        $filesMissing++
    }
}

# Check zip files
Write-Host "`nChecking compressed archives..." -ForegroundColor Yellow
foreach ($file in $expectedZipFiles) {
    if (Test-Path $file) {
        Write-Host "  ✅ $($file.Split('\')[-1])" -ForegroundColor Green
    } else {
        Write-Host "  ❌ $($file.Split('\')[-1])" -ForegroundColor Red
    }
}

# Summary
Write-Host "`n" + ("=" * 50)
Write-Host "VERIFICATION SUMMARY" -ForegroundColor Cyan
Write-Host ("=" * 50)
Write-Host "Files Found: $filesFound" -ForegroundColor Green
Write-Host "Files Missing: $filesMissing" -ForegroundColor $(if ($filesMissing -eq 0) { "Green" } else { "Red" })
Write-Host "Overall Status: $(if ($filesMissing -eq 0) { "✅ COMPLETE" } else { "❌ INCOMPLETE" })" -ForegroundColor $(if ($filesMissing -eq 0) { "Green" } else { "Red" })

if ($filesMissing -eq 0) {
    Write-Host "`n🎉 All WHISKEY AI enhancement files are in place!" -ForegroundColor Green
    Write-Host "The enhancement suite is ready for installation and use." -ForegroundColor Yellow
} else {
    Write-Host "`n⚠️  Some files are missing. Please check the installation." -ForegroundColor Red
}
# Simple Verification Script for WHISKEY AI Enhancements

Write-Host "WHISKEY AI Enhancement Verification" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan

Set-Location "d:\OneDrive\Desktop\Boozer_App_Main\nexus-ai-enhancements"

# Check if required files exist
Write-Host "`nChecking files..." -ForegroundColor Yellow
$files = @("multi_modal_enhancement.py", "proactive_ai_module.py", "emotional_intelligence_module.py", "advanced_security_module.py", "multi_agent_collaboration.py", "edge_ai_module.py", "enhancement_integration.py")

$allExist = $true
foreach ($file in $files) {
    if (Test-Path $file) {
        Write-Host "  ✅ $file" -ForegroundColor Green
    } else {
        Write-Host "  ❌ $file" -ForegroundColor Red
        $allExist = $false
    }
}

if ($allExist) {
    Write-Host "  All enhancement files present!" -ForegroundColor Green
} else {
    Write-Host "  Some files missing!" -ForegroundColor Red
}

# Run the test
Write-Host "`nRunning tests..." -ForegroundColor Yellow
python test_enhancements.py

Write-Host "`nRunning demo..." -ForegroundColor Yellow
python demo_enhanced_nexus.py

Write-Host "`nVerification complete!" -ForegroundColor Cyan
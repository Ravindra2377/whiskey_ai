# Setup Environment Script for WHISKEY AI Orchestration System

Write-Host "🚀 Setting up WHISKEY AI Orchestration System Environment" -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Green

# Check if Python is installed
try {
    $pythonVersion = python --version
    Write-Host "✅ Python is installed: $pythonVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Python is not installed. Please install Python 3.8 or higher." -ForegroundColor Red
    exit 1
}

# Check if pip is installed
try {
    $pipVersion = pip --version
    Write-Host "✅ pip is installed: $pipVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ pip is not installed. Please install pip." -ForegroundColor Red
    exit 1
}

# Install dependencies
Write-Host "📦 Installing dependencies..." -ForegroundColor Yellow
try {
    pip install -r requirements.txt
    Write-Host "✅ Dependencies installed successfully!" -ForegroundColor Green
} catch {
    Write-Host "❌ Failed to install dependencies: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🎉 Setup completed successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Set your API keys as environment variables:" -ForegroundColor Yellow
Write-Host "   `$env:OPENAI_API_KEY='your-openai-api-key'" -ForegroundColor Cyan
Write-Host "   `$env:ANTHROPIC_API_KEY='your-anthropic-api-key'" -ForegroundColor Cyan
Write-Host "   `$env:GOOGLE_API_KEY='your-google-ai-api-key'" -ForegroundColor Cyan
Write-Host ""
Write-Host "2. Run the API server:" -ForegroundColor Yellow
Write-Host "   python api_server.py" -ForegroundColor Cyan
Write-Host ""
Write-Host "3. Or run the command-line interface:" -ForegroundColor Yellow
Write-Host "   python cli.py --prompt 'Your question here'" -ForegroundColor Cyan
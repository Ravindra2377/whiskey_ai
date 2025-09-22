# Run WHISKEY AI Orchestration API Server

Write-Host "🚀 Starting WHISKEY AI Orchestration API Server" -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Green
Write-Host "Make sure you have set your API keys as environment variables:" -ForegroundColor Yellow
Write-Host "   `$env:OPENAI_API_KEY='your-openai-api-key'" -ForegroundColor Cyan
Write-Host "   `$env:ANTHROPIC_API_KEY='your-anthropic-api-key'" -ForegroundColor Cyan
Write-Host "   `$env:GOOGLE_API_KEY='your-google-ai-api-key'" -ForegroundColor Cyan
Write-Host ""
Write-Host "Starting server on http://localhost:5000" -ForegroundColor Yellow
Write-Host ""

# Run the API server
python api_server.py
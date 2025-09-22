@echo off
echo Starting WHISKEY AI Orchestration API Server...
echo ==========================================
echo Make sure you have set your API keys as environment variables:
echo set OPENAI_API_KEY=your-openai-api-key
echo set ANTHROPIC_API_KEY=your-anthropic-api-key
echo set GOOGLE_API_KEY=your-google-ai-api-key
echo.
echo Starting server on http://localhost:5000
echo.

python api_server.py

pause
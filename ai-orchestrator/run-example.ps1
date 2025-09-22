# Run WHISKEY AI Orchestration CLI Example

Write-Host "🚀 Running WHISKEY AI Orchestration CLI Example" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""

# Run a simple example
Write-Host "Testing with a simple question..." -ForegroundColor Yellow
python cli.py --prompt "What is the capital of France?" --task_type "conversation"

Write-Host ""
Write-Host "Testing with a code generation request..." -ForegroundColor Yellow
python cli.py --prompt "Generate a Python function to calculate factorial" --task_type "code_generation"
Param(
  [string]$Root = (Resolve-Path .).Path,
  [string]$Out = "operations-catalog.json"
)

$Jar = Join-Path $PSScriptRoot 'nexus\target\nexus-1.0.0.jar'
& "$PSScriptRoot\nexus\mvnw.cmd" -Pcli -DskipTests package | Out-Host
if (!(Test-Path $Jar)) { Write-Error "Build failed or jar missing: $Jar"; exit 1 }

Write-Host "Running ingest..." -ForegroundColor Cyan
& java -jar $Jar ingest --root=$Root --out=$Out | Out-Host

Write-Host "Catalog summary:" -ForegroundColor Cyan
& java -jar $Jar catalog summary --file=$Out | Out-Host

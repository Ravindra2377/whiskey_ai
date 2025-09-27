Param(
  [string]$Root = (Resolve-Path .).Path,
  [string]$Out = "operations-catalog.json"
)

$Jar = Join-Path $PSScriptRoot 'nexus\target\nexus-1.0.0.jar'

function Invoke-GradleBuild {
  $gradle = Get-Command gradle -ErrorAction SilentlyContinue
  if ($null -ne $gradle) {
    Write-Host "Building via Gradle..." -ForegroundColor Cyan
    & gradle -p (Join-Path $PSScriptRoot 'nexus') bootJar | Out-Host
  }
}

function Invoke-MavenBuild {
  $mavenWrapper = Join-Path $PSScriptRoot 'nexus\mvnw.cmd'
  if (Test-Path $mavenWrapper) {
    Write-Host "Building via Maven wrapper..." -ForegroundColor Cyan
    & $mavenWrapper -Pcli -DskipTests package | Out-Host
  }
}

Invoke-GradleBuild
if (!(Test-Path $Jar)) {
  Write-Warning "Gradle build unavailable or jar missing. Falling back to Maven wrapper."
  Invoke-MavenBuild
}

if (!(Test-Path $Jar)) {
  Write-Error "Build failed or jar missing: $Jar"
  Write-Host "Install Gradle (https://gradle.org/install/) or Maven and re-run." -ForegroundColor Yellow
  exit 1
}

Write-Host "Running ingest..." -ForegroundColor Cyan
& java -jar $Jar ingest --root=$Root --out=$Out | Out-Host

Write-Host "Catalog summary:" -ForegroundColor Cyan
& java -jar $Jar catalog summary --file=$Out | Out-Host

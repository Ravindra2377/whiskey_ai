Param(
  [string]$Audio = "voice-list-python.wav"
)

Write-Host "=== NEXUS Voice Demo ===" -ForegroundColor Cyan


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
    try {
      & $mavenWrapper -q -f "$PSScriptRoot\nexus\pom.xml" -Pcli -DskipTests package
    } catch {
      Write-Warning "Maven wrapper failed. If Maven is installed globally, run: mvn -f nexus/pom.xml -Pcli -DskipTests package"
    }
  }
}

Invoke-GradleBuild
$jar = Join-Path $PSScriptRoot 'nexus\target\nexus-1.0.0.jar'
if (-not (Test-Path $jar)) {
  Write-Warning "Gradle build unavailable or jar missing. Falling back to Maven wrapper."
  Invoke-MavenBuild
}

if (-not (Test-Path $jar)) {
  Write-Warning "Jar not found at $jar. Build may have failed."
  Write-Host "Install Gradle (https://gradle.org/install/) or Maven and re-run." -ForegroundColor Yellow
}

if (-not (Test-Path $Audio)) {
  # Create a tiny placeholder file so the demo runs
  Set-Content -Path $Audio -Value "placeholder" -Encoding ASCII
}

Write-Host "Running voice -> nl pipeline with file: $Audio" -ForegroundColor Cyan
& java -jar $jar voice --file=$Audio --echo

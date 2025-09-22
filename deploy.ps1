# WHISKEY AI Deployment Script
param(
    [string]$ServerIP = "YOUR-SERVER-IP"
)

Write-Host "🚀 Deploying WHISKEY AI to $ServerIP" -ForegroundColor Green

# Build application
Write-Host "📦 Building..." -ForegroundColor Yellow
.\mvnw clean package -DskipTests

# Copy to server
Write-Host "🚚 Copying files..." -ForegroundColor Yellow
& scp "target/nexus-ai-*.jar" "docker-compose.prod.yml" "root@${ServerIP}:/opt/nexus-ai/"

# Deploy
Write-Host "🔄 Deploying..." -ForegroundColor Yellow
$deployScript = @"
cd /opt/nexus-ai
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml up -d
echo \"⏳ Waiting for startup...\"
sleep 60
docker-compose -f docker-compose.prod.yml ps
"@

$deployScript | ssh "root@$ServerIP" "bash -s"

# Health check
Write-Host "🏥 Health check..." -ForegroundColor Yellow
& curl -f "http://${ServerIP}/actuator/health"

Write-Host "✅ Deployment successful!" -ForegroundColor Green
Write-Host "🌐 Access: http://$ServerIP" -ForegroundColor Cyan
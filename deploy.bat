@echo off
setlocal

set SERVER_IP=YOUR-SERVER-IP
echo 🚀 Deploying WHISKEY AI to %SERVER_IP%

REM Build application
echo 📦 Building...
./mvnw clean package -DskipTests

REM Copy to server
echo 🚚 Copying files...
scp target/nexus-ai-*.jar root@%SERVER_IP%:/opt/nexus-ai/
scp docker-compose.prod.yml root@%SERVER_IP%:/opt/nexus-ai/

REM Deploy
echo 🔄 Deploying...
ssh root@%SERVER_IP% << 'EOF'
cd /opt/nexus-ai
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml up -d
echo ⏳ Waiting for startup...
sleep 60
docker-compose -f docker-compose.prod.yml ps
EOF

REM Health check
echo 🏥 Health check...
curl -f http://%SERVER_IP%/actuator/health

echo ✅ Deployment successful!
echo 🌐 Access: http://%SERVER_IP%
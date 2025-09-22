#!/bin/bash
set -e

SERVER_IP="YOUR-SERVER-IP"
echo "🚀 Deploying WHISKEY AI to $SERVER_IP"

# Build application
echo "📦 Building..."
./mvnw clean package -DskipTests

# Copy to server
echo "🚚 Copying files..."
scp target/nexus-ai-*.jar docker-compose.prod.yml root@$SERVER_IP:/opt/nexus-ai/

# Deploy
echo "🔄 Deploying..."
ssh root@$SERVER_IP << 'EOF'
cd /opt/nexus-ai
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml up -d
echo "⏳ Waiting for startup..."
sleep 60
docker-compose -f docker-compose.prod.yml ps
EOF

# Health check
echo "🏥 Health check..."
curl -f http://$SERVER_IP/actuator/health

echo "✅ Deployment successful!"
echo "🌐 Access: http://$SERVER_IP"
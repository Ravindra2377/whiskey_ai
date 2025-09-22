#!/bin/bash
HEALTH_URL="http://localhost/actuator/health"
if curl -f $HEALTH_URL > /dev/null 2>&1; then
    echo "✅ WHISKEY AI is healthy"
else
    echo "❌ WHISKEY AI is down - restarting..."
    cd /opt/nexus-ai
    docker-compose -f docker-compose.prod.yml restart
fi
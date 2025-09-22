#!/bin/bash
BACKUP_DIR="/opt/nexus-ai/backups"
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p $BACKUP_DIR

docker exec nexus-ai-postgres-1 pg_dump -U nexus_admin nexus_ai > $BACKUP_DIR/backup_$DATE.sql
gzip $BACKUP_DIR/backup_$DATE.sql

# Keep last 7 days
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +7 -delete
echo "✅ Backup completed: backup_$DATE.sql.gz"
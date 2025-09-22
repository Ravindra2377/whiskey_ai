# 🚀 Whiskey AI Database Connection Setup Guide

## Current Status
✅ **PostgreSQL**: Running (localhost:5432)
❌ **Redis**: Not running (needed for caching)
❌ **InfluxDB**: Not running (needed for metrics)

## Quick Setup Options

### Option 1: Install Services Locally (Recommended for Development)

#### Redis Installation
1. **Download Redis for Windows**:
   - Visit: https://redis.io/docs/getting-started/installation/install-redis-on-windows/
   - Or use WSL: `wsl --install` then `sudo apt install redis-server`

2. **Start Redis**:
   ```bash
   # If using WSL
   sudo service redis-server start
   
   # Or download Windows executable and run
   redis-server.exe
   ```

#### InfluxDB Installation
1. **Download InfluxDB**:
   - Visit: https://www.influxdata.com/downloads/
   - Download Windows binary

2. **Start InfluxDB**:
   ```bash
   # Extract and run
   influxd.exe
   ```

### Option 2: Use Docker Desktop (Full Setup)

1. **Install Docker Desktop**:
   - Download from: https://www.docker.com/products/docker-desktop/
   - Install and restart computer
   - Start Docker Desktop

2. **Run All Services**:
   ```bash
   docker-compose up -d
   ```

### Option 3: Cloud Services (Production Ready)

#### Redis Cloud
- **Redis Cloud**: https://redis.com/redis-enterprise-cloud/
- **AWS ElastiCache**: Redis-compatible managed service

#### InfluxDB Cloud
- **InfluxDB Cloud**: https://www.influxdata.com/products/influxdb-cloud/
- **AWS Timestream**: Time-series database alternative

## Configuration Files Created

### ✅ Application Properties
- `nexus/src/main/resources/application-prod.properties` - Updated with all database connections
- Environment variables supported for easy deployment

### ✅ Docker Configuration
- `docker-compose.yml` - Basic setup
- `docker-compose.override.yml` - Full development stack with Grafana monitoring
- `redis.conf` - Redis configuration

### ✅ Python Connection Scripts
- `setup_database_connections.py` - Full connection manager
- `check_database_services.py` - Service availability checker
- `database_requirements.txt` - Python dependencies

## Testing Connections

Once all services are running:

```bash
# Check service status
python check_database_services.py

# Test full connections (requires psycopg2, redis, influxdb-client)
python setup_database_connections.py

# Start Nexus application
cd nexus
./mvnw spring-boot:run
```

## Environment Variables

Set these for production deployment:

```bash
# PostgreSQL
DB_HOST=localhost
DB_PORT=5432
DB_NAME=nexus_ai
DB_USER=nexus_admin
DB_PASSWORD=SecurePassword123!

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_DB=0

# InfluxDB
INFLUXDB_URL=http://localhost:8086
INFLUXDB_TOKEN=admin-token
INFLUXDB_ORG=nexus-ai
INFLUXDB_BUCKET=metrics
```

## Next Steps

1. ✅ **PostgreSQL is ready** - Already running
2. 🔧 **Start Redis** - Choose installation method above
3. 🔧 **Start InfluxDB** - Choose installation method above  
4. 🧪 **Test connections** - Run check script
5. 🚀 **Start Nexus app** - `cd nexus && ./mvnw spring-boot:run`
6. 🌐 **Access API** - http://localhost:8094

## API Endpoints

Once running, the platform provides:

- **Health Check**: `GET /actuator/health`
- **Database Status**: `GET /actuator/info`
- **Metrics**: `GET /actuator/metrics`
- **Main API**: `GET /api/whiskey/status`

## Monitoring Stack

With full Docker setup:
- **Grafana Dashboard**: http://localhost:3000 (admin/admin)
- **InfluxDB UI**: http://localhost:8086
- **Redis Monitor**: Connect to localhost:6379

---

The platform is designed for robust, enterprise-grade database integration. Choose the setup method that best fits your environment!
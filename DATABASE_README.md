# WHISKEY AI DATABASE SETUP

## 📋 Overview

This document explains how to set up and use the database components for the WHISKEY AI platform. The setup includes PostgreSQL for primary data storage, Redis for caching, and InfluxDB for analytics.

## 🏗️ Prerequisites

1. Docker and Docker Compose installed
2. Java 11+ installed
3. Maven installed
4. At least 4GB of available RAM

## 🚀 Quick Start

### 1. Start Database Services

```bash
# Using the provided script
./start-databases.sh

# Or directly with Docker Compose
docker-compose up -d
```

### 2. Verify Services are Running

```bash
# Check container status
docker-compose ps

# Expected output should show all services as "Up"
```

### 3. Compile the Application

```bash
cd nexus
./mvnw clean compile
```

### 4. Run Database Tests

```bash
./mvnw test -Dtest=DatabaseConnectionTest
```

## 🗃️ Database Services

### PostgreSQL (Port 5432)
- **Database**: nexus_ai
- **Username**: nexus_admin
- **Password**: SecurePassword123!
- **Purpose**: Primary data storage for clients, tickets, and AI agents

### Redis (Port 6379)
- **Purpose**: Caching and session management
- **Persistence**: Enabled with AOF (Append Only File)

### InfluxDB (Port 8086)
- **Organization**: nexus-ai
- **Bucket**: metrics
- **Username**: admin
- **Password**: SecurePassword123!
- **Purpose**: Time-series metrics and analytics

## 🛠️ Configuration Files

### docker-compose.yml
Defines all database services and their configurations.

### application-dev.properties
Development configuration for connecting to the databases.

### application-prod.properties
Production configuration (uses environment variables for sensitive data).

## 🧪 Testing

### DatabaseConnectionTest
Verifies that the application can connect to the PostgreSQL database and perform basic CRUD operations.

To run:
```bash
./mvnw test -Dtest=DatabaseConnectionTest
```

## 🔧 Troubleshooting

### Common Issues

1. **Port Conflicts**
   - Ensure ports 5432, 6379, and 8086 are available
   - Stop any existing services using these ports

2. **Docker Permission Issues**
   - Run Docker as administrator on Windows
   - Add your user to the docker group on Linux

3. **Connection Refused**
   - Wait 30 seconds after starting containers for initialization
   - Check container logs: `docker-compose logs [service_name]`

4. **Memory Issues**
   - Ensure at least 4GB RAM is available
   - Adjust Docker resource limits if necessary

### Useful Commands

```bash
# View container logs
docker-compose logs postgres
docker-compose logs redis
docker-compose logs influxdb

# Connect to PostgreSQL
docker exec -it nexus-postgres psql -U nexus_admin -d nexus_ai

# Connect to Redis
docker exec -it nexus-redis redis-cli

# Stop all services
docker-compose down

# Remove all data volumes
docker-compose down -v
```

## 📈 Monitoring

### PostgreSQL
```sql
-- View active connections
SELECT count(*) FROM pg_stat_activity;

-- View table sizes
SELECT schemaname, tablename, pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables WHERE schemaname = 'public';
```

### Redis
```bash
# Check Redis info
redis-cli info

# Check memory usage
redis-cli info memory
```

## 🔒 Security Notes

1. **Development Only**: The passwords in the configuration files are for development only
2. **Production**: Use environment variables or secure vaults for sensitive data
3. **Network**: Restrict database access to application servers only
4. **Updates**: Keep database images updated with security patches

## 📚 Additional Resources

- [WHISKEY_DATABASE_SETUP_GUIDE.md](WHISKEY_DATABASE_SETUP_GUIDE.md) - Complete setup guide
- [WHISKEY_DATABASE_SCHEMA.md](WHISKEY_DATABASE_SCHEMA.md) - Database schema documentation
- [WHISKEY_DATABASE_IMPLEMENTATION_SUMMARY.md](WHISKEY_DATABASE_IMPLEMENTATION_SUMMARY.md) - Implementation summary

## 🆘 Support

For issues with the database setup, contact the development team or check the Docker and application logs for error messages.
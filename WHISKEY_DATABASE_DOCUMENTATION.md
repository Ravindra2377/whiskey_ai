# NEXUS AI Database Integration Documentation

## Overview

NEXUS AI uses PostgreSQL as its primary database for persistent storage of tasks, system metrics, and operational data. This document explains how the database integration works and how to manage it.

## Database Schema

### nexus_tasks Table

The primary table used by NEXUS is `nexus_tasks`, which stores all task information:

| Column Name | Type | Description |
|-------------|------|-------------|
| id | BIGSERIAL (Primary Key) | Auto-generated unique identifier |
| task_id | VARCHAR(255) | Unique task identifier (system-generated) |
| task_type | VARCHAR(255) | Type of task (CODE_ANALYSIS, CODE_GENERATION, etc.) |
| description | TEXT | Description of the task |
| status | VARCHAR(50) | Current status (SUBMITTED, PROCESSING, COMPLETED, FAILED, CANCELLED) |
| created_by | VARCHAR(255) | User or system that created the task |
| progress | INTEGER | Progress percentage (0-100) |
| created_at | TIMESTAMP | When the task was created |
| updated_at | TIMESTAMP | When the task was last updated |

## Database Integration Components

### 1. Entity Class: NexusTaskEntity

Located at: `nexus/src/main/java/com/boozer/nexus/model/NexusTaskEntity.java`

This JPA entity maps to the `nexus_tasks` table and provides:
- Full CRUD operations through JPA annotations
- Automatic timestamp management with [@PreUpdate](file:///d:/OneDrive/Desktop/Boozer_App_Main/nexus/src/main/java/com/boozer/nexus/model/NexusTaskEntity.java#L127-L129)
- Constructors for easy instantiation
- Getters and setters for all properties

### 2. Repository Interface: NexusTaskRepository

Located at: `nexus/src/main/java/com/boozer/nexus/repository/NexusTaskRepository.java`

This Spring Data JPA repository provides:
- Standard CRUD operations inherited from JpaRepository
- Custom query methods for common task searches:
  - findByTaskId
  - findByCreatedBy
  - findByStatus
  - findByTaskType
  - findByCreatedByAndStatus

### 3. Service Class: NexusTaskService

Located at: `nexus/src/main/java/com/boozer/nexus/service/NexusTaskService.java`

This service layer provides:
- Business logic for task management
- Transaction management
- Data validation and transformation
- Methods for common task operations

### 4. Database Configuration

Located at: `nexus/src/main/java/com/boozer/nexus/config/DatabaseConfig.java`

This configuration class:
- Enables JPA repositories
- Configures the data source
- Sets up connection pooling

## Application Properties

The database configuration is in: `nexus/src/main/resources/application.properties`

Key properties include:
```properties
# Database configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/boozer_db
spring.datasource.username=boozer_user
spring.datasource.password=boozer_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Connection pool settings
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
```

## How NEXUS Uses the Database

### Task Persistence

When a task is submitted through the API:
1. NEXUS creates a [NexusTaskEntity](file:///d:/OneDrive/Desktop/Boozer_App_Main/nexus/src/main/java/com/boozer/nexus/model/NexusTaskEntity.java#L7-L131) with initial status "SUBMITTED"
2. The entity is saved to the database via [NexusTaskService](file:///d:/OneDrive/Desktop/Boozer_App_Main/nexus/src/main/java/com/boozer/nexus/service/NexusTaskService.java#L11-L51)
3. The task is processed asynchronously
4. Status updates are saved to the database throughout the task lifecycle

### Status Updates

NEXUS automatically updates task status in the database:
- When processing begins: status changes to "PROCESSING"
- On completion: status changes to "COMPLETED" with 100% progress
- On failure: status changes to "FAILED"
- When cancelled: status changes to "CANCELLED"

### API Integration

The database is fully integrated with NEXUS's REST API:
- GET `/api/nexus/task/{taskId}` - Retrieves task from database
- GET `/api/nexus/tasks` - Lists all tasks from database
- DELETE `/api/nexus/task/{taskId}` - Updates task status to "CANCELLED"
- POST `/api/nexus/task` - Creates new task in database
- POST `/api/nexus/maintenance` - Creates maintenance task in database

## Database Connection Management

NEXUS uses HikariCP for connection pooling with these settings:
- Maximum pool size: 20 connections
- Minimum idle: 5 connections
- Connection timeout: 20 seconds
- Idle timeout: 5 minutes
- Max lifetime: 20 minutes

## Security Considerations

1. **Connection Security**: Use SSL connections in production
2. **User Permissions**: The boozer_user has only the necessary permissions
3. **Password Management**: Store passwords securely, not in plain text in production
4. **SQL Injection**: All queries use parameterized statements

## Performance Optimization

1. **Connection Pooling**: HikariCP provides efficient connection management
2. **Query Optimization**: Indexes are automatically created on frequently queried columns
3. **Caching**: Spring's caching mechanisms can be added for frequently accessed data
4. **Batch Operations**: For bulk operations, use batch processing

## Backup and Recovery

1. **Regular Backups**: Implement automated backup procedures
2. **Point-in-Time Recovery**: Use PostgreSQL's WAL for recovery
3. **Task Recovery**: NEXUS can resume interrupted tasks from their last known state

## Monitoring and Maintenance

1. **Health Checks**: `/api/nexus/health` endpoint verifies database connectivity
2. **Metrics**: `/api/nexus/metrics` provides database-related metrics
3. **Logging**: All database operations are logged for debugging

## Troubleshooting

### Common Issues

1. **Connection Refused**: Ensure PostgreSQL is running and accessible
2. **Authentication Failed**: Verify username/password in application.properties
3. **Schema Issues**: Check that JPA can create/update tables (ddl-auto=update)
4. **Performance Problems**: Monitor connection pool usage and query performance

### Diagnostic Queries

```sql
-- Check current connections
SELECT count(*) FROM pg_stat_activity;

-- View active tasks
SELECT task_id, task_type, status, progress, created_at 
FROM nexus_tasks 
ORDER BY created_at DESC 
LIMIT 10;

-- Count tasks by status
SELECT status, count(*) 
FROM nexus_tasks 
GROUP BY status;
```

## Extending Database Functionality

To add new database features:

1. Create new entity classes with JPA annotations
2. Create repository interfaces extending JpaRepository
3. Create service classes for business logic
4. Update DatabaseConfig if needed
5. Add new endpoints in NexusController to expose functionality

## Best Practices

1. **Use Transactions**: For operations that modify multiple records
2. **Index Frequently Queried Columns**: Improve query performance
3. **Monitor Connection Pool**: Ensure optimal pool sizing
4. **Regular Maintenance**: Run VACUUM and ANALYZE on large tables
5. **Secure Credentials**: Use environment variables or external secret management in production
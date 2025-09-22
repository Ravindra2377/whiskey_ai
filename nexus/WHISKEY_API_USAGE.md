# NEXUS AI API Usage Guide

## Overview
NEXUS is an autonomous AI engineer that can perform various software engineering tasks through its REST API. All tasks are persisted in a PostgreSQL database for audit trail and recovery.

## Base URL
```
http://localhost:8085/api/nexus
```

## Database Integration
NEXUS uses PostgreSQL for persistent storage of all tasks and their lifecycle information. The database schema includes a `nexus_tasks` table that tracks:
- Task ID and type
- Description and status
- Creation and update timestamps
- Progress percentage
- Creator information

All API operations automatically interact with the database to ensure data consistency and persistence.

## API Endpoints

### 1. Submit a Task
```
POST /task
```

Submit a task for NEXUS to process.

**Request Body:**
```json
{
  "type": "CODE_MODIFICATION",
  "description": "Description of the task",
  "parameters": {
    "priority": "HIGH"
  },
  "createdBy": "user_name"
}
```

**Response:**
```json
{
  "status": "ACCEPTED",
  "message": "Task submitted successfully",
  "taskId": "1758123365282",
  "taskType": "CODE_MODIFICATION"
}
```

### 2. Get Task Status
```
GET /task/{taskId}
```

Check the status of a specific task.

### 3. Get All Tasks
```
GET /tasks
```

Retrieve all tasks and their statuses.

### 4. Cancel a Task
```
DELETE /task/{taskId}
```

Cancel a specific task.

### 5. System Health
```
GET /health
```

Check the health status of the NEXUS system.

### 6. System Information
```
GET /info
```

Get detailed information about the NEXUS system and its capabilities.

### 7. System Metrics
```
GET /metrics
```

Get system performance metrics.

### 8. Trigger Autonomous Maintenance
```
POST /maintenance
```

Trigger autonomous maintenance tasks.

### 9. Get Recommendations
```
GET /recommendations
```

Get AI-generated recommendations for system improvements.

### 10. Boozer File Management
```
POST /boozer-files/import
GET /boozer-files
GET /boozer-files/path/{filePath}
GET /boozer-files/content/{filePath}
GET /boozer-files/search?query={searchTerm}
GET /boozer-files/type/{fileType}
GET /boozer-files/package/{packageName}
GET /boozer-files/class/{className}
GET /boozer-files/info
```

Manage Boozer application files stored in the NEXUS database.

### 11. Permanent File Migration
Files can be permanently migrated to the NEXUS database and accessed through the Boozer file management endpoints. After migration, the local directory can be cleaned to keep only NEXUS-related files while maintaining access to all content through the API.

## Available Task Types
- CODE_MODIFICATION
- CI_CD_OPERATION
- INFRASTRUCTURE_OPERATION
- MONITORING_OPERATION
- FEATURE_DEVELOPMENT
- BUG_FIX
- SECURITY_PATCH
- PERFORMANCE_OPTIMIZATION
- DATABASE_MIGRATION
- AUTONOMOUS_MAINTENANCE

## Example Usage (PowerShell)

### Submit a Task
```powershell
$task = @{
    type = "CODE_MODIFICATION"
    description = "Add new feature to user authentication"
    parameters = @{ priority = "HIGH" }
    createdBy = "API_USER"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8085/api/nexus/task" -Method POST -Body $task -ContentType "application/json"
```

### Check Task Status
```powershell
Invoke-WebRequest -Uri "http://localhost:8085/api/nexus/task/1758123365282" -Method GET
```

### Get System Health
```powershell
Invoke-WebRequest -Uri "http://localhost:8085/api/nexus/health" -Method GET
```

### Import Boozer Files
```powershell
$importRequest = @{
    directoryPath = "../backend"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8085/api/nexus/boozer-files/import" -Method POST -Body $importRequest -ContentType "application/json"
```

### List Boozer Files
```powershell
Invoke-WebRequest -Uri "http://localhost:8085/api/nexus/boozer-files" -Method GET
```

### Search Boozer Files
```powershell
Invoke-WebRequest -Uri "http://localhost:8085/api/nexus/boozer-files/search?query=user" -Method GET
```

## Integration with Boozer Application

NEXUS can be integrated with the Boozer application to:
1. Automatically analyze and improve code quality
2. Perform automated testing and deployment
3. Monitor application performance
4. Apply security patches
5. Optimize database queries
6. Generate new features based on requirements
7. Access and modify Boozer source code through the database
8. Permanently store files in the database and maintain a clean directory structure

The NEXUS AI system runs independently and can be controlled through its API endpoints, making it a powerful autonomous engineering assistant for the Boozer application.
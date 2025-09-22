# NEXUS AI System API Documentation

## Overview
The NEXUS AI System is a standalone autonomous AI engineer that can perform various software engineering tasks including code analysis, generation, modification, testing, deployment, monitoring, performance optimization, bug fixing, and security patching.

## Base URL
```
http://localhost:8085/api/nexus
```

## API Endpoints

### 1. Submit a Task
**POST** `/task`

Submit a task to NEXUS for processing.

#### Request Body
```json
{
  "type": "string", // Task type (see supported task types)
  "description": "string", // Description of the task
  "parameters": {}, // Additional parameters for the task
  "createdBy": "string" // User or system that created the task
}
```

#### Response
```json
{
  "status": "string", // Status of the request
  "message": "string", // Message describing the result
  "taskId": "string", // ID of the submitted task
  "taskType": "string" // Type of the submitted task
}
```

### 2. Get Task Status
**GET** `/task/{taskId}`

Get the status of a specific task.

#### Response
```json
{
  "taskId": "string",
  "status": "string", // SUBMITTED, PROCESSING, COMPLETED, FAILED, CANCELLED
  "taskType": "string",
  "message": "string",
  "progress": "integer", // 0-100
  "createdAt": "long", // Timestamp
  "completedAt": "long" // Timestamp (null if not completed)
}
```

### 3. Get All Tasks
**GET** `/tasks`

Get a list of all tasks.

#### Response
Array of task status objects (same as above).

### 4. Cancel a Task
**DELETE** `/task/{taskId}`

Cancel a specific task.

#### Response
```json
{
  "status": "string", // SUCCESS or ERROR
  "message": "string"
}
```

### 5. Trigger Autonomous Maintenance
**POST** `/maintenance`

Trigger autonomous system maintenance.

#### Response
```json
{
  "status": "string", // ACCEPTED or ERROR
  "message": "string",
  "taskId": "string",
  "taskType": "string"
}
```

### 6. Get System Health
**GET** `/health`

Get the health status of the NEXUS system.

#### Response
```json
{
  "status": "string", // HEALTHY
  "version": "string",
  "timestamp": "long"
}
```

### 7. Get System Information
**GET** `/info`

Get detailed information about the NEXUS system.

#### Response
```json
{
  "name": "string",
  "version": "string",
  "description": "string",
  "capabilities": {
    "taskTypes": ["string"],
    "supportedOperations": ["string"]
  },
  "system": {
    "javaVersion": "string",
    "osName": "string",
    "osVersion": "string",
    "availableProcessors": "integer",
    "maxMemory": "long"
  }
}
```

### 8. Get System Metrics
**GET** `/metrics`

Get current system metrics.

#### Response
```json
{
  "cpuUsage": "double",
  "memoryUsage": "double",
  "diskUsage": "double",
  "activeTasks": "integer",
  "completedTasks": "integer",
  "failedTasks": "integer",
  "timestamp": "long"
}
```

### 9. Get Recommendations
**GET** `/recommendations`

Get system recommendations.

#### Response
```json
{
  "status": "string",
  "count": "integer",
  "recommendations": ["string"]
}
```

## Supported Task Types
- CODE_ANALYSIS
- CODE_GENERATION
- CODE_MODIFICATION
- TESTING
- DEPLOYMENT
- MONITORING
- PERFORMANCE_OPTIMIZATION
- BUG_FIXING
- SECURITY_PATCHING
- AUTONOMOUS_MAINTENANCE

## Example Usage

### Submit a Code Analysis Task
```bash
curl -X POST http://localhost:8085/api/nexus/task \
  -H "Content-Type: application/json" \
  -d '{
    "type": "CODE_ANALYSIS",
    "description": "Analyze the user authentication module",
    "parameters": {
      "module": "user-auth",
      "depth": "detailed"
    },
    "createdBy": "admin"
  }'
```

### Check Task Status
```bash
curl -X GET http://localhost:8085/api/nexus/task/1640995200000
```

### Trigger Autonomous Maintenance
```bash
curl -X POST http://localhost:8085/api/nexus/maintenance
```

## Actuator Endpoints
The NEXUS system also exposes Spring Boot Actuator endpoints for monitoring:

- Health: `http://localhost:8085/actuator/health`
- Info: `http://localhost:8085/actuator/info`

## Starting the NEXUS System
To start the NEXUS AI system:

```bash
cd D:\OneDrive\Desktop\NEXUS_Standalone\nexus-ai
.\mvnw spring-boot:run
```

The system will start on port 8085.
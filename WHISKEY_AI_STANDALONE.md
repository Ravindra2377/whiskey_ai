# NEXUS AI Standalone System

## Overview
This is the standalone version of the NEXUS AI system, which has been extracted from the Boozer application and can now operate independently.

## Directory Structure
```
nexus-ai/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── nexus/
│   │   │           └── ai/
│   │   │               ├── CICDAgent.java
│   │   │               ├── FeedbackLoop.java
│   │   │               ├── InfraAgent.java
│   │   │               ├── MonitoringAgent.java
│   │   │               ├── PolicyEngine.java
│   │   │               ├── RepoAgent.java
│   │   │               ├── NexusApplication.java
│   │   │               ├── NexusConfig.java
│   │   │               ├── NexusController.java
│   │   │               ├── NexusOrchestrator.java
│   │   │               ├── NexusResult.java
│   │   │               ├── NexusService.java
│   │   │               ├── NexusTask.java
│   │   │               └── dto/
│   │   │                   ├── TaskRequestDTO.java
│   │   │                   └── TaskResponseDTO.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── nexus/
│                   └── ai/
├── target/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Key Changes from Boozer Integration
1. **Package Structure**: Updated from `com.boozer.nexus` to `com.nexus.ai`
2. **Maven Group ID**: Updated from `com.boozer` to `com.nexus`
3. **Project Name**: Updated to "NEXUS AI Standalone System"
4. **Independent Operation**: Can now run without the Boozer application

## Building the System
To build the NEXUS AI standalone system:
```bash
./mvnw clean package
```

## Running the System
To run the NEXUS AI standalone system:
```bash
java -jar target/nexus-1.0.0.jar
```

The system will start on port 8085 by default.

## API Endpoints
Once running, the NEXUS AI system exposes the following REST API endpoints:

- `POST /api/nexus/task` - Submit a task for processing
- `GET /api/nexus/task/{taskId}` - Get the status of a specific task
- `GET /api/nexus/tasks` - Get all tasks
- `DELETE /api/nexus/task/{taskId}` - Cancel a task
- `GET /api/nexus/health` - System health check
- `GET /api/nexus/info` - System information
- `GET /api/nexus/metrics` - System metrics
- `GET /api/nexus/recommendations` - AI-generated recommendations
- `POST /api/nexus/maintenance` - Trigger autonomous maintenance

## Supported Task Types
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

## Integration
The NEXUS AI standalone system can be integrated with any application through its REST API. The system provides autonomous software engineering capabilities including code analysis, modification, testing, deployment, and maintenance.

## Configuration
The system can be configured through the `application.properties` file in `src/main/resources/`.
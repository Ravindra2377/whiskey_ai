# NEXUS AI System

NEXUS is an advanced AI-powered enterprise technical services platform that provides comprehensive support for performance optimization, security scanning, code refactoring, infrastructure scaling, and API integration. It features specialized AI agents, enterprise client management, and full PostgreSQL database integration.

## Features

- Advanced technical services (performance optimization, security scanning, code refactoring, infrastructure scaling, API integration)
- Specialized AI agents for domain-specific tasks
- Enterprise client management with isolated tenant environments
- Autonomous code analysis and modification
- CI/CD pipeline automation
- Infrastructure management
- Monitoring and feedback loops
- Policy enforcement
- Full PostgreSQL database integration for task persistence

## Prerequisites

- Java 11 or higher
- Maven 3.6+
- PostgreSQL 10+

## Database Setup

NEXUS uses PostgreSQL for persistent storage of tasks and their lifecycle information. Before running NEXUS, you need to:

1. Install PostgreSQL
2. Create the database and user:

```sql
CREATE USER boozer_user WITH PASSWORD 'pASSWORD@11';
CREATE DATABASE boozer_db OWNER boozer_user;
GRANT ALL PRIVILEGES ON DATABASE boozer_db TO boozer_user;
```

The database configuration is in `src/main/resources/application.properties`.

## Building and Running

The CLI is packaged with Gradle. From the repo root:

```bash
# Build the CLI jar
gradle -p nexus bootJar

# Run the CLI (non-interactive)
java -jar nexus/target/nexus-1.0.0.jar --help
```

Maven remains available as a fallback:

```bash
nexus/mvnw -pl :nexus -am -DskipTests package
```

On Windows use the corresponding `.cmd` wrappers.

## API Documentation

See [NEXUS_API_USAGE.md](NEXUS_API_USAGE.md) for detailed API documentation.

## Database Integration

All tasks are stored in the `nexus_tasks` table with full lifecycle tracking:
- Task submission and progress tracking
- Status updates in real-time
- Complete audit trail
- Recovery capabilities for interrupted tasks

See [NEXUS_DATABASE_DOCUMENTATION.md](NEXUS_DATABASE_DOCUMENTATION.md) for detailed database documentation.

## Testing

```bash
# Run unit tests
./mvnw test
```

## Accessing the System

Once running, NEXUS is accessible at:
- API Base URL: http://localhost:8094/api/nexus
- Health Check: http://localhost:8094/api/nexus/health
- API Docs: http://localhost:8094/api/nexus/info

## Voice Assistant

NEXUS now supports a voice-driven interface backed by OpenAI Whisper.

1. Export your OpenAI API key:
	```bash
	export NEXUS_OPENAI_KEY=sk-...
	```
	(On Windows PowerShell: `setx NEXUS_OPENAI_KEY "sk-..."` and restart the shell.)
2. Connect a microphone and ensure your OS grants microphone permission to Java.
3. Start the live assistant:
	```bash
	java -jar nexus/target/nexus-1.0.0.jar voice --live --echo
	```
4. Say "Hey NEXUS" followed by a command, e.g. "show system health". Say "Hey NEXUS exit" to quit.

Voice interactions and their outcomes are automatically logged to PostgreSQL whenever the CLI is launched with database support enabled (`nexus.db.enabled=true`).

## License

This project is licensed under the MIT License.
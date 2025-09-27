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
- Context-aware voice assistant with confirmations, repeats, and adaptive hints
- Live voice analytics with REST endpoint and optional web dashboard

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

The database configuration is in `src/main/resources/application.properties`. CLI sessions also honour `src/main/resources/application-cli.yml`, which supports environment-driven toggles:

```yaml
nexus:
	db:
		enabled: ${NEXUS_DB_ENABLED:false}

spring:
	datasource:
		url: ${NEXUS_DB_URL:}
		username: ${NEXUS_DB_USER:}
		password: ${NEXUS_DB_PASSWORD:}
		driver-class-name: ${NEXUS_DB_DRIVER:org.postgresql.Driver}
	jpa:
		hibernate:
			ddl-auto: ${NEXUS_DB_DDL:update}
		show-sql: ${NEXUS_DB_SHOW_SQL:false}
```
Setting `NEXUS_DB_ENABLED=true` alongside your datasource credentials enables persistence and analytics without editing property files. Provide optional overrides for driver, schema management (`NEXUS_DB_DDL`) and SQL logging (`NEXUS_DB_SHOW_SQL`) as needed.

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


Advanced options:

- `--no-wake-word`: treat every transcript as a command (say `exit` to stop).
- `--echo`: prints incremental transcript fragments as they stream in.
- Auto-recovery will restart the microphone if the input stream drops during a session.
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

1. Export your OpenAI API key (either variable name works):
	```bash
	export NEXUS_OPENAI_KEY=sk-...
	# or
	export OPENAI_API_KEY=sk-...
	```
	(On Windows PowerShell: `setx NEXUS_OPENAI_KEY "sk-..."` or `setx OPENAI_API_KEY "sk-..."`, then restart the shell.)
2. Connect a microphone and ensure your OS grants microphone permission to Java.
3. Start the live assistant:
	```bash
	java -jar nexus/target/nexus-1.0.0.jar voice --live --echo
	```
4. Say "Hey NEXUS" followed by a command, e.g. "show system health". Say "Hey NEXUS exit" to quit.

To validate your environment before going live, run:

```bash
java -jar nexus/target/nexus-1.0.0.jar voice --check-config
```

This confirms API keys, microphone support, and database logging availability.

Voice interactions and their outcomes are automatically logged to PostgreSQL whenever the CLI is launched with database support enabled (`nexus.db.enabled=true`).

Conversation tips:

- Say “repeat that” or “do it again” to replay the most recent successful automation.
- If Nexus prompts for confirmation, simply answer “yes” or “no”; anything else cancels the pending action.
- Adaptive hints appear when wake-word timing, microphone noise, or configuration issues are detected.

To review voice usage analytics once logging is enabled:

```bash
java -jar nexus/target/nexus-1.0.0.jar analytics --voice
```

Expose the same metrics for dashboards by adding `--server[=PORT]` (defaults to 8088):

```bash
java -jar nexus/target/nexus-1.0.0.jar analytics --voice --server
# or specify a port
java -jar nexus/target/nexus-1.0.0.jar analytics --voice --server=9090
```

The server provides an HTML summary at `/`, JSON metrics at `/metrics`, and a `/health` endpoint suitable for uptime checks.

For a richer view, run the companion dashboard:

```bash
cd nexus/voice-analytics-dashboard
npm install
npm run start
# Visit http://localhost:3000 and set the API base URL to your analytics port (default http://localhost:8088)
```

## AI Code Generation

The `generate` command turns natural language specifications into production-ready code via OpenAI's GPT models.

1. Ensure `NEXUS_OPENAI_KEY` or `OPENAI_API_KEY` is set (the same key used for voice features).
2. Describe the desired implementation:

```bash
java -jar nexus/target/nexus-1.0.0.jar generate --spec "Create a Spring REST controller for customer onboarding" --language=java --include-tests
```

Key options:

- `--language=<lang>`: Target language (default `java`).
- `--include-tests`: Ask the model to produce companion tests.
- `--output=path`: Write the generated implementation to a file.
- `--tests-output=path`: Write generated tests to a separate file.
- `--model=gpt-4o-mini`: Override the model; defaults to `gpt-4o-mini`.
- `--temperature=0.2`: Control creativity (0-1).

Notes and generation metadata are printed after each run for quick validation. Remember to review and harden AI-generated code before deploying to production systems.

Tip: Natural language requests like `nl "Hey NEXUS, generate a Python service with tests"` now route directly into the `generate` command while maintaining conversational context. If NEXUS needs confirmation (for example, deployments or large code generations captured via voice), just reply "yes" to resume the pending action, or "no" to cancel.
Natural language also understands voice analytics intents—ask `nl "start the voice analytics server"` to launch the HTTP endpoint or `nl "show top voice intents"` for a quick summary inside the CLI. See [`docs/voice-intent-flow.md`](docs/voice-intent-flow.md) for a full end-to-end sequence diagram of the voice pipeline.

## License

This project is licensed under the MIT License.
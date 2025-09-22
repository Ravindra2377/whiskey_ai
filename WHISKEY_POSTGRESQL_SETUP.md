# NEXUS AI PostgreSQL Database Setup Guide

This guide will help you set up PostgreSQL for the NEXUS AI system and connect it to your database.

## Prerequisites

Before proceeding, ensure you have:
- NEXUS AI system (already included in your project)
- Administrative privileges on your system

## Step 1: Install PostgreSQL

1. Download PostgreSQL from the official website: https://www.postgresql.org/download/windows/
2. Run the installer and follow these settings:
   - Port: 5432 (default)
   - Username: postgres
   - Password: Choose a secure password and remember it
   - Database: postgres (default)

## Step 2: Create Database and User for NEXUS

After installing PostgreSQL, create the database and user for NEXUS:

1. Open pgAdmin or use psql command line tool
2. Connect to PostgreSQL with the postgres user
3. Run these SQL commands:

```sql
-- Create database user for NEXUS
CREATE USER boozer_user WITH PASSWORD 'boozer_password';

-- Create development database  
CREATE DATABASE boozer_dev OWNER boozer_user;

-- Create production database
CREATE DATABASE boozer_db OWNER boozer_user;

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE boozer_dev TO boozer_user;
GRANT ALL PRIVILEGES ON DATABASE boozer_db TO boozer_user;
```

## Step 3: Verify Database Configuration

NEXUS is already configured to connect to PostgreSQL. The configuration is in:
`nexus/src/main/resources/application.properties`

The default configuration is:
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
```

If you used different credentials during PostgreSQL setup, update these values accordingly.

## Step 4: Database Schema

NEXUS will automatically create the required database schema when it starts. The system uses one main table:

### nexus_tasks Table
- `id`: Primary key (auto-generated)
- `task_id`: Unique task identifier
- `task_type`: Type of task (e.g., CODE_ANALYSIS, CODE_GENERATION)
- `description`: Task description
- `status`: Current status (SUBMITTED, PROCESSING, COMPLETED, FAILED, CANCELLED)
- `created_by`: User who created the task
- `progress`: Task progress percentage (0-100)
- `created_at`: Timestamp when task was created
- `updated_at`: Timestamp when task was last updated

## Step 5: Start NEXUS with PostgreSQL

Navigate to the NEXUS directory and start the application:

```bash
cd nexus
./mvnw spring-boot:run
```

Or on Windows:
```cmd
cd nexus
mvnw.cmd spring-boot:run
```

## Step 6: Verify Connection

Once NEXUS is running, you can verify the database connection:

1. Check the logs for successful database connection messages
2. Access the health endpoint: `http://localhost:8085/api/nexus/health`
3. Submit a test task and verify it appears in the database

## Troubleshooting

### Common Issues:

1. **Connection Refused**: Ensure PostgreSQL is running and accepting connections on port 5432
2. **Authentication Failed**: Verify username and password in application.properties
3. **Database Not Found**: Ensure the database exists and the user has access
4. **Driver Not Found**: Ensure PostgreSQL JDBC driver is in the classpath (already included in pom.xml)

### Testing Database Connection:

You can test the database connection with this Python script:

```python
import psycopg2

try:
    connection = psycopg2.connect(
        host="localhost",
        database="boozer_db",
        user="boozer_user",
        password="boozer_password",
        port="5432"
    )
    
    cursor = connection.cursor()
    cursor.execute("SELECT version();")
    record = cursor.fetchone()
    print("Connected to PostgreSQL:", record)
    
except Exception as error:
    print("Error connecting to PostgreSQL:", error)
    
finally:
    if 'connection' in locals():
        cursor.close()
        connection.close()
```

## Database Management

### Viewing Tasks in Database:

You can query the tasks directly from the database:

```sql
-- View all tasks
SELECT * FROM nexus_tasks;

-- View tasks by status
SELECT * FROM nexus_tasks WHERE status = 'COMPLETED';

-- View recent tasks
SELECT * FROM nexus_tasks ORDER BY created_at DESC LIMIT 10;
```

### Backup and Maintenance:

Regular database maintenance is recommended:
1. Set up automated backups
2. Monitor database performance
3. Optimize queries as needed
4. Apply security updates

## NEXUS Database Integration Features

The NEXUS AI system uses the database for:

1. **Task Persistence**: All tasks are stored in the database with full lifecycle tracking
2. **Status Updates**: Real-time status updates are saved to the database
3. **Audit Trail**: Complete history of all AI operations
4. **Reporting**: Query task history and performance metrics
5. **Recovery**: Restart interrupted tasks from their last known state

## Security Considerations

1. Use strong passwords for database users
2. Restrict database access to only necessary applications
3. Regularly update PostgreSQL to the latest stable version
4. Use SSL connections in production environments
5. Implement proper backup and disaster recovery procedures

## Next Steps

After setting up PostgreSQL:
1. Start NEXUS AI system
2. Submit test tasks through the API
3. Monitor database for proper task storage
4. Explore the API endpoints for task management
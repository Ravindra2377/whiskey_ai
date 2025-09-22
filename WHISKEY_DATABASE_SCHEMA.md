# WHISKEY AI DATABASE SCHEMA

## 📊 Entity Relationship Diagram

```mermaid
erDiagram
    CLIENTS ||--o{ TECHNICAL_TICKETS : "creates"
    CLIENTS ||--o{ AI_AGENTS : "uses"
    
    CLIENTS {
        bigint id PK
        varchar client_id UK
        varchar company_name
        varchar contact_email
        varchar tier
        jsonb configuration
        timestamp created_at
        timestamp updated_at
    }
    
    TECHNICAL_TICKETS {
        bigint id PK
        varchar ticket_id UK
        bigint client_id FK
        varchar title
        text description
        varchar status
        jsonb metadata
        timestamp created_at
        timestamp resolved_at
    }
    
    AI_AGENTS {
        bigint id PK
        varchar agent_id UK
        varchar agent_name
        varchar agent_type
        jsonb capabilities
        double precision performance_score
        timestamp created_at
    }
```

## 🏢 CLIENTS Table

### Description
Stores information about enterprise clients using the WHISKEY AI platform.

### Columns
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| client_id | VARCHAR | UNIQUE, NOT NULL | External client identifier |
| company_name | VARCHAR | NOT NULL | Legal name of the company |
| contact_email | VARCHAR | | Primary contact email |
| tier | VARCHAR | | Service tier (STARTER, PROFESSIONAL, ENTERPRISE, GLOBAL_ENTERPRISE) |
| configuration | JSONB | | Flexible client configuration settings |
| created_at | TIMESTAMP | | Record creation timestamp |
| updated_at | TIMESTAMP | | Record last update timestamp |

### Indexes
- Primary Key: id
- Unique Constraint: client_id
- Index on tier (for tier-based queries)

## 🎫 TECHNICAL_TICKETS Table

### Description
Tracks technical support tickets created by clients for AI-related issues.

### Columns
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| ticket_id | VARCHAR | UNIQUE, NOT NULL | External ticket identifier |
| client_id | BIGINT | FOREIGN KEY, NOT NULL | Reference to CLIENTS.id |
| title | VARCHAR | NOT NULL | Brief ticket title |
| description | TEXT | | Detailed ticket description |
| status | VARCHAR | | Current ticket status (OPEN, IN_PROGRESS, RESOLVED, CLOSED) |
| metadata | JSONB | | Flexible ticket metadata |
| created_at | TIMESTAMP | | Ticket creation timestamp |
| resolved_at | TIMESTAMP | | Ticket resolution timestamp |

### Indexes
- Primary Key: id
- Unique Constraint: ticket_id
- Foreign Key: client_id → CLIENTS.id
- Index on client_id and status (for client ticket queries)

## 🤖 AI_AGENTS Table

### Description
Catalogs the various AI agents available in the WHISKEY platform.

### Columns
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| agent_id | VARCHAR | UNIQUE, NOT NULL | External agent identifier |
| agent_name | VARCHAR | NOT NULL | Human-readable agent name |
| agent_type | VARCHAR | | Agent specialization (DATABASE_SPECIALIST, CLOUD_EXPERT, SECURITY_ANALYST, DEVOPS_ENGINEER) |
| capabilities | JSONB | | Flexible agent capabilities definition |
| performance_score | DOUBLE PRECISION | | Numerical performance rating |
| created_at | TIMESTAMP | | Agent creation timestamp |

### Indexes
- Primary Key: id
- Unique Constraint: agent_id
- Index on agent_type (for type-based queries)

## 🔗 Relationships

### CLIENTS → TECHNICAL_TICKETS
- One-to-Many relationship
- A client can create multiple technical tickets
- Enforced by foreign key constraint on TECHNICAL_TICKETS.client_id

### CLIENTS → AI_AGENTS
- One-to-Many relationship
- A client can use multiple AI agents
- This relationship is conceptual and may be implemented through metadata rather than direct foreign keys

## 🧠 JSONB Column Usage

### CLIENTS.configuration
Stores flexible client settings such as:
- API rate limits
- Feature flags
- Custom integration configurations
- Billing preferences

### TECHNICAL_TICKETS.metadata
Stores ticket-specific information such as:
- Error logs and stack traces
- System information
- Reproduction steps
- Resolution notes

### AI_AGENTS.capabilities
Stores agent-specific capabilities such as:
- Supported operations
- Performance benchmarks
- Integration configurations
- Specialization parameters

## 🔐 Security Considerations

1. **Data Encryption**: Sensitive data should be encrypted at rest
2. **Access Control**: Database roles should be properly configured
3. **Audit Logging**: All data modifications should be logged
4. **Connection Security**: Use SSL/TLS for database connections
5. **Parameterized Queries**: Prevent SQL injection through proper parameterization

## 📈 Performance Considerations

1. **Indexing**: Proper indexes on frequently queried columns
2. **Connection Pooling**: HikariCP for efficient connection management
3. **Caching**: Redis for frequently accessed data
4. **Partitioning**: Consider table partitioning for large datasets
5. **Query Optimization**: Regular query plan analysis

## 🔄 Data Lifecycle

### CLIENTS
- Creation: When a new enterprise client signs up
- Updates: When client information or configuration changes
- Archival: When a client terminates their service

### TECHNICAL_TICKETS
- Creation: When a client submits a support ticket
- Updates: As the ticket progresses through resolution
- Closure: When the ticket is resolved or closed

### AI_AGENTS
- Creation: When a new AI agent is deployed
- Updates: When agent capabilities or performance metrics change
- Deletion: When an agent is retired from service

## 📊 Analytics Considerations

The schema supports various analytical queries such as:
- Client tier distribution
- Ticket resolution time analysis
- Agent performance trending
- Client engagement metrics
- Support load balancing

This schema provides a robust foundation for the WHISKEY AI platform's data needs while maintaining flexibility for future enhancements.
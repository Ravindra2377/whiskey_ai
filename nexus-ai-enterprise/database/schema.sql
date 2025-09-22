-- NEXUS AI Enterprise Platform - Database Schema

-- Create database
CREATE DATABASE nexus_ai_enterprise;

-- Connect to database
\c nexus_ai_enterprise;

-- Core enterprise tables

-- Tenants table
CREATE TABLE tenants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_name VARCHAR(255) NOT NULL,
    subscription_tier VARCHAR(50) NOT NULL DEFAULT 'startup',
    admin_email VARCHAR(255) NOT NULL,
    region VARCHAR(50) NOT NULL DEFAULT 'us-east-1',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    status VARCHAR(50) NOT NULL DEFAULT 'active'
);

-- Integrations table
CREATE TABLE integrations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID REFERENCES tenants(id) ON DELETE CASCADE,
    system_type VARCHAR(100) NOT NULL,
    system_name VARCHAR(255),
    config JSONB,
    status VARCHAR(50) NOT NULL DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Support tickets table
CREATE TABLE support_tickets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID REFERENCES tenants(id) ON DELETE CASCADE,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    priority VARCHAR(50) NOT NULL DEFAULT 'medium',
    status VARCHAR(50) NOT NULL DEFAULT 'open',
    resolution TEXT,
    ai_confidence FLOAT,
    assigned_to VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    resolved_at TIMESTAMP
);

-- Usage events table
CREATE TABLE usage_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID REFERENCES tenants(id) ON DELETE CASCADE,
    event_type VARCHAR(100) NOT NULL,
    details JSONB,
    billable_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    created_at TIMESTAMP DEFAULT NOW()
);

-- Invoices table
CREATE TABLE invoices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID REFERENCES tenants(id) ON DELETE CASCADE,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    status VARCHAR(50) NOT NULL DEFAULT 'pending',
    due_date TIMESTAMP NOT NULL,
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Connections table
CREATE TABLE connections (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID REFERENCES tenants(id) ON DELETE CASCADE,
    integration_id UUID REFERENCES integrations(id) ON DELETE CASCADE,
    connection_type VARCHAR(100) NOT NULL,
    connection_details JSONB,
    status VARCHAR(50) NOT NULL DEFAULT 'disconnected',
    last_connected TIMESTAMP,
    health_status VARCHAR(50) DEFAULT 'unknown',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Indexes for performance
CREATE INDEX idx_tenants_subscription_tier ON tenants(subscription_tier);
CREATE INDEX idx_tenants_status ON tenants(status);
CREATE INDEX idx_integrations_tenant_id ON integrations(tenant_id);
CREATE INDEX idx_integrations_system_type ON integrations(system_type);
CREATE INDEX idx_support_tickets_tenant_id ON support_tickets(tenant_id);
CREATE INDEX idx_support_tickets_status ON support_tickets(status);
CREATE INDEX idx_support_tickets_priority ON support_tickets(priority);
CREATE INDEX idx_usage_events_tenant_id ON usage_events(tenant_id);
CREATE INDEX idx_usage_events_event_type ON usage_events(event_type);
CREATE INDEX idx_usage_events_created_at ON usage_events(created_at);
CREATE INDEX idx_invoices_tenant_id ON invoices(tenant_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_connections_tenant_id ON connections(tenant_id);
CREATE INDEX idx_connections_status ON connections(status);

-- Sample data for testing
INSERT INTO tenants (company_name, subscription_tier, admin_email, region) 
VALUES 
    ('Acme Corporation', 'enterprise', 'admin@acme.com', 'us-west-2'),
    ('Globex Inc', 'scaleup', 'admin@globex.com', 'eu-west-1'),
    ('Initech LLC', 'startup', 'admin@initech.com', 'us-east-1');

-- Sample integrations
INSERT INTO integrations (tenant_id, system_type, system_name, config, status)
SELECT id, 'web_service', 'API Gateway', '{"url": "https://api.acme.com", "timeout": 30}', 'active'
FROM tenants WHERE company_name = 'Acme Corporation';

-- Sample support tickets
INSERT INTO support_tickets (tenant_id, title, description, priority, status)
SELECT id, 'Database Connection Timeout', 'Experiencing timeouts when connecting to PostgreSQL database', 'high', 'open'
FROM tenants WHERE company_name = 'Acme Corporation';

-- Sample usage events
INSERT INTO usage_events (tenant_id, event_type, details, billable_amount)
SELECT id, 'api_call', '{"endpoint": "/api/v1/users", "method": "GET"}', 0.01
FROM tenants WHERE company_name = 'Acme Corporation';

-- Grant permissions
GRANT ALL PRIVILEGES ON TABLE tenants TO nexus_ai_user;
GRANT ALL PRIVILEGES ON TABLE integrations TO nexus_ai_user;
GRANT ALL PRIVILEGES ON TABLE support_tickets TO nexus_ai_user;
GRANT ALL PRIVILEGES ON TABLE usage_events TO nexus_ai_user;
GRANT ALL PRIVILEGES ON TABLE invoices TO nexus_ai_user;
GRANT ALL PRIVILEGES ON TABLE connections TO nexus_ai_user;

-- Grant usage on sequences
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO nexus_ai_user;
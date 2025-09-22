-- Database schema for WHISKEY AI Quantitative Development

-- Table for financial data
CREATE TABLE IF NOT EXISTS financial_data (
    id BIGSERIAL PRIMARY KEY,
    asset_symbol VARCHAR(20) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    open_price DECIMAL(18, 8) NOT NULL,
    high_price DECIMAL(18, 8) NOT NULL,
    low_price DECIMAL(18, 8) NOT NULL,
    close_price DECIMAL(18, 8) NOT NULL,
    volume BIGINT NOT NULL,
    data_source VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_financial_data_asset_symbol ON financial_data(asset_symbol);
CREATE INDEX IF NOT EXISTS idx_financial_data_timestamp ON financial_data(timestamp);
CREATE INDEX IF NOT EXISTS idx_financial_data_asset_timestamp ON financial_data(asset_symbol, timestamp);

-- Table for trading strategies
CREATE TABLE IF NOT EXISTS trading_strategies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    strategy_type VARCHAR(50) NOT NULL,
    parameters JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for trade executions
CREATE TABLE IF NOT EXISTS trade_executions (
    id BIGSERIAL PRIMARY KEY,
    strategy_id BIGINT REFERENCES trading_strategies(id),
    asset_symbol VARCHAR(20) NOT NULL,
    quantity DECIMAL(18, 8) NOT NULL,
    order_type VARCHAR(20) NOT NULL,
    price DECIMAL(18, 8),
    execution_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    result JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for risk analysis
CREATE TABLE IF NOT EXISTS risk_analysis (
    id BIGSERIAL PRIMARY KEY,
    portfolio_id VARCHAR(100),
    risk_model VARCHAR(50) NOT NULL,
    timeframe VARCHAR(20) NOT NULL,
    metrics JSONB,
    scenarios JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for alpha factors
CREATE TABLE IF NOT EXISTS alpha_factors (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    factor_type VARCHAR(50) NOT NULL,
    historical_performance JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for AI models
CREATE TABLE IF NOT EXISTS ai_models (
    id BIGSERIAL PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL,
    domain VARCHAR(50) NOT NULL,
    version VARCHAR(20),
    training_data TEXT,
    performance_metrics TEXT,
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for client tenants
CREATE TABLE IF NOT EXISTS client_tenants (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) UNIQUE NOT NULL,
    client_name VARCHAR(100) NOT NULL,
    client_email VARCHAR(100),
    client_industry VARCHAR(50),
    subscription_tier VARCHAR(20),
    contact_person VARCHAR(100),
    company_size VARCHAR(20),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for client tenants
CREATE INDEX IF NOT EXISTS idx_client_tenants_tenant_id ON client_tenants(tenant_id);
CREATE INDEX IF NOT EXISTS idx_client_tenants_subscription_tier ON client_tenants(subscription_tier);
CREATE INDEX IF NOT EXISTS idx_client_tenants_status ON client_tenants(status);
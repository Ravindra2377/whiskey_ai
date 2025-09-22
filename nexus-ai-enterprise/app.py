from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import Dict, Any, List
import uuid
import asyncio

from nexus_ai.connectors.network_discovery import NetworkDiscoveryEngine
from nexus_ai.connectors.config_generator import IntegrationConfigGenerator
from nexus_ai.connectors.integration_engine import UniversalIntegrationEngine
from nexus_ai.platform.tenant_manager import TenantManager
from nexus_ai.support.ai_engine import EnterpriseSupportEngine
from nexus_ai.billing.revenue_engine import RevenueEngine

app = FastAPI(
    title="Nexus AI Enterprise Platform API",
    description="API for the Nexus AI Enterprise Platform",
    version="1.0.0"
)

# Initialize platform components
discovery_engine = NetworkDiscoveryEngine()
config_generator = IntegrationConfigGenerator()
integration_engine = UniversalIntegrationEngine()
tenant_manager = TenantManager()
support_engine = EnterpriseSupportEngine()
revenue_engine = RevenueEngine()

# Pydantic models for request/response
class CompanyInfo(BaseModel):
    company_name: str
    subscription_tier: str = "basic"

class SupportTicket(BaseModel):
    ticket_id: str = None
    title: str
    description: str
    priority: str = "medium"

class UsageEvent(BaseModel):
    event_type: str
    quantity: int = 0
    details: Dict[str, Any] = {}

class DiscoveryRequest(BaseModel):
    company_domain: str

class IntegrationResponse(BaseModel):
    integration_id: str
    connections_established: List[Dict[str, Any]]
    failed_connections: List[Dict[str, Any]]
    duration_seconds: float

# API Routes

@app.get("/")
async def root():
    return {"message": "Nexus AI Enterprise Platform API"}

# Network Discovery Endpoints
@app.post("/discovery/network")
async def discover_network(request: DiscoveryRequest):
    """Discover all systems in an enterprise network"""
    try:
        result = await discovery_engine.discover_enterprise_systems(request.company_domain)
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# Integration Config Endpoints
@app.post("/config/generate")
async def generate_config(discovered_systems: Dict[str, Any]):
    """Generate integration configuration for discovered systems"""
    try:
        config = await config_generator.generate_integration_config(discovered_systems)
        return config
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# Integration Engine Endpoints
@app.post("/integration/execute", response_model=IntegrationResponse)
async def execute_integration(config: Dict[str, Any]):
    """Execute integration with discovered systems"""
    try:
        result = await integration_engine.execute_integration(config)
        return IntegrationResponse(**result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# Tenant Management Endpoints
@app.post("/tenants")
async def create_tenant(company_info: CompanyInfo):
    """Create a new enterprise tenant"""
    try:
        tenant = await tenant_manager.create_tenant(company_info.dict())
        return tenant
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/tenants")
async def list_tenants():
    """List all enterprise tenants"""
    try:
        tenants = await tenant_manager.list_tenants()
        return tenants
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/tenants/{tenant_id}")
async def get_tenant(tenant_id: str):
    """Get information about a specific tenant"""
    try:
        tenant = await tenant_manager.get_tenant(tenant_id)
        if tenant:
            return tenant
        else:
            raise HTTPException(status_code=404, detail="Tenant not found")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# Support Engine Endpoints
@app.post("/support/tickets")
async def create_support_ticket(ticket: SupportTicket):
    """Create and handle a support ticket"""
    try:
        # Ensure ticket has an ID
        ticket_data = ticket.dict()
        if not ticket_data.get('ticket_id'):
            ticket_data['ticket_id'] = f"TICKET-{uuid.uuid4().hex[:8].upper()}"
        
        resolution = await support_engine.handle_support_request(ticket_data)
        return resolution
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# Billing Engine Endpoints
@app.post("/billing/usage")
async def track_usage(tenant_id: str, usage_event: UsageEvent):
    """Track usage for billing purposes"""
    try:
        event = await revenue_engine.track_usage(tenant_id, usage_event.dict())
        return event
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/billing/invoices")
async def generate_invoice(tenant_id: str):
    """Generate monthly invoice for a tenant"""
    try:
        invoice = await revenue_engine.generate_monthly_bill(tenant_id)
        return invoice
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/billing/usage/{tenant_id}")
async def get_usage_events(tenant_id: str):
    """Get usage events for a tenant"""
    try:
        events = revenue_engine.get_usage_events(tenant_id)
        return events
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
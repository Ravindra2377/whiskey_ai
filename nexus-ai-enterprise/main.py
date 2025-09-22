import asyncio
import logging
from nexus_ai.connectors.network_discovery import NetworkDiscoveryEngine
from nexus_ai.connectors.config_generator import IntegrationConfigGenerator
from nexus_ai.connectors.integration_engine import UniversalIntegrationEngine
from nexus_ai.platform.tenant_manager import TenantManager
from nexus_ai.support.ai_engine import EnterpriseSupportEngine
from nexus_ai.billing.revenue_engine import RevenueEngine

# Set up logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

async def main():
    """Main function to demonstrate the enterprise platform components"""
    logger.info("Starting Nexus AI Enterprise Platform Demo")
    
    # 1. Network Discovery Engine
    logger.info("=== Network Discovery Engine Demo ===")
    discovery_engine = NetworkDiscoveryEngine()
    discovered_systems = await discovery_engine.discover_enterprise_systems("example.com")
    logger.info(f"Discovered {discovered_systems['total_systems_discovered']} systems")
    
    # 2. Integration Config Generator
    logger.info("=== Integration Config Generator Demo ===")
    config_generator = IntegrationConfigGenerator()
    integration_config = await config_generator.generate_integration_config(discovered_systems)
    logger.info(f"Generated integration config with {len(integration_config['connections'])} connections")
    
    # 3. Universal Integration Engine
    logger.info("=== Universal Integration Engine Demo ===")
    integration_engine = UniversalIntegrationEngine()
    integration_result = await integration_engine.execute_integration(integration_config)
    logger.info(f"Integration completed: {len(integration_result['connections_established'])} connections established")
    
    # 4. Tenant Manager
    logger.info("=== Tenant Manager Demo ===")
    tenant_manager = TenantManager()
    company_info = {
        'company_name': 'Acme Corporation',
        'subscription_tier': 'enterprise'
    }
    tenant = await tenant_manager.create_tenant(company_info)
    logger.info(f"Created tenant: {tenant['tenant_id']}")
    
    # 5. Enterprise Support Engine
    logger.info("=== Enterprise Support Engine Demo ===")
    support_engine = EnterpriseSupportEngine()
    support_ticket = {
        'ticket_id': 'TICKET-001',
        'title': 'Database Connection Issue',
        'description': 'Unable to connect to PostgreSQL database',
        'priority': 'high'
    }
    resolution = await support_engine.handle_support_request(support_ticket)
    logger.info(f"Resolved ticket with confidence: {resolution['confidence']:.2f}")
    
    # 6. Revenue Engine
    logger.info("=== Revenue Engine Demo ===")
    revenue_engine = RevenueEngine()
    usage_event = {
        'event_type': 'api_call',
        'quantity': 10000
    }
    tracked_event = await revenue_engine.track_usage(tenant['tenant_id'], usage_event)
    logger.info(f"Tracked usage event: {tracked_event['event_type']} - ${tracked_event['billable_amount']:.2f}")
    
    invoice = await revenue_engine.generate_monthly_bill(tenant['tenant_id'])
    logger.info(f"Generated invoice: ${invoice['total_amount']:.2f}")
    
    logger.info("Whiskey AI Enterprise Platform Demo completed successfully!")

if __name__ == "__main__":
    asyncio.run(main())
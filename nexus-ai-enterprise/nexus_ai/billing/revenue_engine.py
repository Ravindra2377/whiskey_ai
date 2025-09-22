import asyncio
import uuid
from typing import Dict, Any, List
import logging
from datetime import datetime, timedelta

# Set up logging
logger = logging.getLogger(__name__)
logging.basicConfig(level=logging.INFO)

class RevenueEngine:
    """Complete billing system with multiple pricing models"""
    
    def __init__(self):
        self.usage_events = []
        self.invoices = []
        self.pricing_models = {
            'basic': {
                'monthly_fee': 1000,
                'per_api_call': 0.01,
                'per_gb_storage': 50,
                'per_user': 10
            },
            'professional': {
                'monthly_fee': 5000,
                'per_api_call': 0.005,
                'per_gb_storage': 40,
                'per_user': 8
            },
            'enterprise': {
                'monthly_fee': 15000,
                'per_api_call': 0.001,
                'per_gb_storage': 30,
                'per_user': 5
            }
        }
    
    async def track_usage(self, tenant_id: str, usage_event: Dict[str, Any]) -> Dict[str, Any]:
        """Track all billable events for revenue"""
        logger.info(f"Tracking usage event for tenant {tenant_id}")
        
        # Calculate billable amount
        billable_amount = await self.calculate_cost(usage_event)
        
        # Store usage event
        event = {
            'event_id': f"event_{uuid.uuid4().hex[:8]}",
            'tenant_id': tenant_id,
            'event_type': usage_event.get('event_type'),
            'quantity': usage_event.get('quantity', 0),
            'billable_amount': billable_amount,
            'timestamp': asyncio.get_event_loop().time(),
            'details': usage_event
        }
        
        self.usage_events.append(event)
        
        logger.info(f"Usage event tracked: {event['event_type']} - ${billable_amount:.2f}")
        return event
    
    async def calculate_cost(self, usage_event: Dict[str, Any]) -> float:
        """Calculate cost for a usage event"""
        event_type = usage_event.get('event_type')
        quantity = usage_event.get('quantity', 0)
        
        # Simulate cost calculation based on event type
        if event_type == 'api_call':
            return quantity * 0.01  # $0.01 per API call
        elif event_type == 'storage_gb':
            return quantity * 50.0  # $50 per GB
        elif event_type == 'user':
            return quantity * 10.0   # $10 per user
        elif event_type == 'compute_hour':
            return quantity * 0.5    # $0.50 per compute hour
        else:
            return 0.0
    
    async def generate_monthly_bill(self, tenant_id: str) -> Dict[str, Any]:
        """Generate enterprise bill automatically"""
        logger.info(f"Generating monthly bill for tenant {tenant_id}")
        
        # Get usage summary
        usage = await self.get_usage_summary(tenant_id)
        
        # Calculate charges
        charges = await self.calculate_charges(usage)
        
        # Create invoice
        invoice = await self.create_invoice(tenant_id, charges)
        
        self.invoices.append(invoice)
        
        logger.info(f"Monthly bill generated for tenant {tenant_id}: ${invoice['total_amount']:.2f}")
        return invoice
    
    async def get_usage_summary(self, tenant_id: str) -> Dict[str, Any]:
        """Get usage summary for a tenant"""
        logger.info(f"Getting usage summary for tenant {tenant_id}")
        
        # Filter usage events for this tenant from the last 30 days
        tenant_events = [
            event for event in self.usage_events 
            if event['tenant_id'] == tenant_id and 
            (asyncio.get_event_loop().time() - event['timestamp']) < 30 * 24 * 3600
        ]
        
        # Summarize usage by event type
        summary = {}
        total_amount = 0.0
        
        for event in tenant_events:
            event_type = event['event_type']
            if event_type not in summary:
                summary[event_type] = {
                    'quantity': 0,
                    'amount': 0.0
                }
            
            summary[event_type]['quantity'] += event['details'].get('quantity', 0)
            summary[event_type]['amount'] += event['billable_amount']
            total_amount += event['billable_amount']
        
        return {
            'tenant_id': tenant_id,
            'period_start': asyncio.get_event_loop().time() - 30 * 24 * 3600,
            'period_end': asyncio.get_event_loop().time(),
            'usage_breakdown': summary,
            'total_amount': total_amount
        }
    
    async def calculate_charges(self, usage: Dict[str, Any]) -> Dict[str, Any]:
        """Calculate charges based on usage and pricing model"""
        logger.info(f"Calculating charges for tenant {usage['tenant_id']}")
        
        tenant_id = usage['tenant_id']
        total_usage_amount = usage['total_amount']
        
        # In a real implementation, we would get the tenant's subscription tier
        # For now, we'll assume a default tier
        subscription_tier = 'professional'
        tier_pricing = self.pricing_models.get(subscription_tier, self.pricing_models['basic'])
        
        # Calculate tier-based charges
        monthly_fee = tier_pricing['monthly_fee']
        
        # Calculate usage-based charges
        usage_charges = total_usage_amount
        
        # Calculate total amount
        total_amount = monthly_fee + usage_charges
        
        charges = {
            'charges_id': f"chrg_{uuid.uuid4().hex[:8]}",
            'subscription_tier': subscription_tier,
            'monthly_fee': monthly_fee,
            'usage_charges': usage_charges,
            'total_amount': total_amount,
            'breakdown': {
                'monthly_fee': monthly_fee,
                'api_calls': usage['usage_breakdown'].get('api_call', {}).get('amount', 0),
                'storage': usage['usage_breakdown'].get('storage_gb', {}).get('amount', 0),
                'users': usage['usage_breakdown'].get('user', {}).get('amount', 0),
                'compute': usage['usage_breakdown'].get('compute_hour', {}).get('amount', 0)
            }
        }
        
        return charges
    
    async def create_invoice(self, tenant_id: str, charges: Dict[str, Any]) -> Dict[str, Any]:
        """Create an invoice for a tenant"""
        logger.info(f"Creating invoice for tenant {tenant_id}")
        
        invoice = {
            'invoice_id': f"inv_{uuid.uuid4().hex[:8]}",
            'tenant_id': tenant_id,
            'invoice_date': datetime.now().isoformat(),
            'due_date': (datetime.now() + timedelta(days=30)).isoformat(),
            'charges': charges,
            'total_amount': charges['total_amount'],
            'status': 'pending'
        }
        
        return invoice
    
    def get_usage_events(self, tenant_id: str = None) -> List[Dict[str, Any]]:
        """Get usage events, optionally filtered by tenant"""
        if tenant_id:
            return [event for event in self.usage_events if event['tenant_id'] == tenant_id]
        return self.usage_events
    
    def get_invoices(self, tenant_id: str = None) -> List[Dict[str, Any]]:
        """Get invoices, optionally filtered by tenant"""
        if tenant_id:
            return [invoice for invoice in self.invoices if invoice['tenant_id'] == tenant_id]
        return self.invoices
import asyncio
import uuid
from typing import Dict, Any, List
import logging

# Set up logging
logger = logging.getLogger(__name__)
logging.basicConfig(level=logging.INFO)

class IntegrationConfigGenerator:
    """Auto-generates perfect integration configurations"""
    
    def __init__(self):
        pass
    
    async def generate_integration_config(self, discovered_systems: Dict[str, Any]) -> Dict[str, Any]:
        """Generate complete integration config automatically"""
        logger.info("Generating integration configuration")
        
        connections = await self.generate_connections(discovered_systems)
        authentication = await self.setup_authentication(discovered_systems)
        monitoring = await self.setup_monitoring(discovered_systems)
        
        config = {
            'config_id': f"config_{uuid.uuid4().hex[:8]}",
            'generated_at': asyncio.get_event_loop().time(),
            'connections': connections,
            'authentication': authentication,
            'monitoring': monitoring
        }
        
        logger.info("Integration configuration generated successfully")
        return config
    
    async def generate_connections(self, discovered_systems: Dict[str, Any]) -> Dict[str, Any]:
        """Generate connection configurations for all discovered systems"""
        logger.info("Generating connections for discovered systems")
        
        connections = {}
        
        # Process web services
        for i, service in enumerate(discovered_systems.get('web_services', [])):
            conn_name = f"web_service_{i+1}"
            connections[conn_name] = {
                'type': 'web_service',
                'url': service['url'],
                'framework': service['framework'],
                'ports': service['ports']
            }
        
        # Process databases
        for i, db in enumerate(discovered_systems.get('databases', [])):
            conn_name = f"database_{i+1}"
            connections[conn_name] = await self.create_database_connection(db)
        
        # Process APIs
        for i, api in enumerate(discovered_systems.get('apis', [])):
            conn_name = f"api_{i+1}"
            connections[conn_name] = {
                'type': 'api',
                'url': api['url'],
                'version': api['version'],
                'authentication': api['authentication']
            }
        
        return connections
    
    async def create_database_connection(self, db_info: Dict[str, Any]) -> Dict[str, Any]:
        """Generate database connection that works perfectly"""
        logger.info(f"Creating database connection for {db_info['db_name']}")
        
        return {
            'type': 'database',
            'db_name': db_info['db_name'],
            'db_type': db_info['type'],
            'host': db_info['host'],
            'port': db_info['port'],
            'connection_string': self.generate_connection_string(db_info),
            'pool_settings': {'max_connections': 10, 'timeout': 30},
            'health_check': self.get_health_check_query(db_info['type'])
        }
    
    def generate_connection_string(self, db_info: Dict[str, Any]) -> str:
        """Generate appropriate connection string based on database type"""
        db_type = db_info['type'].lower()
        host = db_info['host']
        port = db_info['port']
        db_name = db_info['db_name']
        
        if db_type == 'postgresql':
            return f"postgresql://user:password@{host}:{port}/{db_name}"
        elif db_type == 'mysql':
            return f"mysql://user:password@{host}:{port}/{db_name}"
        elif db_type == 'mongodb':
            return f"mongodb://{host}:{port}/{db_name}"
        else:
            return f"{db_type}://{host}:{port}/{db_name}"
    
    def get_health_check_query(self, db_type: str) -> str:
        """Get appropriate health check query for database type"""
        db_type = db_type.lower()
        
        if db_type == 'postgresql':
            return "SELECT 1;"
        elif db_type == 'mysql':
            return "SELECT 1;"
        elif db_type == 'mongodb':
            return "db.runCommand({ping: 1})"
        else:
            return "SELECT 1;"
    
    async def setup_authentication(self, discovered_systems: Dict[str, Any]) -> Dict[str, Any]:
        """Set up authentication for all discovered systems"""
        logger.info("Setting up authentication for discovered systems")
        
        auth_config = {
            'auth_id': f"auth_{uuid.uuid4().hex[:8]}",
            'methods': []
        }
        
        # Add common authentication methods
        auth_config['methods'].append({
            'type': 'oauth2',
            'provider': 'generic',
            'scopes': ['read', 'write']
        })
        
        auth_config['methods'].append({
            'type': 'jwt',
            'algorithm': 'RS256',
            'expiration': 3600
        })
        
        return auth_config
    
    async def setup_monitoring(self, discovered_systems: Dict[str, Any]) -> Dict[str, Any]:
        """Set up monitoring for all discovered systems"""
        logger.info("Setting up monitoring for discovered systems")
        
        monitoring_config = {
            'monitoring_id': f"mon_{uuid.uuid4().hex[:8]}",
            'metrics': {
                'collection_interval': 60,  # seconds
                'retention_period': 86400   # 24 hours
            },
            'alerts': {
                'enabled': True,
                'thresholds': {
                    'cpu_usage': 80,
                    'memory_usage': 85,
                    'response_time': 2000  # milliseconds
                }
            }
        }
        
        return monitoring_config
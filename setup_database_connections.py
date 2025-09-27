#!/usr/bin/env python3
"""
Whiskey AI Database Connection Builder
Establishes connections to PostgreSQL, Redis, and InfluxDB for the platform
"""
import os
import sys
import time
import psycopg2
import redis
from influxdb_client import InfluxDBClient
from influxdb_client.client.write_api import SYNCHRONOUS
import json
from datetime import datetime

class DatabaseConnectionManager:
    def __init__(self):
        self.config = {
            'postgresql': {
                'host': os.getenv('DB_HOST', 'localhost'),
                'port': int(os.getenv('DB_PORT', '5432')),
                'database': os.getenv('DB_NAME', 'nexus_ai'),
                'user': os.getenv('DB_USER', 'nexus_admin'),
                'password': os.getenv('DB_PASSWORD', 'pASSWORD@11')
            },
            'redis': {
                'host': os.getenv('REDIS_HOST', 'localhost'),
                'port': int(os.getenv('REDIS_PORT', '6379')),
                'db': int(os.getenv('REDIS_DB', '0'))
            },
            'influxdb': {
                'url': os.getenv('INFLUXDB_URL', 'http://localhost:8086'),
                'token': os.getenv('INFLUXDB_TOKEN', 'admin-token'),
                'org': os.getenv('INFLUXDB_ORG', 'nexus-ai'),
                'bucket': os.getenv('INFLUXDB_BUCKET', 'metrics')
            }
        }
        
        self.connections = {}
        self.connection_status = {}
    
    def test_postgresql_connection(self):
        """Test PostgreSQL connection and create database if needed"""
        print("🔧 Testing PostgreSQL connection...")
        
        try:
            # First try to connect to the specific database
            conn = psycopg2.connect(
                host=self.config['postgresql']['host'],
                port=self.config['postgresql']['port'],
                database=self.config['postgresql']['database'],
                user=self.config['postgresql']['user'],
                password=self.config['postgresql']['password']
            )
            
            cursor = conn.cursor()
            cursor.execute("SELECT version();")
            version = cursor.fetchone()[0]
            print(f"✅ PostgreSQL connected successfully: {version}")
            
            # Test basic operations
            cursor.execute("SELECT COUNT(*) FROM pg_tables WHERE schemaname = 'public';")
            table_count = cursor.fetchone()[0]
            print(f"📊 Found {table_count} tables in public schema")
            
            self.connections['postgresql'] = conn
            self.connection_status['postgresql'] = 'connected'
            
            cursor.close()
            return True
            
        except psycopg2.OperationalError as e:
            if "does not exist" in str(e):
                print(f"⚠️  Database '{self.config['postgresql']['database']}' doesn't exist. Attempting to create...")
                return self.create_postgresql_database()
            else:
                print(f"❌ PostgreSQL connection failed: {e}")
                self.connection_status['postgresql'] = f'failed: {e}'
                return False
        except Exception as e:
            print(f"❌ PostgreSQL unexpected error: {e}")
            self.connection_status['postgresql'] = f'error: {e}'
            return False
    
    def create_postgresql_database(self):
        """Create PostgreSQL database if it doesn't exist"""
        try:
            # Connect to default postgres database to create our database
            conn = psycopg2.connect(
                host=self.config['postgresql']['host'],
                port=self.config['postgresql']['port'],
                database='postgres',
                user=self.config['postgresql']['user'],
                password=self.config['postgresql']['password']
            )
            conn.autocommit = True
            cursor = conn.cursor()
            
            cursor.execute(f"CREATE DATABASE {self.config['postgresql']['database']};")
            print(f"✅ Database '{self.config['postgresql']['database']}' created successfully")
            
            cursor.close()
            conn.close()
            
            # Now connect to the newly created database
            return self.test_postgresql_connection()
            
        except Exception as e:
            print(f"❌ Failed to create database: {e}")
            return False
    
    def setup_postgresql_schema(self):
        """Setup PostgreSQL schema from schema file"""
        print("🔧 Setting up PostgreSQL schema...")
        
        schema_file = "nexus/src/main/resources/database-schema.sql"
        if not os.path.exists(schema_file):
            print(f"⚠️  Schema file not found: {schema_file}")
            return False
        
        try:
            with open(schema_file, 'r') as f:
                schema_sql = f.read()
            
            conn = self.connections.get('postgresql')
            if not conn:
                print("❌ No PostgreSQL connection available")
                return False
            
            cursor = conn.cursor()
            cursor.execute(schema_sql)
            conn.commit()
            
            print("✅ PostgreSQL schema setup completed")
            return True
            
        except Exception as e:
            print(f"❌ Schema setup failed: {e}")
            return False
    
    def test_redis_connection(self):
        """Test Redis connection"""
        print("🔧 Testing Redis connection...")
        
        try:
            r = redis.Redis(
                host=self.config['redis']['host'],
                port=self.config['redis']['port'],
                db=self.config['redis']['db'],
                decode_responses=True
            )
            
            # Test connection
            r.ping()
            
            # Test basic operations
            r.set('whiskey_ai_test', 'connection_test')
            test_value = r.get('whiskey_ai_test')
            
            if test_value == 'connection_test':
                print("✅ Redis connected successfully")
                print(f"📊 Redis info: {r.info()['redis_version']}")
                
                # Setup some initial cache structure
                r.hset('whiskey_ai:config', mapping={
                    'platform': 'Whiskey AI',
                    'version': '1.0.0',
                    'startup_time': datetime.now().isoformat()
                })
                
                self.connections['redis'] = r
                self.connection_status['redis'] = 'connected'
                return True
            else:
                print("❌ Redis test operation failed")
                return False
                
        except redis.ConnectionError as e:
            print(f"❌ Redis connection failed: {e}")
            self.connection_status['redis'] = f'failed: {e}'
            return False
        except Exception as e:
            print(f"❌ Redis unexpected error: {e}")
            self.connection_status['redis'] = f'error: {e}'
            return False
    
    def test_influxdb_connection(self):
        """Test InfluxDB connection"""
        print("🔧 Testing InfluxDB connection...")
        
        try:
            client = InfluxDBClient(
                url=self.config['influxdb']['url'],
                token=self.config['influxdb']['token'],
                org=self.config['influxdb']['org']
            )
            
            # Test connection
            health = client.health()
            if health.status == "pass":
                print(f"✅ InfluxDB connected successfully: {health.version}")
                
                # Test write operation
                write_api = client.write_api(write_options=SYNCHRONOUS)
                
                # Write a test point
                test_data = [
                    {
                        "measurement": "whiskey_ai_test",
                        "tags": {"service": "connection_test"},
                        "fields": {"value": 1.0, "status": "connected"},
                        "time": datetime.utcnow()
                    }
                ]
                
                write_api.write(
                    bucket=self.config['influxdb']['bucket'],
                    record=test_data
                )
                
                print("📊 InfluxDB test write successful")
                
                self.connections['influxdb'] = client
                self.connection_status['influxdb'] = 'connected'
                return True
            else:
                print(f"❌ InfluxDB health check failed: {health.status}")
                return False
                
        except Exception as e:
            print(f"❌ InfluxDB connection failed: {e}")
            self.connection_status['influxdb'] = f'failed: {e}'
            return False
    
    def generate_connection_report(self):
        """Generate a comprehensive connection report"""
        print("\n" + "="*60)
        print("🚀 WHISKEY AI DATABASE CONNECTION REPORT")
        print("="*60)
        
        all_connected = True
        
        for db_type, status in self.connection_status.items():
            if status == 'connected':
                print(f"✅ {db_type.upper()}: Connected")
            else:
                print(f"❌ {db_type.upper()}: {status}")
                all_connected = False
        
        print("\n" + "-"*60)
        print("📋 CONNECTION DETAILS:")
        print(f"   PostgreSQL: {self.config['postgresql']['host']}:{self.config['postgresql']['port']}")
        print(f"   Redis: {self.config['redis']['host']}:{self.config['redis']['port']}")
        print(f"   InfluxDB: {self.config['influxdb']['url']}")
        
        if all_connected:
            print("\n🎉 ALL DATABASES CONNECTED SUCCESSFULLY!")
            print("   Platform ready for full operation")
        else:
            print("\n⚠️  SOME CONNECTIONS FAILED")
            print("   Check individual error messages above")
        
        print("="*60)
        return all_connected
    
    def setup_sample_data(self):
        """Setup sample data for testing"""
        print("\n🔧 Setting up sample data...")
        
        # PostgreSQL sample data
        if 'postgresql' in self.connections:
            try:
                conn = self.connections['postgresql']
                cursor = conn.cursor()
                
                # Insert sample trading strategy
                cursor.execute("""
                    INSERT INTO trading_strategies (name, description, strategy_type, parameters) 
                    VALUES (%s, %s, %s, %s)
                    ON CONFLICT DO NOTHING
                """, (
                    'Sample Strategy',
                    'Test trading strategy for Whiskey AI',
                    'momentum',
                    json.dumps({"lookback": 20, "threshold": 0.02})
                ))
                
                conn.commit()
                print("✅ PostgreSQL sample data inserted")
                
            except Exception as e:
                print(f"⚠️  PostgreSQL sample data failed: {e}")
        
        # Redis sample data
        if 'redis' in self.connections:
            try:
                r = self.connections['redis']
                
                # Set some configuration values
                r.hset('whiskey_ai:settings', mapping={
                    'max_positions': '10',
                    'risk_limit': '0.02',
                    'default_strategy': 'momentum'
                })
                
                print("✅ Redis sample data inserted")
                
            except Exception as e:
                print(f"⚠️  Redis sample data failed: {e}")
        
        # InfluxDB sample data
        if 'influxdb' in self.connections:
            try:
                client = self.connections['influxdb']
                write_api = client.write_api(write_options=SYNCHRONOUS)
                
                # Write sample metrics
                sample_metrics = [
                    {
                        "measurement": "system_metrics",
                        "tags": {"component": "api", "environment": "development"},
                        "fields": {"cpu_usage": 45.2, "memory_usage": 1024, "requests_per_second": 150},
                        "time": datetime.utcnow()
                    }
                ]
                
                write_api.write(
                    bucket=self.config['influxdb']['bucket'],
                    record=sample_metrics
                )
                
                print("✅ InfluxDB sample data inserted")
                
            except Exception as e:
                print(f"⚠️  InfluxDB sample data failed: {e}")
    
    def close_connections(self):
        """Close all database connections"""
        print("\n🔌 Closing database connections...")
        
        for db_type, conn in self.connections.items():
            try:
                if db_type == 'postgresql':
                    conn.close()
                elif db_type == 'influxdb':
                    conn.close()
                print(f"✅ {db_type} connection closed")
            except Exception as e:
                print(f"⚠️  Error closing {db_type}: {e}")

def main():
    """Main function to establish all database connections"""
    print("🚀 Starting Whiskey AI Database Connection Setup...")
    print("=" * 60)
    
    manager = DatabaseConnectionManager()
    
    try:
        # Test all connections
        postgres_ok = manager.test_postgresql_connection()
        if postgres_ok:
            manager.setup_postgresql_schema()
        
        redis_ok = manager.test_redis_connection()
        influx_ok = manager.test_influxdb_connection()
        
        # Generate report
        all_connected = manager.generate_connection_report()
        
        if all_connected:
            manager.setup_sample_data()
            
            print("\n🎯 NEXT STEPS:")
            print("   1. Start the Nexus application: cd nexus && ./mvnw spring-boot:run")
            print("   2. Access the API at: http://localhost:8094")
            print("   3. Monitor with: docker stats (if using containers)")
            
        return all_connected
        
    except KeyboardInterrupt:
        print("\n⚠️  Setup interrupted by user")
        return False
    finally:
        manager.close_connections()

if __name__ == "__main__":
    success = main()
    sys.exit(0 if success else 1)
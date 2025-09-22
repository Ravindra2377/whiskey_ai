#!/usr/bin/env python3
"""
Whiskey AI Database Connection Checker
Checks which database services are available and provides setup instructions
"""
import socket
import sys
from datetime import datetime

class DatabaseChecker:
    def __init__(self):
        self.services = {
            'PostgreSQL': {'host': 'localhost', 'port': 5432},
            'Redis': {'host': 'localhost', 'port': 6379},
            'InfluxDB': {'host': 'localhost', 'port': 8086}
        }
        
    def check_port(self, host, port, timeout=3):
        """Check if a port is open"""
        try:
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.settimeout(timeout)
            result = sock.connect_ex((host, port))
            sock.close()
            return result == 0
        except Exception:
            return False
    
    def check_all_services(self):
        """Check all database services"""
        print("🚀 Whiskey AI Database Service Checker")
        print("=" * 50)
        print(f"Timestamp: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        print("")
        
        results = {}
        all_running = True
        
        for service, config in self.services.items():
            is_running = self.check_port(config['host'], config['port'])
            results[service] = is_running
            
            status = "✅ RUNNING" if is_running else "❌ NOT RUNNING"
            print(f"{service:12} {config['host']}:{config['port']:5} -> {status}")
            
            if not is_running:
                all_running = False
        
        print("\n" + "=" * 50)
        
        if all_running:
            print("🎉 ALL DATABASE SERVICES ARE RUNNING!")
            print("\n🔧 Next steps:")
            print("   1. Run: python setup_database_connections.py")
            print("   2. Start Nexus app: cd nexus && ./mvnw spring-boot:run")
            print("   3. Access API at: http://localhost:8094")
        else:
            print("⚠️  SOME SERVICES ARE NOT RUNNING")
            self.provide_setup_instructions(results)
        
        return all_running
    
    def provide_setup_instructions(self, results):
        """Provide setup instructions for missing services"""
        print("\n📋 SETUP INSTRUCTIONS:")
        print("-" * 30)
        
        if not results.get('PostgreSQL', False):
            print("\n🐘 PostgreSQL Setup:")
            print("   Option 1 - Docker: docker run -d --name postgres -p 5432:5432 -e POSTGRES_DB=nexus_ai -e POSTGRES_USER=nexus_admin -e POSTGRES_PASSWORD=SecurePassword123! postgres:15")
            print("   Option 2 - Local: Download from https://www.postgresql.org/download/")
            
        if not results.get('Redis', False):
            print("\n🔴 Redis Setup:")
            print("   Option 1 - Docker: docker run -d --name redis -p 6379:6379 redis:7-alpine")
            print("   Option 2 - Local: Download from https://redis.io/download")
            
        if not results.get('InfluxDB', False):
            print("\n📊 InfluxDB Setup:")
            print("   Option 1 - Docker: docker run -d --name influxdb -p 8086:8086 -e DOCKER_INFLUXDB_INIT_MODE=setup -e DOCKER_INFLUXDB_INIT_USERNAME=admin -e DOCKER_INFLUXDB_INIT_PASSWORD=SecurePassword123! influxdb:2.7")
            print("   Option 2 - Local: Download from https://www.influxdata.com/downloads/")
        
        print("\n🐳 Docker Compose (Recommended):")
        print("   Run: docker-compose up -d")
        print("   This will start all services automatically")
        
        print("\n💡 Alternative - Use Docker Desktop:")
        print("   1. Install Docker Desktop")
        print("   2. Run: docker-compose up -d")
        print("   3. All services will be available")

def main():
    checker = DatabaseChecker()
    return checker.check_all_services()

if __name__ == "__main__":
    success = main()
    sys.exit(0 if success else 1)
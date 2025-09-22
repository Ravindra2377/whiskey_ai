import requests
import json

# Base URL for the API
BASE_URL = "http://localhost:8094/api/nexus/enterprise"

def test_api_endpoints():
    print("Testing Universal Enterprise Integration Engine API")
    print("=" * 50)
    
    # Test 1: Health check
    print("\n1. Testing Health Check Endpoint")
    try:
        response = requests.get(f"{BASE_URL}/health")
        print(f"   Status Code: {response.status_code}")
        print(f"   Response: {response.json()}")
    except Exception as e:
        print(f"   Error: {e}")
    
    # Test 2: Test endpoint
    print("\n2. Testing Test Endpoint")
    try:
        response = requests.get(f"{BASE_URL}/test")
        print(f"   Status Code: {response.status_code}")
        print(f"   Response: {response.text}")
    except Exception as e:
        print(f"   Error: {e}")
    
    # Test 3: System discovery
    print("\n3. Testing System Discovery Endpoint")
    try:
        discovery_params = {
            "scan_network": True,
            "scan_apis": True,
            "scan_databases": True
        }
        response = requests.post(f"{BASE_URL}/discover-systems", json=discovery_params)
        print(f"   Status Code: {response.status_code}")
        result = response.json()
        print(f"   Status: {result.get('status')}")
        print(f"   Message: {result.get('message')}")
        print(f"   Systems Discovered: {len(result.get('discoveredSystems', []))}")
    except Exception as e:
        print(f"   Error: {e}")
    
    # Test 4: Client registration
    print("\n4. Testing Client Registration Endpoint")
    try:
        client_data = {
            "clientName": "TestCorp",
            "tier": "Enterprise",
            "configuration": {
                "industry": "Technology",
                "employee_count": 1000
            }
        }
        response = requests.post(f"{BASE_URL}/register-client", json=client_data)
        print(f"   Status Code: {response.status_code}")
        if response.status_code == 200:
            result = response.json()
            print(f"   Client Name: {result.get('clientName')}")
            print(f"   Client ID: {result.get('clientId')}")
            print(f"   Tier: {result.get('tier')}")
        else:
            print(f"   Error: {response.text}")
    except Exception as e:
        print(f"   Error: {e}")
    
    # Test 5: Technical support
    print("\n5. Testing Technical Support Endpoint")
    try:
        support_request = {
            "systemId": "SYS_WEB_001",
            "supportType": "performance_optimization",
            "description": "Application running slowly during peak hours",
            "priority": "HIGH"
        }
        response = requests.post(f"{BASE_URL}/technical-support", json=support_request)
        print(f"   Status Code: {response.status_code}")
        if response.status_code == 200:
            result = response.json()
            print(f"   Request ID: {result.get('requestId')}")
            print(f"   Status: {result.get('status')}")
            print(f"   Message: {result.get('message')}")
        else:
            print(f"   Error: {response.text}")
    except Exception as e:
        print(f"   Error: {e}")
    
    # Test 6: Metrics
    print("\n6. Testing Metrics Endpoint")
    try:
        response = requests.get(f"{BASE_URL}/metrics")
        print(f"   Status Code: {response.status_code}")
        if response.status_code == 200:
            result = response.json()
            print(f"   Health Status: {result.get('healthStatus')}")
            print(f"   Total Systems Discovered: {result.get('totalSystemsDiscovered', 0)}")
            print(f"   Total Connections: {result.get('totalConnectionsConfigured', 0)}")
            print(f"   Total Agents: {result.get('totalAgentsDeployed', 0)}")
        else:
            print(f"   Error: {response.text}")
    except Exception as e:
        print(f"   Error: {e}")

if __name__ == "__main__":
    test_api_endpoints()
import requests
import json

# Base URLs for the API
BASE_URL = "http://localhost:8094"
API_BASE = f"{BASE_URL}/api/nexus"

def test_endpoints():
    print("Testing NEXUS AI API Endpoints")
    print("=" * 40)
    
    # Test 1: Root endpoint
    print("\n1. Testing Root Endpoint")
    try:
        response = requests.get(BASE_URL)
        print(f"   Status Code: {response.status_code}")
        print(f"   Success: {response.status_code == 200}")
    except Exception as e:
        print(f"   Error: {e}")
    
    # Test 2: Actuator endpoints
    print("\n2. Testing Actuator Endpoints")
    try:
        response = requests.get(f"{BASE_URL}/actuator")
        print(f"   Actuator Status Code: {response.status_code}")
        if response.status_code == 200:
            data = response.json()
            print(f"   Available endpoints: {list(data.get('_links', {}).keys())}")
    except Exception as e:
        print(f"   Error: {e}")
    
    # Test 3: Try different API paths
    print("\n3. Testing Various API Paths")
    paths_to_test = [
        "/health",
        "/info",
        "/metrics",
        "/api/nexus/health",
        "/api/nexus/info",
        "/api/nexus/metrics",
        "/api/nexus/enterprise/health",
        "/api/nexus/enterprise/test",
        "/api/nexus/enhanced/health",
        "/api/nexus/advanced/health"
    ]
    
    for path in paths_to_test:
        try:
            response = requests.get(f"{BASE_URL}{path}")
            print(f"   {path}: {response.status_code}")
        except Exception as e:
            print(f"   {path}: Error - {str(e)[:50]}...")

if __name__ == "__main__":
    test_endpoints()
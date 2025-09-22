import requests
import time

# Base URL for our API
BASE_URL = "http://localhost:8000"

def test_root_endpoint():
    """Test the root endpoint"""
    print("Testing root endpoint...")
    response = requests.get(f"{BASE_URL}/")
    print(f"Status Code: {response.status_code}")
    print(f"Response: {response.json()}")
    print()

def test_discovery_endpoint():
    """Test the network discovery endpoint"""
    print("Testing network discovery endpoint...")
    payload = {
        "company_domain": "example.com"
    }
    response = requests.post(f"{BASE_URL}/discovery/network", json=payload)
    print(f"Status Code: {response.status_code}")
    if response.status_code == 200:
        data = response.json()
        print(f"Discovered {data.get('total_systems_discovered', 0)} systems")
        print("Sample web services:", data.get('web_services', [])[:2])
    else:
        print(f"Error: {response.text}")
    print()

def test_tenant_creation():
    """Test the tenant creation endpoint"""
    print("Testing tenant creation endpoint...")
    payload = {
        "company_name": "Test Corp",
        "subscription_tier": "professional"
    }
    response = requests.post(f"{BASE_URL}/tenants", json=payload)
    print(f"Status Code: {response.status_code}")
    if response.status_code == 200:
        data = response.json()
        print(f"Created tenant: {data.get('tenant_id')}")
        print(f"Access URL: {data.get('access_url')}")
        return data.get('tenant_id')
    else:
        print(f"Error: {response.text}")
    print()
    return None

def test_support_ticket():
    """Test the support ticket endpoint"""
    print("Testing support ticket endpoint...")
    payload = {
        "title": "Test Issue",
        "description": "This is a test support ticket",
        "priority": "medium"
    }
    response = requests.post(f"{BASE_URL}/support/tickets", json=payload)
    print(f"Status Code: {response.status_code}")
    if response.status_code == 200:
        data = response.json()
        print(f"Ticket resolved with confidence: {data.get('confidence', 0):.2f}")
    else:
        print(f"Error: {response.text}")
    print()

def test_usage_tracking(tenant_id):
    """Test the usage tracking endpoint"""
    if not tenant_id:
        print("No tenant ID provided, skipping usage tracking test")
        print()
        return
        
    print("Testing usage tracking endpoint...")
    payload = {
        "event_type": "api_call",
        "quantity": 100
    }
    response = requests.post(f"{BASE_URL}/billing/usage?tenant_id={tenant_id}", json=payload)
    print(f"Status Code: {response.status_code}")
    if response.status_code == 200:
        data = response.json()
        print(f"Tracked usage event: {data.get('event_type')} - ${data.get('billable_amount', 0):.2f}")
    else:
        print(f"Error: {response.text}")
    print()

def main():
    """Run all tests"""
    print("Testing Whiskey AI Enterprise Platform API")
    print("=" * 50)
    
    # Give the server a moment to start
    time.sleep(2)
    
    # Test endpoints
    test_root_endpoint()
    test_discovery_endpoint()
    tenant_id = test_tenant_creation()
    test_support_ticket()
    test_usage_tracking(tenant_id)
    
    print("API testing completed!")

if __name__ == "__main__":
    main()
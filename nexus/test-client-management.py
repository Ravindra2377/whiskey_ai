import requests
import json

# Base URL for the NEXUS AI API
BASE_URL = "http://localhost:8094/api/nexus"

def test_client_onboarding():
    """Test the enterprise client onboarding endpoint"""
    print("Testing Enterprise Client Onboarding Endpoint...")
    
    payload = {
        "clientName": "TechCorp Solutions",
        "industry": "Technology",
        "companySize": "500-1000",
        "contactPerson": "John Smith",
        "email": "john.smith@techcorp.com",
        "subscriptionTier": "PREMIUM"
    }
    
    response = requests.post(f"{BASE_URL}/clients/enterprise/onboard", json=payload)
    print(f"Status Code: {response.status_code}")
    if response.status_code == 200:
        result = response.json()
        print(f"Response: {json.dumps(result, indent=2)}")
        return result.get("clientId") if result.get("status") == "SUCCESS" else None
    else:
        print(f"Error: {response.text}")
        return None
    print()

def test_client_registration():
    """Test the enterprise client registration endpoint"""
    print("Testing Enterprise Client Registration Endpoint...")
    
    payload = {
        "clientName": "Global Industries Inc",
        "tier": "ENTERPRISE",
        "configuration": {
            "systems": ["ERP", "CRM", "HRIS"],
            "integrationLevel": "FULL"
        }
    }
    
    response = requests.post(f"{BASE_URL}/enterprise/register-client", json=payload)
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(response.json(), indent=2)}")
    print()

if __name__ == "__main__":
    print("Testing NEXUS AI Client Management Endpoints\n")
    
    try:
        # Test client onboarding
        client_id = test_client_onboarding()
        if client_id:
            print(f"Successfully onboarded client with ID: {client_id}\n")
        
        # Test client registration
        test_client_registration()
        
        print("All client management tests completed!")
    except Exception as e:
        print(f"Error occurred during testing: {str(e)}")
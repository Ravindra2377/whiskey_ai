import requests
import json
import time

# Base URL for the API
BASE_URL = "http://localhost:8094"
API_BASE = f"{BASE_URL}/api/nexus"

def test_endpoint(url, method="GET", data=None):
    """Test a single endpoint and return the result"""
    try:
        if method == "GET":
            response = requests.get(url, timeout=5)
        elif method == "POST":
            response = requests.post(url, json=data, timeout=5)
        elif method == "DELETE":
            response = requests.delete(url, timeout=5)
        else:
            return {"status": "ERROR", "error": f"Unsupported method: {method}"}
        
        return {
            "status_code": response.status_code,
            "success": response.status_code in [200, 201, 202],
            "response": response.json() if response.content else {},
            "error": None
        }
    except requests.exceptions.Timeout:
        return {"status_code": 0, "success": False, "response": {}, "error": "Timeout"}
    except requests.exceptions.ConnectionError:
        return {"status_code": 0, "success": False, "response": {}, "error": "Connection Error"}
    except Exception as e:
        return {"status_code": 0, "success": False, "response": {}, "error": str(e)}

def test_all_endpoints():
    print("Testing NEXUS AI API Endpoints")
    print("=" * 50)
    
    # Test basic endpoints that should always be available
    basic_endpoints = [
        ("/", "GET"),
        ("/actuator", "GET"),
        ("/actuator/info", "GET"),
        ("/actuator/metrics", "GET"),
    ]
    
    print("\n1. Testing Basic Endpoints")
    for path, method in basic_endpoints:
        url = f"{BASE_URL}{path}"
        result = test_endpoint(url, method)
        status_display = result.get('status_code', 'N/A')
        success_display = '✓' if result.get('success') else '✗'
        print(f"   {method} {path}: {status_display} {success_display}")
        if result.get('error'):
            print(f"     Error: {result['error']}")
    
    # Test core Nexus/Whiskey endpoints
    print("\n2. Testing Core Nexus/Whiskey Endpoints")
    core_endpoints = [
        ("/api/nexus/health", "GET"),
        ("/api/nexus/info", "GET"),
        ("/api/nexus/metrics", "GET"),
        ("/api/nexus/recommendations", "GET"),
    ]
    
    for path, method in core_endpoints:
        url = f"{BASE_URL}{path}"
        result = test_endpoint(url, method)
        status_display = result.get('status_code', 'N/A')
        success_display = '✓' if result.get('success') else '✗'
        print(f"   {method} {path}: {status_display} {success_display}")
        if result.get('error'):
            print(f"     Error: {result['error']}")
    
    # Test enhanced endpoints
    print("\n3. Testing Enhanced AI Feature Endpoints")
    enhanced_endpoints = [
        ("/api/nexus/enhanced/health", "GET"),
        ("/api/nexus/enhanced/info", "GET"),
        ("/api/nexus/enhanced/supermodel-info", "GET"),
    ]
    
    for path, method in enhanced_endpoints:
        url = f"{BASE_URL}{path}"
        result = test_endpoint(url, method)
        status_display = result.get('status_code', 'N/A')
        success_display = '✓' if result.get('success') else '✗'
        print(f"   {method} {path}: {status_display} {success_display}")
        if result.get('error'):
            print(f"     Error: {result['error']}")
    
    # Test advanced endpoints
    print("\n4. Testing Advanced AI Capability Endpoints")
    advanced_endpoints = [
        ("/api/nexus/advanced/health", "GET"),
    ]
    
    for path, method in advanced_endpoints:
        url = f"{BASE_URL}{path}"
        result = test_endpoint(url, method)
        status_display = result.get('status_code', 'N/A')
        success_display = '✓' if result.get('success') else '✗'
        print(f"   {method} {path}: {status_display} {success_display}")
        if result.get('error'):
            print(f"     Error: {result['error']}")
    
    # Test enterprise endpoints
    print("\n5. Testing Enterprise Integration Endpoints")
    enterprise_endpoints = [
        ("/api/nexus/enterprise/health", "GET"),
        ("/api/nexus/enterprise/test", "GET"),
    ]
    
    for path, method in enterprise_endpoints:
        url = f"{BASE_URL}{path}"
        result = test_endpoint(url, method)
        status_display = result.get('status_code', 'N/A')
        success_display = '✓' if result.get('success') else '✗'
        print(f"   {method} {path}: {status_display} {success_display}")
        if result.get('error'):
            print(f"     Error: {result['error']}")
    
    # Test support endpoints
    print("\n6. Testing Technical Support Endpoints")
    support_endpoints = [
        ("/api/nexus/support/info", "GET"),
    ]
    
    for path, method in support_endpoints:
        url = f"{BASE_URL}{path}"
        result = test_endpoint(url, method)
        status_display = result.get('status_code', 'N/A')
        success_display = '✓' if result.get('success') else '✗'
        print(f"   {method} {path}: {status_display} {success_display}")
        if result.get('error'):
            print(f"     Error: {result['error']}")
    
    print("\n" + "=" * 50)
    print("Endpoint testing completed!")

if __name__ == "__main__":
    test_all_endpoints()
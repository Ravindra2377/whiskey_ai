import requests
import json

# Base URL for the NEXUS AI API
BASE_URL = "http://localhost:8094/api/nexus/enterprise"

def test_performance_optimization():
    """Test the performance optimization endpoint"""
    print("Testing Performance Optimization Endpoint...")
    
    payload = {
        "systemId": "SYS-001",
        "systemType": "database",
        "currentMetrics": {
            "cpuUsage": 85,
            "memoryUsage": 75,
            "diskUsage": 60
        }
    }
    
    response = requests.post(f"{BASE_URL}/optimize-performance", json=payload)
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(response.json(), indent=2)}")
    print()

def test_security_scan():
    """Test the security vulnerability scanning endpoint"""
    print("Testing Security Vulnerability Scanning Endpoint...")
    
    payload = {
        "systemId": "SYS-002",
        "systemType": "api",
        "targets": ["database", "api-gateway", "frontend"]
    }
    
    response = requests.post(f"{BASE_URL}/scan-security", json=payload)
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(response.json(), indent=2)}")
    print()

def test_code_refactoring():
    """Test the code refactoring suggestions endpoint"""
    print("Testing Code Refactoring Suggestions Endpoint...")
    
    payload = {
        "code": "public class Example { public void longMethod() { /* Very long method with duplicated code */ } }",
        "language": "java",
        "fileName": "Example.java"
    }
    
    response = requests.post(f"{BASE_URL}/refactor-code", json=payload)
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(response.json(), indent=2)}")
    print()

def test_infrastructure_scaling():
    """Test the infrastructure scaling recommendations endpoint"""
    print("Testing Infrastructure Scaling Recommendations Endpoint...")
    
    payload = {
        "environment": "production",
        "currentMetrics": {
            "cpuAverage": 75,
            "memoryAverage": 80,
            "storageUsage": 85
        },
        "growthProjection": "2x"
    }
    
    response = requests.post(f"{BASE_URL}/scale-infrastructure", json=payload)
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(response.json(), indent=2)}")
    print()

def test_api_integration():
    """Test the API integration assistance endpoint"""
    print("Testing API Integration Assistance Endpoint...")
    
    payload = {
        "targetApi": "PaymentGatewayAPI",
        "integrationType": "oauth",
        "authDetails": {
            "clientId": "client-123",
            "clientSecret": "secret-456"
        }
    }
    
    response = requests.post(f"{BASE_URL}/integrate-api", json=payload)
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(response.json(), indent=2)}")
    print()

if __name__ == "__main__":
    print("Testing NEXUS AI Technical Services Endpoints\n")
    
    try:
        test_performance_optimization()
        test_security_scan()
        test_code_refactoring()
        test_infrastructure_scaling()
        test_api_integration()
        
        print("All tests completed successfully!")
    except Exception as e:
        print(f"Error occurred during testing: {str(e)}")
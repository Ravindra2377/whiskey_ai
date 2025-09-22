import requests
import json
import time

# Base URLs for the NEXUS AI APIs
ENTERPRISE_BASE_URL = "http://localhost:8094/api/nexus/enterprise"
CLIENT_BASE_URL = "http://localhost:8094/api/nexus/clients"

def test_health_endpoint():
    """Test the health check endpoint"""
    print("Testing Health Endpoint...")
    try:
        response = requests.get(f"{ENTERPRISE_BASE_URL}/health")
        print(f"✓ Health Check: Status {response.status_code}")
        return response.status_code == 200
    except Exception as e:
        print(f"✗ Health Check Failed: {str(e)}")
        return False

def test_test_endpoint():
    """Test the test endpoint"""
    print("Testing Test Endpoint...")
    try:
        response = requests.get(f"{ENTERPRISE_BASE_URL}/test")
        print(f"✓ Test Endpoint: Status {response.status_code}")
        return response.status_code == 200
    except Exception as e:
        print(f"✗ Test Endpoint Failed: {str(e)}")
        return False

def test_performance_optimization():
    """Test the performance optimization endpoint"""
    print("Testing Performance Optimization Endpoint...")
    try:
        payload = {
            "systemId": "SYS-001",
            "systemType": "database",
            "currentMetrics": {
                "cpuUsage": 85,
                "memoryUsage": 75,
                "diskUsage": 60
            }
        }
        response = requests.post(f"{ENTERPRISE_BASE_URL}/optimize-performance", json=payload)
        print(f"✓ Performance Optimization: Status {response.status_code}")
        return response.status_code == 200
    except Exception as e:
        print(f"✗ Performance Optimization Failed: {str(e)}")
        return False

def test_security_scan():
    """Test the security vulnerability scanning endpoint"""
    print("Testing Security Vulnerability Scanning Endpoint...")
    try:
        payload = {
            "systemId": "SYS-002",
            "systemType": "api",
            "targets": ["database", "api-gateway", "frontend"]
        }
        response = requests.post(f"{ENTERPRISE_BASE_URL}/scan-security", json=payload)
        print(f"✓ Security Scan: Status {response.status_code}")
        return response.status_code == 200
    except Exception as e:
        print(f"✗ Security Scan Failed: {str(e)}")
        return False

def test_code_refactoring():
    """Test the code refactoring suggestions endpoint"""
    print("Testing Code Refactoring Suggestions Endpoint...")
    try:
        payload = {
            "code": "public class Example { public void longMethod() { /* Very long method with duplicated code */ } }",
            "language": "java",
            "fileName": "Example.java"
        }
        response = requests.post(f"{ENTERPRISE_BASE_URL}/refactor-code", json=payload)
        print(f"✓ Code Refactoring: Status {response.status_code}")
        return response.status_code == 200
    except Exception as e:
        print(f"✗ Code Refactoring Failed: {str(e)}")
        return False

def test_infrastructure_scaling():
    """Test the infrastructure scaling recommendations endpoint"""
    print("Testing Infrastructure Scaling Recommendations Endpoint...")
    try:
        payload = {
            "environment": "production",
            "currentMetrics": {
                "cpuAverage": 75,
                "memoryAverage": 80,
                "storageUsage": 85
            },
            "growthProjection": "2x"
        }
        response = requests.post(f"{ENTERPRISE_BASE_URL}/scale-infrastructure", json=payload)
        print(f"✓ Infrastructure Scaling: Status {response.status_code}")
        return response.status_code == 200
    except Exception as e:
        print(f"✗ Infrastructure Scaling Failed: {str(e)}")
        return False

def test_api_integration():
    """Test the API integration assistance endpoint"""
    print("Testing API Integration Assistance Endpoint...")
    try:
        payload = {
            "targetApi": "PaymentGatewayAPI",
            "integrationType": "oauth",
            "authDetails": {
                "clientId": "client-123",
                "clientSecret": "secret-456"
            }
        }
        response = requests.post(f"{ENTERPRISE_BASE_URL}/integrate-api", json=payload)
        print(f"✓ API Integration: Status {response.status_code}")
        return response.status_code == 200
    except Exception as e:
        print(f"✗ API Integration Failed: {str(e)}")
        return False

def test_client_onboarding():
    """Test the enterprise client onboarding endpoint"""
    print("Testing Enterprise Client Onboarding Endpoint...")
    try:
        payload = {
            "clientName": "TechCorp Solutions",
            "industry": "Technology",
            "companySize": "500-1000",
            "contactPerson": "John Smith",
            "email": "john.smith@techcorp.com",
            "subscriptionTier": "PREMIUM"
        }
        response = requests.post(f"{CLIENT_BASE_URL}/enterprise/onboard", json=payload)
        print(f"✓ Client Onboarding: Status {response.status_code}")
        return response.status_code == 200
    except Exception as e:
        print(f"✗ Client Onboarding Failed: {str(e)}")
        return False

def test_client_registration():
    """Test the enterprise client registration endpoint"""
    print("Testing Enterprise Client Registration Endpoint...")
    try:
        payload = {
            "clientName": "Global Industries Inc",
            "tier": "ENTERPRISE",
            "configuration": {
                "systems": ["ERP", "CRM", "HRIS"],
                "integrationLevel": "FULL"
            }
        }
        response = requests.post(f"{ENTERPRISE_BASE_URL}/register-client", json=payload)
        print(f"✓ Client Registration: Status {response.status_code}")
        return response.status_code == 200
    except Exception as e:
        print(f"✗ Client Registration Failed: {str(e)}")
        return False

def main():
    print("=" * 60)
    print("NEXUS AI COMPREHENSIVE SYSTEM VERIFICATION")
    print("=" * 60)
    print()
    
    # Wait a moment for the server to be fully ready
    time.sleep(2)
    
    # Run all tests
    tests = [
        test_health_endpoint,
        test_test_endpoint,
        test_performance_optimization,
        test_security_scan,
        test_code_refactoring,
        test_infrastructure_scaling,
        test_api_integration,
        test_client_onboarding,
        test_client_registration
    ]
    
    passed = 0
    total = len(tests)
    
    for test in tests:
        if test():
            passed += 1
        print()
    
    print("=" * 60)
    print(f"VERIFICATION COMPLETE: {passed}/{total} tests passed")
    if passed == total:
        print("🎉 ALL SYSTEMS OPERATIONAL - NEXUS AI IS READY FOR LAUNCH!")
    else:
        print("⚠️  Some tests failed. Please check the system.")
    print("=" * 60)

if __name__ == "__main__":
    main()
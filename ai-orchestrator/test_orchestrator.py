"""
Test Script for AI Orchestration System
"""

import asyncio
import json
from app import AIOrchestrationApp
from core import TaskComplexity

async def test_basic_request():
    """Test a basic AI request"""
    app = AIOrchestrationApp()
    
    # Create a test request
    request_data = {
        'id': 'test_001',
        'prompt': 'What is the capital of France?',
        'task_type': 'conversation',
        'complexity': 'simple',
        'context': {},
        'user_id': 'test_user',
        'priority': 1
    }
    
    print("Testing basic request...")
    response = await app.process_request(request_data)
    print(f"Response: {json.dumps(response, default=str, indent=2)}")
    print()

async def test_code_generation():
    """Test code generation request"""
    app = AIOrchestrationApp()
    
    # Create a code generation request
    request_data = {
        'id': 'test_002',
        'prompt': 'Generate a simple Python function to calculate factorial',
        'task_type': 'code_generation',
        'complexity': 'moderate',
        'context': {'language': 'Python'},
        'user_id': 'test_user',
        'priority': 3
    }
    
    print("Testing code generation request...")
    response = await app.process_request(request_data)
    print(f"Response: {json.dumps(response, default=str, indent=2)}")
    print()

async def test_complex_analysis():
    """Test complex analysis request"""
    app = AIOrchestrationApp()
    
    # Create a complex analysis request
    request_data = {
        'id': 'test_003',
        'prompt': 'Analyze the performance implications of using microservices architecture',
        'task_type': 'analysis',
        'complexity': 'complex',
        'context': {'domain': 'software_architecture'},
        'user_id': 'test_user',
        'priority': 4
    }
    
    print("Testing complex analysis request...")
    response = await app.process_request(request_data)
    print(f"Response: {json.dumps(response, default=str, indent=2)}")
    print()

async def main():
    """Run all tests"""
    print("🚀 Testing AI Orchestration System")
    print("=" * 50)
    
    try:
        await test_basic_request()
        await test_code_generation()
        await test_complex_analysis()
        
        print("✅ All tests completed successfully!")
    except Exception as e:
        print(f"❌ Test failed with error: {e}")
        raise

if __name__ == "__main__":
    asyncio.run(main())
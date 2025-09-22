"""
Test script for WHISKEY AI enhancements
Verifies that enhancement modules integrate correctly with WHISKEY AI core
"""

import asyncio
import sys
import os
from datetime import datetime

# Add the enhancements directory to Python path
sys.path.append(os.path.join(os.path.dirname(__file__)))

async def test_enhancement_integration():
    """Test the integration of all enhancement modules"""
    print("🧪 Testing WHISKEY AI Enhancement Integration")
    print("=" * 50)
    
    try:
        # Import enhancement modules
        from enhancement_integration import EnhancementIntegrator
        
        # Create a mock WHISKEY core for testing
        class MockWhiskeyCore:
            def __init__(self):
                self.active_tasks = []
                self.config = {}
        
        nexus_core = MockNexusCore()
        
        # Initialize enhancement integrator
        print("🔄 Initializing Enhancement Integrator...")
        enhancer = EnhancementIntegrator(nexus_core)
        
        # Test enhancement status
        print("📊 Checking Enhancement Status...")
        status = enhancer.get_enhancement_status()
        print(f"   Overall Status: {status['overall_status']}")
        print(f"   Modules Active: {len([m for m in status['modules'].values() if m['status'] in ['active', 'initialized']])}")
        
        # Test processing enhanced request
        print("\n🚀 Testing Enhanced Request Processing...")
        test_request = {
            'request_id': 'test_001',
            'user_id': 'developer_123',
            'text': 'Generate a Python function to calculate fibonacci sequence',
            'timestamp': datetime.now().isoformat()
        }
        
        result = await enhancer.process_enhanced_request(test_request)
        print(f"   Processing Time: {result.get('processing_time_ms', 'N/A')} ms")
        print(f"   Success: {'error' not in result}")
        
        if 'error' in result:
            print(f"   Error: {result['error']}")
        else:
            print(f"   Multi-modal Analysis: {result.get('multi_modal_analysis', {}).get('status', 'N/A')}")
            print(f"   Security Status: {result.get('security_status', {}).get('security_status', 'N/A')}")
            print(f"   Proactive Insights: {result.get('proactive_insights', {}).get('predictions_count', 0)} predictions")
        
        # Test shutdown
        print("\n🛑 Testing Graceful Shutdown...")
        await enhancer.shutdown_enhancements()
        
        print("\n✅ All Enhancement Tests Completed Successfully!")
        return True
        
    except Exception as e:
        print(f"❌ Enhancement Test Failed: {str(e)}")
        import traceback
        traceback.print_exc()
        return False

async def test_individual_modules():
    """Test individual enhancement modules"""
    print("\n🧪 Testing Individual Enhancement Modules")
    print("=" * 50)
    
    modules_tested = 0
    
    # Test Multi-Modal Enhancement
    try:
        from multi_modal_enhancement import MultiModalProcessor
        processor = MultiModalProcessor({'enable_vision': True, 'enable_voice': True})
        print("✅ Multi-Modal Enhancement Module: OK")
        modules_tested += 1
    except Exception as e:
        print(f"❌ Multi-Modal Enhancement Module: {str(e)}")
    
    # Test Proactive AI Module
    try:
        from proactive_ai_module import ProactiveIntelligence
        # Just test import, not initialization to avoid background tasks
        print("✅ Proactive AI Module: OK")
        modules_tested += 1
    except Exception as e:
        print(f"❌ Proactive AI Module: {str(e)}")
    
    # Test Emotional Intelligence Module
    try:
        from emotional_intelligence_module import EmotionalIntelligenceEngine
        print("✅ Emotional Intelligence Module: OK")
        modules_tested += 1
    except Exception as e:
        print(f"❌ Emotional Intelligence Module: {str(e)}")
    
    # Test Advanced Security Module
    try:
        from advanced_security_module import AdvancedSecurityEngine
        print("✅ Advanced Security Module: OK")
        modules_tested += 1
    except Exception as e:
        print(f"❌ Advanced Security Module: {str(e)}")
    
    # Test Multi-Agent Collaboration Module
    try:
        from multi_agent_collaboration import MultiAgentOrchestrator
        print("✅ Multi-Agent Collaboration Module: OK")
        modules_tested += 1
    except Exception as e:
        print(f"❌ Multi-Agent Collaboration Module: {str(e)}")
    
    # Test Edge AI Module
    try:
        from edge_ai_module import EdgeAIProcessor
        print("✅ Edge AI Module: OK")
        modules_tested += 1
    except Exception as e:
        print(f"❌ Edge AI Module: {str(e)}")
    
    print(f"\n📊 Individual Module Test Results: {modules_tested}/6 modules OK")
    return modules_tested == 6

def main():
    """Main test function"""
    print("NEXUS AI Enhancement Test Suite")
    print("=================================")
    
    # Test individual modules
    individual_success = asyncio.run(test_individual_modules())
    
    # Test integration
    integration_success = asyncio.run(test_enhancement_integration())
    
    print("\n" + "=" * 50)
    print("FINAL TEST RESULTS")
    print("=" * 50)
    print(f"Individual Modules: {'✅ PASS' if individual_success else '❌ FAIL'}")
    print(f"Integration Test: {'✅ PASS' if integration_success else '❌ FAIL'}")
    print(f"Overall Result: {'✅ ALL TESTS PASSED' if individual_success and integration_success else '❌ SOME TESTS FAILED'}")

if __name__ == "__main__":
    main()
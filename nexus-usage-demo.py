"""
Simple demo script to interact with WHISKEY AI
"""

import requests
import json
import time

# NEXUS AI API endpoint
NEXUS_API_BASE = "http://localhost:8085/api/nexus"

def test_nexus_connection():
    """Test if NEXUS AI is accessible"""
    try:
        response = requests.get(f"{NEXUS_API_BASE}/info")
        if response.status_code == 200:
            print("✅ Connected to NEXUS AI")
            info = response.json()
            print(f"   System: {info.get('name', 'Unknown')}")
            print(f"   Version: {info.get('version', 'Unknown')}")
            return True
        else:
            print(f"❌ Failed to connect to WHISKEY AI. Status code: {response.status_code}")
            return False
    except Exception as e:
        print(f"❌ Error connecting to NEXUS AI: {str(e)}")
        return False

def submit_task(description, user_id="demo_user", task_type="CODE_MODIFICATION"):
    """Submit a task to WHISKEY AI"""
    try:
        task_data = {
            "type": task_type,
            "description": description,
            "createdBy": user_id,
            "parameters": {}
        }
        
        response = requests.post(f"{NEXUS_API_BASE}/task", json=task_data)
        if response.status_code == 202:  # ACCEPTED
            task_info = response.json()
            print(f"✅ Task submitted successfully")
            print(f"   Task ID: {task_info.get('taskId', 'Unknown')}")
            print(f"   Status: {task_info.get('status', 'Unknown')}")
            return task_info.get('taskId')
        else:
            print(f"❌ Failed to submit task. Status code: {response.status_code}")
            print(f"   Response: {response.text}")
            return None
    except Exception as e:
        print(f"❌ Error submitting task: {str(e)}")
        return None

def check_task_status(task_id):
    """Check the status of a task"""
    try:
        response = requests.get(f"{NEXUS_API_BASE}/task/{task_id}")
        if response.status_code == 200:
            task_info = response.json()
            print(f"✅ Task status retrieved")
            print(f"   Status: {task_info.get('status', 'Unknown')}")
            print(f"   Progress: {task_info.get('progress', 0)}%")
            print(f"   Message: {task_info.get('message', 'N/A')}")
            return task_info
        else:
            print(f"❌ Failed to get task status. Status code: {response.status_code}")
            return None
    except Exception as e:
        print(f"❌ Error checking task status: {str(e)}")
        return None

def get_all_tasks():
    """Get all tasks"""
    try:
        response = requests.get(f"{NEXUS_API_BASE}/tasks")
        if response.status_code == 200:
            tasks = response.json()
            print(f"✅ Retrieved {len(tasks)} tasks")
            for task in tasks:
                print(f"   - Task {task.get('taskId', 'Unknown')}: {task.get('status', 'Unknown')} ({task.get('progress', 0)}%)")
            return tasks
        else:
            print(f"❌ Failed to get tasks. Status code: {response.status_code}")
            return []
    except Exception as e:
        print(f"❌ Error getting tasks: {str(e)}")
        return []

def get_system_health():
    """Get system health"""
    try:
        response = requests.get(f"{NEXUS_API_BASE}/health")
        if response.status_code == 200:
            health = response.json()
            print(f"✅ System Health: {health.get('status', 'Unknown')}")
            return health
        else:
            print(f"❌ Failed to get system health. Status code: {response.status_code}")
            return None
    except Exception as e:
        print(f"❌ Error getting system health: {str(e)}")
        return None

def get_system_metrics():
    """Get system metrics"""
    try:
        response = requests.get(f"{NEXUS_API_BASE}/metrics")
        if response.status_code == 200:
            metrics = response.json()
            print(f"✅ System Metrics:")
            print(f"   CPU Usage: {metrics.get('cpuUsage', 'N/A')}%")
            print(f"   Memory Usage: {metrics.get('memoryUsage', 'N/A')}%")
            print(f"   Active Tasks: {metrics.get('activeTasks', 'N/A')}")
            return metrics
        else:
            print(f"❌ Failed to get system metrics. Status code: {response.status_code}")
            return None
    except Exception as e:
        print(f"❌ Error getting system metrics: {str(e)}")
        return None

def trigger_maintenance():
    """Trigger autonomous maintenance"""
    try:
        response = requests.post(f"{NEXUS_API_BASE}/maintenance")
        if response.status_code == 202:  # ACCEPTED
            task_info = response.json()
            print(f"✅ Maintenance task triggered successfully")
            print(f"   Task ID: {task_info.get('taskId', 'Unknown')}")
            print(f"   Status: {task_info.get('status', 'Unknown')}")
            return task_info.get('taskId')
        else:
            print(f"❌ Failed to trigger maintenance. Status code: {response.status_code}")
            print(f"   Response: {response.text}")
            return None
    except Exception as e:
        print(f"❌ Error triggering maintenance: {str(e)}")
        return None

def main():
    """Main demo function"""
    print("🥃 NEXUS AI Usage Demo")
    print("=" * 30)
    
    # Test connection
    if not test_nexus_connection():
        return
    
    print("\n" + "-" * 30)
    
    # Get system health
    print("Checking system health...")
    get_system_health()
    
    print("\n" + "-" * 30)
    
    # Get system metrics
    print("Checking system metrics...")
    get_system_metrics()
    
    print("\n" + "-" * 30)
    
    # Submit a sample task
    print("Submitting a sample task...")
    task_id = submit_task("Generate a Python function to calculate fibonacci sequence", "demo_user", "CODE_MODIFICATION")
    
    if task_id:
        print("\n" + "-" * 30)
        
        # Wait a moment for processing
        print("Waiting for task processing...")
        time.sleep(3)
        
        # Check task status
        print("Checking task status...")
        check_task_status(task_id)
        
        print("\n" + "-" * 30)
        
        # Get all tasks
        print("Getting all tasks...")
        get_all_tasks()
    
    print("\n" + "-" * 30)
    
    # Trigger maintenance
    print("Triggering autonomous maintenance...")
    maintenance_task_id = trigger_maintenance()
    
    if maintenance_task_id:
        print("\n" + "-" * 30)
        
        # Wait a moment for processing
        print("Waiting for maintenance processing...")
        time.sleep(3)
        
        # Check maintenance task status
        print("Checking maintenance task status...")
        check_task_status(maintenance_task_id)
    
    print("\n" + "=" * 30)
    print("Demo completed!")

if __name__ == "__main__":
    main()
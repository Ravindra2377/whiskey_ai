#!/usr/bin/env python3
"""
WHISKEY Database Integration Test
This script tests the database integration of WHISKEY AI.
"""

import requests
import time
import json
import psycopg2
from threading import Thread

def start_whiskey():
    """Start WHISKEY in a separate thread"""
    import subprocess
    import os
    
    # Change to whiskey directory
    os.chdir('whiskey')
    
    # Start WHISKEY (this would normally be done in a separate process)
    # For now, we'll just test the API assuming WHISKEY is running
    print("Please start WHISKEY manually with: cd whiskey && ./mvnw spring-boot:run")
    print("Then press Enter to continue with the test...")
    input()

def test_database_connection():
    """Test connection to PostgreSQL database"""
    try:
        conn = psycopg2.connect(
            host="localhost",
            database="boozer_db",
            user="boozer_user",
            password="boozer_password",
            port="5432"
        )
        
        cursor = conn.cursor()
        cursor.execute("SELECT version();")
        version = cursor.fetchone()
        print(f"✓ Connected to PostgreSQL: {version[0]}")
        
        # Check if whiskey_tasks table exists
        cursor.execute("""
            SELECT EXISTS (
                SELECT FROM information_schema.tables 
                WHERE table_name = 'whiskey_tasks'
            );
        """)
        table_exists = cursor.fetchone()[0]
        
        if table_exists:
            print("✓ WHISKEY tasks table exists")
        else:
            print("⚠ WHISKEY tasks table does not exist yet")
            
        cursor.close()
        conn.close()
        return True
        
    except Exception as e:
        print(f"✗ Error connecting to database: {e}")
        return False

def test_whiskey_api():
    """Test WHISKEY API endpoints"""
    base_url = "http://localhost:8085/api/whiskey"
    
    try:
        # Test health endpoint
        response = requests.get(f"{base_url}/health")
        if response.status_code == 200:
            print("✓ WHISKEY health endpoint is accessible")
            health_data = response.json()
            print(f"  Health status: {health_data.get('status', 'Unknown')}")
        else:
            print(f"✗ WHISKEY health endpoint returned {response.status_code}")
            return False
            
        # Test info endpoint
        response = requests.get(f"{base_url}/info")
        if response.status_code == 200:
            print("✓ WHISKEY info endpoint is accessible")
        else:
            print(f"✗ WHISKEY info endpoint returned {response.status_code}")
            
        return True
        
    except requests.exceptions.ConnectionError:
        print("✗ Cannot connect to WHISKEY API. Is it running?")
        return False
    except Exception as e:
        print(f"✗ Error testing WHISKEY API: {e}")
        return False

def submit_test_task():
    """Submit a test task to WHISKEY"""
    base_url = "http://localhost:8085/api/whiskey"
    
    # Test task data
    task_data = {
        "type": "CODE_ANALYSIS",
        "description": "Test task for database integration",
        "createdBy": "DATABASE_TEST",
        "parameters": {
            "test": True,
            "purpose": "database_integration_test"
        }
    }
    
    try:
        response = requests.post(f"{base_url}/task", json=task_data)
        if response.status_code == 202:
            print("✓ Test task submitted successfully")
            task_response = response.json()
            task_id = task_response.get('taskId')
            print(f"  Task ID: {task_id}")
            return task_id
        else:
            print(f"✗ Failed to submit test task: {response.status_code}")
            print(f"  Response: {response.text}")
            return None
            
    except Exception as e:
        print(f"✗ Error submitting test task: {e}")
        return None

def check_task_in_database(task_id):
    """Check if the task exists in the database"""
    try:
        conn = psycopg2.connect(
            host="localhost",
            database="boozer_db",
            user="boozer_user",
            password="boozer_password",
            port="5432"
        )
        
        cursor = conn.cursor()
        
        # Check if task exists
        cursor.execute("SELECT task_id, task_type, description, status FROM whiskey_tasks WHERE task_id = %s", (task_id,))
        task_record = cursor.fetchone()
        
        if task_record:
            print("✓ Task found in database:")
            print(f"  Task ID: {task_record[0]}")
            print(f"  Task Type: {task_record[1]}")
            print(f"  Description: {task_record[2]}")
            print(f"  Status: {task_record[3]}")
            return True
        else:
            print("⚠ Task not found in database")
            return False
            
        cursor.close()
        conn.close()
        
    except Exception as e:
        print(f"✗ Error checking task in database: {e}")
        return False

def main():
    print("===============================================")
    print("WHISKEY AI Database Integration Test")
    print("===============================================")
    print()
    
    # Test database connection
    print("1. Testing database connection...")
    if not test_database_connection():
        print("Database connection test failed. Exiting.")
        return 1
    
    print()
    
    # Test WHISKEY API
    print("2. Testing WHISKEY API...")
    if not test_whiskey_api():
        print("WHISKEY API test failed. Exiting.")
        return 1
    
    print()
    
    # Submit test task
    print("3. Submitting test task...")
    task_id = submit_test_task()
    if not task_id:
        print("Failed to submit test task. Exiting.")
        return 1
    
    print()
    
    # Wait a moment for the task to be processed
    print("4. Waiting for task to be processed...")
    time.sleep(2)
    
    # Check task in database
    print("5. Checking task in database...")
    if check_task_in_database(task_id):
        print()
        print("🎉 Database integration test completed successfully!")
        print("   WHISKEY is properly connected to PostgreSQL")
        return 0
    else:
        print()
        print("❌ Database integration test failed!")
        print("   Task was not found in the database")
        return 1

if __name__ == "__main__":
    exit(main())
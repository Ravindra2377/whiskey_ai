#!/usr/bin/env python3
"""
Script to import all non-nexus files into the NEXUS AI database
"""

import requests
import json
import time
import os

def import_directory_files(directory_path, base_url="http://localhost:8085/api/nexus/boozer-files"):
    """Import files from a specific directory into NEXUS database"""
    
    # Request payload
    payload = {
        "directoryPath": directory_path
    }
    
    headers = {
        "Content-Type": "application/json"
    }
    
    try:
        print(f"Importing files from {directory_path} into NEXUS database...")
        response = requests.post(f"{base_url}/import", 
                                data=json.dumps(payload), 
                                headers=headers)
        
        if response.status_code == 200:
            result = response.json()
            print(f"✓ Success: {result.get('message')}")
            print(f"  Imported {result.get('importedCount')} files")
            return result.get('importedCount', 0)
        else:
            print(f"✗ Error: {response.status_code}")
            print(f"  {response.text}")
            return 0
            
    except requests.exceptions.ConnectionError:
        print("✗ Error: Cannot connect to NEXUS API")
        print("  Please make sure NEXUS is running on port 8085")
        return 0
    except Exception as e:
        print(f"✗ Error: {e}")
        return 0

def check_nexus_health():
    """Check if NEXUS is running and healthy"""
    try:
        response = requests.get("http://localhost:8085/api/nexus/health")
        if response.status_code == 200:
            health = response.json()
            if health.get("status") == "HEALTHY":
                print("✓ NEXUS is running and healthy")
                return True
            else:
                print(f"⚠ NEXUS health status: {health.get('status')}")
                return True  # Still running, just not healthy
        else:
            print(f"✗ NEXUS health check failed: {response.status_code}")
            return False
    except requests.exceptions.ConnectionError:
        print("✗ NEXUS is not running")
        return False
    except Exception as e:
        print(f"✗ Error checking NEXUS health: {e}")
        return False

def get_file_info():
    """Get information about imported files"""
    try:
        response = requests.get("http://localhost:8085/api/nexus/boozer-files/info")
        if response.status_code == 200:
            info = response.json()
            print(f"✓ Database contains {info.get('totalFiles')} files")
            print("  File types:")
            for file_type, count in info.get('fileTypes', {}).items():
                print(f"    {file_type}: {count}")
            return True
        else:
            print(f"✗ Error getting file info: {response.status_code}")
            return False
    except Exception as e:
        print(f"✗ Error getting file info: {e}")
        return False

def main():
    print("===============================================")
    print("All Non-Nexus Files Import to NEXUS Database")
    print("===============================================")
    print()
    
    # Check if NEXUS is running
    print("1. Checking NEXUS status...")
    if not check_nexus_health():
        print()
        print("Please start NEXUS before running this script:")
        print("  cd nexus")
        print("  ./mvnw spring-boot:run")
        print()
        return 1
    
    print()
    
    # Directories to import (excluding nexus directory)
    directories_to_import = [
        "../backend/src/main",
        "../frontend/src",
        "../src/main"
    ]
    
    total_imported = 0
    
    # Import files from each directory
    for i, directory in enumerate(directories_to_import, 2):
        print(f"{i}. Importing files from {directory}...")
        if os.path.exists(directory):
            imported_count = import_directory_files(directory)
            total_imported += imported_count
        else:
            print(f"  Directory {directory} does not exist, skipping...")
        print()
    
    # Wait a moment for processing
    time.sleep(2)
    
    # Get file information
    print(f"{len(directories_to_import) + 2}. Checking imported files...")
    get_file_info()
    
    print()
    print("🎉 All non-nexus files import completed successfully!")
    print(f"   Total files imported: {total_imported}")
    print("   You can now access all files through NEXUS API endpoints:")
    print("   - List all files: GET http://localhost:8085/api/nexus/boozer-files")
    print("   - Search files: GET http://localhost:8085/api/nexus/boozer-files/search?query=YourQuery")
    print("   - Get file by path: GET http://localhost:8085/api/nexus/boozer-files/path/{filePath}")
    print("   - Get file content: GET http://localhost:8085/api/nexus/boozer-files/content/{filePath}")
    
    return 0

if __name__ == "__main__":
    exit(main())
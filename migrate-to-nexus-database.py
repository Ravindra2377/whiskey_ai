#!/usr/bin/env python3
"""
Migrate All Files to NEXUS Database and Clean Directory
"""

import requests
import json
import os
import shutil
import glob
import time

def check_nexus_health():
    """Check if NEXUS is running and healthy"""
    try:
        response = requests.get("http://localhost:8085/api/nexus/health", timeout=5)
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
        print("")
        print("Please start NEXUS before running this script:")
        print("  cd nexus")
        print("  ./mvnw spring-boot:run")
        print("")
        return False
    except Exception as e:
        print(f"✗ Error checking NEXUS health: {e}")
        return False

def import_files_to_nexus():
    """Import all files to NEXUS database"""
    base_url = "http://localhost:8085/api/nexus/boozer-files"
    
    # Request payload
    payload = {
        "directoryPath": "."
    }
    
    headers = {
        "Content-Type": "application/json"
    }
    
    try:
        print("Importing files to NEXUS database...")
        response = requests.post(f"{base_url}/import", 
                                data=json.dumps(payload), 
                                headers=headers)
        
        if response.status_code == 200:
            result = response.json()
            print(f"✓ Success: {result.get('message')}")
            print(f"  Imported {result.get('importedCount')} files")
            return True
        else:
            print(f"✗ Error: {response.status_code}")
            print(f"  {response.text}")
            return False
            
    except requests.exceptions.ConnectionError:
        print("✗ Error: Cannot connect to NEXUS API")
        print("  Please make sure NEXUS is running on port 8085")
        return False
    except Exception as e:
        print(f"✗ Error: {e}")
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

def clean_directory():
    """Clean directory to keep only NEXUS files"""
    print("Cleaning directory to keep only NEXUS files...")
    
    # Patterns of files/directories to keep
    keep_patterns = [
        "nexus",
        "nexus-ai-*",
        "NEXUS_*",
        "import-boozer-files.*",
        "migrate-to-nexus-database.*",
        "*.zip"
    ]
    
    # Current script name to keep
    current_script = "migrate-to-nexus-database.py"
    
    deleted_count = 0
    
    # Get all items in current directory
    try:
        items = os.listdir(".")
        
        for item in items:
            # Skip current directory and parent directory
            if item in [".", ".."]:
                continue
                
            # Check if item should be kept
            should_keep = False
            
            # Special case: keep this script
            if item == current_script:
                should_keep = True
            
            # Check against keep patterns
            if not should_keep:
                for pattern in keep_patterns:
                    if glob.fnmatch.fnmatch(item, pattern):
                        should_keep = True
                        break
            
            # Delete if not in keep list
            if not should_keep:
                try:
                    item_path = os.path.join(".", item)
                    if os.path.isdir(item_path):
                        shutil.rmtree(item_path)
                        print(f"  Deleted directory: {item}")
                    else:
                        os.remove(item_path)
                        print(f"  Deleted file: {item}")
                    deleted_count += 1
                except Exception as e:
                    print(f"  Failed to delete: {item} - {e}")
            else:
                print(f"  Keeping: {item}")
                
        print(f"  Deleted {deleted_count} items")
        return True
        
    except Exception as e:
        print(f"✗ Error cleaning directory: {e}")
        return False

def main():
    print("===============================================")
    print("Migrating Files to NEXUS Database")
    print("===============================================")
    print()
    
    # Check if NEXUS is running
    print("1. Checking NEXUS status...")
    if not check_nexus_health():
        return 1
    
    print()
    
    # Import files
    print("2. Importing all files to NEXUS database...")
    if not import_files_to_nexus():
        return 1
    
    print()
    
    # Wait a moment for processing
    time.sleep(2)
    
    # Get file information
    print("3. Verifying imported files...")
    get_file_info()
    
    print()
    
    # Clean directory
    print("4. Cleaning directory...")
    if not clean_directory():
        return 1
    
    print()
    print("🎉 Migration completed successfully!")
    print("   All files have been imported to NEXUS database")
    print("   Directory has been cleaned to keep only NEXUS files")
    print()
    print("   You can now access your files through NEXUS API:")
    print("   - List all files: GET http://localhost:8085/api/nexus/boozer-files")
    print("   - Search files: GET http://localhost:8085/api/nexus/boozer-files/search?query=YourQuery")
    print("   - Get file content: GET http://localhost:8085/api/nexus/boozer-files/content/{filePath}")
    
    return 0

if __name__ == "__main__":
    exit(main())
#!/usr/bin/env python3
"""
WHISKEY PostgreSQL Setup Verification Script
This script verifies that WHISKEY is properly set up with PostgreSQL integration.
"""

import os
import sys

def check_file_exists(filepath):
    """Check if a file exists"""
    exists = os.path.exists(filepath)
    status = "✓" if exists else "✗"
    print(f"{status} {filepath}")
    return exists

def check_directory_exists(dirpath):
    """Check if a directory exists"""
    exists = os.path.exists(dirpath)
    status = "✓" if exists else "✗"
    print(f"{status} {dirpath}")
    return exists

def main():
    print("===============================================")
    print("WHISKEY PostgreSQL Setup Verification")
    print("===============================================")
    print()
    
    print("Checking required files...")
    
    # Check main WHISKEY directory
    whiskey_dir = "whiskey"
    if not check_directory_exists(whiskey_dir):
        print("ERROR: WHISKEY directory not found!")
        return 1
    
    # Check database-related files
    files_to_check = [
        "whiskey/pom.xml",
        "whiskey/src/main/resources/application.properties",
        "whiskey/src/main/java/com/boozer/whiskey/model/WhiskeyTaskEntity.java",
        "whiskey/src/main/java/com/boozer/whiskey/repository/WhiskeyTaskRepository.java",
        "whiskey/src/main/java/com/boozer/whiskey/service/WhiskeyTaskService.java",
        "whiskey/src/main/java/com/boozer/whiskey/config/DatabaseConfig.java",
        "whiskey/src/main/java/com/boozer/whiskey/WhiskeyController.java"
    ]
    
    all_files_exist = True
    for filepath in files_to_check:
        if not check_file_exists(filepath):
            all_files_exist = False
    
    print()
    
    # Check documentation files
    print("Checking documentation files...")
    docs_to_check = [
        "WHISKEY_POSTGRESQL_SETUP.md",
        "WHISKEY_DATABASE_DOCUMENTATION.md",
        "WHISKEY_POSTGRESQL_INTEGRATION_SUMMARY.md"
    ]
    
    all_docs_exist = True
    for filepath in docs_to_check:
        if not check_file_exists(filepath):
            all_docs_exist = False
    
    print()
    
    # Check script files
    print("Checking script files...")
    scripts_to_check = [
        "setup-postgresql-automated.ps1",
        "initialize-whiskey-database.py",
        "start-whiskey-with-postgres.bat",
        "start-whiskey-with-postgres.ps1",
        "test-whiskey-database.py"
    ]
    
    all_scripts_exist = True
    for filepath in scripts_to_check:
        if not check_file_exists(filepath):
            all_scripts_exist = False
    
    print()
    
    # Summary
    print("===============================================")
    print("SUMMARY")
    print("===============================================")
    
    if all_files_exist:
        print("✓ All WHISKEY database integration files are in place")
    else:
        print("✗ Some WHISKEY database integration files are missing")
    
    if all_docs_exist:
        print("✓ All documentation files are in place")
    else:
        print("✗ Some documentation files are missing")
    
    if all_scripts_exist:
        print("✓ All script files are in place")
    else:
        print("✗ Some script files are missing")
    
    print()
    
    if all_files_exist and all_docs_exist and all_scripts_exist:
        print("🎉 WHISKEY PostgreSQL integration is properly set up!")
        print()
        print("NEXT STEPS:")
        print("1. Install PostgreSQL if not already installed")
        print("2. Create the database and user as described in WHISKEY_POSTGRESQL_SETUP.md")
        print("3. Start WHISKEY with: cd whiskey && ./mvnw spring-boot:run")
        print("4. Test the integration with test-whiskey-database.py")
        return 0
    else:
        print("❌ WHISKEY PostgreSQL integration is incomplete!")
        print("Please check the missing files and ensure all components are properly installed.")
        return 1

if __name__ == "__main__":
    sys.exit(main())
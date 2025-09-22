#!/usr/bin/env python3
"""
Verification script for WHISKEY permanent file migration functionality
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
    print("WHISKEY Permanent File Migration Verification")
    print("===============================================")
    print()
    
    print("Checking required files...")
    
    # Check main WHISKEY directory
    whiskey_dir = "whiskey"
    if not check_directory_exists(whiskey_dir):
        print("ERROR: WHISKEY directory not found!")
        return 1
    
    # Check migration scripts
    scripts_to_check = [
        "migrate-to-whiskey-database.py",
        "migrate-to-whiskey-database.ps1",
        "migrate-to-whiskey-database.bat"
    ]
    
    all_scripts_exist = True
    for filepath in scripts_to_check:
        if not check_file_exists(filepath):
            all_scripts_exist = False
    
    print()
    
    # Check documentation files
    print("Checking documentation files...")
    docs_to_check = [
        "WHISKEY_PERMANENT_FILE_MIGRATION.md"
    ]
    
    all_docs_exist = True
    for filepath in docs_to_check:
        if not check_file_exists(filepath):
            all_docs_exist = False
    
    print()
    
    # Check existing Boozer file import components
    print("Checking existing Boozer file import components...")
    existing_components = [
        "whiskey/src/main/java/com/boozer/whiskey/model/BoozerFileEntity.java",
        "whiskey/src/main/java/com/boozer/whiskey/repository/BoozerFileRepository.java",
        "whiskey/src/main/java/com/boozer/whiskey/service/BoozerFileService.java",
        "whiskey/src/main/java/com/boozer/whiskey/BoozerFileController.java"
    ]
    
    all_components_exist = True
    for filepath in existing_components:
        if not check_file_exists(filepath):
            all_components_exist = False
    
    print()
    
    # Summary
    print("===============================================")
    print("SUMMARY")
    print("===============================================")
    
    if all_scripts_exist:
        print("✓ All migration scripts are in place")
    else:
        print("✗ Some migration scripts are missing")
    
    if all_docs_exist:
        print("✓ All documentation files are in place")
    else:
        print("✗ Some documentation files are missing")
    
    if all_components_exist:
        print("✓ All existing Boozer file import components are in place")
    else:
        print("✗ Some existing Boozer file import components are missing")
    
    print()
    
    if all_scripts_exist and all_docs_exist and all_components_exist:
        print("🎉 WHISKEY permanent file migration functionality is properly set up!")
        print()
        print("NEXT STEPS:")
        print("1. Start WHISKEY: cd whiskey && ./mvnw spring-boot:run")
        print("2. Run the migration script: python migrate-to-whiskey-database.py")
        print("3. All files will be imported to WHISKEY database and directory will be cleaned")
        return 0
    else:
        print("❌ WHISKEY permanent file migration functionality is incomplete!")
        print("Please check the missing files and ensure all components are properly installed.")
        return 1

if __name__ == "__main__":
    sys.exit(main())
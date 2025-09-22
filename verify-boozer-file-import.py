#!/usr/bin/env python3
"""
Verification script for WHISKEY Boozer file import functionality
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
    print("WHISKEY Boozer File Import Verification")
    print("===============================================")
    print()
    
    print("Checking required files...")
    
    # Check main WHISKEY directory
    whiskey_dir = "whiskey"
    if not check_directory_exists(whiskey_dir):
        print("ERROR: WHISKEY directory not found!")
        return 1
    
    # Check new database components
    files_to_check = [
        "whiskey/src/main/java/com/boozer/whiskey/model/BoozerFileEntity.java",
        "whiskey/src/main/java/com/boozer/whiskey/repository/BoozerFileRepository.java",
        "whiskey/src/main/java/com/boozer/whiskey/service/BoozerFileService.java",
        "whiskey/src/main/java/com/boozer/whiskey/BoozerFileController.java"
    ]
    
    all_files_exist = True
    for filepath in files_to_check:
        if not check_file_exists(filepath):
            all_files_exist = False
    
    print()
    
    # Check documentation files
    print("Checking documentation files...")
    docs_to_check = [
        "WHISKEY_BOOZER_FILE_IMPORT.md"
    ]
    
    all_docs_exist = True
    for filepath in docs_to_check:
        if not check_file_exists(filepath):
            all_docs_exist = False
    
    print()
    
    # Check script files
    print("Checking script files...")
    scripts_to_check = [
        "import-boozer-files.py",
        "import-boozer-files.ps1",
        "import-boozer-files.bat"
    ]
    
    all_scripts_exist = True
    for filepath in scripts_to_check:
        if not check_file_exists(filepath):
            all_scripts_exist = False
    
    print()
    
    # Check if backend directory exists (source of files to import)
    print("Checking Boozer app directory...")
    backend_exists = check_directory_exists("backend")
    
    print()
    
    # Summary
    print("===============================================")
    print("SUMMARY")
    print("===============================================")
    
    if all_files_exist:
        print("✓ All WHISKEY Boozer file import components are in place")
    else:
        print("✗ Some WHISKEY Boozer file import components are missing")
    
    if all_docs_exist:
        print("✓ All documentation files are in place")
    else:
        print("✗ Some documentation files are missing")
    
    if all_scripts_exist:
        print("✓ All script files are in place")
    else:
        print("✗ Some script files are missing")
    
    if backend_exists:
        print("✓ Boozer app directory found")
    else:
        print("⚠ Boozer app directory not found (needed for import)")
    
    print()
    
    if all_files_exist and all_docs_exist and all_scripts_exist:
        print("🎉 WHISKEY Boozer file import functionality is properly set up!")
        print()
        print("NEXT STEPS:")
        print("1. Start WHISKEY: cd whiskey && ./mvnw spring-boot:run")
        print("2. Run the import script: python import-boozer-files.py")
        print("3. Access Boozer files through WHISKEY API endpoints")
        return 0
    else:
        print("❌ WHISKEY Boozer file import functionality is incomplete!")
        print("Please check the missing files and ensure all components are properly installed.")
        return 1

if __name__ == "__main__":
    sys.exit(main())
# NEXUS Permanent File Migration

## Overview

This document describes how to permanently migrate all files to the NEXUS AI database and clean the directory to keep only NEXUS-related files. This process ensures that all your project files are stored in the database while maintaining a clean directory structure with only NEXUS components.

## How It Works

The migration process:
1. Imports all files from the current directory to the NEXUS database
2. Cleans the directory by removing all non-NEXUS files
3. Keeps only essential NEXUS files and tools
4. Maintains access to all files through NEXUS API endpoints

## Files Kept After Migration

The following files and directories are preserved after migration:
- `nexus/` - Main NEXUS AI system
- `nexus-ai-*` - NEXUS AI related files and directories
- `NEXUS_*` - NEXUS documentation and configuration files
- `import-boozer-files.*` - File import scripts
- `migrate-to-nexus-database.*` - Migration scripts
- `*.zip` - Archive files

## Prerequisites

1. NEXUS AI system must be running
2. PostgreSQL database must be accessible
3. All files to be migrated should be in the current directory

## Usage

### Method 1: PowerShell Script (Windows)
```powershell
.\migrate-to-nexus-database.ps1
```

### Method 2: Batch File (Windows)
```cmd
migrate-to-nexus-database.bat
```

### Method 3: Python Script (Cross-platform)
```bash
python migrate-to-nexus-database.py
```

## Process Steps

1. **Health Check**: Verifies NEXUS is running and accessible
2. **File Import**: Imports all files from current directory to NEXUS database
3. **Verification**: Confirms files were successfully imported
4. **Directory Cleanup**: Removes all non-essential files and directories
5. **Completion**: Reports migration status and provides access information

## Accessing Files After Migration

Once migrated, all files can be accessed through NEXUS API endpoints:

### List All Files
```
GET http://localhost:8085/api/nexus/boozer-files
```

### Search Files
```
GET http://localhost:8085/api/nexus/boozer-files/search?query={searchTerm}
```

### Get File by Path
```
GET http://localhost:8085/api/nexus/boozer-files/path/{filePath}
```

### Get File Content
```
GET http://localhost:8085/api/nexus/boozer-files/content/{filePath}
```

### File Information
```
GET http://localhost:8085/api/nexus/boozer-files/info
```

## Benefits

1. **Permanent Storage**: All files stored securely in PostgreSQL database
2. **Clean Directory**: Reduced clutter with only essential NEXUS files
3. **Centralized Access**: All files accessible through single API
4. **Persistent Availability**: Files available even after directory cleanup
5. **Searchable**: Full-text search capabilities for all migrated files

## Security Considerations

1. **Database Security**: Ensure PostgreSQL credentials are secure
2. **API Access**: Protect NEXUS API endpoints with authentication
3. **File Content**: Only migrate files that should be stored in the database
4. **Backup**: Consider backing up important files before migration

## Troubleshooting

### Common Issues

1. **NEXUS Not Running**: Start NEXUS before running migration
2. **Database Connection**: Check PostgreSQL connectivity
3. **File Access**: Ensure scripts have permission to read files
4. **API Errors**: Verify NEXUS is functioning correctly

### Diagnostic Commands

Check NEXUS health:
```
GET http://localhost:8085/api/nexus/health
```

Get file import statistics:
```
GET http://localhost:8085/api/nexus/boozer-files/info
```

List all files in database:
```
GET http://localhost:8085/api/nexus/boozer-files
```

## Recovery

If you need to recover files after migration:
1. Access files through NEXUS API endpoints
2. Download file content as needed
3. Recreate directory structure if necessary

## Example Usage

### Before Migration
```bash
project/
├── backend/
├── frontend/
├── documentation/
├── nexus/
├── README.md
├── config.json
└── ...
```

### After Migration
```bash
project/
├── nexus/
├── NEXUS_DOCUMENTATION.md
├── import-boozer-files.py
└── migrate-to-nexus-database.py
```

All original files are now stored in the NEXUS database and accessible through API endpoints.
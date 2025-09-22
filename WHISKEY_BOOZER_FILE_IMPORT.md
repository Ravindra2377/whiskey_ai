# NEXUS AI Boozer File Import Documentation

## Overview

This document describes how to import all Boozer application files into the NEXUS AI database for analysis and processing. Once imported, NEXUS can access, analyze, and modify Boozer files through its API.

## How It Works

The file import process:
1. Scans the Boozer application directory structure
2. Reads all relevant source code files
3. Stores file content and metadata in the PostgreSQL database
4. Indexes files by path, type, package, and class name
5. Makes files accessible through NEXUS API endpoints

## Database Schema

### boozer_files Table

| Column Name | Type | Description |
|-------------|------|-------------|
| id | BIGSERIAL (Primary Key) | Auto-generated unique identifier |
| file_path | VARCHAR(255) | Relative path to the file |
| file_name | VARCHAR(255) | File name with extension |
| file_type | VARCHAR(50) | File extension (java, js, xml, etc.) |
| file_size | BIGINT | Size of file content in characters |
| content | TEXT | Full file content |
| package_name | VARCHAR(255) | Java package name (for .java files) |
| class_name | VARCHAR(255) | Java class name (for .java files) |
| created_at | TIMESTAMP | When the record was created |
| updated_at | TIMESTAMP | When the record was last updated |

## API Endpoints

### Import Files
```
POST /api/nexus/boozer-files/import
```
Imports all Boozer files from the specified directory into the database.

Request Body:
```json
{
  "directoryPath": "../backend"
}
```

### List All Files
```
GET /api/nexus/boozer-files
```
Returns a list of all imported Boozer files with metadata.

### Get File by Path
```
GET /api/nexus/boozer-files/path/{filePath}
```
Returns metadata for a specific file by its path.

### Get File Content
```
GET /api/nexus/boozer-files/content/{filePath}
```
Returns the content of a specific file by its path.

### Search Files
```
GET /api/nexus/boozer-files/search?query={searchTerm}
```
Searches for files by file name, package name, or class name.

### Get Files by Type
```
GET /api/nexus/boozer-files/type/{fileType}
```
Returns all files of a specific type (e.g., java, js, xml).

### Get Files by Package
```
GET /api/nexus/boozer-files/package/{packageName}
```
Returns all files in a specific Java package.

### Get Files by Class
```
GET /api/nexus/boozer-files/class/{className}
```
Returns files containing a specific Java class.

### File Information
```
GET /api/nexus/boozer-files/info
```
Returns statistics about imported files.

## Supported File Types

The import process supports these file types:
- Java source files (.java)
- JavaScript files (.js)
- Python files (.py)
- XML configuration files (.xml)
- Properties files (.properties)
- SQL files (.sql)
- Markdown documentation (.md)
- JSON files (.json)

## Usage

### Prerequisites
1. NEXUS AI system must be running
2. PostgreSQL database must be accessible
3. Boozer application files must be available

### Import Process
1. Start NEXUS AI system:
   ```bash
   cd nexus
   ./mvnw spring-boot:run
   ```

2. Run the import script:
   ```bash
   python import-boozer-files.py
   ```
   
   Or on Windows:
   ```powershell
   .\import-boozer-files.ps1
   ```

3. Alternatively, use the API directly:
   ```bash
   curl -X POST http://localhost:8085/api/nexus/boozer-files/import \
        -H "Content-Type: application/json" \
        -d '{"directoryPath":"../backend"}'
   ```

## Accessing Files Through NEXUS

Once imported, Boozer files can be accessed through NEXUS for:
- Code analysis and review
- Automated refactoring
- Bug detection and fixing
- Performance optimization
- Security scanning
- Documentation generation

### Example Usage

Get a list of all Java controller files:
```
GET http://localhost:8085/api/nexus/boozer-files/type/java
```

Search for files related to "user":
```
GET http://localhost:8085/api/nexus/boozer-files/search?query=user
```

Get the content of a specific file:
```
GET http://localhost:8085/api/nexus/boozer-files/content/com/boozer/controller/UserController.java
```

## Benefits

1. **Centralized Access**: All Boozer files accessible through a single API
2. **Persistent Storage**: File content stored securely in PostgreSQL
3. **Searchable**: Files indexed for quick search and retrieval
4. **Metadata Rich**: Package and class information extracted for Java files
5. **Integration Ready**: Seamless integration with NEXUS AI capabilities

## Security Considerations

1. **File Content**: Only import files that should be accessible to NEXUS
2. **Database Access**: Secure database credentials
3. **API Security**: Protect NEXUS API endpoints with authentication
4. **Data Privacy**: Ensure no sensitive information is stored in imported files

## Troubleshooting

### Common Issues

1. **Connection Refused**: Ensure NEXUS is running on port 8085
2. **Directory Not Found**: Verify the directory path in the import request
3. **Database Errors**: Check PostgreSQL connectivity and permissions
4. **File Access Issues**: Ensure NEXUS has read access to Boozer files

### Diagnostic Endpoints

Check NEXUS health:
```
GET http://localhost:8085/api/nexus/health
```

Get file import statistics:
```
GET http://localhost:8085/api/nexus/boozer-files/info
```
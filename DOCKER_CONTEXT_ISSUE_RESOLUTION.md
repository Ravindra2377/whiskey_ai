# DOCKER CONTEXT ISSUE RESOLUTION FOR WHISKEY AI

## 📋 Current Issue

You're experiencing a Docker context issue where Docker is trying to use the Linux engine instead of the Windows engine:

```
request returned 500 Internal Server Error for API route and version 
http://%2F%2F.%2Fpipe%2FdockerDesktopLinuxEngine/v1.51/...
```

This error indicates that Docker Desktop is configured to use Linux containers, but your system is trying to communicate with it using the Windows Docker engine.

## 🔍 Root Cause

Docker Desktop can run in two modes:
1. **Windows Containers Mode** - Uses Windows-based containers (what we need)
2. **Linux Containers Mode** - Uses Linux-based containers (currently active)

The issue occurs because:
1. Docker Desktop is set to Linux containers mode
2. But the Docker client is trying to communicate using Windows engine endpoints

## 🛠️ Solution Steps

### Step 1: Switch to Windows Containers

1. **Right-click the Docker icon** in your system tray (bottom right)
2. **Look for the menu option**:
   - If you see "Switch to Windows containers" → Click it
   - If you see "Switch to Linux containers" → You're already in the correct mode
3. **Wait for Docker Desktop to restart** (this may take a minute)

### Step 2: Verify Context Switch

Run these commands in PowerShell:

```powershell
# Check current context
docker context ls

# Switch to default context if needed
docker context use default

# Verify Docker is working
docker version
```

The output should show the "default" context with an asterisk (*) indicating it's active.

### Step 3: Restart Docker Services

1. **Close Docker Desktop** completely
2. **Wait 10 seconds**
3. **Start Docker Desktop** from the Start Menu
4. **Wait for full initialization** (whale icon stops animating)

### Step 4: Start Database Services

After Docker is running correctly:

```powershell
# Navigate to project directory
cd "D:\OneDrive\Desktop\Boozer_App_Main"

# Start database services
docker compose up -d
```

## 🧪 Verification Commands

After completing the steps, verify everything works:

```powershell
# Check Docker version
docker version

# Check Docker info
docker info

# Check running services
docker compose ps

# Test individual services
docker exec -it nexus-postgres pg_isready -U nexus_admin -d nexus_ai
docker exec -it nexus-redis redis-cli ping
docker exec -it nexus-influxdb influx ping
```

## 🔄 Alternative Approach: Use Scripts

We've created several scripts to help with this process:

1. **[reset-docker-context-and-start.ps1](file://d:\OneDrive\Desktop\Boozer_App_Main\reset-docker-context-and-start.ps1)** - Resets context and starts services
2. **[fix-docker-context.bat](file://d:\OneDrive\Desktop\Boozer_App_Main\fix-docker-context.bat)** - Fixes context and starts services (batch)
3. **[diagnose-docker-issues.ps1](file://d:\OneDrive\Desktop\Boozer_App_Main\diagnose-docker-issues.ps1)** - Diagnoses Docker issues
4. **[test-java-compilation.ps1](file://d:\OneDrive\Desktop\Boozer_App_Main\test-java-compilation.ps1)** - Tests Java compilation independently

## ⚠️ Common Pitfalls

1. **Switching Contexts**: Make sure you've actually switched to Windows containers
2. **Restarting Docker**: Always restart Docker Desktop after switching container types
3. **Path Issues**: Ensure you're in the correct directory when running commands
4. **Permissions**: Some commands may require administrator privileges

## 📞 If Problems Persist

If you continue to experience issues:

1. **Reset Docker Desktop**:
   - Open Docker Desktop
   - Go to Settings > Reset
   - Click "Reset to factory defaults"
   - Restart Docker Desktop

2. **Reinstall Docker Desktop**:
   - Uninstall Docker Desktop
   - Download the latest version from docker.com
   - Install with default settings

3. **Check Windows Features**:
   - Press Win + R, type "optionalfeatures"
   - Ensure "Hyper-V" and "Containers" are enabled

## 🎯 Quick Reference

**Correct Docker Context Output:**
```
NAME                DESCRIPTION                               DOCKER ENDPOINT                     ERROR
default *           Current DOCKER_HOST based configuration   npipe:////./pipe/docker_engine
desktop-linux       Docker Desktop                            npipe:////./pipe/dockerDesktopLinuxEngine
```

**Incorrect Docker Context Output:**
```
NAME              DESCRIPTION                               DOCKER ENDPOINT                     ERROR
default           Current DOCKER_HOST based configuration   npipe:////./pipe/docker_engine
desktop-linux *   Docker Desktop                            npipe:////./pipe/dockerDesktopLinuxEngine
```

Note the asterisk (*) indicating the active context.

## 🚀 Next Steps After Resolution

Once Docker is working correctly:

1. **Start database services**: `docker compose up -d`
2. **Compile Java application**: `.\mvnw.cmd clean compile`
3. **Run database tests**: `.\mvnw.cmd test -Dtest=DatabaseConnectionTest`
4. **Start the application**: `.\mvnw.cmd spring-boot:run`

This should resolve the Docker context issue and allow you to proceed with the WHISKEY AI database setup.
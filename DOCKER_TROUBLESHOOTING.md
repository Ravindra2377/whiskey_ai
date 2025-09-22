# DOCKER TROUBLESHOOTING GUIDE FOR WHISKEY AI

## 📋 Common Docker Issues and Solutions

### Issue 1: "Access is denied" when starting Docker service

**Problem:**
```
System error 5 has occurred.
Access is denied.
```

**Solution:**
1. Run Command Prompt or PowerShell as Administrator
2. Try starting Docker Desktop from the Start Menu instead of command line
3. Check if your user account is in the "docker-users" group:
   - Open Computer Management
   - Navigate to Local Users and Groups > Groups
   - Double-click "docker-users"
   - Add your user account if it's not listed

### Issue 2: "500 Internal Server Error" when pulling images

**Problem:**
```
request returned 500 Internal Server Error for API route
```

**Solution:**
1. Restart Docker Desktop completely
2. Check Windows features:
   - Press Win + R, type "optionalfeatures", press Enter
   - Ensure "Hyper-V" and "Containers" are enabled
3. Reset Docker Desktop to factory defaults:
   - Open Docker Desktop
   - Go to Settings > Reset
   - Click "Reset to factory defaults"
4. Restart your computer

### Issue 3: Docker service not found

**Problem:**
```
Cannot find any service with service name 'docker'
```

**Solution:**
1. Docker Desktop uses a different service name: "com.docker.service"
2. Use the correct service name in commands:
   ```cmd
   net start com.docker.service
   net stop com.docker.service
   ```

### Issue 4: Docker Desktop won't start

**Problem:**
- Docker Desktop icon appears but never finishes starting
- No Docker icon in system tray

**Solution:**
1. Check if virtualization is enabled in BIOS/UEFI
2. Ensure Windows Subsystem for Linux (WSL) is installed:
   ```powershell
   wsl --install
   ```
3. Update WSL:
   ```powershell
   wsl --update
   ```
4. Restart the WSL service:
   ```cmd
   net stop wslservice
   net start wslservice
   ```

## 🔧 Advanced Troubleshooting

### Reset Docker Desktop Completely

1. Close Docker Desktop
2. Open PowerShell as Administrator
3. Run these commands:
   ```powershell
   # Stop Docker service
   Stop-Service com.docker.service
   
   # Remove Docker data
   Remove-Item -Recurse -Force "$env:APPDATA\Docker"
   Remove-Item -Recurse -Force "$env:LOCALAPPDATA\Docker"
   
   # Restart Docker service
   Start-Service com.docker.service
   ```

4. Start Docker Desktop from the Start Menu

### Check Docker Logs

1. Open Docker Desktop
2. Click on the bug icon in the toolbar
3. Select "Troubleshoot"
4. Click "View logs"

### Reinstall Docker Desktop

1. Uninstall Docker Desktop:
   - Go to Control Panel > Programs > Programs and Features
   - Uninstall "Docker Desktop"
2. Remove residual files:
   - Delete folders:
     - `C:\Program Files\Docker`
     - `C:\ProgramData\Docker`
     - `%APPDATA%\Docker`
     - `%LOCALAPPDATA%\Docker`
3. Download latest Docker Desktop from [docker.com](https://docker.com)
4. Install as Administrator

## 🔄 Alternative Solutions

### Use Docker Toolbox (Legacy)

If Docker Desktop continues to have issues:

1. Download Docker Toolbox from Docker's archive
2. Install Docker Toolbox
3. Use Docker Quickstart Terminal instead of PowerShell/CMD

### Use Podman as Alternative

1. Install Podman Desktop
2. Install Podman CLI
3. Replace docker commands with podman:
   ```bash
   # Instead of: docker compose up -d
   # Use: podman-compose up -d
   ```

## 🛡️ Security Considerations

### Running Scripts as Administrator

When running the provided scripts:

1. **Always review scripts before running them**
2. **Only run scripts from trusted sources**
3. **Understand what each command does**

### Docker Security Best Practices

1. **Don't run containers as root**
2. **Keep Docker updated**
3. **Use official images when possible**
4. **Scan images for vulnerabilities**

## 📞 Getting Additional Help

### Docker Community Resources

1. [Docker Community Forums](https://forums.docker.com/)
2. [Docker Documentation](https://docs.docker.com/)
3. [Docker GitHub Issues](https://github.com/docker/for-win/issues)

### Windows-Specific Resources

1. [Microsoft Containers Documentation](https://docs.microsoft.com/en-us/virtualization/windowscontainers/)
2. [Windows Subsystem for Linux Documentation](https://docs.microsoft.com/en-us/windows/wsl/)

## 🎯 Quick Verification Commands

After resolving Docker issues, verify everything works:

```powershell
# Check Docker version
docker version

# Run hello world container
docker run hello-world

# Check Docker Compose version
docker compose version

# Start WHISKEY AI database services
docker compose up -d

# Check service status
docker compose ps

# Verify PostgreSQL
docker exec -it nexus-postgres psql -U nexus_admin -d nexus_ai -c "\dt"

# Verify Redis
docker exec -it nexus-redis redis-cli ping

# Verify InfluxDB
docker exec -it nexus-influxdb influx ping
```

## ⚠️ Important Notes

1. **Always backup important data** before making system changes
2. **Restart your computer** after making significant changes to Docker configuration
3. **Some changes require administrator privileges** - be prepared to run scripts as administrator
4. **Docker Desktop requires Windows 10/11 Pro or Enterprise** for all features

This guide should help you resolve most Docker issues encountered while setting up the WHISKEY AI database services.
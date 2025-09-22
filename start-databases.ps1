# Start databases
Write-Host "Starting databases..."
docker-compose up -d

# Wait 30 seconds for databases to initialize
Write-Host "Waiting 30 seconds for databases to initialize..."
Start-Sleep -Seconds 30

Write-Host "Databases started successfully!"
Write-Host "PostgreSQL is running on port 5432"
Write-Host "Redis is running on port 6379"
Write-Host "InfluxDB is running on port 8086"
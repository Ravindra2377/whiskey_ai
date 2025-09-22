@echo off
echo Starting databases...
docker-compose up -d

echo Waiting 30 seconds for databases to initialize...
timeout /t 30 /nobreak >nul

echo Databases started successfully!
echo PostgreSQL is running on port 5432
echo Redis is running on port 6379
echo InfluxDB is running on port 8086
pause
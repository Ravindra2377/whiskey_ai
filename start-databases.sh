#!/bin/bash

# Start databases
echo "Starting databases..."
docker-compose up -d

# Wait 30 seconds for databases to initialize
echo "Waiting 30 seconds for databases to initialize..."
sleep 30

echo "Databases started successfully!"
echo "PostgreSQL is running on port 5432"
echo "Redis is running on port 6379"
echo "InfluxDB is running on port 8086"
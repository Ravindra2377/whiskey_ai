@echo off
echo ===============================================
echo BOOZER APP - PostgreSQL Database Setup
echo ===============================================

echo.
echo This script will help you set up PostgreSQL for the BOOZER application.
echo.

echo Step 1: Download and Install PostgreSQL
echo ----------------------------------------
echo 1. Download PostgreSQL from: https://www.postgresql.org/download/windows/
echo 2. Install PostgreSQL with these settings:
echo    - Port: 5432 (default)
echo    - Username: postgres
echo    - Password: postgres (or remember your chosen password)
echo    - Database: postgres (default)
echo.

echo Step 2: Create BOOZER Database
echo -------------------------------
echo Run these commands in PostgreSQL (psql) or pgAdmin:
echo.
echo -- Connect to PostgreSQL
echo psql -U postgres -h localhost
echo.
echo -- Create database user
echo CREATE USER boozer_user WITH PASSWORD 'boozer_password';
echo.
echo -- Create development database  
echo CREATE DATABASE boozer_dev OWNER boozer_user;
echo.
echo -- Create production database
echo CREATE DATABASE boozer_db OWNER boozer_user;
echo.
echo -- Grant permissions
echo GRANT ALL PRIVILEGES ON DATABASE boozer_dev TO boozer_user;
echo GRANT ALL PRIVILEGES ON DATABASE boozer_db TO boozer_user;
echo.

echo Step 3: Environment Variables (Optional)
echo ----------------------------------------
echo You can set these environment variables:
echo set DATABASE_URL=jdbc:postgresql://localhost:5432/boozer_dev
echo set DB_USERNAME=boozer_user  
echo set DB_PASSWORD=boozer_password
echo.

echo Step 4: Alternative - Using Default Postgres User
echo -------------------------------------------------
echo If you prefer to use the default postgres user:
echo 1. Update application-development.properties:
echo    spring.datasource.username=postgres
echo    spring.datasource.password=your_postgres_password
echo    spring.datasource.url=jdbc:postgresql://localhost:5432/boozer_dev
echo.

echo Step 5: Verify Connection
echo -------------------------
echo After setup, start the Spring Boot application with:
echo .\mvnw.cmd spring-boot:run --spring.profiles.active=development
echo.

echo ===============================================
echo Setup Complete!
echo ===============================================
pause
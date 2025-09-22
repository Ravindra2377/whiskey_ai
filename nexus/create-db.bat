@echo off
echo Creating PostgreSQL database and user for WHISKEY...

REM Set the path to PostgreSQL bin directory
set PGPATH="C:\Program Files\PostgreSQL\17\bin"

REM Create database
%PGPATH%\psql.exe -U postgres -c "CREATE DATABASE boozer_db;" 2>nul
if %errorlevel% equ 0 (
    echo Database 'boozer_db' created successfully
) else (
    echo Database 'boozer_db' already exists or creation failed
)

REM Create user
%PGPATH%\psql.exe -U postgres -c "CREATE USER boozer_user WITH PASSWORD 'boozer_password';" 2>nul
if %errorlevel% equ 0 (
    echo User 'boozer_user' created successfully
) else (
    echo User 'boozer_user' already exists or creation failed
)

REM Grant privileges
%PGPATH%\psql.exe -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE boozer_db TO boozer_user;" >nul 2>&1
echo Granted privileges to boozer_user

echo PostgreSQL setup for WHISKEY completed!
pause
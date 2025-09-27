#!/usr/bin/env python3
"""
NEXUS Database Initialization Script
This script helps initialize the PostgreSQL database for NEXUS AI.
"""

import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT
import sys

def create_database_and_user():
    """Create the NEXUS database and user"""
    try:
        # Connect to PostgreSQL as postgres user
        conn = psycopg2.connect(
            host="localhost",
            database="postgres",  # Connect to default database
            user="postgres",
            password="pASSWORD@11"   # Updated password
        )
        conn.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)
        cursor = conn.cursor()
        
        # Create user
        try:
            cursor.execute("CREATE USER boozer_user WITH PASSWORD 'pASSWORD@11';")
            print("Created user 'boozer_user'")
        except psycopg2.Error as e:
            if "already exists" in str(e):
                print("User 'boozer_user' already exists")
            else:
                print(f"Error creating user: {e}")
        
        # Create databases
        try:
            cursor.execute("CREATE DATABASE boozer_dev OWNER boozer_user;")
            print("Created database 'boozer_dev'")
        except psycopg2.Error as e:
            if "already exists" in str(e):
                print("Database 'boozer_dev' already exists")
            else:
                print(f"Error creating database 'boozer_dev': {e}")
        
        try:
            cursor.execute("CREATE DATABASE boozer_db OWNER boozer_user;")
            print("Created database 'boozer_db'")
        except psycopg2.Error as e:
            if "already exists" in str(e):
                print("Database 'boozer_db' already exists")
            else:
                print(f"Error creating database 'boozer_db': {e}")
        
        # Grant privileges
        try:
            cursor.execute("GRANT ALL PRIVILEGES ON DATABASE boozer_dev TO boozer_user;")
            cursor.execute("GRANT ALL PRIVILEGES ON DATABASE boozer_db TO boozer_user;")
            print("Granted privileges to 'boozer_user'")
        except psycopg2.Error as e:
            print(f"Error granting privileges: {e}")
        
        cursor.close()
        conn.close()
        return True
        
    except Exception as e:
        print(f"Error connecting to PostgreSQL: {e}")
        print("Please ensure PostgreSQL is installed and running.")
        return False

def test_nexus_connection():
    """Test connection to NEXUS database"""
    try:
        conn = psycopg2.connect(
            host="localhost",
            database="boozer_db",
            user="boozer_user",
            password="pASSWORD@11",
            port="5432"
        )
        
        cursor = conn.cursor()
        
        # Test query
        cursor.execute("SELECT version();")
        version = cursor.fetchone()
        print(f"Connected to PostgreSQL: {version[0]}")
        
        # Check if nexus_tasks table exists
        cursor.execute("""
            SELECT EXISTS (
                SELECT FROM information_schema.tables 
                WHERE table_name = 'nexus_tasks'
            );
        """)
        table_exists = cursor.fetchone()[0]
        
        if table_exists:
            print("NEXUS tasks table already exists")
            cursor.execute("SELECT COUNT(*) FROM nexus_tasks;")
            count = cursor.fetchone()[0]
            print(f"Current task count: {count}")
        else:
            print("NEXUS tasks table does not exist yet")
            print("It will be created automatically when NEXUS starts")
        
        cursor.close()
        conn.close()
        return True
        
    except Exception as e:
        print(f"Error connecting to WHISKEY database: {e}")
        return False

def main():
    print("===============================================")
    print("NEXUS AI - PostgreSQL Database Initialization")
    print("===============================================")
    print()
    
    if len(sys.argv) > 1 and sys.argv[1] == "--create-db":
        print("Creating database and user...")
        if create_database_and_user():
            print("Database setup completed successfully!")
        else:
            print("Database setup failed!")
            return 1
    else:
        print("Testing database connection...")
        print("To create database and user, run with --create-db flag")
        print()
    
    print("Testing NEXUS database connection...")
    if test_nexus_connection():
        print()
        print("Database connection test completed successfully!")
        print()
        print("Next steps:")
        print("1. Start NEXUS AI: cd nexus && ./mvnw spring-boot:run")
        print("2. Access the API at: http://localhost:8085/api/nexus")
        print("3. Submit tasks and monitor database activity")
        return 0
    else:
        print()
        print("Database connection test failed!")
        print("Please check your PostgreSQL installation and configuration.")
        return 1

if __name__ == "__main__":
    sys.exit(main())
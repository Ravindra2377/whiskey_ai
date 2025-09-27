import psycopg2
from psycopg2 import sql
import sys

try:
    # Connect to PostgreSQL as superuser
    print("Connecting to PostgreSQL as superuser...")
    connection = psycopg2.connect(
        host="localhost",
        database="postgres",  # Connect to default database first
    user="postgres",
    password="pASSWORD@11",  # Updated superuser password
        port="5432"
    )
    
    connection.autocommit = True
    cursor = connection.cursor()
    
    # Create database
    print("Creating database 'boozer_db'...")
    cursor.execute("CREATE DATABASE boozer_db;")
    print("✓ Database 'boozer_db' created successfully")
    
except Exception as e:
    if "already exists" in str(e):
        print("ℹ Database 'boozer_db' already exists")
    else:
        print(f"Error creating database: {e}")
    
try:
    # Create user
    print("Creating user 'boozer_user'...")
    cursor.execute("CREATE USER boozer_user WITH PASSWORD 'pASSWORD@11';")
    print("✓ User 'boozer_user' created successfully")
    
except Exception as e:
    if "already exists" in str(e):
        print("ℹ User 'boozer_user' already exists")
    else:
        print(f"Error creating user: {e}")

try:
    # Grant privileges
    print("Granting privileges to 'boozer_user'...")
    cursor.execute("GRANT ALL PRIVILEGES ON DATABASE boozer_db TO boozer_user;")
    print("✓ Granted privileges to boozer_user")
    
except Exception as e:
    print(f"Error granting privileges: {e}")

finally:
    # Close the connection
    if 'cursor' in locals():
        cursor.close()
    if 'connection' in locals():
        connection.close()
    print("PostgreSQL setup for NEXUS completed!")
import psycopg2
from psycopg2 import sql

try:
    # Try to connect to PostgreSQL
    connection = psycopg2.connect(
        host="localhost",
        database="boozer_db",
        user="boozer_user",
    password="pASSWORD@11",
        port="5432"
    )
    
    cursor = connection.cursor()
    
    # Print PostgreSQL version
    cursor.execute("SELECT version();")
    record = cursor.fetchone()
    print("You are connected to - ", record, "\n")
    
    # Check if whiskey_tasks table exists
    cursor.execute("""
        SELECT EXISTS (
            SELECT FROM information_schema.tables 
            WHERE table_name = 'whiskey_tasks'
        );
    """)
    table_exists = cursor.fetchone()[0]
    print("whiskey_tasks table exists:", table_exists)
    
    if table_exists:
        # Count records in the table
        cursor.execute("SELECT COUNT(*) FROM whiskey_tasks;")
        count = cursor.fetchone()[0]
        print("Number of records in whiskey_tasks:", count)
    
except Exception as error:
    print("Error while connecting to PostgreSQL:", error)
    
finally:
    # Close the connection
    if 'connection' in locals():
        cursor.close()
        connection.close()
        print("PostgreSQL connection is closed")
#!/usr/bin/env python3
"""
WHISKEY Database Integration Test
This script tests the database integration of WHISKEY AI.
"""

import argparse
import json
import os
import time
from dataclasses import dataclass
from typing import Optional, Tuple

import psycopg2
import urllib.request
import urllib.error
from contextlib import contextmanager


DEFAULT_DB_HOST = os.getenv("WHISKEY_DB_HOST", "localhost")
DEFAULT_DB_PORT = int(os.getenv("WHISKEY_DB_PORT", "5432"))
DEFAULT_DB_NAME = os.getenv("WHISKEY_DB_NAME", "boozer_db")
DEFAULT_DB_USER = os.getenv("WHISKEY_DB_USER", "boozer_user")
DEFAULT_DB_PASSWORD = os.getenv("WHISKEY_DB_PASSWORD", "pASSWORD@11")
DEFAULT_API_BASE = os.getenv("WHISKEY_API_BASE", "http://localhost:8085/api/whiskey")


def env_flag(name: str, default: bool = False) -> bool:
    value = os.getenv(name)
    if value is None:
        return default
    return value.strip().lower() in {"1", "true", "yes", "on", "y"}


@dataclass
class TestConfig:
    db_host: str = DEFAULT_DB_HOST
    db_port: int = DEFAULT_DB_PORT
    db_name: str = DEFAULT_DB_NAME
    db_user: str = DEFAULT_DB_USER
    db_password: str = DEFAULT_DB_PASSWORD
    api_base_url: str = DEFAULT_API_BASE
    wait_seconds: float = float(os.getenv("WHISKEY_TASK_WAIT", "2"))
    retries: int = int(os.getenv("WHISKEY_API_RETRIES", "3"))
    retry_delay: float = float(os.getenv("WHISKEY_API_RETRY_DELAY", "1.5"))
    auto_confirm: bool = env_flag("WHISKEY_AUTO_CONFIRM")
    skip_task_submission: bool = env_flag("WHISKEY_SKIP_TASK")


def parse_args() -> TestConfig:
    parser = argparse.ArgumentParser(description="End-to-end integration check for WHISKEY AI and PostgreSQL")
    parser.add_argument("--db-host", default=DEFAULT_DB_HOST, help="Database host (default from WHISKEY_DB_HOST or localhost)")
    parser.add_argument("--db-port", type=int, default=DEFAULT_DB_PORT, help="Database port (default 5432)")
    parser.add_argument("--db-name", default=DEFAULT_DB_NAME, help="Database name")
    parser.add_argument("--db-user", default=DEFAULT_DB_USER, help="Database user")
    parser.add_argument("--db-password", default=DEFAULT_DB_PASSWORD, help="Database password")
    parser.add_argument("--api-base", default=DEFAULT_API_BASE, help="Base URL for the WHISKEY API")
    parser.add_argument("--wait-seconds", type=float, default=float(os.getenv("WHISKEY_TASK_WAIT", "2")), help="Wait duration before verifying the task")
    parser.add_argument("--retries", type=int, default=int(os.getenv("WHISKEY_API_RETRIES", "3")), help="Number of retries for API calls")
    parser.add_argument("--retry-delay", type=float, default=float(os.getenv("WHISKEY_API_RETRY_DELAY", "1.5")), help="Delay between API retries in seconds")
    parser.add_argument("--auto-confirm", action="store_true", help="Skip manual prompt to start WHISKEY")
    parser.add_argument("--skip-task", action="store_true", help="Run connectivity tests only (skip task submission)")

    args = parser.parse_args()
    env_auto_confirm = env_flag("WHISKEY_AUTO_CONFIRM")
    env_skip_task = env_flag("WHISKEY_SKIP_TASK")
    return TestConfig(
        db_host=args.db_host,
        db_port=args.db_port,
        db_name=args.db_name,
        db_user=args.db_user,
        db_password=args.db_password,
        api_base_url=args.api_base.rstrip("/"),
        wait_seconds=max(args.wait_seconds, 0.0),
        retries=max(args.retries, 0),
        retry_delay=max(args.retry_delay, 0.1),
        auto_confirm=args.auto_confirm or env_auto_confirm,
        skip_task_submission=args.skip_task or env_skip_task,
    )


def http_json_request(
    method: str,
    url: str,
    payload: Optional[dict] = None,
    timeout: float = 5,
    retries: int = 0,
    retry_delay: float = 1.0,
) -> Tuple[int, Optional[dict], str]:
    """Perform an HTTP request expecting JSON responses with simple retry support."""

    headers = {"Accept": "application/json"}
    data_bytes = None
    if payload is not None:
        data_bytes = json.dumps(payload).encode("utf-8")
        headers["Content-Type"] = "application/json"

    last_error: Optional[urllib.error.URLError] = None
    for attempt in range(max(retries, 0) + 1):
        request = urllib.request.Request(url, data=data_bytes, headers=headers, method=method)
        try:
            with urllib.request.urlopen(request, timeout=timeout) as response:
                text = response.read().decode("utf-8", errors="replace")
                parsed = None
                if text:
                    try:
                        parsed = json.loads(text)
                    except json.JSONDecodeError:
                        parsed = None
                return response.getcode(), parsed, text
        except urllib.error.HTTPError as exc:
            text = exc.read().decode("utf-8", errors="replace")
            parsed = None
            if text:
                try:
                    parsed = json.loads(text)
                except json.JSONDecodeError:
                    parsed = None
            return exc.code, parsed, text
        except urllib.error.URLError as exc:
            last_error = exc
            if attempt < max(retries, 0):
                time.sleep(max(retry_delay, 0.1))
                continue
            raise

    # Should never hit here, but keeps type-checkers happy.
    if last_error:
        raise last_error
    raise RuntimeError("HTTP request failed without a captured error")


@contextmanager
def db_connection(config: TestConfig):
    conn = psycopg2.connect(
        host=config.db_host,
        database=config.db_name,
        user=config.db_user,
        password=config.db_password,
        port=config.db_port,
    )
    try:
        yield conn
    finally:
        conn.close()

def prompt_whiskey_start(auto_confirm: bool) -> None:
    """Inform the operator to launch WHISKEY unless auto-confirm is enabled."""

    if auto_confirm:
        print("ℹ Auto-confirm enabled; skipping manual WHISKEY start prompt.")
        return

    print("Please start WHISKEY manually (e.g. `cd whiskey && ./mvnw spring-boot:run`).")
    input("Press Enter once the service is running and ready...")

def test_database_connection(config: TestConfig) -> bool:
    """Test connection to PostgreSQL database"""
    try:
        with db_connection(config) as conn:
            with conn.cursor() as cursor:
                cursor.execute("SELECT version();")
                version = cursor.fetchone()
                if version:
                    print(f"✓ Connected to PostgreSQL: {version[0]}")

                cursor.execute(
                    """
                    SELECT EXISTS (
                        SELECT FROM information_schema.tables 
                        WHERE table_name = 'whiskey_tasks'
                    );
                    """
                )
                table_exists = cursor.fetchone()[0]

                if table_exists:
                    print("✓ WHISKEY tasks table exists")
                else:
                    print("⚠ WHISKEY tasks table does not exist yet")
        return True

    except Exception as e:
        print(f"✗ Error connecting to database: {e}")
        return False

def test_whiskey_api(config: TestConfig) -> bool:
    """Test WHISKEY API endpoints"""
    base_url = config.api_base_url
    
    try:
        status, data, raw = http_json_request(
            "GET",
            f"{base_url}/health",
            timeout=5,
            retries=config.retries,
            retry_delay=config.retry_delay,
        )
        if status == 200:
            print("✓ WHISKEY health endpoint is accessible")
            if isinstance(data, dict):
                print(f"  Health status: {data.get('status', 'Unknown')}")
        else:
            print(f"✗ WHISKEY health endpoint returned {status}")
            if raw:
                print(f"  Response: {raw}")
            return False

        status, _, _ = http_json_request(
            "GET",
            f"{base_url}/info",
            timeout=5,
            retries=config.retries,
            retry_delay=config.retry_delay,
        )
        if status == 200:
            print("✓ WHISKEY info endpoint is accessible")
        else:
            print(f"✗ WHISKEY info endpoint returned {status}")
            return False

        return True
        
    except urllib.error.URLError:
        print("✗ Cannot connect to WHISKEY API. Is it running?")
        return False
    except Exception as e:
        print(f"✗ Error testing WHISKEY API: {e}")
        return False

def submit_test_task(config: TestConfig) -> Optional[str]:
    """Submit a test task to WHISKEY"""
    base_url = config.api_base_url
    
    # Test task data
    task_data = {
        "type": "CODE_ANALYSIS",
        "description": "Test task for database integration",
        "createdBy": "DATABASE_TEST",
        "parameters": {
            "test": True,
            "purpose": "database_integration_test"
        }
    }
    
    try:
        status, data, raw = http_json_request(
            "POST",
            f"{base_url}/task",
            payload=task_data,
            timeout=10,
            retries=config.retries,
            retry_delay=config.retry_delay,
        )
        if status == 202 and isinstance(data, dict):
            print("✓ Test task submitted successfully")
            task_id = data.get('taskId')
            print(f"  Task ID: {task_id}")
            return task_id
        print(f"✗ Failed to submit test task: {status}")
        if raw:
            print(f"  Response: {raw}")
        return None

    except Exception as e:
        print(f"✗ Error submitting test task: {e}")
        return None

def check_task_in_database(config: TestConfig, task_id: str) -> bool:
    """Check if the task exists in the database"""
    try:
        with db_connection(config) as conn:
            with conn.cursor() as cursor:
                cursor.execute(
                    "SELECT task_id, task_type, description, status FROM whiskey_tasks WHERE task_id = %s",
                    (task_id,),
                )
                task_record = cursor.fetchone()

                if task_record:
                    print("✓ Task found in database:")
                    print(f"  Task ID: {task_record[0]}")
                    print(f"  Task Type: {task_record[1]}")
                    print(f"  Description: {task_record[2]}")
                    print(f"  Status: {task_record[3]}")
                    return True
                else:
                    print("⚠ Task not found in database")
                    return False

    except Exception as e:
        print(f"✗ Error checking task in database: {e}")
        return False

def main() -> int:
    config = parse_args()

    print("===============================================")
    print("WHISKEY AI Database Integration Test")
    print("===============================================")
    print(f"Database: {config.db_user}@{config.db_host}:{config.db_port}/{config.db_name}")
    print(f"API: {config.api_base_url}")
    print()

    prompt_whiskey_start(config.auto_confirm)

    if not test_database_connection(config):
        print("Database connection test failed. Exiting.")
        return 1

    print()

    if not test_whiskey_api(config):
        print("WHISKEY API test failed. Exiting.")
        return 1

    if config.skip_task_submission:
        print()
        print("Skipping task submission per configuration. Connectivity checks passed.")
        return 0

    print()
    print("Submitting test task...")
    task_id = submit_test_task(config)
    if not task_id:
        print("Failed to submit test task. Exiting.")
        return 1

    if config.wait_seconds > 0:
        print()
        print(f"Waiting {config.wait_seconds:.1f}s for task processing...")
        time.sleep(config.wait_seconds)

    print()
    print("Checking task in database...")
    if check_task_in_database(config, task_id):
        print()
        print("🎉 Database integration test completed successfully!")
        print("   WHISKEY is properly connected to PostgreSQL")
        return 0

    print()
    print("❌ Database integration test failed!")
    print("   Task was not found in the database")
    return 1

if __name__ == "__main__":
    exit(main())
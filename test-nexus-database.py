#!/usr/bin/env python3
"""
NEXUS Database Integration Test
This script validates the PostgreSQL integration for the NEXUS AI platform.
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


def _get_env_value(*names: str, default=None, caster=None):
    for name in names:
        if not name:
            continue
        value = os.getenv(name)
        if value is None:
            continue
        if caster:
            try:
                return caster(value)
            except (TypeError, ValueError):
                continue
        return value
    return default


DEFAULT_DB_HOST = _get_env_value("NEXUS_DB_HOST", "WHISKEY_DB_HOST", default="localhost")
DEFAULT_DB_PORT = _get_env_value("NEXUS_DB_PORT", "WHISKEY_DB_PORT", default=5432, caster=int)
DEFAULT_DB_NAME = _get_env_value("NEXUS_DB_NAME", "WHISKEY_DB_NAME", default="boozer_db")
DEFAULT_DB_USER = _get_env_value("NEXUS_DB_USER", "WHISKEY_DB_USER", default="boozer_user")
DEFAULT_DB_PASSWORD = _get_env_value("NEXUS_DB_PASSWORD", "WHISKEY_DB_PASSWORD", default="pASSWORD@11")
DEFAULT_API_BASE = _get_env_value("NEXUS_API_BASE", "WHISKEY_API_BASE", default="http://localhost:8085/api/nexus")


def env_flag(*names: str, default: bool = False) -> bool:
    for name in names:
        if not name:
            continue
        value = os.getenv(name)
        if value is None:
            continue
        return value.strip().lower() in {"1", "true", "yes", "on", "y"}
    return default


@dataclass
class TestConfig:
    db_host: str = DEFAULT_DB_HOST
    db_port: int = DEFAULT_DB_PORT
    db_name: str = DEFAULT_DB_NAME
    db_user: str = DEFAULT_DB_USER
    db_password: str = DEFAULT_DB_PASSWORD
    api_base_url: str = DEFAULT_API_BASE
    wait_seconds: float = _get_env_value(
        "NEXUS_TASK_WAIT",
        "WHISKEY_TASK_WAIT",
        default=2.0,
        caster=float,
    )
    retries: int = _get_env_value(
        "NEXUS_API_RETRIES",
        "WHISKEY_API_RETRIES",
        default=3,
        caster=int,
    )
    retry_delay: float = _get_env_value(
        "NEXUS_API_RETRY_DELAY",
        "WHISKEY_API_RETRY_DELAY",
        default=1.5,
        caster=float,
    )
    status_retries: int = _get_env_value(
        "NEXUS_TASK_STATUS_RETRIES",
        "WHISKEY_TASK_STATUS_RETRIES",
        default=5,
        caster=int,
    )
    status_retry_delay: float = _get_env_value(
        "NEXUS_TASK_STATUS_RETRY_DELAY",
        "WHISKEY_TASK_STATUS_RETRY_DELAY",
        default=2.0,
        caster=float,
    )
    auto_confirm: bool = env_flag("NEXUS_AUTO_CONFIRM", "WHISKEY_AUTO_CONFIRM")
    skip_task_submission: bool = env_flag("NEXUS_SKIP_TASK", "WHISKEY_SKIP_TASK")
    cleanup_task: bool = True


def parse_args() -> TestConfig:
    parser = argparse.ArgumentParser(description="End-to-end integration check for NEXUS AI and PostgreSQL")
    parser.add_argument(
        "--db-host",
        default=DEFAULT_DB_HOST,
        help="Database host (defaults to NEXUS_DB_HOST, WHISKEY_DB_HOST, or localhost)",
    )
    parser.add_argument("--db-port", type=int, default=DEFAULT_DB_PORT, help="Database port (default 5432)")
    parser.add_argument("--db-name", default=DEFAULT_DB_NAME, help="Database name")
    parser.add_argument("--db-user", default=DEFAULT_DB_USER, help="Database user")
    parser.add_argument("--db-password", default=DEFAULT_DB_PASSWORD, help="Database password")
    parser.add_argument("--api-base", default=DEFAULT_API_BASE, help="Base URL for the NEXUS API")
    parser.add_argument(
        "--wait-seconds",
        type=float,
        default=_get_env_value("NEXUS_TASK_WAIT", "WHISKEY_TASK_WAIT", default=2.0, caster=float),
        help="Wait duration before verifying the task",
    )
    parser.add_argument(
        "--retries",
        type=int,
        default=_get_env_value("NEXUS_API_RETRIES", "WHISKEY_API_RETRIES", default=3, caster=int),
        help="Number of retries for API calls",
    )
    parser.add_argument(
        "--retry-delay",
        type=float,
        default=_get_env_value("NEXUS_API_RETRY_DELAY", "WHISKEY_API_RETRY_DELAY", default=1.5, caster=float),
        help="Delay between API retries in seconds",
    )
    parser.add_argument(
        "--status-retries",
        type=int,
        default=_get_env_value(
            "NEXUS_TASK_STATUS_RETRIES",
            "WHISKEY_TASK_STATUS_RETRIES",
            default=5,
            caster=int,
        ),
        help="Number of polling attempts for task status",
    )
    parser.add_argument(
        "--status-retry-delay",
        type=float,
        default=_get_env_value(
            "NEXUS_TASK_STATUS_RETRY_DELAY",
            "WHISKEY_TASK_STATUS_RETRY_DELAY",
            default=2.0,
            caster=float,
        ),
        help="Delay between task status polling attempts",
    )
    parser.add_argument("--auto-confirm", action="store_true", help="Skip manual prompt to start NEXUS")
    parser.add_argument("--skip-task", action="store_true", help="Run connectivity tests only (skip task submission)")
    parser.add_argument("--no-cleanup", action="store_true", help="Retain the submitted test task in the database")

    args = parser.parse_args()
    env_auto_confirm = env_flag("NEXUS_AUTO_CONFIRM", "WHISKEY_AUTO_CONFIRM")
    env_skip_task = env_flag("NEXUS_SKIP_TASK", "WHISKEY_SKIP_TASK")
    env_skip_cleanup = env_flag("NEXUS_SKIP_CLEANUP", "WHISKEY_SKIP_CLEANUP")
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
        status_retries=max(args.status_retries, 0),
        status_retry_delay=max(args.status_retry_delay, 0.1),
        auto_confirm=args.auto_confirm or env_auto_confirm,
        skip_task_submission=args.skip_task or env_skip_task,
        cleanup_task=not (args.no_cleanup or env_skip_cleanup),
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


def prompt_nexus_start(auto_confirm: bool) -> None:
    """Inform the operator to launch NEXUS unless auto-confirm is enabled."""

    if auto_confirm:
        print("ℹ Auto-confirm enabled; skipping manual NEXUS start prompt.")
        return

    print("Please start NEXUS manually (e.g. `cd nexus && ./mvnw spring-boot:run`).")
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
                        WHERE table_name = 'nexus_tasks'
                    );
                    """
                )
                table_exists = cursor.fetchone()[0]

                if table_exists:
                    print("✓ NEXUS tasks table exists")
                else:
                    print("⚠ NEXUS tasks table does not exist yet")
        return True

    except Exception as e:
        print(f"✗ Error connecting to database: {e}")
        return False

def test_nexus_api(config: TestConfig) -> bool:
    """Test NEXUS API endpoints"""
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
            print("✓ NEXUS health endpoint is accessible")
            if isinstance(data, dict):
                print(f"  Health status: {data.get('status', 'Unknown')}")
        else:
            print(f"✗ NEXUS health endpoint returned {status}")
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
            print("✓ NEXUS info endpoint is accessible")
        else:
            print(f"✗ NEXUS info endpoint returned {status}")
            return False

        return True
        
    except urllib.error.URLError:
        print("✗ Cannot connect to NEXUS API. Is it running?")
        return False
    except Exception as e:
        print(f"✗ Error testing NEXUS API: {e}")
        return False

def submit_test_task(config: TestConfig) -> Optional[str]:
    """Submit a test task to NEXUS"""
    base_url = config.api_base_url
    
    # Test task data
    task_data = {
        "type": "CODE_MODIFICATION",
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
            task_id = data.get('taskId') or data.get('task_id')
            print(f"  Task ID: {task_id}")
            return task_id
        print(f"✗ Failed to submit test task: {status}")
        if raw:
            print(f"  Response: {raw}")
        return None

    except Exception as e:
        print(f"✗ Error submitting test task: {e}")
        return None


def poll_task_status(config: TestConfig, task_id: str) -> Tuple[bool, Optional[str]]:
    """Poll the NEXUS task status endpoint until completion or retry limit reached."""

    endpoint = f"{config.api_base_url}/task/{task_id}"
    last_status: Optional[str] = None

    for attempt in range(max(config.status_retries, 0) + 1):
        try:
            status_code, data, raw = http_json_request(
                "GET",
                endpoint,
                timeout=5,
                retries=0,
                retry_delay=config.status_retry_delay,
            )
        except urllib.error.URLError as exc:
            last_status = f"connection-error: {exc.reason}"
            print(f"✗ Attempt {attempt + 1}: unable to reach task status endpoint ({exc.reason})")
        except Exception as exc:  # pragma: no cover - defensive
            last_status = f"error: {exc}"
            print(f"✗ Attempt {attempt + 1}: error checking task status: {exc}")
        else:
            if status_code == 200 and isinstance(data, dict):
                current_status = data.get("status") or data.get("taskStatus")
                last_status = str(current_status) if current_status is not None else None
                print(f"• Task status (attempt {attempt + 1}/{config.status_retries + 1}): {last_status}")

                if current_status and current_status.upper() in {"COMPLETED", "FAILED", "CANCELLED"}:
                    return True, last_status
            elif status_code == 404:
                last_status = "not-found"
                print(f"⚠ Task {task_id} not yet visible via API (attempt {attempt + 1})")
            else:
                last_status = f"unexpected-status:{status_code}"
                details = raw if raw else "<no body>"
                print(f"⚠ Unexpected response ({status_code}): {details}")

        if attempt < max(config.status_retries, 0):
            time.sleep(max(config.status_retry_delay, 0.1))

    print("⚠ Task did not reach a terminal state within polling window")
    return False, last_status


def check_task_in_database(config: TestConfig, task_id: str) -> bool:
    """Check if the task exists in the database"""
    try:
        with db_connection(config) as conn:
            with conn.cursor() as cursor:
                cursor.execute(
                    """
                    SELECT task_id,
                           task_type,
                           description,
                           status,
                           COALESCE(progress, 0) AS progress,
                           created_by,
                           created_at,
                           updated_at
                    FROM nexus_tasks
                    WHERE task_id = %s
                    """,
                    (task_id,),
                )
                task_record = cursor.fetchone()

                if task_record:
                    (
                        db_task_id,
                        db_task_type,
                        db_description,
                        db_status,
                        db_progress,
                        db_created_by,
                        db_created_at,
                        db_updated_at,
                    ) = task_record

                    print("✓ Task found in database:")
                    print(f"  Task ID: {db_task_id}")
                    print(f"  Task Type: {db_task_type}")
                    print(f"  Description: {db_description}")
                    print(f"  Status: {db_status}")
                    print(f"  Progress: {db_progress}%")
                    if db_created_by:
                        print(f"  Created By: {db_created_by}")
                    if db_created_at:
                        print(f"  Created At: {db_created_at}")
                    if db_updated_at:
                        print(f"  Updated At: {db_updated_at}")
                    return True
                else:
                    print("⚠ Task not found in database")
                    return False

    except Exception as e:
        print(f"✗ Error checking task in database: {e}")
        return False

def cleanup_test_task(config: TestConfig, task_id: str) -> bool:
    """Remove the submitted test task from the database to keep state clean."""

    try:
        with db_connection(config) as conn:
            with conn.cursor() as cursor:
                cursor.execute("DELETE FROM nexus_tasks WHERE task_id = %s", (task_id,))
                deleted = cursor.rowcount
            conn.commit()

        if deleted:
            print(f"✓ Cleaned up test task {task_id} from database")
            return True

        print(f"⚠ Cleanup requested but task {task_id} was not found during deletion")
        return False

    except Exception as e:
        print(f"✗ Error cleaning up test task: {e}")
        return False


def main() -> int:
    config = parse_args()

    print("===============================================")
    print("NEXUS AI Database Integration Test")
    print("===============================================")
    print(f"Database: {config.db_user}@{config.db_host}:{config.db_port}/{config.db_name}")
    print(f"API: {config.api_base_url}")
    print()

    prompt_nexus_start(config.auto_confirm)

    if not test_database_connection(config):
        print("Database connection test failed. Exiting.")
        return 1

    print()

    if not test_nexus_api(config):
        print("NEXUS API test failed. Exiting.")
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
    print("Polling task status via API...")
    api_terminal, observed_status = poll_task_status(config, task_id)
    if api_terminal:
        print(f"✓ API reported terminal status: {observed_status}")
    else:
        print("⚠ Task did not report a terminal status via API within the allotted polling attempts")

    print()
    print("Checking task in database...")
    if check_task_in_database(config, task_id):
        print()
        print("🎉 Database integration test completed successfully!")
        print("   NEXUS is properly connected to PostgreSQL")

        if config.cleanup_task:
            print()
            cleanup_test_task(config, task_id)

        return 0

    print()
    print("❌ Database integration test failed!")
    print("   Task was not found in the database")

    if config.cleanup_task:
        print()
        cleanup_test_task(config, task_id)

    return 1

if __name__ == "__main__":
    exit(main())
#!/usr/bin/env python3
"""
NEXUS Database Integration Test

This script validates the PostgreSQL integration for the NEXUS AI platform by:

• Verifying database connectivity and required tables.
• Exercising key REST endpoints (health/info) on the NEXUS API.
• Optionally submitting a configurable test task and ensuring it reaches an
    expected terminal status while persisting to PostgreSQL.

Each phase is tracked with timing metadata, a rich console summary, and an
optional JSON report for downstream automation.
"""

import argparse
import json
import os
import time
from contextlib import contextmanager
from dataclasses import dataclass, field
from typing import Any, Dict, List, Optional, Tuple

import psycopg2
import urllib.error
import urllib.request


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

DEFAULT_JSON_OUTPUT_PATH = _get_env_value("NEXUS_DB_TEST_JSON", "WHISKEY_DB_TEST_JSON")
DEFAULT_TASK_TYPE = _get_env_value("NEXUS_TEST_TASK_TYPE", "WHISKEY_TEST_TASK_TYPE", default="CODE_MODIFICATION")
DEFAULT_TASK_DESCRIPTION = _get_env_value(
    "NEXUS_TEST_TASK_DESCRIPTION",
    "WHISKEY_TEST_TASK_DESCRIPTION",
    default="Test task for database integration",
)
DEFAULT_TASK_CREATED_BY = _get_env_value(
    "NEXUS_TEST_TASK_CREATED_BY",
    "WHISKEY_TEST_TASK_CREATED_BY",
    default="DATABASE_TEST",
)
DEFAULT_EXPECTED_STATUS = _get_env_value(
    "NEXUS_EXPECTED_TASK_STATUS",
    "WHISKEY_EXPECTED_TASK_STATUS",
    default="COMPLETED",
)


def _default_task_parameters() -> Dict[str, Any]:
    value = _get_env_value(
        "NEXUS_TEST_TASK_PARAMETERS",
        "WHISKEY_TEST_TASK_PARAMETERS",
        caster=lambda raw: json.loads(raw),
    )
    if isinstance(value, dict):
        return value
    return {"test": True, "purpose": "database_integration_test"}


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
    json_output_path: Optional[str] = DEFAULT_JSON_OUTPUT_PATH
    task_type: str = DEFAULT_TASK_TYPE
    task_description: str = DEFAULT_TASK_DESCRIPTION
    task_created_by: str = DEFAULT_TASK_CREATED_BY
    task_parameters: Dict[str, Any] = field(default_factory=_default_task_parameters)
    expected_status: Optional[str] = DEFAULT_EXPECTED_STATUS


@dataclass
class StepResult:
    name: str
    success: bool
    duration: float
    info: Optional[str] = None


def _coerce_success(outcome) -> bool:
    if isinstance(outcome, tuple) and outcome:
        first = outcome[0]
        if isinstance(first, bool):
            return first
    if isinstance(outcome, dict):
        success_value = outcome.get("success")
        if isinstance(success_value, bool):
            return success_value
    return bool(outcome)


def print_summary(results: List[StepResult]) -> None:
    if not results:
        return
    print()
    print("===============================================")
    print("Summary")
    print("===============================================")
    for result in results:
        status_icon = "✓" if result.success else "✗"
        info_suffix = f" - {result.info}" if result.info else ""
        print(f"{status_icon} {result.name} ({result.duration:.2f}s){info_suffix}")


def write_json_summary(path: str, results: List[StepResult], overall_success: bool) -> None:
    payload = {
        "overallSuccess": overall_success,
        "generatedAt": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
        "totalDuration": sum(step.duration for step in results),
        "steps": [
            {
                "name": step.name,
                "success": step.success,
                "duration": step.duration,
                "info": step.info,
            }
            for step in results
        ],
    }

    directory = os.path.dirname(path)
    if directory:
        os.makedirs(directory, exist_ok=True)

    with open(path, "w", encoding="utf-8") as handle:
        json.dump(payload, handle, indent=2)
        handle.write("\n")

    print(f"Summary written to {path}")


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
    parser.add_argument(
        "--json-output",
        help="Write a JSON summary of the test run to the specified file path",
        default=DEFAULT_JSON_OUTPUT_PATH,
    )
    parser.add_argument("--task-type", default=DEFAULT_TASK_TYPE, help="Task type to submit for the integration test")
    parser.add_argument(
        "--task-description",
        default=DEFAULT_TASK_DESCRIPTION,
        help="Task description used when submitting the integration test task",
    )
    parser.add_argument(
        "--task-created-by",
        default=DEFAULT_TASK_CREATED_BY,
        help="Identifier recorded as the creator of the integration test task",
    )
    parser.add_argument(
        "--task-parameters",
        help="JSON object specifying task parameters to include in the submission",
    )
    parser.add_argument(
        "--expected-status",
        default=DEFAULT_EXPECTED_STATUS,
        help="Expected terminal status for the test task (e.g. COMPLETED)",
    )

    args = parser.parse_args()
    env_auto_confirm = env_flag("NEXUS_AUTO_CONFIRM", "WHISKEY_AUTO_CONFIRM")
    env_skip_task = env_flag("NEXUS_SKIP_TASK", "WHISKEY_SKIP_TASK")
    env_skip_cleanup = env_flag("NEXUS_SKIP_CLEANUP", "WHISKEY_SKIP_CLEANUP")
    task_parameters = _default_task_parameters()
    if args.task_parameters:
        try:
            parsed_params = json.loads(args.task_parameters)
            if isinstance(parsed_params, dict):
                task_parameters = parsed_params
            else:
                raise ValueError("Task parameters JSON must encode an object")
        except (json.JSONDecodeError, ValueError) as exc:
            raise SystemExit(f"Invalid --task-parameters value: {exc}") from exc

    expected_status_raw = args.expected_status.upper().strip() if args.expected_status else None
    expected_status = expected_status_raw or None

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
        json_output_path=args.json_output,
        task_type=args.task_type,
        task_description=args.task_description,
        task_created_by=args.task_created_by,
        task_parameters=task_parameters,
        expected_status=expected_status,
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
    
    task_data = {
        "type": config.task_type,
        "description": config.task_description,
        "createdBy": config.task_created_by,
        "parameters": config.task_parameters,
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


def poll_task_status(config: TestConfig, task_id: str) -> Dict[str, Any]:
    """Poll the NEXUS task status endpoint until completion or retry limit reached."""

    endpoint = f"{config.api_base_url}/task/{task_id}"
    retries = max(config.status_retries, 0)
    delay = max(config.status_retry_delay, 0.1)
    expected = config.expected_status.upper() if config.expected_status else None

    history: List[str] = []
    last_status: Optional[str] = None
    attempts = 0

    for attempt in range(retries + 1):
        attempts = attempt + 1
        try:
            status_code, data, raw = http_json_request(
                "GET",
                endpoint,
                timeout=5,
                retries=0,
                retry_delay=delay,
            )
        except urllib.error.URLError as exc:
            last_status = f"connection-error:{exc.reason}"
            history.append(last_status)
            print(f"✗ Attempt {attempts}: unable to reach task status endpoint ({exc.reason})")
        except Exception as exc:  # pragma: no cover - defensive
            last_status = f"error:{exc}"
            history.append(last_status)
            print(f"✗ Attempt {attempts}: error checking task status: {exc}")
        else:
            if status_code == 200 and isinstance(data, dict):
                current_status = data.get("status") or data.get("taskStatus")
                normalized = str(current_status) if current_status is not None else None
                last_status = normalized
                history.append(normalized or "<no-status>")
                print(f"• Task status (attempt {attempts}/{retries + 1}): {normalized}")

                if normalized and normalized.upper() in {"COMPLETED", "FAILED", "CANCELLED"}:
                    matches_expected = expected is None or normalized.upper() == expected
                    return {
                        "success": matches_expected,
                        "terminal": True,
                        "status": normalized,
                        "matchesExpected": matches_expected,
                        "expectedStatus": config.expected_status,
                        "attempts": attempts,
                        "history": history,
                    }
            elif status_code == 404:
                last_status = "not-found"
                history.append(last_status)
                print(f"⚠ Task {task_id} not yet visible via API (attempt {attempts})")
            else:
                last_status = f"unexpected-status:{status_code}"
                history.append(last_status)
                details = raw if raw else "<no body>"
                print(f"⚠ Unexpected response ({status_code}): {details}")

        if attempt < retries:
            time.sleep(delay)

    print("⚠ Task did not reach a terminal state within polling window")
    matches_expected = False
    return {
        "success": False,
        "terminal": False,
        "status": last_status,
        "matchesExpected": matches_expected,
        "expectedStatus": config.expected_status,
        "attempts": attempts,
        "history": history,
    }


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

    results: List[StepResult] = []
    exit_code = 0

    def run_step(
        name: str,
        func,
        *,
        info_success=None,
        info_failure=None,
        critical: bool = True,
    ):
        nonlocal exit_code
        start = time.perf_counter()
        info: Optional[str] = None
        outcome = None
        try:
            outcome = func()
            success = _coerce_success(outcome)
        except Exception as exc:  # pragma: no cover - defensive guard
            success = False
            info = f"Exception: {exc}"
            print(f"✗ {name} encountered an unexpected error: {exc}")
        duration = time.perf_counter() - start

        if success and info_success:
            info = info_success(outcome)
        elif not success and info_failure and info is None:
            info = info_failure(outcome)

        results.append(StepResult(name=name, success=success, duration=duration, info=info))

        if not success and critical:
            exit_code = 1

        return success, outcome

    def finalize() -> int:
        overall_success = exit_code == 0
        print_summary(results)
        print(f"Overall result: {'PASS' if overall_success else 'FAIL'}")
        if config.json_output_path:
            try:
                write_json_summary(config.json_output_path, results, overall_success)
            except Exception as exc:  # pragma: no cover - defensive
                print(f"⚠ Unable to write JSON summary: {exc}")
                if overall_success:
                    print("  (This warning does not affect the test result.)")
        return exit_code

    print("===============================================")
    print("NEXUS AI Database Integration Test")
    print("===============================================")
    print(f"Database: {config.db_user}@{config.db_host}:{config.db_port}/{config.db_name}")
    print(f"API: {config.api_base_url}")
    print()

    prompt_nexus_start(config.auto_confirm)

    db_success, _ = run_step(
        "Database connectivity",
        lambda: test_database_connection(config),
        info_success=lambda _: "Connection verified",
        info_failure=lambda _: "Connection attempt failed",
    )
    if not db_success:
        print("Database connection test failed. Exiting.")
        return finalize()

    print()

    api_success, _ = run_step(
        "NEXUS API availability",
        lambda: test_nexus_api(config),
        info_success=lambda _: "Health and info endpoints reachable",
        info_failure=lambda _: "API checks failed",
    )
    if not api_success:
        print("NEXUS API test failed. Exiting.")
        return finalize()

    if config.skip_task_submission:
        print()
        print("Skipping task submission per configuration. Connectivity checks passed.")
        results.append(
            StepResult(
                name="Task submission",
                success=True,
                duration=0.0,
                info="Skipped per configuration",
            )
        )
        return finalize()

    print()
    print("Submitting test task...")
    submission_success, task_id = run_step(
        "Submit test task",
        lambda: submit_test_task(config),
        info_success=lambda tid: f"Task ID {tid}",
        info_failure=lambda _: "Submission failed",
    )
    if not submission_success or not task_id:
        print("Failed to submit test task. Exiting.")
        return finalize()

    if config.wait_seconds > 0:
        print()
        print(f"Waiting {config.wait_seconds:.1f}s for task processing...")
        time.sleep(config.wait_seconds)

    print()
    print("Polling task status via API...")
    poll_success, poll_outcome = run_step(
        "Poll task status via API",
        lambda: poll_task_status(config, task_id),
        info_success=lambda outcome: (
            f"Status {outcome.get('status')}" if isinstance(outcome, dict) else "Status unknown"
        ),
        info_failure=lambda outcome: (
            "No status reported"
            if not isinstance(outcome, dict)
            else (
                f"Status {outcome.get('status')} (expected {outcome.get('expectedStatus')})"
                if outcome.get('expectedStatus')
                else f"Status {outcome.get('status')}"
            )
        ),
        critical=False,
    )
    api_terminal = False
    observed_status: Optional[str] = None
    matches_expected = True
    if isinstance(poll_outcome, dict):
        api_terminal = bool(poll_outcome.get("terminal"))
        observed_status = poll_outcome.get("status")
        matches_expected = bool(poll_outcome.get("matchesExpected", True))
    if api_terminal and matches_expected:
        print(f"✓ API reported terminal status: {observed_status}")
    elif api_terminal and not matches_expected:
        print(
            f"⚠ Task reached terminal status {observed_status}, but expected {config.expected_status}"
        )
    else:
        print("⚠ Task did not report a terminal status via API within the allotted polling attempts")

    print()
    print("Checking task in database...")
    db_check_success, _ = run_step(
        "Verify task in database",
        lambda: check_task_in_database(config, task_id),
        info_success=lambda _: f"Task {task_id} present",
        info_failure=lambda _: "Task not located",
    )

    if db_check_success:
        print()
        print("🎉 Database integration test completed successfully!")
        print("   NEXUS is properly connected to PostgreSQL")

        if config.cleanup_task:
            print()
            run_step(
                "Cleanup test task",
                lambda: cleanup_test_task(config, task_id),
                info_success=lambda _: f"Task {task_id} removed",
                info_failure=lambda _: "Cleanup skipped or not required",
                critical=False,
            )

        return finalize()

    print()
    print("❌ Database integration test failed!")
    print("   Task was not found in the database")

    if config.cleanup_task:
        print()
        run_step(
            "Cleanup test task",
            lambda: cleanup_test_task(config, task_id),
            info_success=lambda _: f"Task {task_id} removed",
            info_failure=lambda _: "Cleanup skipped or not required",
            critical=False,
        )

    return finalize()

if __name__ == "__main__":
    exit(main())
"""
Rate Limiter for AI Provider API Calls
"""

import asyncio
import time
from typing import List

class RateLimiter:
    """Rate limiter for API calls"""

    def __init__(self, requests_per_minute: int):
        self.requests_per_minute = requests_per_minute
        self.requests = []

    async def wait(self):
        """Wait if rate limit would be exceeded"""
        now = time.time()

        # Remove old requests
        self.requests = [req_time for req_time in self.requests if now - req_time < 60]

        # Check if we need to wait
        if len(self.requests) >= self.requests_per_minute:
            sleep_time = 60 - (now - self.requests[0])
            if sleep_time > 0:
                await asyncio.sleep(sleep_time)

        # Add current request
        self.requests.append(now)
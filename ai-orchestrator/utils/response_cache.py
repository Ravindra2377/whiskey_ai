"""
Response Cache Manager for AI Orchestration
"""

import hashlib
from typing import Dict, Any, Optional
from datetime import datetime, timedelta

from core import AIRequest, AIResponse

class ResponseCacheManager:
    """Response cache manager for optimization"""

    def __init__(self):
        self.cache = {}
        self.cache_ttl = 3600  # 1 hour

    async def get_cached_response(self, request: AIRequest) -> Optional[AIResponse]:
        """Get cached response if available"""
        cache_key = self.generate_cache_key(request)

        if cache_key in self.cache:
            cached_data = self.cache[cache_key]
            if datetime.now() - cached_data['timestamp'] < timedelta(seconds=self.cache_ttl):
                return cached_data['response']

        return None

    async def cache_response(self, request: AIRequest, response: AIResponse):
        """Cache response for future use"""
        cache_key = self.generate_cache_key(request)
        self.cache[cache_key] = {
            'response': response,
            'timestamp': datetime.now()
        }

    def generate_cache_key(self, request: AIRequest) -> str:
        """Generate cache key for request"""
        key_data = f"{request.prompt}{request.task_type}{request.complexity.value}"
        return hashlib.md5(key_data.encode()).hexdigest()

    async def cleanup_expired(self):
        """Clean up expired cache entries"""
        now = datetime.now()
        expired_keys = []

        for key, data in self.cache.items():
            if now - data['timestamp'] > timedelta(seconds=self.cache_ttl):
                expired_keys.append(key)

        for key in expired_keys:
            del self.cache[key]
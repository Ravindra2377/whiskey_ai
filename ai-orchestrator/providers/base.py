"""
Base Provider Class for AI Orchestration
"""

from abc import ABC, abstractmethod
from typing import Dict, Any
from core import AIRequest, AIResponse, AIProvider

class BaseProvider(ABC):
    """Base class for all AI providers"""

    def __init__(self, provider_type: AIProvider):
        self.provider_type = provider_type

    @abstractmethod
    async def process_request(self, request: AIRequest) -> AIResponse:
        """Process an AI request and return a response"""
        pass

    @abstractmethod
    async def check_availability(self) -> bool:
        """Check if the provider is available"""
        pass

    @abstractmethod
    async def get_performance_metrics(self) -> Dict[str, Any]:
        """Get performance metrics for the provider"""
        pass
"""
Anthropic Claude Provider Implementation
"""

import anthropic
import time
from typing import Dict, List, Any
from datetime import datetime

from core import AIRequest, AIResponse, AIProvider, TaskComplexity
from providers.base import BaseProvider
from utils.rate_limiter import RateLimiter

class AnthropicProvider(BaseProvider):
    """Anthropic Claude provider implementation"""

    def __init__(self, api_key: str, model_preferences: Dict[str, str]):
        super().__init__(AIProvider.ANTHROPIC)
        self.client = anthropic.AsyncAnthropic(api_key=api_key)
        self.models = model_preferences
        self.rate_limiter = RateLimiter(requests_per_minute=500)

    async def process_request(self, request: AIRequest) -> AIResponse:
        """Process request using Anthropic Claude"""
        start_time = time.time()

        await self.rate_limiter.wait()

        model = self.select_model(request.task_type)

        try:
            response = await self.client.messages.create(
                model=model,
                max_tokens=request.max_tokens or 2000,
                temperature=request.temperature or 0.7,
                messages=[{
                    "role": "user",
                    "content": request.prompt
                }]
            )

            processing_time = time.time() - start_time

            return AIResponse(
                id=request.id,
                provider=AIProvider.ANTHROPIC,
                content=response.content[0].text,
                confidence=0.85,
                processing_time=processing_time,
                cost=self.calculate_cost(response.usage),
                metadata={
                    'model': model,
                    'usage': response.usage.__dict__,
                    'stop_reason': response.stop_reason
                },
                timestamp=datetime.now()
            )

        except Exception as e:
            raise Exception(f"Anthropic API error: {e}")

    def select_model(self, task_type: str) -> str:
        """Select appropriate Claude model"""
        task_lower = task_type.lower()

        if 'analysis' in task_lower or 'reasoning' in task_lower:
            return self.models.get('analysis', 'claude-3-opus-20240229')
        elif 'code' in task_lower:
            return self.models.get('code', 'claude-3-sonnet-20240229')
        else:
            return self.models.get('chat', 'claude-3-opus-20240229')

    def calculate_cost(self, usage) -> float:
        """Calculate Anthropic cost"""
        # Anthropic pricing
        input_cost_per_1k = 0.015
        output_cost_per_1k = 0.075

        input_cost = (usage.input_tokens / 1000) * input_cost_per_1k
        output_cost = (usage.output_tokens / 1000) * output_cost_per_1k

        return input_cost + output_cost

    async def check_availability(self) -> bool:
        """Check if Anthropic is available"""
        try:
            # Simple health check
            await self.client.count_tokens("test")
            return True
        except Exception:
            return False

    async def get_performance_metrics(self) -> Dict[str, Any]:
        """Get performance metrics for Anthropic provider"""
        # This would return actual performance data
        return {
            'avg_response_time': 3.2,
            'success_rate': 0.92,
            'error_rate': 0.08
        }
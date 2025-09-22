"""
OpenAI Provider Implementation
"""

import openai
import time
from typing import Dict, List, Any
from datetime import datetime

from core import AIRequest, AIResponse, AIProvider, TaskComplexity
from providers.base import BaseProvider
from utils.rate_limiter import RateLimiter

class OpenAIProvider(BaseProvider):
    """OpenAI provider implementation"""

    def __init__(self, api_key: str, model_preferences: Dict[str, str]):
        super().__init__(AIProvider.OPENAI)
        self.client = openai.AsyncOpenAI(api_key=api_key)
        self.models = model_preferences
        self.rate_limiter = RateLimiter(requests_per_minute=1000)

    async def process_request(self, request: AIRequest) -> AIResponse:
        """Process request using OpenAI"""
        start_time = time.time()

        # Wait for rate limit
        await self.rate_limiter.wait()

        # Select appropriate model
        model = self.select_model(request.task_type)

        # Prepare messages
        messages = self.prepare_messages(request)

        try:
            # Make API call
            response = await self.client.chat.completions.create(
                model=model,
                messages=messages,
                max_tokens=request.max_tokens or 2000,
                temperature=request.temperature or 0.7
            )

            processing_time = time.time() - start_time

            return AIResponse(
                id=request.id,
                provider=AIProvider.OPENAI,
                content=response.choices[0].message.content,
                confidence=0.9,  # Would be calculated based on response metrics
                processing_time=processing_time,
                cost=self.calculate_cost(response.usage),
                metadata={
                    'model': model,
                    'usage': response.usage.model_dump(),
                    'finish_reason': response.choices[0].finish_reason
                },
                timestamp=datetime.now()
            )

        except Exception as e:
            raise Exception(f"OpenAI API error: {e}")

    def select_model(self, task_type: str) -> str:
        """Select appropriate OpenAI model based on task type"""
        task_lower = task_type.lower()

        if 'code' in task_lower or 'programming' in task_lower:
            return self.models.get('code', 'gpt-4')
        elif 'vision' in task_lower or 'image' in task_lower:
            return self.models.get('vision', 'gpt-4-vision-preview')
        else:
            return self.models.get('chat', 'gpt-4-turbo-preview')

    def prepare_messages(self, request: AIRequest) -> List[Dict[str, str]]:
        """Prepare messages for OpenAI API"""
        messages = [
            {
                "role": "system",
                "content": "You are WHISKEY AI, an advanced AI development assistant. Provide expert-level responses with practical, actionable advice."
            },
            {
                "role": "user",
                "content": request.prompt
            }
        ]

        # Add context if available
        if request.context:
            context_str = str(request.context)
            messages.insert(-1, {
                "role": "system",
                "content": f"Additional context: {context_str}"
            })

        return messages

    def calculate_cost(self, usage) -> float:
        """Calculate cost based on token usage"""
        # OpenAI pricing (as of 2024)
        input_cost_per_1k = 0.01  # $0.01 per 1K input tokens
        output_cost_per_1k = 0.03  # $0.03 per 1K output tokens

        input_cost = (usage.prompt_tokens / 1000) * input_cost_per_1k
        output_cost = (usage.completion_tokens / 1000) * output_cost_per_1k

        return input_cost + output_cost

    async def check_availability(self) -> bool:
        """Check if OpenAI is available"""
        try:
            # Simple health check
            await self.client.models.list()
            return True
        except Exception:
            return False

    async def get_performance_metrics(self) -> Dict[str, Any]:
        """Get performance metrics for OpenAI provider"""
        # This would return actual performance data
        return {
            'avg_response_time': 2.5,
            'success_rate': 0.95,
            'error_rate': 0.05
        }
"""
Google AI Provider Implementation
"""

import google.generativeai as genai
import time
from typing import Dict, List, Any
from datetime import datetime

from core import AIRequest, AIResponse, AIProvider, TaskComplexity
from providers.base import BaseProvider
from utils.rate_limiter import RateLimiter

class GoogleAIProvider(BaseProvider):
    """Google AI provider implementation"""

    def __init__(self, api_key: str, model_preferences: Dict[str, str]):
        super().__init__(AIProvider.GOOGLE)
        genai.configure(api_key=api_key)
        self.models = model_preferences
        self.rate_limiter = RateLimiter(requests_per_minute=600)

    async def process_request(self, request: AIRequest) -> AIResponse:
        """Process request using Google AI"""
        start_time = time.time()

        await self.rate_limiter.wait()

        model_name = self.select_model(request.task_type)
        model = genai.GenerativeModel(model_name)

        try:
            response = await model.generate_content_async(
                request.prompt,
                generation_config=genai.types.GenerationConfig(
                    max_output_tokens=request.max_tokens or 2000,
                    temperature=request.temperature or 0.7
                )
            )

            processing_time = time.time() - start_time

            return AIResponse(
                id=request.id,
                provider=AIProvider.GOOGLE,
                content=response.text,
                confidence=0.8,
                processing_time=processing_time,
                cost=0.001,  # Google AI is generally cheaper
                metadata={
                    'model': model_name,
                    'safety_ratings': [rating.__dict__ for rating in response.candidates[0].safety_ratings]
                },
                timestamp=datetime.now()
            )

        except Exception as e:
            raise Exception(f"Google AI error: {e}")

    def select_model(self, task_type: str) -> str:
        """Select appropriate Google AI model"""
        task_lower = task_type.lower()

        if 'vision' in task_lower or 'image' in task_lower:
            return self.models.get('vision', 'gemini-pro-vision')
        elif 'code' in task_lower:
            return self.models.get('code', 'gemini-pro')
        else:
            return self.models.get('chat', 'gemini-pro')

    async def check_availability(self) -> bool:
        """Check if Google AI is available"""
        try:
            # Simple health check
            genai.list_models()
            return True
        except Exception:
            return False

    async def get_performance_metrics(self) -> Dict[str, Any]:
        """Get performance metrics for Google AI provider"""
        # This would return actual performance data
        return {
            'avg_response_time': 2.0,
            'success_rate': 0.90,
            'error_rate': 0.10
        }
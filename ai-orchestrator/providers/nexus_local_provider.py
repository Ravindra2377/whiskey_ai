"""
Local Whiskey AI Provider Implementation
"""

import asyncio
import time
from typing import Dict, Any
from datetime import datetime

from core import AIRequest, AIResponse, AIProvider, TaskComplexity
from providers.base import BaseProvider

class WhiskeyLocalProvider(BaseProvider):
    """Local Whiskey AI provider (fallback)"""

    def __init__(self, enhanced_capabilities: bool = True):
        super().__init__(AIProvider.WHISKEY_LOCAL)
        self.enhanced_capabilities = enhanced_capabilities

    async def process_request(self, request: AIRequest) -> AIResponse:
        """Process request using local Whiskey AI capabilities"""
        start_time = time.time()

        # Simulate local processing
        await asyncio.sleep(0.5)  # Simulate processing time

        # Generate response based on request type
        response_content = self.generate_local_response(request)

        processing_time = time.time() - start_time

        return AIResponse(
            id=request.id,
            provider=AIProvider.WHISKEY_LOCAL,
            content=response_content,
            confidence=0.75,
            processing_time=processing_time,
            cost=0.0,  # Local processing is free
            metadata={
                'local_processing': True,
                'enhanced_capabilities': self.enhanced_capabilities
            },
            timestamp=datetime.now()
        )

    def generate_local_response(self, request: AIRequest) -> str:
        """Generate response using local capabilities"""
        task_type = request.task_type.lower()

        if 'code' in task_type:
            return f"Local code generation for: {request.prompt[:100]}..."
        elif 'analysis' in task_type:
            return f"Local analysis result for: {request.prompt[:100]}..."
        else:
            return f"Local processing completed for: {request.prompt[:100]}..."

    async def check_availability(self) -> bool:
        """Local provider is always available"""
        return True

    async def get_performance_metrics(self) -> Dict[str, Any]:
        """Get performance metrics for local provider"""
        return {
            'avg_response_time': 0.5,
            'success_rate': 1.0,
            'error_rate': 0.0
        }
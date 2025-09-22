"""
Main Application for AI Orchestration
"""

import asyncio
import logging
from typing import Dict, Any

from core import AIRequest, TaskComplexity, AIProvider
from orchestrator import AdvancedAIOrchestrator
from config.settings import Config
from providers.openai_provider import OpenAIProvider
from providers.anthropic_provider import AnthropicProvider
from providers.google_provider import GoogleAIProvider
from providers.nexus_local_provider import NexusLocalProvider

# Set up logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class AIOrchestrationApp:
    """Main application class for AI orchestration"""

    def __init__(self):
        self.config = Config()
        self.orchestrator = AdvancedAIOrchestrator(self.config.provider_configs)
        self.initialize_providers()

    def initialize_providers(self):
        """Initialize all AI providers"""
        # OpenAI Configuration
        if self.config.provider_configs.get('openai', {}).get('api_key'):
            try:
                self.orchestrator.providers[AIProvider.OPENAI] = OpenAIProvider(
                    api_key=self.config.provider_configs['openai']['api_key'],
                    model_preferences=self.config.provider_configs['openai'].get('models', {
                        'chat': 'gpt-4-turbo-preview',
                        'code': 'gpt-4',
                        'vision': 'gpt-4-vision-preview',
                        'embedding': 'text-embedding-3-large'
                    })
                )
                logger.info("✅ OpenAI provider initialized")
            except Exception as e:
                logger.warning(f"⚠️ Failed to initialize OpenAI provider: {e}")

        # Anthropic Configuration
        if self.config.provider_configs.get('anthropic', {}).get('api_key'):
            try:
                self.orchestrator.providers[AIProvider.ANTHROPIC] = AnthropicProvider(
                    api_key=self.config.provider_configs['anthropic']['api_key'],
                    model_preferences=self.config.provider_configs['anthropic'].get('models', {
                        'chat': 'claude-3-opus-20240229',
                        'code': 'claude-3-sonnet-20240229',
                        'analysis': 'claude-3-opus-20240229'
                    })
                )
                logger.info("✅ Anthropic provider initialized")
            except Exception as e:
                logger.warning(f"⚠️ Failed to initialize Anthropic provider: {e}")

        # Google AI Configuration
        if self.config.provider_configs.get('google', {}).get('api_key'):
            try:
                self.orchestrator.providers[AIProvider.GOOGLE] = GoogleAIProvider(
                    api_key=self.config.provider_configs['google']['api_key'],
                    model_preferences=self.config.provider_configs['google'].get('models', {
                        'chat': 'gemini-pro',
                        'vision': 'gemini-pro-vision',
                        'code': 'gemini-pro'
                    })
                )
                logger.info("✅ Google AI provider initialized")
            except Exception as e:
                logger.warning(f"⚠️ Failed to initialize Google AI provider: {e}")

        # Local Nexus AI (always available)
        self.orchestrator.providers[AIProvider.NEXUS_LOCAL] = NexusLocalProvider(
            enhanced_capabilities=True
        )
        logger.info("✅ Local NEXUS provider initialized")

        logger.info(f"✅ Initialized {len(self.orchestrator.providers)} AI providers")

    async def process_request(self, request_data: Dict[str, Any]) -> Dict[str, Any]:
        """Process an AI request"""
        # Create AIRequest object
        request = AIRequest(
            id=request_data.get('id', 'req_001'),
            prompt=request_data.get('prompt', ''),
            task_type=request_data.get('task_type', 'general'),
            complexity=TaskComplexity(request_data.get('complexity', 'moderate')),
            context=request_data.get('context', {}),
            user_id=request_data.get('user_id', 'user_123'),
            priority=request_data.get('priority', 3),
            timestamp=request_data.get('timestamp')
        )

        # Process with orchestrator
        response = await self.orchestrator.process_request(request)

        # Return response as dictionary
        return {
            'id': response.id,
            'provider': response.provider.value,
            'content': response.content,
            'confidence': response.confidence,
            'processing_time': response.processing_time,
            'cost': response.cost,
            'metadata': response.metadata,
            'timestamp': response.timestamp.isoformat()
        }

    def run_example(self):
        """Run an example request"""
        example_request = {
            'id': 'example_001',
            'prompt': 'Generate a React component for user authentication',
            'task_type': 'code_generation',
            'complexity': 'moderate',
            'context': {'framework': 'React', 'styling': 'Tailwind'},
            'user_id': 'user_123',
            'priority': 3
        }

        # Run async function
        response = asyncio.run(self.process_request(example_request))
        print(f"Response: {response}")

if __name__ == "__main__":
    app = AIOrchestrationApp()
    app.run_example()
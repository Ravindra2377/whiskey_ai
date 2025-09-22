"""
Configuration Settings for AI Orchestration
"""

import os
from typing import Dict, Any

class Config:
    """Configuration class for AI orchestration"""

    def __init__(self):
        self.openai_api_key = os.getenv('OPENAI_API_KEY')
        self.anthropic_api_key = os.getenv('ANTHROPIC_API_KEY')
        self.google_api_key = os.getenv('GOOGLE_API_KEY')
        
        self.provider_configs = {
            'openai': {
                'api_key': self.openai_api_key,
                'models': {
                    'chat': 'gpt-4-turbo-preview',
                    'code': 'gpt-4',
                    'vision': 'gpt-4-vision-preview',
                    'embedding': 'text-embedding-3-large'
                }
            },
            'anthropic': {
                'api_key': self.anthropic_api_key,
                'models': {
                    'chat': 'claude-3-opus-20240229',
                    'code': 'claude-3-sonnet-20240229',
                    'analysis': 'claude-3-opus-20240229'
                }
            },
            'google': {
                'api_key': self.google_api_key,
                'models': {
                    'chat': 'gemini-pro',
                    'vision': 'gemini-pro-vision',
                    'code': 'gemini-pro'
                }
            }
        }

    def get_provider_config(self, provider_name: str) -> Dict[str, Any]:
        """Get configuration for a specific provider"""
        return self.provider_configs.get(provider_name, {})
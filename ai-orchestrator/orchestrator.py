"""
Advanced AI Orchestrator that intelligently routes requests to optimal providers
Combines OpenAI, Anthropic, Google AI, and local Whiskey AI capabilities
"""

import asyncio
import json
import time
from datetime import datetime, timedelta
from typing import Dict, List, Any, Optional
import logging
import hashlib

from core import AIProvider, TaskComplexity, AIRequest, AIResponse
from routing.router import IntelligentRouter, RoutingStrategy
from utils.cost_optimizer import CostOptimizer
from utils.quality_monitor import QualityMonitor
from utils.response_cache import ResponseCacheManager
from utils.load_balancer import IntelligentLoadBalancer
from utils.rate_limiter import RateLimiter

class AdvancedAIOrchestrator:
    """
    Advanced AI orchestrator that intelligently routes requests to optimal providers
    Combines OpenAI, Anthropic, Google AI, and local Whiskey AI capabilities
    """

    def __init__(self, config: Dict[str, Any]):
        self.config = config
        self.providers = {}
        self.load_balancer = IntelligentLoadBalancer()
        self.cost_optimizer = CostOptimizer()
        self.quality_monitor = QualityMonitor()
        self.cache_manager = ResponseCacheManager()

        # Initialize AI providers
        self.initialize_providers()

        # Start monitoring and optimization
        # asyncio.create_task(self.start_monitoring())

        logging.info("🚀 Advanced AI Orchestrator initialized with multi-provider support")

    def initialize_providers(self):
        """Initialize all AI providers with proper configuration"""
        # Providers will be initialized externally and added to self.providers
        # This method is kept for compatibility
        pass

    async def process_request(self, request: AIRequest) -> AIResponse:
        """
        Intelligently process AI request using optimal provider selection
        """
        # Check cache first
        cached_response = await self.cache_manager.get_cached_response(request)
        if cached_response:
            logging.info(f"📋 Cache hit for request {request.id}")
            return cached_response

        # Select optimal provider
        optimal_provider = await self.select_optimal_provider(request)

        try:
            # Process with selected provider
            response = await self.providers[optimal_provider].process_request(request)

            # Quality check and validation
            validated_response = await self.quality_monitor.validate_response(response, request)

            # Cache successful response
            await self.cache_manager.cache_response(request, validated_response)

            # Update provider metrics
            await self.update_provider_metrics(optimal_provider, validated_response, True)

            logging.info(f"✅ Processed request {request.id} with {optimal_provider.value}")
            return validated_response

        except Exception as e:
            logging.error(f"❌ Error processing request {request.id}: {e}")

            # Fallback to alternative provider
            fallback_response = await self.handle_failure_fallback(request, optimal_provider)

            # Update provider metrics (failure)
            await self.update_provider_metrics(optimal_provider, None, False)

            return fallback_response

    async def select_optimal_provider(self, request: AIRequest) -> AIProvider:
        """
        Select optimal AI provider based on multiple factors
        """
        factors = {
            'cost': 0.3,
            'performance': 0.3,
            'capability': 0.2,
            'availability': 0.1,
            'quality': 0.1
        }

        provider_scores = {}

        # If no external providers are available, use local provider
        if len(self.providers) <= 1:  # Only local provider available
            logging.info("⚠️ No external providers available, using local WHISKEY provider")
            return AIProvider.WHISKEY_LOCAL

        for provider, provider_instance in self.providers.items():
            # Skip local provider in scoring unless it's the only option
            if provider == AIProvider.WHISKEY_LOCAL and len(self.providers) > 1:
                continue
                
            # Calculate capability score
            capability_score = await self.calculate_capability_score(provider, request)

            # Get cost estimate
            cost_score = await self.cost_optimizer.calculate_cost_score(provider, request)

            # Get performance metrics
            performance_score = await self.get_performance_score(provider)

            # Check availability
            availability_score = await self.check_provider_availability(provider)

            # Get quality score
            quality_score = await self.quality_monitor.get_provider_quality_score(provider)

            # Calculate weighted score
            total_score = (
                capability_score * factors['capability'] +
                cost_score * factors['cost'] +
                performance_score * factors['performance'] +
                availability_score * factors['availability'] +
                quality_score * factors['quality']
            )

            provider_scores[provider] = total_score

        # Select provider with highest score, fallback to local if none available
        if provider_scores:
            optimal_provider = max(provider_scores.items(), key=lambda x: x[1])[0]
            logging.info(f"🎯 Selected {optimal_provider.value} with score {provider_scores[optimal_provider]:.3f}")
            return optimal_provider
        else:
            logging.info("⚠️ No suitable providers found, using local WHISKEY provider")
            return AIProvider.WHISKEY_LOCAL

    async def calculate_capability_score(self, provider: AIProvider, request: AIRequest) -> float:
        """Calculate capability score for provider based on request type"""
        # This will be implemented with actual capability matrices
        return 0.8

    async def handle_failure_fallback(self, request: AIRequest, failed_provider: AIProvider) -> AIResponse:
        """Handle provider failure with intelligent fallback"""
        # Get alternative providers (exclude failed one)
        alternative_providers = [p for p in self.providers.keys() if p != failed_provider]

        if not alternative_providers:
            # Use local Whiskey AI as last resort
            return await self.providers[AIProvider.WHISKEY_LOCAL].process_request(request)

        # Try alternative providers in order of capability
        for provider in alternative_providers:
            try:
                response = await self.providers[provider].process_request(request)
                logging.info(f"🔄 Fallback successful with {provider.value}")
                return response
            except Exception as e:
                logging.warning(f"⚠️ Fallback failed with {provider.value}: {e}")
                continue

        # Final fallback to local processing
        return await self.providers[AIProvider.WHISKEY_LOCAL].process_request(request)

    async def start_monitoring(self):
        """Start continuous monitoring and optimization"""
        while True:
            try:
                # Monitor provider health
                await self.monitor_provider_health()

                # Optimize cost allocation
                await self.cost_optimizer.optimize_allocation()

                # Update load balancing
                await self.load_balancer.update_weights()

                # Clean cache
                await self.cache_manager.cleanup_expired()

                await asyncio.sleep(60)  # Monitor every minute

            except Exception as e:
                logging.error(f"Monitoring error: {e}")
                await asyncio.sleep(60)

    async def monitor_provider_health(self):
        """Monitor health of all providers"""
        pass

    async def update_provider_metrics(self, provider: AIProvider, response: AIResponse, success: bool):
        """Update provider metrics based on response"""
        pass

    async def get_performance_score(self, provider: AIProvider) -> float:
        """Get performance score for provider"""
        return 0.8

    async def check_provider_availability(self, provider: AIProvider) -> float:
        """Check if provider is available"""
        return 1.0
"""
Intelligent Router for Advanced AI Orchestration
"""

import asyncio
import numpy as np
from typing import Dict, List, Any, Tuple
from dataclasses import dataclass
from enum import Enum
import json
from datetime import datetime

from core import AIProvider, TaskComplexity, AIRequest, AIResponse

class RoutingStrategy(Enum):
    COST_OPTIMIZED = "cost_optimized"
    PERFORMANCE_OPTIMIZED = "performance_optimized"
    QUALITY_OPTIMIZED = "quality_optimized"
    BALANCED = "balanced"
    AVAILABILITY_FIRST = "availability_first"

@dataclass
class RoutingDecision:
    selected_provider: AIProvider
    confidence_score: float
    reasoning: str
    alternatives: List[Tuple[AIProvider, float]]
    estimated_cost: float
    estimated_time: float

class IntelligentRouter:
    """
    Advanced routing algorithm for optimal AI provider selection
    Uses machine learning and heuristics to make routing decisions
    """

    def __init__(self):
        self.routing_history = []
        self.provider_performance = {}
        self.cost_tracker = CostTracker()
        self.performance_predictor = PerformancePredictor()
        self.quality_assessor = QualityAssessor()

    async def route_request(self, 
                          request: AIRequest, 
                          available_providers: List[AIProvider],
                          strategy: RoutingStrategy = RoutingStrategy.BALANCED) -> RoutingDecision:
        """
        Route request to optimal provider based on strategy
        """

        # Step 1: Analyze request characteristics
        request_features = await self.analyze_request(request)

        # Step 2: Score all available providers
        provider_scores = {}
        for provider in available_providers:
            score = await self.score_provider(provider, request, request_features, strategy)
            provider_scores[provider] = score

        # Step 3: Select optimal provider
        sorted_providers = sorted(provider_scores.items(), key=lambda x: x[1], reverse=True)
        selected_provider, best_score = sorted_providers[0]

        # Step 4: Generate routing decision
        decision = RoutingDecision(
            selected_provider=selected_provider,
            confidence_score=best_score,
            reasoning=await self.generate_reasoning(selected_provider, request, strategy),
            alternatives=sorted_providers[1:4],  # Top 3 alternatives
            estimated_cost=await self.estimate_cost(selected_provider, request),
            estimated_time=await self.estimate_processing_time(selected_provider, request)
        )

        # Step 5: Log decision for learning
        await self.log_routing_decision(request, decision)

        return decision

    async def analyze_request(self, request: AIRequest) -> Dict[str, Any]:
        """Analyze request to extract routing-relevant features"""

        features = {
            'complexity_score': self.calculate_complexity_score(request),
            'urgency_score': self.calculate_urgency_score(request),
            'cost_sensitivity': self.calculate_cost_sensitivity(request),
            'quality_requirements': self.calculate_quality_requirements(request),
            'task_category': self.categorize_task(request),
            'context_richness': len(str(request.context)) / 1000,  # Normalize
            'user_tier': self.get_user_tier(request.user_id)
        }

        return features

    def calculate_complexity_score(self, request: AIRequest) -> float:
        """Calculate complexity score from 0-1"""
        complexity_mapping = {
            TaskComplexity.SIMPLE: 0.2,
            TaskComplexity.MODERATE: 0.5,
            TaskComplexity.COMPLEX: 0.8,
            TaskComplexity.ENTERPRISE: 1.0
        }

        base_score = complexity_mapping.get(request.complexity, 0.5)

        # Adjust based on prompt length and context
        prompt_factor = min(len(request.prompt) / 2000, 1.0)
        context_factor = min(len(str(request.context)) / 5000, 1.0)

        return min(base_score + (prompt_factor + context_factor) * 0.2, 1.0)

    def calculate_urgency_score(self, request: AIRequest) -> float:
        """Calculate urgency from 0-1 based on priority and timestamps"""
        priority_factor = request.priority / 5.0

        # Add time-based urgency (if request has been waiting)
        time_factor = 0.0
        if hasattr(request, 'created_at'):
            wait_time = (datetime.now() - request.created_at).total_seconds()
            time_factor = min(wait_time / 300, 0.3)  # Max 0.3 boost after 5 minutes

        return min(priority_factor + time_factor, 1.0)

    def calculate_cost_sensitivity(self, request: AIRequest) -> float:
        """Calculate cost sensitivity from 0-1"""
        # Higher score = more cost sensitive
        user_tier = self.get_user_tier(request.user_id)

        tier_sensitivity = {
            'free': 0.9,
            'basic': 0.7,
            'premium': 0.4,
            'enterprise': 0.1
        }

        return tier_sensitivity.get(user_tier, 0.5)

    def calculate_quality_requirements(self, request: AIRequest) -> float:
        """Calculate quality requirements from 0-1"""
        # Higher score = higher quality requirements
        base_quality = {
            TaskComplexity.SIMPLE: 0.3,
            TaskComplexity.MODERATE: 0.5,
            TaskComplexity.COMPLEX: 0.8,
            TaskComplexity.ENTERPRISE: 1.0
        }

        quality_score = base_quality.get(request.complexity, 0.5)

        # Adjust based on task type
        high_quality_tasks = ['code_review', 'security_analysis', 'architecture_design']
        if any(task in request.task_type.lower() for task in high_quality_tasks):
            quality_score = min(quality_score + 0.3, 1.0)

        return quality_score

    def categorize_task(self, request: AIRequest) -> str:
        """Categorize task type for routing decisions"""
        prompt_lower = request.prompt.lower()
        task_lower = request.task_type.lower()

        categories = {
            'code_generation': ['generate code', 'write function', 'create class', 'implement'],
            'code_review': ['review code', 'check code', 'analyze code', 'code quality'],
            'debugging': ['debug', 'fix bug', 'error', 'troubleshoot'],
            'analysis': ['analyze', 'examine', 'investigate', 'research'],
            'creative': ['creative', 'design', 'write story', 'brainstorm'],
            'conversation': ['chat', 'discuss', 'explain', 'help'],
            'technical_writing': ['documentation', 'write guide', 'create manual']
        }

        for category, keywords in categories.items():
            if any(keyword in prompt_lower or keyword in task_lower for keyword in keywords):
                return category

        return 'general'

    def get_user_tier(self, user_id: str) -> str:
        """Get user tier for cost and quality considerations"""
        # This would typically query a user database
        # For now, return a default tier
        return 'basic'

    async def score_provider(self, 
                           provider: AIProvider, 
                           request: AIRequest, 
                           features: Dict[str, Any],
                           strategy: RoutingStrategy) -> float:
        """Score provider based on request features and strategy"""

        # Get base capability score
        capability_score = await self.get_capability_score(provider, request, features)

        # Get performance metrics
        performance_score = await self.get_performance_score(provider, features)

        # Get cost score
        cost_score = await self.get_cost_score(provider, features)

        # Get quality score
        quality_score = await self.get_quality_score(provider, features)

        # Get availability score
        availability_score = await self.get_availability_score(provider)

        # Combine scores based on strategy
        weights = self.get_strategy_weights(strategy)

        total_score = (
            capability_score * weights['capability'] +
            performance_score * weights['performance'] +
            cost_score * weights['cost'] +
            quality_score * weights['quality'] +
            availability_score * weights['availability']
        )

        return total_score

    def get_strategy_weights(self, strategy: RoutingStrategy) -> Dict[str, float]:
        """Get scoring weights based on routing strategy"""

        weight_configs = {
            RoutingStrategy.COST_OPTIMIZED: {
                'capability': 0.15,
                'performance': 0.15,
                'cost': 0.5,
                'quality': 0.1,
                'availability': 0.1
            },
            RoutingStrategy.PERFORMANCE_OPTIMIZED: {
                'capability': 0.2,
                'performance': 0.5,
                'cost': 0.1,
                'quality': 0.1,
                'availability': 0.1
            },
            RoutingStrategy.QUALITY_OPTIMIZED: {
                'capability': 0.3,
                'performance': 0.1,
                'cost': 0.1,
                'quality': 0.4,
                'availability': 0.1
            },
            RoutingStrategy.BALANCED: {
                'capability': 0.25,
                'performance': 0.25,
                'cost': 0.2,
                'quality': 0.2,
                'availability': 0.1
            },
            RoutingStrategy.AVAILABILITY_FIRST: {
                'capability': 0.15,
                'performance': 0.15,
                'cost': 0.15,
                'quality': 0.15,
                'availability': 0.4
            }
        }

        return weight_configs.get(strategy, weight_configs[RoutingStrategy.BALANCED])

    async def get_capability_score(self, provider: AIProvider, request: AIRequest, features: Dict) -> float:
        """Get capability score for provider"""

        # Base capability matrix (from previous implementation)
        capability_matrix = {
            AIProvider.OPENAI: {
                'code_generation': 0.95,
                'code_review': 0.9,
                'debugging': 0.85,
                'analysis': 0.9,
                'creative': 0.9,
                'conversation': 0.9,
                'technical_writing': 0.85
            },
            AIProvider.ANTHROPIC: {
                'code_generation': 0.9,
                'code_review': 0.95,
                'debugging': 0.9,
                'analysis': 0.95,
                'creative': 0.85,
                'conversation': 0.95,
                'technical_writing': 0.9
            },
            AIProvider.GOOGLE: {
                'code_generation': 0.85,
                'code_review': 0.8,
                'debugging': 0.8,
                'analysis': 0.85,
                'creative': 0.75,
                'conversation': 0.8,
                'technical_writing': 0.8
            },
            AIProvider.WHISKEY_LOCAL: {
                'code_generation': 0.8,
                'code_review': 0.75,
                'debugging': 0.7,
                'analysis': 0.75,
                'creative': 0.65,
                'conversation': 0.7,
                'technical_writing': 0.7
            }
        }

        task_category = features.get('task_category', 'general')
        provider_capabilities = capability_matrix.get(provider, {})
        base_score = provider_capabilities.get(task_category, 0.5)

        # Adjust for complexity
        complexity_factor = 1.0 - (features['complexity_score'] * 0.2)
        if provider == AIProvider.OPENAI and features['complexity_score'] > 0.7:
            complexity_factor = 1.1  # OpenAI handles complex tasks well

        return min(base_score * complexity_factor, 1.0)

    async def get_performance_score(self, provider: AIProvider, features: Dict) -> float:
        """Get performance score based on historical data"""

        # Simulate performance scoring (would use real metrics)
        base_performance = {
            AIProvider.OPENAI: 0.85,
            AIProvider.ANTHROPIC: 0.8,
            AIProvider.GOOGLE: 0.9,  # Often faster
            AIProvider.WHISKEY_LOCAL: 0.95  # Local is fastest
        }

        score = base_performance.get(provider, 0.5)

        # Adjust for urgency
        if features['urgency_score'] > 0.8 and provider == AIProvider.WHISKEY_LOCAL:
            score *= 1.2  # Local is best for urgent requests

        return min(score, 1.0)

    async def get_cost_score(self, provider: AIProvider, features: Dict) -> float:
        """Get cost score (higher is better/cheaper)"""

        # Base cost scores (inverted - higher is cheaper)
        cost_scores = {
            AIProvider.OPENAI: 0.6,  # More expensive
            AIProvider.ANTHROPIC: 0.5,  # Most expensive
            AIProvider.GOOGLE: 0.8,  # Cheaper
            AIProvider.WHISKEY_LOCAL: 1.0  # Free
        }

        base_score = cost_scores.get(provider, 0.5)

        # Adjust for cost sensitivity
        if features['cost_sensitivity'] > 0.7:
            if provider == AIProvider.WHISKEY_LOCAL:
                base_score *= 1.3  # Boost local for cost-sensitive users
            else:
                base_score *= 0.8  # Penalize paid services

        return min(base_score, 1.0)

    async def get_quality_score(self, provider: AIProvider, features: Dict) -> float:
        """Get quality score based on historical performance"""

        quality_scores = {
            AIProvider.OPENAI: 0.9,
            AIProvider.ANTHROPIC: 0.95,  # Often highest quality
            AIProvider.GOOGLE: 0.8,
            AIProvider.WHISKEY_LOCAL: 0.7
        }

        base_score = quality_scores.get(provider, 0.5)

        # Boost for high-quality requirements
        if features['quality_requirements'] > 0.8:
            if provider in [AIProvider.OPENAI, AIProvider.ANTHROPIC]:
                base_score *= 1.1

        return min(base_score, 1.0)

    async def get_availability_score(self, provider: AIProvider) -> float:
        """Get availability score for provider"""

        # This would check actual availability
        # For now, simulate based on provider characteristics
        availability_scores = {
            AIProvider.OPENAI: 0.95,
            AIProvider.ANTHROPIC: 0.9,
            AIProvider.GOOGLE: 0.85,
            AIProvider.WHISKEY_LOCAL: 1.0  # Always available
        }

        return availability_scores.get(provider, 0.5)

    async def generate_reasoning(self, provider: AIProvider, request: AIRequest, strategy: RoutingStrategy) -> str:
        """Generate human-readable reasoning for routing decision"""

        reasons = []

        # Provider-specific reasons
        provider_reasons = {
            AIProvider.OPENAI: "OpenAI selected for strong general capabilities and code generation",
            AIProvider.ANTHROPIC: "Claude selected for superior reasoning and analysis quality",
            AIProvider.GOOGLE: "Google AI selected for cost-effectiveness and good performance",
            AIProvider.WHISKEY_LOCAL: "Local processing selected for speed, cost savings, and privacy"
        }

        reasons.append(provider_reasons.get(provider, "Provider selected based on optimization criteria"))

        # Strategy-specific reasons
        if strategy == RoutingStrategy.COST_OPTIMIZED:
            reasons.append("Cost optimization prioritized in selection")
        elif strategy == RoutingStrategy.PERFORMANCE_OPTIMIZED:
            reasons.append("Performance optimization prioritized in selection")
        elif strategy == RoutingStrategy.QUALITY_OPTIMIZED:
            reasons.append("Quality optimization prioritized in selection")

        # Task-specific reasons
        if request.complexity == TaskComplexity.ENTERPRISE:
            reasons.append("Enterprise-grade processing capabilities required")

        if request.priority >= 4:
            reasons.append("High priority request requiring immediate attention")

        return ". ".join(reasons) + "."

    async def estimate_cost(self, provider: AIProvider, request: AIRequest) -> float:
        """Estimate cost for processing request"""

        # Rough cost estimates per request
        cost_estimates = {
            AIProvider.OPENAI: 0.02,  # $0.02 average
            AIProvider.ANTHROPIC: 0.03,  # $0.03 average
            AIProvider.GOOGLE: 0.01,  # $0.01 average
            AIProvider.WHISKEY_LOCAL: 0.0  # Free
        }

        base_cost = cost_estimates.get(provider, 0.01)

        # Adjust for complexity
        complexity_multiplier = {
            TaskComplexity.SIMPLE: 0.5,
            TaskComplexity.MODERATE: 1.0,
            TaskComplexity.COMPLEX: 2.0,
            TaskComplexity.ENTERPRISE: 3.0
        }

        return base_cost * complexity_multiplier.get(request.complexity, 1.0)

    async def estimate_processing_time(self, provider: AIProvider, request: AIRequest) -> float:
        """Estimate processing time in seconds"""

        base_times = {
            AIProvider.OPENAI: 3.0,
            AIProvider.ANTHROPIC: 4.0,
            AIProvider.GOOGLE: 2.5,
            AIProvider.WHISKEY_LOCAL: 1.0
        }

        base_time = base_times.get(provider, 3.0)

        # Adjust for complexity
        complexity_factor = {
            TaskComplexity.SIMPLE: 0.5,
            TaskComplexity.MODERATE: 1.0,
            TaskComplexity.COMPLEX: 1.5,
            TaskComplexity.ENTERPRISE: 2.0
        }

        return base_time * complexity_factor.get(request.complexity, 1.0)

    async def log_routing_decision(self, request: AIRequest, decision: RoutingDecision):
        """Log routing decision for learning and optimization"""

        log_entry = {
            'timestamp': datetime.now(),
            'request_id': request.id,
            'selected_provider': decision.selected_provider.value,
            'confidence_score': decision.confidence_score,
            'estimated_cost': decision.estimated_cost,
            'estimated_time': decision.estimated_time,
            'complexity': request.complexity.value,
            'priority': request.priority
        }

        self.routing_history.append(log_entry)

        # Keep only recent history (last 1000 decisions)
        if len(self.routing_history) > 1000:
            self.routing_history = self.routing_history[-1000:]

# Supporting classes for routing
class CostTracker:
    def __init__(self):
        self.daily_costs = {}
        self.monthly_budgets = {}

class PerformancePredictor:
    def __init__(self):
        self.performance_history = {}

class QualityAssessor:
    def __init__(self):
        self.quality_metrics = {}
package com.boozer.nexus.ai.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozer.nexus.ai.integration.providers.*;
import com.boozer.nexus.ai.integration.routing.IntelligentRouter;
import com.boozer.nexus.ai.integration.cache.ResponseCache;
import com.boozer.nexus.ai.integration.models.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;

/**
 * External AI Integration Service - Production Implementation
 * 
 * This service orchestrates multiple AI providers (OpenAI, Anthropic, Google AI)
 * with intelligent routing, caching, fallback mechanisms, and comprehensive
 * monitoring capabilities.
 * 
 * Features:
 * - Multi-provider AI integration with automatic failover
 * - Intelligent request routing based on task complexity and provider capabilities
 * - Response caching with TTL for performance optimization
 * - Cost tracking and optimization across providers
 * - Real-time quality assessment and provider scoring
 * - Comprehensive audit logging and metrics collection
 */
@Service
public class ExternalAIIntegrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExternalAIIntegrationService.class);
    
    @Autowired
    private OpenAIProvider openAIProvider;
    
    @Autowired
    private AnthropicProvider anthropicProvider;
    
    @Autowired
    private GoogleAIProvider googleAIProvider;
    
    @Autowired
    private IntelligentRouter intelligentRouter;
    
    @Autowired
    private ResponseCache responseCache;
    
    @Value("${nexus.ai.fallback.enabled:true}")
    private boolean fallbackEnabled;
    
    @Value("${nexus.ai.cache.enabled:true}")
    private boolean cacheEnabled;
    
    @Value("${nexus.ai.timeout.seconds:30}")
    private int timeoutSeconds;
    
    private final Map<String, AIProvider> providers = new HashMap<>();
    private final Map<String, ProviderMetrics> providerMetrics = new HashMap<>();
    
    public void init() {
        // Initialize providers
        providers.put("openai", openAIProvider);
        providers.put("anthropic", anthropicProvider);
        providers.put("google", googleAIProvider);
        
        // Initialize metrics tracking
        providers.keySet().forEach(provider -> 
            providerMetrics.put(provider, new ProviderMetrics(provider)));
        
        logger.info("External AI Integration Service initialized with {} providers", providers.size());
    }
    
    /**
     * Process AI request with intelligent provider selection and fallback
     */
    public CompletableFuture<AIResponse> processRequest(AIRequest request) {
        String cacheKey = generateCacheKey(request);
        
        // Check cache first
        if (cacheEnabled) {
            AIResponse cachedResponse = responseCache.get(cacheKey);
            if (cachedResponse != null) {
                logger.debug("Cache hit for request: {}", request.getId());
                return CompletableFuture.completedFuture(cachedResponse);
            }
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Select optimal provider using intelligent routing
                String selectedProvider = intelligentRouter.selectProvider(request, providerMetrics);
                logger.info("Selected provider '{}' for request type: {}", selectedProvider, request.getType());
                
                // Process with primary provider
                AIResponse response = processWithProvider(selectedProvider, request);
                
                if (response.isSuccessful()) {
                    // Cache successful response
                    if (cacheEnabled) {
                        responseCache.put(cacheKey, response, request.getCacheTTL());
                    }
                    
                    // Update metrics
                    updateProviderMetrics(selectedProvider, response, true);
                    
                    return response;
                } else if (fallbackEnabled) {
                    // Try fallback providers
                    return processWithFallback(request, selectedProvider);
                } else {
                    throw new AIIntegrationException("Primary provider failed and fallback disabled", response.getError());
                }
                
            } catch (Exception e) {
                logger.error("AI request processing failed: {}", e.getMessage(), e);
                throw new AIIntegrationException("AI request processing failed", e);
            }
        }).orTimeout(timeoutSeconds, TimeUnit.SECONDS);
    }
    
    /**
     * Process request with specific provider
     */
    private AIResponse processWithProvider(String providerName, AIRequest request) {
        AIProvider provider = providers.get(providerName);
        if (provider == null) {
            throw new AIIntegrationException("Provider not found: " + providerName);
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            AIResponse response = provider.processRequest(request);
            long processingTime = System.currentTimeMillis() - startTime;
            
            response.setProvider(providerName);
            response.setProcessingTimeMs(processingTime);
            response.setTimestamp(LocalDateTime.now());
            
            logger.info("Provider '{}' processed request in {}ms", providerName, processingTime);
            return response;
            
        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            logger.error("Provider '{}' failed after {}ms: {}", providerName, processingTime, e.getMessage());
            
            AIResponse errorResponse = AIResponse.builder()
                .requestId(request.getId())
                .provider(providerName)
                .successful(false)
                .error(e.getMessage())
                .processingTimeMs(processingTime)
                .timestamp(LocalDateTime.now())
                .build();
                
            return errorResponse;
        }
    }
    
    /**
     * Process with fallback providers when primary fails
     */
    private AIResponse processWithFallback(AIRequest request, String failedProvider) {
        List<String> fallbackProviders = intelligentRouter.getFallbackProviders(failedProvider, request);
        
        for (String fallbackProvider : fallbackProviders) {
            logger.info("Attempting fallback with provider: {}", fallbackProvider);
            
            AIResponse response = processWithProvider(fallbackProvider, request);
            if (response.isSuccessful()) {
                updateProviderMetrics(fallbackProvider, response, true);
                
                // Mark original provider failure
                updateProviderMetrics(failedProvider, response, false);
                
                logger.info("Fallback successful with provider: {}", fallbackProvider);
                return response;
            }
        }
        
        throw new AIIntegrationException("All providers failed for request: " + request.getId());
    }
    
    /**
     * Batch processing for multiple requests
     */
    public CompletableFuture<List<AIResponse>> processBatchRequests(List<AIRequest> requests) {
        logger.info("Processing batch of {} requests", requests.size());
        
        List<CompletableFuture<AIResponse>> futures = requests.stream()
            .map(this::processRequest)
            .toList();
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .toList());
    }
    
    /**
     * Get real-time provider health status
     */
    public ProviderHealthStatus getProviderHealth() {
        Map<String, ProviderStatus> statusMap = new HashMap<>();
        
        providers.forEach((name, provider) -> {
            try {
                boolean isHealthy = provider.healthCheck();
                ProviderMetrics metrics = providerMetrics.get(name);
                
                statusMap.put(name, ProviderStatus.builder()
                    .name(name)
                    .healthy(isHealthy)
                    .successRate(metrics.getSuccessRate())
                    .averageResponseTime(metrics.getAverageResponseTime())
                    .totalRequests(metrics.getTotalRequests())
                    .lastChecked(LocalDateTime.now())
                    .build());
                    
            } catch (Exception e) {
                logger.warn("Health check failed for provider {}: {}", name, e.getMessage());
                statusMap.put(name, ProviderStatus.builder()
                    .name(name)
                    .healthy(false)
                    .error(e.getMessage())
                    .lastChecked(LocalDateTime.now())
                    .build());
            }
        });
        
        return ProviderHealthStatus.builder()
            .providers(statusMap)
            .timestamp(LocalDateTime.now())
            .overallHealthy(statusMap.values().stream().allMatch(ProviderStatus::isHealthy))
            .build();
    }
    
    /**
     * Get comprehensive usage statistics
     */
    public AIUsageStatistics getUsageStatistics(LocalDateTime since) {
        Map<String, ProviderUsageStats> providerStats = new HashMap<>();
        
        providerMetrics.forEach((name, metrics) -> {
            providerStats.put(name, ProviderUsageStats.builder()
                .provider(name)
                .totalRequests(metrics.getTotalRequests())
                .successfulRequests(metrics.getSuccessfulRequests())
                .failedRequests(metrics.getFailedRequests())
                .totalTokensUsed(metrics.getTotalTokensUsed())
                .totalCostUSD(metrics.getTotalCostUSD())
                .averageResponseTime(metrics.getAverageResponseTime())
                .successRate(metrics.getSuccessRate())
                .build());
        });
        
        return AIUsageStatistics.builder()
            .since(since)
            .generatedAt(LocalDateTime.now())
            .providerStats(providerStats)
            .totalRequests(providerStats.values().stream().mapToLong(ProviderUsageStats::getTotalRequests).sum())
            .totalCost(providerStats.values().stream().mapToDouble(ProviderUsageStats::getTotalCostUSD).sum())
            .overallSuccessRate(calculateOverallSuccessRate(providerStats.values()))
            .build();
    }
    
    /**
     * Optimize provider selection based on current performance
     */
    public void optimizeProviderSelection() {
        logger.info("Optimizing provider selection based on current metrics");
        intelligentRouter.updateProviderWeights(providerMetrics);
    }
    
    /**
     * Emergency failover - disable problematic provider
     */
    public void disableProvider(String providerName, String reason) {
        logger.warn("Disabling provider '{}': {}", providerName, reason);
        intelligentRouter.disableProvider(providerName);
        // TODO: Send alert notification
    }
    
    /**
     * Re-enable previously disabled provider
     */
    public void enableProvider(String providerName) {
        logger.info("Re-enabling provider: {}", providerName);
        intelligentRouter.enableProvider(providerName);
    }
    
    // Helper methods
    
    private String generateCacheKey(AIRequest request) {
        return String.format("ai_request_%s_%s_%d", 
            request.getType(), 
            Integer.toHexString(request.getContent().hashCode()),
            request.getParameters().hashCode());
    }
    
    private void updateProviderMetrics(String provider, AIResponse response, boolean success) {
        ProviderMetrics metrics = providerMetrics.get(provider);
        if (metrics != null) {
            metrics.recordRequest(response.getProcessingTimeMs(), success, 
                response.getTokensUsed(), response.getCostUSD());
        }
    }
    
    private double calculateOverallSuccessRate(Collection<ProviderUsageStats> stats) {
        long totalRequests = stats.stream().mapToLong(ProviderUsageStats::getTotalRequests).sum();
        long totalSuccessful = stats.stream().mapToLong(ProviderUsageStats::getSuccessfulRequests).sum();
        
        return totalRequests > 0 ? (double) totalSuccessful / totalRequests : 0.0;
    }
}

/**
 * Custom exception for AI integration failures
 */
class AIIntegrationException extends RuntimeException {
    public AIIntegrationException(String message) {
        super(message);
    }
    
    public AIIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
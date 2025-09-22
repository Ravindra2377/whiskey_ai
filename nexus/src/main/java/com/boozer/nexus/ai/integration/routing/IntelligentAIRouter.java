package com.boozer.nexus.ai.integration.routing;

import com.boozer.nexus.ai.integration.models.*;
import com.boozer.nexus.ai.integration.providers.AIProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Intelligent AI Router
 * 
 * Advanced routing system that automatically selects the optimal AI provider
 * based on request characteristics, provider capabilities, cost, and performance.
 */
@Component
public class IntelligentAIRouter {
    
    private static final Logger logger = LoggerFactory.getLogger(IntelligentAIRouter.class);
    
    @Autowired
    private List<AIProvider> providers;
    
    private final Map<String, ProviderPerformanceHistory> performanceHistory = new HashMap<>();
    private final Map<AIRequestType, List<String>> typePreferences = new HashMap<>();
    
    public IntelligentAIRouter() {
        initializeTypePreferences();
    }
    
    /**
     * Routes AI request to the most suitable provider based on multiple factors
     */
    public AIProvider selectProvider(AIRequest request) {
        logger.debug("Selecting provider for request type: {}", request.getType());
        
        // Get all available providers
        List<AIProvider> availableProviders = providers.stream()
            .filter(provider -> provider.healthCheck())
            .collect(Collectors.toList());
            
        if (availableProviders.isEmpty()) {
            throw new RuntimeException("No healthy AI providers available");
        }
        
        // Score each provider for this request
        Map<AIProvider, Double> providerScores = new HashMap<>();
        
        for (AIProvider provider : availableProviders) {
            double score = calculateProviderScore(provider, request);
            providerScores.put(provider, score);
            
            logger.debug("Provider {} scored {} for request type {}", 
                provider.getProviderName(), score, request.getType());
        }
        
        // Select provider with highest score
        AIProvider selectedProvider = providerScores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(availableProviders.get(0));
            
        logger.info("Selected provider {} for request type {} with score {}", 
            selectedProvider.getProviderName(), request.getType(), 
            providerScores.get(selectedProvider));
            
        return selectedProvider;
    }
    
    /**
     * Get fallback providers in order of preference
     */
    public List<AIProvider> getFallbackProviders(AIRequest request, AIProvider primaryProvider) {
        return providers.stream()
            .filter(provider -> !provider.equals(primaryProvider))
            .filter(provider -> provider.supportsRequestType(request.getType()))
            .filter(AIProvider::healthCheck)
            .sorted((p1, p2) -> Double.compare(
                calculateProviderScore(p2, request),
                calculateProviderScore(p1, request)
            ))
            .collect(Collectors.toList());
    }
    
    /**
     * Update provider performance metrics
     */
    public void updateProviderPerformance(String providerName, AIResponse response) {
        ProviderPerformanceHistory history = performanceHistory.computeIfAbsent(
            providerName, k -> new ProviderPerformanceHistory()
        );
        
        history.addResponse(response);
        
        logger.debug("Updated performance history for provider {}: success rate {}, avg latency {}ms",
            providerName, history.getSuccessRate(), history.getAverageLatency());
    }
    
    /**
     * Get provider recommendations for request type
     */
    public List<String> getProviderRecommendations(AIRequestType requestType) {
        List<String> typeBasedPreferences = typePreferences.get(requestType);
        if (typeBasedPreferences == null) {
            typeBasedPreferences = Arrays.asList("openai", "anthropic", "google");
        }
        
        // Filter by availability and sort by performance
        return typeBasedPreferences.stream()
            .filter(providerName -> providers.stream()
                .anyMatch(p -> p.getProviderName().equals(providerName) && p.healthCheck()))
            .sorted((p1, p2) -> {
                ProviderPerformanceHistory h1 = performanceHistory.get(p1);
                ProviderPerformanceHistory h2 = performanceHistory.get(p2);
                
                if (h1 == null && h2 == null) return 0;
                if (h1 == null) return 1;
                if (h2 == null) return -1;
                
                return Double.compare(h2.getOverallScore(), h1.getOverallScore());
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get routing analytics and statistics
     */
    public RoutingAnalytics getAnalytics() {
        Map<String, Integer> providerUsage = new HashMap<>();
        Map<String, Double> providerSuccessRates = new HashMap<>();
        Map<String, Double> providerAvgLatency = new HashMap<>();
        
        for (Map.Entry<String, ProviderPerformanceHistory> entry : performanceHistory.entrySet()) {
            String providerName = entry.getKey();
            ProviderPerformanceHistory history = entry.getValue();
            
            providerUsage.put(providerName, history.getTotalRequests());
            providerSuccessRates.put(providerName, history.getSuccessRate());
            providerAvgLatency.put(providerName, history.getAverageLatency());
        }
        
        return RoutingAnalytics.builder()
            .providerUsage(providerUsage)
            .providerSuccessRates(providerSuccessRates)
            .providerAverageLatency(providerAvgLatency)
            .totalRequests(performanceHistory.values().stream()
                .mapToInt(ProviderPerformanceHistory::getTotalRequests)
                .sum())
            .lastUpdated(LocalDateTime.now())
            .build();
    }
    
    // Private helper methods
    
    private double calculateProviderScore(AIProvider provider, AIRequest request) {
        double score = 0.0;
        
        // 1. Type compatibility (40% weight)
        if (provider.supportsRequestType(request.getType())) {
            score += 40.0;
            
            // Bonus for preferred providers for this type
            List<String> preferences = typePreferences.get(request.getType());
            if (preferences != null) {
                int index = preferences.indexOf(provider.getProviderName());
                if (index >= 0) {
                    score += (preferences.size() - index) * 5.0; // Bonus for higher preference
                }
            }
        } else {
            return 0.0; // Cannot handle this request type
        }
        
        // 2. Performance history (25% weight)
        ProviderPerformanceHistory history = performanceHistory.get(provider.getProviderName());
        if (history != null) {
            double performanceScore = history.getOverallScore() * 25.0;
            score += performanceScore;
        } else {
            score += 15.0; // Default score for new providers
        }
        
        // 3. Cost efficiency (15% weight)
        double estimatedCost = provider.estimateCost(request);
        double costScore = calculateCostScore(estimatedCost, request);
        score += costScore * 15.0;
        
        // 4. Capability match (10% weight)
        ProviderCapabilities capabilities = provider.getCapabilities();
        double capabilityScore = calculateCapabilityScore(capabilities, request);
        score += capabilityScore * 10.0;
        
        // 5. Current load/health (10% weight)
        boolean healthy = provider.healthCheck();
        score += healthy ? 10.0 : 0.0;
        
        return score;
    }
    
    private double calculateCostScore(double estimatedCost, AIRequest request) {
        // Convert cost to score (lower cost = higher score)
        if (estimatedCost <= 0.001) return 1.0;      // Very cheap
        if (estimatedCost <= 0.01) return 0.8;       // Cheap
        if (estimatedCost <= 0.05) return 0.6;       // Moderate
        if (estimatedCost <= 0.10) return 0.4;       // Expensive
        return 0.2; // Very expensive
    }
    
    private double calculateCapabilityScore(ProviderCapabilities capabilities, AIRequest request) {
        double score = 0.0;
        
        // Check specific capabilities needed for this request
        if (request.getParameters().containsKey("functions") && capabilities.isSupportsFunctionCalling()) {
            score += 0.3;
        }
        
        if (request.getParameters().containsKey("media") && capabilities.isSupportsImageInput()) {
            score += 0.3;
        }
        
        if (request.getMaxTokens() > 4000 && capabilities.getMaxTokens() >= request.getMaxTokens()) {
            score += 0.2;
        }
        
        if (request.getParameters().containsKey("stream") && capabilities.isSupportsStreaming()) {
            score += 0.2;
        }
        
        return Math.min(1.0, score);
    }
    
    private void initializeTypePreferences() {
        // Define provider preferences for each request type based on strengths
        
        // OpenAI excels at code and reasoning
        typePreferences.put(AIRequestType.CODE_GENERATION, Arrays.asList("openai", "anthropic", "google"));
        typePreferences.put(AIRequestType.CODE_REVIEW, Arrays.asList("openai", "anthropic", "google"));
        typePreferences.put(AIRequestType.FUNCTION_CALLING, Arrays.asList("openai", "google", "anthropic"));
        typePreferences.put(AIRequestType.REASONING, Arrays.asList("openai", "anthropic", "google"));
        
        // Anthropic excels at analysis and safety
        typePreferences.put(AIRequestType.ANALYSIS, Arrays.asList("anthropic", "openai", "google"));
        typePreferences.put(AIRequestType.DOCUMENT_ANALYSIS, Arrays.asList("anthropic", "google", "openai"));
        typePreferences.put(AIRequestType.ETHICAL_REVIEW, Arrays.asList("anthropic", "openai", "google"));
        typePreferences.put(AIRequestType.CONTENT_MODERATION, Arrays.asList("anthropic", "openai", "google"));
        
        // Google excels at multimodal and search
        typePreferences.put(AIRequestType.IMAGE_ANALYSIS, Arrays.asList("google", "openai", "anthropic"));
        typePreferences.put(AIRequestType.MULTIMODAL, Arrays.asList("google", "openai", "anthropic"));
        typePreferences.put(AIRequestType.SEARCH_QUERY, Arrays.asList("google", "openai", "anthropic"));
        typePreferences.put(AIRequestType.FACT_CHECKING, Arrays.asList("google", "anthropic", "openai"));
        
        // Balanced for general tasks
        typePreferences.put(AIRequestType.TEXT_GENERATION, Arrays.asList("openai", "anthropic", "google"));
        typePreferences.put(AIRequestType.CHAT_COMPLETION, Arrays.asList("openai", "anthropic", "google"));
        typePreferences.put(AIRequestType.QUESTION_ANSWERING, Arrays.asList("anthropic", "openai", "google"));
        typePreferences.put(AIRequestType.SUMMARIZATION, Arrays.asList("anthropic", "openai", "google"));
        typePreferences.put(AIRequestType.TRANSLATION, Arrays.asList("google", "openai", "anthropic"));
        typePreferences.put(AIRequestType.CREATIVE_WRITING, Arrays.asList("anthropic", "openai", "google"));
        typePreferences.put(AIRequestType.TECHNICAL_DOCUMENTATION, Arrays.asList("anthropic", "openai", "google"));
        typePreferences.put(AIRequestType.DATA_EXTRACTION, Arrays.asList("openai", "anthropic", "google"));
        typePreferences.put(AIRequestType.CLASSIFICATION, Arrays.asList("openai", "google", "anthropic"));
    }
    
    /**
     * Provider Performance History tracker
     */
    public static class ProviderPerformanceHistory {
        private final List<ResponseMetrics> recentResponses = new ArrayList<>();
        private static final int MAX_HISTORY_SIZE = 100;
        
        public void addResponse(AIResponse response) {
            ResponseMetrics metrics = ResponseMetrics.builder()
                .successful(response.isSuccessful())
                .latency(response.getProcessingTimeMs())
                .qualityScore(response.getQualityScore())
                .timestamp(response.getTimestamp())
                .build();
                
            recentResponses.add(metrics);
            
            // Keep only recent responses
            if (recentResponses.size() > MAX_HISTORY_SIZE) {
                recentResponses.remove(0);
            }
        }
        
        public double getSuccessRate() {
            if (recentResponses.isEmpty()) return 0.0;
            
            long successful = recentResponses.stream()
                .mapToLong(r -> r.isSuccessful() ? 1 : 0)
                .sum();
                
            return (double) successful / recentResponses.size();
        }
        
        public double getAverageLatency() {
            return recentResponses.stream()
                .mapToLong(ResponseMetrics::getLatency)
                .average()
                .orElse(0.0);
        }
        
        public double getAverageQuality() {
            return recentResponses.stream()
                .mapToDouble(ResponseMetrics::getQualityScore)
                .average()
                .orElse(0.0);
        }
        
        public double getOverallScore() {
            double successRate = getSuccessRate();
            double qualityScore = getAverageQuality();
            double latencyScore = Math.max(0.0, 1.0 - (getAverageLatency() / 10000.0)); // 10s = 0 score
            
            return (successRate * 0.5) + (qualityScore * 0.3) + (latencyScore * 0.2);
        }
        
        public int getTotalRequests() {
            return recentResponses.size();
        }
    }
    
    /**
     * Individual response metrics
     */
    public static class ResponseMetrics {
        private final boolean successful;
        private final long latency;
        private final double qualityScore;
        private final LocalDateTime timestamp;
        
        private ResponseMetrics(boolean successful, long latency, double qualityScore, LocalDateTime timestamp) {
            this.successful = successful;
            this.latency = latency;
            this.qualityScore = qualityScore;
            this.timestamp = timestamp;
        }
        
        public static ResponseMetricsBuilder builder() {
            return new ResponseMetricsBuilder();
        }
        
        // Getters
        public boolean isSuccessful() { return successful; }
        public long getLatency() { return latency; }
        public double getQualityScore() { return qualityScore; }
        public LocalDateTime getTimestamp() { return timestamp; }
        
        public static class ResponseMetricsBuilder {
            private boolean successful;
            private long latency;
            private double qualityScore;
            private LocalDateTime timestamp;
            
            public ResponseMetricsBuilder successful(boolean successful) {
                this.successful = successful;
                return this;
            }
            
            public ResponseMetricsBuilder latency(long latency) {
                this.latency = latency;
                return this;
            }
            
            public ResponseMetricsBuilder qualityScore(double qualityScore) {
                this.qualityScore = qualityScore;
                return this;
            }
            
            public ResponseMetricsBuilder timestamp(LocalDateTime timestamp) {
                this.timestamp = timestamp;
                return this;
            }
            
            public ResponseMetrics build() {
                return new ResponseMetrics(successful, latency, qualityScore, timestamp);
            }
        }
    }
    
    /**
     * Routing analytics data
     */
    public static class RoutingAnalytics {
        private final Map<String, Integer> providerUsage;
        private final Map<String, Double> providerSuccessRates;
        private final Map<String, Double> providerAverageLatency;
        private final int totalRequests;
        private final LocalDateTime lastUpdated;
        
        private RoutingAnalytics(Map<String, Integer> providerUsage, 
                               Map<String, Double> providerSuccessRates,
                               Map<String, Double> providerAverageLatency,
                               int totalRequests, 
                               LocalDateTime lastUpdated) {
            this.providerUsage = providerUsage;
            this.providerSuccessRates = providerSuccessRates;
            this.providerAverageLatency = providerAverageLatency;
            this.totalRequests = totalRequests;
            this.lastUpdated = lastUpdated;
        }
        
        public static RoutingAnalyticsBuilder builder() {
            return new RoutingAnalyticsBuilder();
        }
        
        // Getters
        public Map<String, Integer> getProviderUsage() { return providerUsage; }
        public Map<String, Double> getProviderSuccessRates() { return providerSuccessRates; }
        public Map<String, Double> getProviderAverageLatency() { return providerAverageLatency; }
        public int getTotalRequests() { return totalRequests; }
        public LocalDateTime getLastUpdated() { return lastUpdated; }
        
        public static class RoutingAnalyticsBuilder {
            private Map<String, Integer> providerUsage;
            private Map<String, Double> providerSuccessRates;
            private Map<String, Double> providerAverageLatency;
            private int totalRequests;
            private LocalDateTime lastUpdated;
            
            public RoutingAnalyticsBuilder providerUsage(Map<String, Integer> providerUsage) {
                this.providerUsage = providerUsage;
                return this;
            }
            
            public RoutingAnalyticsBuilder providerSuccessRates(Map<String, Double> providerSuccessRates) {
                this.providerSuccessRates = providerSuccessRates;
                return this;
            }
            
            public RoutingAnalyticsBuilder providerAverageLatency(Map<String, Double> providerAverageLatency) {
                this.providerAverageLatency = providerAverageLatency;
                return this;
            }
            
            public RoutingAnalyticsBuilder totalRequests(int totalRequests) {
                this.totalRequests = totalRequests;
                return this;
            }
            
            public RoutingAnalyticsBuilder lastUpdated(LocalDateTime lastUpdated) {
                this.lastUpdated = lastUpdated;
                return this;
            }
            
            public RoutingAnalytics build() {
                return new RoutingAnalytics(providerUsage, providerSuccessRates, 
                                          providerAverageLatency, totalRequests, lastUpdated);
            }
        }
    }
}
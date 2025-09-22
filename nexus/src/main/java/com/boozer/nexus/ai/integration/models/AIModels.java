package com.boozer.nexus.ai.integration.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

/**
 * Core AI integration models for the External AI Integration Service
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIRequest {
    private String id;
    private AIRequestType type;
    private String content;
    private Map<String, Object> parameters;
    private String userId;
    private int priority; // 1-10, higher = more urgent
    private int maxTokens;
    private double temperature;
    private String model;
    private long cacheTTL; // Cache time-to-live in seconds
    private LocalDateTime timestamp;
    private Map<String, String> metadata;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIResponse {
    private String requestId;
    private String provider;
    private boolean successful;
    private String content;
    private String error;
    private int tokensUsed;
    private double costUSD;
    private long processingTimeMs;
    private double qualityScore; // 0.0-1.0
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
    private List<String> warnings;
}

public enum AIRequestType {
    TEXT_GENERATION("text_generation", "General text generation"),
    CODE_GENERATION("code_generation", "Code generation and completion"),
    CODE_REVIEW("code_review", "Code review and analysis"),
    QUESTION_ANSWERING("question_answering", "Question answering"),
    SUMMARIZATION("summarization", "Text summarization"),
    TRANSLATION("translation", "Language translation"),
    ANALYSIS("analysis", "Content analysis"),
    CHAT_COMPLETION("chat_completion", "Chat conversation"),
    EMBEDDINGS("embeddings", "Vector embeddings generation"),
    IMAGE_ANALYSIS("image_analysis", "Image content analysis"),
    FUNCTION_CALLING("function_calling", "Function/tool calling"),
    REASONING("reasoning", "Complex reasoning tasks"),
    CREATIVE_WRITING("creative_writing", "Creative content generation"),
    TECHNICAL_DOCUMENTATION("technical_docs", "Technical documentation"),
    DATA_EXTRACTION("data_extraction", "Structured data extraction"),
    SENTIMENT_ANALYSIS("sentiment", "Sentiment analysis"),
    CLASSIFICATION("classification", "Content classification"),
    OPTIMIZATION("optimization", "Code/process optimization");
    
    private final String code;
    private final String description;
    
    AIRequestType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() { return code; }
    public String getDescription() { return description; }
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderMetrics {
    private String providerName;
    private long totalRequests;
    private long successfulRequests;
    private long failedRequests;
    private double averageResponseTime;
    private long totalTokensUsed;
    private double totalCostUSD;
    private double successRate;
    private LocalDateTime lastUpdated;
    private Map<AIRequestType, ProviderTypeMetrics> typeMetrics;
    
    public ProviderMetrics(String providerName) {
        this.providerName = providerName;
        this.totalRequests = 0;
        this.successfulRequests = 0;
        this.failedRequests = 0;
        this.averageResponseTime = 0.0;
        this.totalTokensUsed = 0;
        this.totalCostUSD = 0.0;
        this.successRate = 0.0;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public synchronized void recordRequest(long responseTime, boolean success, int tokens, double cost) {
        totalRequests++;
        if (success) {
            successfulRequests++;
        } else {
            failedRequests++;
        }
        
        // Update moving average for response time
        averageResponseTime = (averageResponseTime * (totalRequests - 1) + responseTime) / totalRequests;
        
        totalTokensUsed += tokens;
        totalCostUSD += cost;
        successRate = (double) successfulRequests / totalRequests;
        lastUpdated = LocalDateTime.now();
    }
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderTypeMetrics {
    private AIRequestType requestType;
    private long requests;
    private double averageResponseTime;
    private double successRate;
    private double averageCost;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderStatus {
    private String name;
    private boolean healthy;
    private double successRate;
    private double averageResponseTime;
    private long totalRequests;
    private String error;
    private LocalDateTime lastChecked;
    private Map<String, Object> additionalInfo;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderHealthStatus {
    private Map<String, ProviderStatus> providers;
    private boolean overallHealthy;
    private LocalDateTime timestamp;
    private String summary;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderUsageStats {
    private String provider;
    private long totalRequests;
    private long successfulRequests;
    private long failedRequests;
    private long totalTokensUsed;
    private double totalCostUSD;
    private double averageResponseTime;
    private double successRate;
    private Map<AIRequestType, Long> requestsByType;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIUsageStatistics {
    private LocalDateTime since;
    private LocalDateTime generatedAt;
    private Map<String, ProviderUsageStats> providerStats;
    private long totalRequests;
    private double totalCost;
    private double overallSuccessRate;
    private Map<AIRequestType, Long> requestDistribution;
    private List<String> insights;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderCapabilities {
    private String providerName;
    private List<AIRequestType> supportedTypes;
    private Map<String, String> supportedModels;
    private int maxTokens;
    private boolean supportsStreaming;
    private boolean supportsFunctionCalling;
    private boolean supportsImageInput;
    private double costPerToken;
    private double averageLatency;
    private Map<String, Object> limitations;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouterDecision {
    private String selectedProvider;
    private String reason;
    private double confidence;
    private List<String> alternativeProviders;
    private Map<String, Double> providerScores;
    private LocalDateTime timestamp;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityAssessment {
    private double relevanceScore;
    private double accuracyScore;
    private double completenessScore;
    private double coherenceScore;
    private double overallQuality;
    private List<String> issues;
    private List<String> strengths;
    private String assessmentMethod;
}

/**
 * Provider Selection Criteria for intelligent routing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderSelectionCriteria {
    private double costWeight;
    private double speedWeight;
    private double qualityWeight;
    private double reliabilityWeight;
    private boolean requiresSpecificCapability;
    private String requiredCapability;
    private int maxAcceptableLatency;
    private double maxAcceptableCost;
    private double minQualityThreshold;
}
package com.boozer.nexus.ai.integration.providers;

import com.boozer.nexus.ai.integration.models.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Anthropic Claude Provider Implementation
 * 
 * Production-ready implementation for Anthropic Claude models including:
 * - Claude-3 (Opus, Sonnet, Haiku) integration
 * - Constitutional AI principles
 * - Long context handling
 * - Safety and alignment features
 */
@Component
public class AnthropicProvider implements AIProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(AnthropicProvider.class);
    private static final String PROVIDER_NAME = "anthropic";
    
    @Value("${nexus.ai.providers.anthropic.api-key}")
    private String apiKey;
    
    @Value("${nexus.ai.providers.anthropic.base-url:https://api.anthropic.com/v1}")
    private String baseUrl;
    
    @Value("${nexus.ai.providers.anthropic.default-model:claude-3-sonnet-20240229}")
    private String defaultModel;
    
    @Value("${nexus.ai.providers.anthropic.timeout:45000}")
    private int timeoutMs;
    
    private final RestTemplate restTemplate;
    private final Map<String, Double> modelCosts;
    private final Map<String, Integer> modelMaxTokens;
    private long totalRequests = 0;
    private long successfulRequests = 0;
    
    public AnthropicProvider() {
        this.restTemplate = new RestTemplate();
        this.modelCosts = initializeModelCosts();
        this.modelMaxTokens = initializeMaxTokens();
    }
    
    @Override
    public AIResponse processRequest(AIRequest request) throws AIProviderException {
        validateRequest(request);
        
        try {
            totalRequests++;
            
            // Prepare Anthropic API request
            Map<String, Object> anthropicRequest = buildAnthropicRequest(request);
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", "2023-06-01");
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(anthropicRequest, headers);
            
            // Make API call
            long startTime = System.currentTimeMillis();
            ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/messages", 
                entity, 
                Map.class
            );
            long processingTime = System.currentTimeMillis() - startTime;
            
            // Process successful response
            AIResponse aiResponse = processSuccessfulResponse(response, request, processingTime);
            successfulRequests++;
            
            logger.info("Anthropic request processed successfully in {}ms", processingTime);
            return aiResponse;
            
        } catch (HttpClientErrorException e) {
            logger.error("Anthropic API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new AIProviderException(PROVIDER_NAME, 
                "Anthropic API error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(),
                e.getStatusCode().value());
                
        } catch (Exception e) {
            logger.error("Anthropic request failed: {}", e.getMessage(), e);
            throw new AIProviderException(PROVIDER_NAME, "Request processing failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean healthCheck() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", "2023-06-01");
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            // Simple test request
            Map<String, Object> testRequest = Map.of(
                "model", defaultModel,
                "max_tokens", 1,
                "messages", List.of(Map.of("role", "user", "content", "Hi"))
            );
            
            HttpEntity<Map<String, Object>> testEntity = new HttpEntity<>(testRequest, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/messages",
                testEntity,
                Map.class
            );
            
            return response.getStatusCode().is2xxSuccessful();
            
        } catch (Exception e) {
            logger.warn("Anthropic health check failed: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public ProviderCapabilities getCapabilities() {
        return ProviderCapabilities.builder()
            .providerName(PROVIDER_NAME)
            .supportedTypes(Arrays.asList(
                AIRequestType.TEXT_GENERATION,
                AIRequestType.CODE_GENERATION,
                AIRequestType.CODE_REVIEW,
                AIRequestType.QUESTION_ANSWERING,
                AIRequestType.SUMMARIZATION,
                AIRequestType.TRANSLATION,
                AIRequestType.ANALYSIS,
                AIRequestType.CHAT_COMPLETION,
                AIRequestType.REASONING,
                AIRequestType.CREATIVE_WRITING,
                AIRequestType.TECHNICAL_DOCUMENTATION,
                AIRequestType.DATA_EXTRACTION,
                AIRequestType.CLASSIFICATION
            ))
            .supportedModels(Map.of(
                "claude-3-opus-20240229", "Most powerful model for complex tasks",
                "claude-3-sonnet-20240229", "Balanced performance and speed",
                "claude-3-haiku-20240307", "Fastest model for simple tasks",
                "claude-2.1", "Previous generation with 200k context",
                "claude-instant-1.2", "Fast and efficient for most tasks"
            ))
            .maxTokens(200000) // Claude-3 supports very long contexts
            .supportsStreaming(true)
            .supportsFunctionCalling(false) // Claude uses different approach
            .supportsImageInput(true) // Claude-3 supports vision
            .costPerToken(0.000015) // Average estimate
            .averageLatency(3000) // 3 seconds average
            .limitations(Map.of(
                "function_calling", "Uses different tool calling format",
                "image_types", "Supports PNG, JPEG, GIF, WebP",
                "max_image_size", "5MB per image"
            ))
            .build();
    }
    
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
    
    @Override
    public double estimateCost(AIRequest request) {
        String model = (String) request.getParameters().getOrDefault("model", defaultModel);
        Double costPerToken = modelCosts.get(model);
        
        if (costPerToken == null) {
            costPerToken = 0.000015; // Default estimate
        }
        
        int estimatedTokens = estimateTokens(request.getContent()) + request.getMaxTokens();
        return estimatedTokens * costPerToken;
    }
    
    @Override
    public boolean supportsRequestType(AIRequestType requestType) {
        return getCapabilities().getSupportedTypes().contains(requestType);
    }
    
    @Override
    public ProviderStatus getStatus() {
        double successRate = totalRequests > 0 ? (double) successfulRequests / totalRequests : 0.0;
        
        return ProviderStatus.builder()
            .name(PROVIDER_NAME)
            .healthy(healthCheck())
            .successRate(successRate)
            .totalRequests(totalRequests)
            .lastChecked(LocalDateTime.now())
            .additionalInfo(Map.of(
                "default_model", defaultModel,
                "base_url", baseUrl,
                "api_key_configured", apiKey != null && !apiKey.isEmpty(),
                "constitutional_ai", "true",
                "long_context_support", "200k tokens"
            ))
            .build();
    }
    
    // Helper methods
    
    private void validateRequest(AIRequest request) throws AIProviderException {
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new AIProviderException(PROVIDER_NAME, "Request content cannot be empty");
        }
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new AIProviderException(PROVIDER_NAME, "Anthropic API key not configured");
        }
        
        // Check token limits
        String model = (String) request.getParameters().getOrDefault("model", defaultModel);
        Integer maxTokens = modelMaxTokens.get(model);
        if (maxTokens != null && request.getMaxTokens() > maxTokens) {
            throw new AIProviderException(PROVIDER_NAME, 
                String.format("Requested tokens (%d) exceed model limit (%d) for %s", 
                    request.getMaxTokens(), maxTokens, model));
        }
    }
    
    private Map<String, Object> buildAnthropicRequest(AIRequest request) {
        Map<String, Object> anthropicRequest = new HashMap<>();
        
        // Model selection
        String model = (String) request.getParameters().getOrDefault("model", defaultModel);
        anthropicRequest.put("model", model);
        
        // Messages format (Anthropic uses different format than OpenAI)
        List<Map<String, String>> messages = new ArrayList<>();
        
        // System message handling (Anthropic separates system from messages)
        String systemMessage = (String) request.getParameters().get("system_message");
        if (systemMessage != null) {
            anthropicRequest.put("system", systemMessage);
        }
        
        // User message
        messages.add(Map.of("role", "user", "content", request.getContent()));
        anthropicRequest.put("messages", messages);
        
        // Parameters
        int maxTokens = request.getMaxTokens() > 0 ? request.getMaxTokens() : 1000;
        anthropicRequest.put("max_tokens", Math.min(maxTokens, 4000)); // Claude has different token limits
        
        // Temperature (Claude uses 0.0-1.0 range)
        double temperature = request.getTemperature() >= 0 ? request.getTemperature() : 0.7;
        anthropicRequest.put("temperature", Math.max(0.0, Math.min(1.0, temperature)));
        
        // Top-p parameter
        if (request.getParameters().containsKey("top_p")) {
            double topP = ((Number) request.getParameters().get("top_p")).doubleValue();
            anthropicRequest.put("top_p", Math.max(0.0, Math.min(1.0, topP)));
        }
        
        // Top-k parameter (Anthropic-specific)
        if (request.getParameters().containsKey("top_k")) {
            anthropicRequest.put("top_k", request.getParameters().get("top_k"));
        }
        
        // Stream parameter
        boolean stream = (Boolean) request.getParameters().getOrDefault("stream", false);
        anthropicRequest.put("stream", stream);
        
        return anthropicRequest;
    }
    
    private AIResponse processSuccessfulResponse(ResponseEntity<Map> response, AIRequest request, long processingTime) {
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = response.getBody();
        
        if (responseBody == null) {
            throw new RuntimeException("Empty response from Anthropic API");
        }
        
        // Extract content from Anthropic response format
        String content = "";
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> contentArray = (List<Map<String, Object>>) responseBody.get("content");
        
        if (contentArray != null && !contentArray.isEmpty()) {
            Map<String, Object> contentBlock = contentArray.get(0);
            if ("text".equals(contentBlock.get("type"))) {
                content = (String) contentBlock.get("text");
            }
        }
        
        // Extract usage information
        @SuppressWarnings("unchecked")
        Map<String, Object> usage = (Map<String, Object>) responseBody.get("usage");
        int tokensUsed = 0;
        double cost = 0.0;
        
        if (usage != null) {
            Integer inputTokens = (Integer) usage.get("input_tokens");
            Integer outputTokens = (Integer) usage.get("output_tokens");
            
            if (inputTokens != null && outputTokens != null) {
                tokensUsed = inputTokens + outputTokens;
                
                // Calculate cost (Anthropic has different pricing for input/output)
                String model = (String) request.getParameters().getOrDefault("model", defaultModel);
                cost = calculateAnthropicCost(model, inputTokens, outputTokens);
            }
        }
        
        // Safety assessment
        List<String> warnings = extractSafetyWarnings(responseBody);
        
        return AIResponse.builder()
            .requestId(request.getId())
            .provider(PROVIDER_NAME)
            .successful(true)
            .content(content)
            .tokensUsed(tokensUsed)
            .costUSD(cost)
            .processingTimeMs(processingTime)
            .qualityScore(calculateQualityScore(content, request))
            .timestamp(LocalDateTime.now())
            .metadata(Map.of(
                "model", request.getParameters().getOrDefault("model", defaultModel),
                "anthropic_response_id", responseBody.getOrDefault("id", "unknown"),
                "stop_reason", responseBody.getOrDefault("stop_reason", "unknown")
            ))
            .warnings(warnings)
            .build();
    }
    
    private Map<String, Double> initializeModelCosts() {
        Map<String, Double> costs = new HashMap<>();
        
        // Anthropic pricing (input/output per token) as of 2024
        // Using average of input/output costs for simplicity
        costs.put("claude-3-opus-20240229", 0.000075);     // $75/$225 per 1M tokens
        costs.put("claude-3-sonnet-20240229", 0.000015);   // $15/$75 per 1M tokens
        costs.put("claude-3-haiku-20240307", 0.000004);    // $0.25/$1.25 per 1M tokens
        costs.put("claude-2.1", 0.000024);                 // $24/$72 per 1M tokens
        costs.put("claude-instant-1.2", 0.000004);         // $1.63/$5.51 per 1M tokens
        
        return costs;
    }
    
    private Map<String, Integer> initializeMaxTokens() {
        Map<String, Integer> maxTokens = new HashMap<>();
        
        maxTokens.put("claude-3-opus-20240229", 4000);
        maxTokens.put("claude-3-sonnet-20240229", 4000);
        maxTokens.put("claude-3-haiku-20240307", 4000);
        maxTokens.put("claude-2.1", 4000);
        maxTokens.put("claude-instant-1.2", 4000);
        
        return maxTokens;
    }
    
    private double calculateAnthropicCost(String model, int inputTokens, int outputTokens) {
        // Anthropic has different pricing for input vs output tokens
        Map<String, Double[]> detailedCosts = Map.of(
            "claude-3-opus-20240229", new Double[]{0.000015, 0.000075},      // input, output
            "claude-3-sonnet-20240229", new Double[]{0.000003, 0.000015},
            "claude-3-haiku-20240307", new Double[]{0.00000025, 0.00000125},
            "claude-2.1", new Double[]{0.000008, 0.000024},
            "claude-instant-1.2", new Double[]{0.00000163, 0.00000551}
        );
        
        Double[] costs = detailedCosts.get(model);
        if (costs != null) {
            return (inputTokens * costs[0]) + (outputTokens * costs[1]);
        }
        
        // Fallback to average cost
        Double averageCost = modelCosts.get(model);
        return averageCost != null ? (inputTokens + outputTokens) * averageCost : 0.0;
    }
    
    private List<String> extractSafetyWarnings(Map<String, Object> responseBody) {
        List<String> warnings = new ArrayList<>();
        
        // Check for safety-related fields in response
        Object stopReason = responseBody.get("stop_reason");
        if ("max_tokens".equals(stopReason)) {
            warnings.add("Response was truncated due to token limit");
        }
        
        // Anthropic doesn't typically expose safety flags in API response,
        // but we can add heuristic checks
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> content = (List<Map<String, Object>>) responseBody.get("content");
        if (content != null && !content.isEmpty()) {
            String text = (String) content.get(0).get("text");
            if (text != null && text.contains("I can't") || text.contains("I cannot")) {
                warnings.add("Response may contain safety-related limitations");
            }
        }
        
        return warnings;
    }
    
    private int estimateTokens(String text) {
        // Anthropic uses similar tokenization to GPT models
        if (text == null) return 0;
        return (int) Math.ceil(text.split("\\s+").length / 0.75);
    }
    
    private double calculateQualityScore(String content, AIRequest request) {
        if (content == null || content.trim().isEmpty()) {
            return 0.0;
        }
        
        double score = 0.6; // Base score (Claude typically produces high-quality outputs)
        
        // Length appropriateness
        int contentLength = content.length();
        if (contentLength > 50 && contentLength < 10000) {
            score += 0.2;
        }
        
        // Completeness check
        if (!content.trim().endsWith("...") && !content.contains("[incomplete]")) {
            score += 0.1;
        }
        
        // Constitutional AI characteristics (well-structured responses)
        if (content.contains("\n\n") || content.matches(".*\\d+\\..*")) {
            score += 0.1; // Well-structured content
        }
        
        return Math.min(1.0, score);
    }
}
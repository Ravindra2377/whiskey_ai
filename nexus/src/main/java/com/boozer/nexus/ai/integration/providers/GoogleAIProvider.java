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
 * Google AI (Gemini) Provider Implementation
 * 
 * Production-ready implementation for Google's Gemini models including:
 * - Gemini Pro and Ultra integration
 * - Multimodal capabilities (text, images, video)
 * - Function calling support
 * - Safety settings and content filtering
 */
@Component
public class GoogleAIProvider implements AIProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(GoogleAIProvider.class);
    private static final String PROVIDER_NAME = "google";
    
    @Value("${nexus.ai.providers.google.api-key}")
    private String apiKey;
    
    @Value("${nexus.ai.providers.google.base-url:https://generativelanguage.googleapis.com/v1beta}")
    private String baseUrl;
    
    @Value("${nexus.ai.providers.google.default-model:gemini-1.5-pro}")
    private String defaultModel;
    
    @Value("${nexus.ai.providers.google.timeout:45000}")
    private int timeoutMs;
    
    private final RestTemplate restTemplate;
    private final Map<String, Double> modelCosts;
    private long totalRequests = 0;
    private long successfulRequests = 0;
    
    public GoogleAIProvider() {
        this.restTemplate = new RestTemplate();
        this.modelCosts = initializeModelCosts();
    }
    
    @Override
    public AIResponse processRequest(AIRequest request) throws AIProviderException {
        validateRequest(request);
        
        try {
            totalRequests++;
            
            // Prepare Google AI request
            Map<String, Object> googleRequest = buildGoogleRequest(request);
            String model = (String) request.getParameters().getOrDefault("model", defaultModel);
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", apiKey);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(googleRequest, headers);
            
            // Build URL with model
            String url = String.format("%s/models/%s:generateContent", baseUrl, model);
            
            // Make API call
            long startTime = System.currentTimeMillis();
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            long processingTime = System.currentTimeMillis() - startTime;
            
            // Process successful response
            AIResponse aiResponse = processSuccessfulResponse(response, request, processingTime);
            successfulRequests++;
            
            logger.info("Google AI request processed successfully in {}ms", processingTime);
            return aiResponse;
            
        } catch (HttpClientErrorException e) {
            logger.error("Google AI API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new AIProviderException(PROVIDER_NAME, 
                "Google AI API error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(),
                e.getStatusCode().value());
                
        } catch (Exception e) {
            logger.error("Google AI request failed: {}", e.getMessage(), e);
            throw new AIProviderException(PROVIDER_NAME, "Request processing failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean healthCheck() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-goog-api-key", apiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            // List models endpoint for health check
            String url = baseUrl + "/models";
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            
            return response.getStatusCode().is2xxSuccessful();
            
        } catch (Exception e) {
            logger.warn("Google AI health check failed: {}", e.getMessage());
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
                AIRequestType.IMAGE_ANALYSIS,
                AIRequestType.FUNCTION_CALLING,
                AIRequestType.REASONING,
                AIRequestType.CREATIVE_WRITING,
                AIRequestType.TECHNICAL_DOCUMENTATION,
                AIRequestType.DATA_EXTRACTION,
                AIRequestType.CLASSIFICATION
            ))
            .supportedModels(Map.of(
                "gemini-1.5-pro", "Most capable multimodal model",
                "gemini-1.5-flash", "Faster version with good performance",
                "gemini-pro", "Text-only high-performance model",
                "gemini-pro-vision", "Multimodal model with vision capabilities",
                "gemini-ultra", "Most powerful model (limited access)"
            ))
            .maxTokens(1000000) // Gemini supports very long contexts
            .supportsStreaming(true)
            .supportsFunctionCalling(true)
            .supportsImageInput(true)
            .costPerToken(0.000001) // Very competitive pricing
            .averageLatency(2500) // 2.5 seconds average
            .limitations(Map.of(
                "image_formats", "PNG, JPEG, WebP, HEIC, HEIF",
                "video_formats", "MP4, MOV, AVI, FLV, MPG, MPEG, MPEGPS, WMV, 3GPP",
                "max_video_length", "10 minutes"
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
            costPerToken = 0.000001; // Default estimate - Google is very competitive
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
                "multimodal_support", "true",
                "long_context_support", "1M tokens"
            ))
            .build();
    }
    
    // Helper methods
    
    private void validateRequest(AIRequest request) throws AIProviderException {
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new AIProviderException(PROVIDER_NAME, "Request content cannot be empty");
        }
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new AIProviderException(PROVIDER_NAME, "Google AI API key not configured");
        }
    }
    
    private Map<String, Object> buildGoogleRequest(AIRequest request) {
        Map<String, Object> googleRequest = new HashMap<>();
        
        // Contents array for Gemini
        List<Map<String, Object>> contents = new ArrayList<>();
        
        // System instruction (if provided)
        String systemMessage = (String) request.getParameters().get("system_message");
        if (systemMessage != null) {
            googleRequest.put("systemInstruction", Map.of(
                "parts", List.of(Map.of("text", systemMessage))
            ));
        }
        
        // User content
        Map<String, Object> userContent = new HashMap<>();
        userContent.put("role", "user");
        
        List<Map<String, Object>> parts = new ArrayList<>();
        parts.add(Map.of("text", request.getContent()));
        
        // Handle multimodal content (images, etc.)
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> media = (List<Map<String, Object>>) request.getParameters().get("media");
        if (media != null) {
            parts.addAll(media);
        }
        
        userContent.put("parts", parts);
        contents.add(userContent);
        googleRequest.put("contents", contents);
        
        // Generation configuration
        Map<String, Object> generationConfig = new HashMap<>();
        
        if (request.getMaxTokens() > 0) {
            generationConfig.put("maxOutputTokens", request.getMaxTokens());
        }
        
        if (request.getTemperature() >= 0) {
            generationConfig.put("temperature", Math.max(0.0, Math.min(2.0, request.getTemperature())));
        }
        
        // Additional parameters
        if (request.getParameters().containsKey("top_p")) {
            double topP = ((Number) request.getParameters().get("top_p")).doubleValue();
            generationConfig.put("topP", Math.max(0.0, Math.min(1.0, topP)));
        }
        
        if (request.getParameters().containsKey("top_k")) {
            generationConfig.put("topK", request.getParameters().get("top_k"));
        }
        
        if (!generationConfig.isEmpty()) {
            googleRequest.put("generationConfig", generationConfig);
        }
        
        // Safety settings
        List<Map<String, Object>> safetySettings = buildSafetySettings(request);
        if (!safetySettings.isEmpty()) {
            googleRequest.put("safetySettings", safetySettings);
        }
        
        // Function calling (Tools)
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> functions = (List<Map<String, Object>>) request.getParameters().get("functions");
        if (functions != null && !functions.isEmpty()) {
            List<Map<String, Object>> tools = new ArrayList<>();
            tools.add(Map.of("functionDeclarations", functions));
            googleRequest.put("tools", tools);
        }
        
        return googleRequest;
    }
    
    private List<Map<String, Object>> buildSafetySettings(AIRequest request) {
        List<Map<String, Object>> safetySettings = new ArrayList<>();
        
        // Default safety settings for production use
        String[] categories = {
            "HARM_CATEGORY_HARASSMENT",
            "HARM_CATEGORY_HATE_SPEECH", 
            "HARM_CATEGORY_SEXUALLY_EXPLICIT",
            "HARM_CATEGORY_DANGEROUS_CONTENT"
        };
        
        String threshold = (String) request.getParameters().getOrDefault("safety_threshold", "BLOCK_MEDIUM_AND_ABOVE");
        
        for (String category : categories) {
            safetySettings.add(Map.of(
                "category", category,
                "threshold", threshold
            ));
        }
        
        return safetySettings;
    }
    
    private AIResponse processSuccessfulResponse(ResponseEntity<Map> response, AIRequest request, long processingTime) {
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = response.getBody();
        
        if (responseBody == null) {
            throw new RuntimeException("Empty response from Google AI API");
        }
        
        // Extract content from Google AI response format
        String content = "";
        List<String> warnings = new ArrayList<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
        
        if (candidates != null && !candidates.isEmpty()) {
            Map<String, Object> candidate = candidates.get(0);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> contentObj = (Map<String, Object>) candidate.get("content");
            if (contentObj != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> parts = (List<Map<String, Object>>) contentObj.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    content = (String) parts.get(0).get("text");
                }
            }
            
            // Check for safety ratings
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> safetyRatings = (List<Map<String, Object>>) candidate.get("safetyRatings");
            if (safetyRatings != null) {
                for (Map<String, Object> rating : safetyRatings) {
                    String probability = (String) rating.get("probability");
                    if ("MEDIUM".equals(probability) || "HIGH".equals(probability)) {
                        String category = (String) rating.get("category");
                        warnings.add("Safety concern detected: " + category);
                    }
                }
            }
            
            // Check finish reason
            String finishReason = (String) candidate.get("finishReason");
            if ("SAFETY".equals(finishReason)) {
                warnings.add("Response blocked due to safety filters");
            } else if ("MAX_TOKENS".equals(finishReason)) {
                warnings.add("Response truncated due to token limit");
            }
        }
        
        // Extract usage information (Google AI may not always provide this)
        @SuppressWarnings("unchecked")
        Map<String, Object> usageMetadata = (Map<String, Object>) responseBody.get("usageMetadata");
        int tokensUsed = 0;
        double cost = 0.0;
        
        if (usageMetadata != null) {
            Integer totalTokenCount = (Integer) usageMetadata.get("totalTokenCount");
            if (totalTokenCount != null) {
                tokensUsed = totalTokenCount;
                
                // Calculate cost
                String model = (String) request.getParameters().getOrDefault("model", defaultModel);
                Double costPerToken = modelCosts.get(model);
                if (costPerToken != null) {
                    cost = tokensUsed * costPerToken;
                }
            }
        } else {
            // Estimate tokens if not provided
            tokensUsed = estimateTokens(request.getContent()) + estimateTokens(content);
            String model = (String) request.getParameters().getOrDefault("model", defaultModel);
            Double costPerToken = modelCosts.get(model);
            if (costPerToken != null) {
                cost = tokensUsed * costPerToken;
            }
        }
        
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
                "candidates_count", candidates != null ? candidates.size() : 0
            ))
            .warnings(warnings)
            .build();
    }
    
    private Map<String, Double> initializeModelCosts() {
        Map<String, Double> costs = new HashMap<>();
        
        // Google AI pricing (per token) as of 2024 - very competitive
        costs.put("gemini-1.5-pro", 0.000007);      // $7 per 1M tokens
        costs.put("gemini-1.5-flash", 0.0000007);   // $0.70 per 1M tokens
        costs.put("gemini-pro", 0.0000005);         // $0.50 per 1M tokens
        costs.put("gemini-pro-vision", 0.00000075); // $0.75 per 1M tokens
        costs.put("gemini-ultra", 0.000015);        // Estimated premium pricing
        
        return costs;
    }
    
    private int estimateTokens(String text) {
        if (text == null) return 0;
        // Google uses similar tokenization to other models
        return (int) Math.ceil(text.split("\\s+").length / 0.75);
    }
    
    private double calculateQualityScore(String content, AIRequest request) {
        if (content == null || content.trim().isEmpty()) {
            return 0.0;
        }
        
        double score = 0.6; // Base score
        
        // Length appropriateness
        int contentLength = content.length();
        if (contentLength > 50 && contentLength < 8000) {
            score += 0.2;
        }
        
        // Completeness check
        if (!content.trim().endsWith("...") && !content.contains("[incomplete]")) {
            score += 0.1;
        }
        
        // Gemini tends to provide well-structured responses
        if (content.contains("\n") || content.matches(".*[.!?]\\s+[A-Z].*")) {
            score += 0.1; // Well-structured content
        }
        
        return Math.min(1.0, score);
    }
}
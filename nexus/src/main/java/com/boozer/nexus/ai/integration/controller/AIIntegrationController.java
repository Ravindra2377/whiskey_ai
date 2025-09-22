package com.boozer.nexus.ai.integration.controller;

import com.boozer.nexus.ai.integration.models.*;
import com.boozer.nexus.ai.integration.service.ExternalAIIntegrationService;
import com.boozer.nexus.ai.integration.routing.IntelligentAIRouter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * NEXUS AI Integration REST API Controller
 * 
 * Comprehensive API for AI provider integration with intelligent routing,
 * cost tracking, performance monitoring, and advanced features.
 */
@RestController
@RequestMapping("/api/v1/ai")
@Validated
@Tag(name = "AI Integration", description = "NEXUS AI Integration Service API")
public class AIIntegrationController {
    
    private static final Logger logger = LoggerFactory.getLogger(AIIntegrationController.class);
    
    @Autowired
    private ExternalAIIntegrationService aiService;
    
    @Autowired
    private IntelligentAIRouter aiRouter;
    
    // ==========================================================================
    // CORE AI REQUEST ENDPOINTS
    // ==========================================================================
    
    @PostMapping("/process")
    @Operation(summary = "Process AI Request", 
               description = "Submit an AI request for processing with automatic provider selection")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Request processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AIResponse> processRequest(
            @Valid @RequestBody AIRequest request,
            @RequestHeader(value = "X-Request-ID", required = false) String requestId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        
        try {
            // Set request ID if not provided
            if (requestId != null) {
                request.setId(requestId);
            } else if (request.getId() == null) {
                request.setId(UUID.randomUUID().toString());
            }
            
            logger.info("Processing AI request {} of type {}", request.getId(), request.getType());
            
            AIResponse response = aiService.processRequest(request);
            
            logger.info("AI request {} completed successfully with provider {}", 
                request.getId(), response.getProvider());
            
            return ResponseEntity.ok(response);
            
        } catch (AIProviderException e) {
            logger.error("AI provider error for request {}: {}", request.getId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(createErrorResponse(request.getId(), e.getMessage(), e.getErrorCode()));
                
        } catch (Exception e) {
            logger.error("Error processing AI request {}: {}", request.getId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(request.getId(), "Internal server error", 500));
        }
    }
    
    @PostMapping("/process/async")
    @Operation(summary = "Process AI Request Asynchronously", 
               description = "Submit an AI request for asynchronous processing")
    public ResponseEntity<Map<String, Object>> processRequestAsync(
            @Valid @RequestBody AIRequest request,
            @RequestHeader(value = "X-Request-ID", required = false) String requestId) {
        
        try {
            if (requestId != null) {
                request.setId(requestId);
            } else if (request.getId() == null) {
                request.setId(UUID.randomUUID().toString());
            }
            
            logger.info("Starting async processing for AI request {} of type {}", 
                request.getId(), request.getType());
            
            CompletableFuture<AIResponse> futureResponse = aiService.processRequestAsync(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("requestId", request.getId());
            response.put("status", "processing");
            response.put("message", "Request submitted for asynchronous processing");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.accepted().body(response);
            
        } catch (Exception e) {
            logger.error("Error starting async processing for request {}: {}", 
                request.getId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to start async processing: " + e.getMessage()));
        }
    }
    
    @PostMapping("/process/batch")
    @Operation(summary = "Process Multiple AI Requests", 
               description = "Submit multiple AI requests for batch processing")
    public ResponseEntity<List<AIResponse>> processBatchRequests(
            @Valid @RequestBody @NotEmpty List<AIRequest> requests,
            @RequestParam(defaultValue = "false") boolean parallel) {
        
        try {
            logger.info("Processing batch of {} AI requests (parallel: {})", requests.size(), parallel);
            
            List<AIResponse> responses = aiService.processBatchRequests(requests, parallel);
            
            logger.info("Batch processing completed. {} out of {} requests successful", 
                responses.stream().mapToLong(r -> r.isSuccessful() ? 1 : 0).sum(),
                responses.size());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error processing batch requests: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(List.of(createErrorResponse("batch", "Batch processing failed: " + e.getMessage(), 500)));
        }
    }
    
    // ==========================================================================
    // PROVIDER MANAGEMENT ENDPOINTS
    // ==========================================================================
    
    @GetMapping("/providers")
    @Operation(summary = "List AI Providers", 
               description = "Get list of all configured AI providers and their status")
    public ResponseEntity<List<ProviderStatus>> getProviders() {
        try {
            List<ProviderStatus> providers = aiService.getProviderStatuses();
            return ResponseEntity.ok(providers);
        } catch (Exception e) {
            logger.error("Error getting provider statuses: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }
    
    @GetMapping("/providers/{providerName}/capabilities")
    @Operation(summary = "Get Provider Capabilities", 
               description = "Get detailed capabilities information for a specific provider")
    public ResponseEntity<ProviderCapabilities> getProviderCapabilities(
            @PathVariable @Parameter(description = "Provider name") String providerName) {
        try {
            ProviderCapabilities capabilities = aiService.getProviderCapabilities(providerName);
            if (capabilities != null) {
                return ResponseEntity.ok(capabilities);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting capabilities for provider {}: {}", providerName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @PostMapping("/providers/{providerName}/health-check")
    @Operation(summary = "Check Provider Health", 
               description = "Perform health check on a specific AI provider")
    public ResponseEntity<Map<String, Object>> checkProviderHealth(
            @PathVariable @Parameter(description = "Provider name") String providerName) {
        try {
            boolean healthy = aiService.checkProviderHealth(providerName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("provider", providerName);
            response.put("healthy", healthy);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error checking health for provider {}: {}", providerName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Health check failed: " + e.getMessage()));
        }
    }
    
    // ==========================================================================
    // ROUTING AND OPTIMIZATION ENDPOINTS
    // ==========================================================================
    
    @PostMapping("/routing/recommend")
    @Operation(summary = "Get Provider Recommendations", 
               description = "Get recommended providers for a specific request type")
    public ResponseEntity<List<String>> getProviderRecommendations(
            @RequestParam @NotNull AIRequestType requestType) {
        try {
            List<String> recommendations = aiRouter.getProviderRecommendations(requestType);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            logger.error("Error getting provider recommendations for type {}: {}", 
                requestType, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }
    
    @GetMapping("/routing/analytics")
    @Operation(summary = "Get Routing Analytics", 
               description = "Get detailed analytics about provider usage and performance")
    public ResponseEntity<IntelligentAIRouter.RoutingAnalytics> getRoutingAnalytics() {
        try {
            IntelligentAIRouter.RoutingAnalytics analytics = aiRouter.getAnalytics();
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            logger.error("Error getting routing analytics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    // ==========================================================================
    // COST TRACKING ENDPOINTS
    // ==========================================================================
    
    @GetMapping("/costs/summary")
    @Operation(summary = "Get Cost Summary", 
               description = "Get summary of AI usage costs")
    public ResponseEntity<Map<String, Object>> getCostSummary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String provider) {
        try {
            Map<String, Object> costSummary = aiService.getCostSummary(startDate, endDate, provider);
            return ResponseEntity.ok(costSummary);
        } catch (Exception e) {
            logger.error("Error getting cost summary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get cost summary: " + e.getMessage()));
        }
    }
    
    @PostMapping("/costs/estimate")
    @Operation(summary = "Estimate Request Cost", 
               description = "Estimate the cost of processing an AI request")
    public ResponseEntity<Map<String, Object>> estimateRequestCost(
            @Valid @RequestBody AIRequest request,
            @RequestParam(required = false) String provider) {
        try {
            double estimatedCost = aiService.estimateRequestCost(request, provider);
            
            Map<String, Object> response = new HashMap<>();
            response.put("estimatedCost", estimatedCost);
            response.put("currency", "USD");
            response.put("provider", provider != null ? provider : "auto-selected");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error estimating request cost: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Cost estimation failed: " + e.getMessage()));
        }
    }
    
    // ==========================================================================
    // MONITORING AND METRICS ENDPOINTS
    // ==========================================================================
    
    @GetMapping("/metrics")
    @Operation(summary = "Get Service Metrics", 
               description = "Get comprehensive metrics about the AI integration service")
    public ResponseEntity<Map<String, Object>> getServiceMetrics() {
        try {
            Map<String, Object> metrics = aiService.getServiceMetrics();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            logger.error("Error getting service metrics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get metrics: " + e.getMessage()));
        }
    }
    
    @GetMapping("/health")
    @Operation(summary = "Service Health Check", 
               description = "Check overall health of the AI integration service")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            boolean healthy = aiService.isServiceHealthy();
            Map<String, Object> health = new HashMap<>();
            health.put("status", healthy ? "UP" : "DOWN");
            health.put("timestamp", LocalDateTime.now());
            health.put("providers", aiService.getProviderStatuses());
            
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            logger.error("Error during health check: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("status", "DOWN", "error", e.getMessage()));
        }
    }
    
    // ==========================================================================
    // ADVANCED FEATURES ENDPOINTS
    // ==========================================================================
    
    @PostMapping("/conversation")
    @Operation(summary = "Multi-turn Conversation", 
               description = "Handle multi-turn conversation with context preservation")
    public ResponseEntity<AIResponse> handleConversation(
            @Valid @RequestBody ConversationRequest conversationRequest) {
        try {
            AIResponse response = aiService.handleConversation(conversationRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error handling conversation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("conversation", "Conversation failed: " + e.getMessage(), 500));
        }
    }
    
    @PostMapping("/function-call")
    @Operation(summary = "Function Calling", 
               description = "Execute AI function calling with structured output")
    public ResponseEntity<Map<String, Object>> executeFunctionCall(
            @Valid @RequestBody FunctionCallRequest functionRequest) {
        try {
            Map<String, Object> result = aiService.executeFunctionCall(functionRequest);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error executing function call: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Function call failed: " + e.getMessage()));
        }
    }
    
    // ==========================================================================
    // CONFIGURATION ENDPOINTS
    // ==========================================================================
    
    @GetMapping("/config")
    @Operation(summary = "Get Service Configuration", 
               description = "Get current configuration of the AI integration service")
    public ResponseEntity<Map<String, Object>> getServiceConfiguration() {
        try {
            Map<String, Object> config = aiService.getServiceConfiguration();
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            logger.error("Error getting service configuration: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get configuration: " + e.getMessage()));
        }
    }
    
    @PostMapping("/config/reload")
    @Operation(summary = "Reload Configuration", 
               description = "Reload the AI service configuration")
    public ResponseEntity<Map<String, Object>> reloadConfiguration() {
        try {
            aiService.reloadConfiguration();
            return ResponseEntity.ok(Map.of(
                "message", "Configuration reloaded successfully",
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            logger.error("Error reloading configuration: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Configuration reload failed: " + e.getMessage()));
        }
    }
    
    // ==========================================================================
    // UTILITY METHODS
    // ==========================================================================
    
    private AIResponse createErrorResponse(String requestId, String errorMessage, int errorCode) {
        return AIResponse.builder()
            .requestId(requestId)
            .provider("system")
            .successful(false)
            .content("Error: " + errorMessage)
            .tokensUsed(0)
            .costUSD(0.0)
            .processingTimeMs(0)
            .qualityScore(0.0)
            .timestamp(LocalDateTime.now())
            .metadata(Map.of("error_code", errorCode, "error_message", errorMessage))
            .warnings(List.of(errorMessage))
            .build();
    }
    
    // ==========================================================================
    // REQUEST/RESPONSE MODELS FOR ADVANCED FEATURES
    // ==========================================================================
    
    public static class ConversationRequest {
        @NotNull
        private String conversationId;
        
        @NotNull @NotEmpty
        private String message;
        
        private List<Map<String, Object>> context;
        private String systemMessage;
        private Map<String, Object> parameters;
        
        // Constructors, getters, setters
        public ConversationRequest() {}
        
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public List<Map<String, Object>> getContext() { return context; }
        public void setContext(List<Map<String, Object>> context) { this.context = context; }
        
        public String getSystemMessage() { return systemMessage; }
        public void setSystemMessage(String systemMessage) { this.systemMessage = systemMessage; }
        
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    }
    
    public static class FunctionCallRequest {
        @NotNull @NotEmpty
        private String functionName;
        
        @NotNull
        private Map<String, Object> parameters;
        
        private String context;
        private String expectedOutput;
        
        // Constructors, getters, setters
        public FunctionCallRequest() {}
        
        public String getFunctionName() { return functionName; }
        public void setFunctionName(String functionName) { this.functionName = functionName; }
        
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
        
        public String getContext() { return context; }
        public void setContext(String context) { this.context = context; }
        
        public String getExpectedOutput() { return expectedOutput; }
        public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
    }
}
package com.boozer.nexus.ai.integration.providers;

import com.boozer.nexus.ai.integration.models.*;

/**
 * Base interface for all AI providers
 */
public interface AIProvider {
    
    /**
     * Process an AI request and return response
     */
    AIResponse processRequest(AIRequest request) throws AIProviderException;
    
    /**
     * Check if the provider is healthy and available
     */
    boolean healthCheck();
    
    /**
     * Get provider capabilities and limitations
     */
    ProviderCapabilities getCapabilities();
    
    /**
     * Get provider name/identifier
     */
    String getProviderName();
    
    /**
     * Estimate cost for a request before processing
     */
    double estimateCost(AIRequest request);
    
    /**
     * Check if provider supports the request type
     */
    boolean supportsRequestType(AIRequestType requestType);
    
    /**
     * Get current provider status and metrics
     */
    ProviderStatus getStatus();
}

/**
 * Exception for AI provider-specific errors
 */
class AIProviderException extends Exception {
    private final String providerName;
    private final int errorCode;
    
    public AIProviderException(String providerName, String message) {
        super(message);
        this.providerName = providerName;
        this.errorCode = 0;
    }
    
    public AIProviderException(String providerName, String message, int errorCode) {
        super(message);
        this.providerName = providerName;
        this.errorCode = errorCode;
    }
    
    public AIProviderException(String providerName, String message, Throwable cause) {
        super(message, cause);
        this.providerName = providerName;
        this.errorCode = 0;
    }
    
    public String getProviderName() { return providerName; }
    public int getErrorCode() { return errorCode; }
}
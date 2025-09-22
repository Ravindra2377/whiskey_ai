package com.boozer.nexus;

public class ClientCredentials {
    private String tenantId;
    private String apiKey;
    private String apiSecret;
    
    // Constructors
    public ClientCredentials() {}
    
    public ClientCredentials(String tenantId, String apiKey, String apiSecret) {
        this.tenantId = tenantId;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }
    
    // Getters and Setters
    public String getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getApiSecret() {
        return apiSecret;
    }
    
    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }
}

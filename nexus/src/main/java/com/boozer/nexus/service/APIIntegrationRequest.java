package com.boozer.nexus.service;

public class APIIntegrationRequest {
    private String targetAPIUrl;
    private String preferredLanguage;
    private String clientId;
    private String integrationName;
    
    // Constructors
    public APIIntegrationRequest() {}
    
    public APIIntegrationRequest(String targetAPIUrl, String preferredLanguage, String clientId, String integrationName) {
        this.targetAPIUrl = targetAPIUrl;
        this.preferredLanguage = preferredLanguage;
        this.clientId = clientId;
        this.integrationName = integrationName;
    }
    
    // Getters and Setters
    public String getTargetAPIUrl() {
        return targetAPIUrl;
    }
    
    public void setTargetAPIUrl(String targetAPIUrl) {
        this.targetAPIUrl = targetAPIUrl;
    }
    
    public String getPreferredLanguage() {
        return preferredLanguage;
    }
    
    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getIntegrationName() {
        return integrationName;
    }
    
    public void setIntegrationName(String integrationName) {
        this.integrationName = integrationName;
    }
}

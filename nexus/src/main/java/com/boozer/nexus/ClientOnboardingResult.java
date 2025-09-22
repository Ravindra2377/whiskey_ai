package com.boozer.nexus;

import java.util.List;

public class ClientOnboardingResult {
    private String tenantId;
    private List<AIAgent> deployedAgents;
    private ClientCredentials credentials;
    private String errorMessage;
    private String status;
    
    // Constructors
    public ClientOnboardingResult() {
        this.status = "SUCCESS";
    }
    
    public ClientOnboardingResult(String tenantId, List<AIAgent> deployedAgents, ClientCredentials credentials) {
        this.tenantId = tenantId;
        this.deployedAgents = deployedAgents;
        this.credentials = credentials;
        this.status = "SUCCESS";
    }
    
    // Getters and Setters
    public String getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    public List<AIAgent> getDeployedAgents() {
        return deployedAgents;
    }
    
    public void setDeployedAgents(List<AIAgent> deployedAgents) {
        this.deployedAgents = deployedAgents;
    }
    
    public ClientCredentials getCredentials() {
        return credentials;
    }
    
    public void setCredentials(ClientCredentials credentials) {
        this.credentials = credentials;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.status = "ERROR";
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}

package com.boozer.nexus.dto;

import java.util.Map;

public class EnterpriseIntegrationRequestDTO {
    private String clientName;
    private String tier;
    private Map<String, Object> discoveryParams;
    private Map<String, Object> configParams;
    private Map<String, Object> deploymentParams;
    
    // Getters and setters
    public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public String getTier() {
        return tier;
    }
    
    public void setTier(String tier) {
        this.tier = tier;
    }
    
    public Map<String, Object> getDiscoveryParams() {
        return discoveryParams;
    }
    
    public void setDiscoveryParams(Map<String, Object> discoveryParams) {
        this.discoveryParams = discoveryParams;
    }
    
    public Map<String, Object> getConfigParams() {
        return configParams;
    }
    
    public void setConfigParams(Map<String, Object> configParams) {
        this.configParams = configParams;
    }
    
    public Map<String, Object> getDeploymentParams() {
        return deploymentParams;
    }
    
    public void setDeploymentParams(Map<String, Object> deploymentParams) {
        this.deploymentParams = deploymentParams;
    }
}

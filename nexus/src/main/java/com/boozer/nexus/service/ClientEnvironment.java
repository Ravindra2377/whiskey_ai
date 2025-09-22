package com.boozer.nexus.service;

import java.util.List;
import java.util.Map;

public class ClientEnvironment {
    private String clientId;
    private String environmentName;
    private List<String> technologyStack;
    private Map<String, Object> historicalData;
    private String status;
    
    // Constructors
    public ClientEnvironment() {}
    
    public ClientEnvironment(String clientId, String environmentName, List<String> technologyStack, 
                           Map<String, Object> historicalData, String status) {
        this.clientId = clientId;
        this.environmentName = environmentName;
        this.technologyStack = technologyStack;
        this.historicalData = historicalData;
        this.status = status;
    }
    
    // Getters and Setters
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getEnvironmentName() {
        return environmentName;
    }
    
    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }
    
    public List<String> getTechnologyStack() {
        return technologyStack;
    }
    
    public void setTechnologyStack(List<String> technologyStack) {
        this.technologyStack = technologyStack;
    }
    
    public Map<String, Object> getHistoricalData() {
        return historicalData;
    }
    
    public void setHistoricalData(Map<String, Object> historicalData) {
        this.historicalData = historicalData;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}

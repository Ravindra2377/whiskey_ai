package com.boozer.nexus.service;

import java.util.Map;

public class SystemMetrics {
    private String clientId;
    private long timestamp;
    private Map<String, Object> metrics;
    
    // Constructors
    public SystemMetrics() {}
    
    public SystemMetrics(String clientId, long timestamp, Map<String, Object> metrics) {
        this.clientId = clientId;
        this.timestamp = timestamp;
        this.metrics = metrics;
    }
    
    // Getters and Setters
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public Map<String, Object> getMetrics() {
        return metrics;
    }
    
    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }
}

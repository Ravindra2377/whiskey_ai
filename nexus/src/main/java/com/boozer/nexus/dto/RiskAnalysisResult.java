package com.boozer.nexus.dto;

import java.util.Map;

public class RiskAnalysisResult {
    private boolean successful;
    private String message;
    private Map<String, Object> monteCarloResults;
    private Map<String, Object> scenarios;
    private Map<String, Object> metrics;
    
    // Constructors
    public RiskAnalysisResult() {}
    
    public RiskAnalysisResult(boolean successful, String message, Map<String, Object> monteCarloResults, 
                             Map<String, Object> scenarios, Map<String, Object> metrics) {
        this.successful = successful;
        this.message = message;
        this.monteCarloResults = monteCarloResults;
        this.scenarios = scenarios;
        this.metrics = metrics;
    }
    
    // Getters and Setters
    public boolean isSuccessful() {
        return successful;
    }
    
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Map<String, Object> getMonteCarloResults() {
        return monteCarloResults;
    }
    
    public void setMonteCarloResults(Map<String, Object> monteCarloResults) {
        this.monteCarloResults = monteCarloResults;
    }
    
    public Map<String, Object> getScenarios() {
        return scenarios;
    }
    
    public void setScenarios(Map<String, Object> scenarios) {
        this.scenarios = scenarios;
    }
    
    public Map<String, Object> getMetrics() {
        return metrics;
    }
    
    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }
}

package com.boozer.nexus.dto;

import java.util.Map;

public class TradingResult {
    private boolean successful;
    private String message;
    private Map<String, Object> optimization;
    private Map<String, Object> timing;
    private Map<String, Object> execution;
    private Map<String, Object> evolution;
    
    // Constructors
    public TradingResult() {}
    
    public TradingResult(boolean successful, String message, Map<String, Object> optimization, 
                        Map<String, Object> timing, Map<String, Object> execution, Map<String, Object> evolution) {
        this.successful = successful;
        this.message = message;
        this.optimization = optimization;
        this.timing = timing;
        this.execution = execution;
        this.evolution = evolution;
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
    
    public Map<String, Object> getOptimization() {
        return optimization;
    }
    
    public void setOptimization(Map<String, Object> optimization) {
        this.optimization = optimization;
    }
    
    public Map<String, Object> getTiming() {
        return timing;
    }
    
    public void setTiming(Map<String, Object> timing) {
        this.timing = timing;
    }
    
    public Map<String, Object> getExecution() {
        return execution;
    }
    
    public void setExecution(Map<String, Object> execution) {
        this.execution = execution;
    }
    
    public Map<String, Object> getEvolution() {
        return evolution;
    }
    
    public void setEvolution(Map<String, Object> evolution) {
        this.evolution = evolution;
    }
}

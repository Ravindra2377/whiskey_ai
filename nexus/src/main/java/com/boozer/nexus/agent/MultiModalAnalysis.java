package com.boozer.nexus.agent;

import java.util.Map;

public class MultiModalAnalysis {
    private Map<String, Object> textAnalysis;
    private Map<String, Object> codeAnalysis;
    private Map<String, Object> imageAnalysis;
    private Map<String, Object> logAnalysis;
    private Map<String, Object> configAnalysis;
    
    // Constructors
    public MultiModalAnalysis() {}
    
    public MultiModalAnalysis(Map<String, Object> textAnalysis, Map<String, Object> codeAnalysis, 
                             Map<String, Object> imageAnalysis, Map<String, Object> logAnalysis, 
                             Map<String, Object> configAnalysis) {
        this.textAnalysis = textAnalysis;
        this.codeAnalysis = codeAnalysis;
        this.imageAnalysis = imageAnalysis;
        this.logAnalysis = logAnalysis;
        this.configAnalysis = configAnalysis;
    }
    
    // Getters and Setters
    public Map<String, Object> getTextAnalysis() {
        return textAnalysis;
    }
    
    public void setTextAnalysis(Map<String, Object> textAnalysis) {
        this.textAnalysis = textAnalysis;
    }
    
    public Map<String, Object> getCodeAnalysis() {
        return codeAnalysis;
    }
    
    public void setCodeAnalysis(Map<String, Object> codeAnalysis) {
        this.codeAnalysis = codeAnalysis;
    }
    
    public Map<String, Object> getImageAnalysis() {
        return imageAnalysis;
    }
    
    public void setImageAnalysis(Map<String, Object> imageAnalysis) {
        this.imageAnalysis = imageAnalysis;
    }
    
    public Map<String, Object> getLogAnalysis() {
        return logAnalysis;
    }
    
    public void setLogAnalysis(Map<String, Object> logAnalysis) {
        this.logAnalysis = logAnalysis;
    }
    
    public Map<String, Object> getConfigAnalysis() {
        return configAnalysis;
    }
    
    public void setConfigAnalysis(Map<String, Object> configAnalysis) {
        this.configAnalysis = configAnalysis;
    }
    
    // Builder pattern
    public static class Builder {
        private MultiModalAnalysis analysis = new MultiModalAnalysis();
        
        public Builder textAnalysis(Map<String, Object> textAnalysis) {
            analysis.textAnalysis = textAnalysis;
            return this;
        }
        
        public Builder codeAnalysis(Map<String, Object> codeAnalysis) {
            analysis.codeAnalysis = codeAnalysis;
            return this;
        }
        
        public Builder imageAnalysis(Map<String, Object> imageAnalysis) {
            analysis.imageAnalysis = imageAnalysis;
            return this;
        }
        
        public Builder logAnalysis(Map<String, Object> logAnalysis) {
            analysis.logAnalysis = logAnalysis;
            return this;
        }
        
        public Builder configAnalysis(Map<String, Object> configAnalysis) {
            analysis.configAnalysis = configAnalysis;
            return this;
        }
        
        public MultiModalAnalysis build() {
            return analysis;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}

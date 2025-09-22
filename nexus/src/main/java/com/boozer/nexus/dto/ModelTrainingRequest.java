package com.boozer.nexus.dto;

import java.util.Map;

public class ModelTrainingRequest {
    private String domain;
    private String trainingData;
    private Map<String, Object> modelParameters;
    private String clientId;
    
    // Constructors
    public ModelTrainingRequest() {}
    
    public ModelTrainingRequest(String domain, String trainingData, Map<String, Object> modelParameters, String clientId) {
        this.domain = domain;
        this.trainingData = trainingData;
        this.modelParameters = modelParameters;
        this.clientId = clientId;
    }
    
    // Getters and Setters
    public String getDomain() {
        return domain;
    }
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public String getTrainingData() {
        return trainingData;
    }
    
    public void setTrainingData(String trainingData) {
        this.trainingData = trainingData;
    }
    
    public Map<String, Object> getModelParameters() {
        return modelParameters;
    }
    
    public void setModelParameters(Map<String, Object> modelParameters) {
        this.modelParameters = modelParameters;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}

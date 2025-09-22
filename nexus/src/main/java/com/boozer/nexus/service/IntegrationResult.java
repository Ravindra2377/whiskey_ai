package com.boozer.nexus.service;

public class IntegrationResult {
    private ClientCodeGeneration generatedCode;
    private TestSuite testSuite;
    private APIDocumentation documentation;
    private String estimatedIntegrationTime;
    private double confidenceScore;
    
    // Constructors
    public IntegrationResult() {}
    
    public IntegrationResult(ClientCodeGeneration generatedCode, TestSuite testSuite, 
                           APIDocumentation documentation, String estimatedIntegrationTime, 
                           double confidenceScore) {
        this.generatedCode = generatedCode;
        this.testSuite = testSuite;
        this.documentation = documentation;
        this.estimatedIntegrationTime = estimatedIntegrationTime;
        this.confidenceScore = confidenceScore;
    }
    
    // Getters and Setters
    public ClientCodeGeneration getGeneratedCode() {
        return generatedCode;
    }
    
    public void setGeneratedCode(ClientCodeGeneration generatedCode) {
        this.generatedCode = generatedCode;
    }
    
    public TestSuite getTestSuite() {
        return testSuite;
    }
    
    public void setTestSuite(TestSuite testSuite) {
        this.testSuite = testSuite;
    }
    
    public APIDocumentation getDocumentation() {
        return documentation;
    }
    
    public void setDocumentation(APIDocumentation documentation) {
        this.documentation = documentation;
    }
    
    public String getEstimatedIntegrationTime() {
        return estimatedIntegrationTime;
    }
    
    public void setEstimatedIntegrationTime(String estimatedIntegrationTime) {
        this.estimatedIntegrationTime = estimatedIntegrationTime;
    }
    
    public double getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
    
    // Builder pattern
    public static class Builder {
        private IntegrationResult result = new IntegrationResult();
        
        public Builder generatedCode(ClientCodeGeneration generatedCode) {
            result.generatedCode = generatedCode;
            return this;
        }
        
        public Builder testSuite(TestSuite testSuite) {
            result.testSuite = testSuite;
            return this;
        }
        
        public Builder documentation(APIDocumentation documentation) {
            result.documentation = documentation;
            return this;
        }
        
        public Builder estimatedIntegrationTime(String estimatedIntegrationTime) {
            result.estimatedIntegrationTime = estimatedIntegrationTime;
            return this;
        }
        
        public Builder confidenceScore(double confidenceScore) {
            result.confidenceScore = confidenceScore;
            return this;
        }
        
        public IntegrationResult build() {
            return result;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}

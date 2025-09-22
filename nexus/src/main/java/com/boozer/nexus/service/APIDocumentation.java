package com.boozer.nexus.service;

import java.util.List;

public class APIDocumentation {
    private int endpointCount;
    private int sampleCount;
    private List<String> endpoints;
    private String format;
    
    // Constructors
    public APIDocumentation() {}
    
    public APIDocumentation(int endpointCount, int sampleCount, List<String> endpoints, String format) {
        this.endpointCount = endpointCount;
        this.sampleCount = sampleCount;
        this.endpoints = endpoints;
        this.format = format;
    }
    
    // Getters and Setters
    public int getEndpointCount() {
        return endpointCount;
    }
    
    public void setEndpointCount(int endpointCount) {
        this.endpointCount = endpointCount;
    }
    
    public int getSampleCount() {
        return sampleCount;
    }
    
    public void setSampleCount(int sampleCount) {
        this.sampleCount = sampleCount;
    }
    
    public List<String> getEndpoints() {
        return endpoints;
    }
    
    public void setEndpoints(List<String> endpoints) {
        this.endpoints = endpoints;
    }
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
}

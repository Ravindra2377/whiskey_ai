package com.boozer.nexus.service;

import java.util.List;

public class ClientCodeGeneration {
    private String language;
    private String clientCode;
    private List<String> exampleUsages;
    private String version;
    
    // Constructors
    public ClientCodeGeneration() {}
    
    public ClientCodeGeneration(String language, String clientCode, List<String> exampleUsages, String version) {
        this.language = language;
        this.clientCode = clientCode;
        this.exampleUsages = exampleUsages;
        this.version = version;
    }
    
    // Getters and Setters
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getClientCode() {
        return clientCode;
    }
    
    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }
    
    public List<String> getExampleUsages() {
        return exampleUsages;
    }
    
    public void setExampleUsages(List<String> exampleUsages) {
        this.exampleUsages = exampleUsages;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
}

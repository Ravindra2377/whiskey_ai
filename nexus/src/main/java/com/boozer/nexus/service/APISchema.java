package com.boozer.nexus.service;

import java.util.List;
import java.util.Map;

public class APISchema {
    private String apiUrl;
    private String apiName;
    private String apiVersion;
    private List<APIEndpoint> endpoints;
    private Map<String, String> authentication;
    private List<String> supportedFormats;
    
    // Constructors
    public APISchema() {}
    
    public APISchema(String apiUrl, String apiName, String apiVersion, List<APIEndpoint> endpoints, 
                    Map<String, String> authentication, List<String> supportedFormats) {
        this.apiUrl = apiUrl;
        this.apiName = apiName;
        this.apiVersion = apiVersion;
        this.endpoints = endpoints;
        this.authentication = authentication;
        this.supportedFormats = supportedFormats;
    }
    
    // Getters and Setters
    public String getApiUrl() {
        return apiUrl;
    }
    
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
    
    public String getApiName() {
        return apiName;
    }
    
    public void setApiName(String apiName) {
        this.apiName = apiName;
    }
    
    public String getApiVersion() {
        return apiVersion;
    }
    
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
    
    public List<APIEndpoint> getEndpoints() {
        return endpoints;
    }
    
    public void setEndpoints(List<APIEndpoint> endpoints) {
        this.endpoints = endpoints;
    }
    
    public Map<String, String> getAuthentication() {
        return authentication;
    }
    
    public void setAuthentication(Map<String, String> authentication) {
        this.authentication = authentication;
    }
    
    public List<String> getSupportedFormats() {
        return supportedFormats;
    }
    
    public void setSupportedFormats(List<String> supportedFormats) {
        this.supportedFormats = supportedFormats;
    }
    
    // Inner class for API endpoints
    public static class APIEndpoint {
        private String path;
        private String method;
        private String description;
        private List<APIParameter> parameters;
        private List<String> responseCodes;
        
        // Constructors
        public APIEndpoint() {}
        
        public APIEndpoint(String path, String method, String description, List<APIParameter> parameters, List<String> responseCodes) {
            this.path = path;
            this.method = method;
            this.description = description;
            this.parameters = parameters;
            this.responseCodes = responseCodes;
        }
        
        // Getters and Setters
        public String getPath() {
            return path;
        }
        
        public void setPath(String path) {
            this.path = path;
        }
        
        public String getMethod() {
            return method;
        }
        
        public void setMethod(String method) {
            this.method = method;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public List<APIParameter> getParameters() {
            return parameters;
        }
        
        public void setParameters(List<APIParameter> parameters) {
            this.parameters = parameters;
        }
        
        public List<String> getResponseCodes() {
            return responseCodes;
        }
        
        public void setResponseCodes(List<String> responseCodes) {
            this.responseCodes = responseCodes;
        }
    }
    
    // Inner class for API parameters
    public static class APIParameter {
        private String name;
        private String type;
        private String description;
        private boolean required;
        
        // Constructors
        public APIParameter() {}
        
        public APIParameter(String name, String type, String description, boolean required) {
            this.name = name;
            this.type = type;
            this.description = description;
            this.required = required;
        }
        
        // Getters and Setters
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public boolean isRequired() {
            return required;
        }
        
        public void setRequired(boolean required) {
            this.required = required;
        }
    }
}

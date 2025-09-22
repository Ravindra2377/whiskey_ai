package com.boozer.nexus.agent;

public class DatabaseIssueRequest {
    private String issueDescription;
    private String databaseType;
    private String query;
    private String clientId;
    
    // Constructors
    public DatabaseIssueRequest() {}
    
    public DatabaseIssueRequest(String issueDescription, String databaseType, String query, String clientId) {
        this.issueDescription = issueDescription;
        this.databaseType = databaseType;
        this.query = query;
        this.clientId = clientId;
    }
    
    // Getters and Setters
    public String getIssueDescription() {
        return issueDescription;
    }
    
    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }
    
    public String getDatabaseType() {
        return databaseType;
    }
    
    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }
    
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}

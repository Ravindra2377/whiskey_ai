package com.boozer.nexus;

public class WhiskeyResult {
    private boolean successful;
    private String message;
    private Object data;
    private long timestamp;
    
    public WhiskeyResult() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public WhiskeyResult(boolean successful, String message, Object data) {
        this();
        this.successful = successful;
        this.message = message;
        this.data = data;
    }
    
    // Getters and setters
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

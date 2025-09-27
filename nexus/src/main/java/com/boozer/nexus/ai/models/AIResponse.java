package com.boozer.nexus.ai.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Minimal stub AIResponse to satisfy current engine and processor dependencies.
 * Expand with real provider metadata later.
 */
public class AIResponse {
    private String requestId;
    private String provider;
    private String content;
    private double qualityScore;
    private boolean successful = true;
    private Map<String,Object> metadata = new HashMap<>();
    private LocalDateTime timestamp = LocalDateTime.now();

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public double getQualityScore() { return qualityScore; }
    public void setQualityScore(double qualityScore) { this.qualityScore = qualityScore; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}

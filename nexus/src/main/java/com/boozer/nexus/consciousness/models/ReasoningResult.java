package com.boozer.nexus.consciousness.models;

import java.time.LocalDateTime;
import java.util.List;

public class ReasoningResult {
    private String query;
    private List<ReasoningStep> reasoningChain;
    private double confidenceLevel;
    private String answer;
    private Object metacognitionEvaluation;
    private LocalDateTime timestamp;
    private boolean successful;

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public List<ReasoningStep> getReasoningChain() { return reasoningChain; }
    public void setReasoningChain(List<ReasoningStep> reasoningChain) { this.reasoningChain = reasoningChain; }
    public double getConfidenceLevel() { return confidenceLevel; }
    public void setConfidenceLevel(double confidenceLevel) { this.confidenceLevel = confidenceLevel; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public Object getMetacognitionEvaluation() { return metacognitionEvaluation; }
    public void setMetacognitionEvaluation(Object metacognitionEvaluation) { this.metacognitionEvaluation = metacognitionEvaluation; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
}

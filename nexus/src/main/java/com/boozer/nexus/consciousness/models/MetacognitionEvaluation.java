package com.boozer.nexus.consciousness.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class MetacognitionEvaluation {
    private double qualityScore;
    private Map<String,Object> evaluationMetrics = new HashMap<>();
    private LocalDateTime timestamp = LocalDateTime.now();

    public double getQualityScore() { return qualityScore; }
    public void setQualityScore(double qualityScore) { this.qualityScore = qualityScore; }
    public Map<String, Object> getEvaluationMetrics() { return evaluationMetrics; }
    public void setEvaluationMetrics(Map<String, Object> evaluationMetrics) { this.evaluationMetrics = evaluationMetrics; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}

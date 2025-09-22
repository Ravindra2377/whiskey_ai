package com.boozer.whiskey.enhanced;

import com.boozer.whiskey.MonitoringAgent;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

@Service
public class EnhancedMonitoringAgent extends MonitoringAgent {
    
    private Random random = new Random();
    
    public EnhancedMonitoringAgent() {
        super();
    }
    
    /**
     * Enhanced metrics collection with predictive analytics
     */
    public EnhancedMetricsData collectEnhancedMetrics(Map<String, Object> parameters) {
        // Collect base metrics
        MetricsData baseMetrics = super.collectMetrics(parameters);
        
        // Add predictive analytics
        EnhancedMetricsData enhancedMetrics = new EnhancedMetricsData();
        enhancedMetrics.setBaseMetrics(baseMetrics);
        
        // Predictive metrics based on trends
        enhancedMetrics.setPredictedCpuUsage(predictNextValue(baseMetrics.getCpuUsage(), 0.85));
        enhancedMetrics.setPredictedMemoryUsage(predictNextValue(baseMetrics.getMemoryUsage(), 0.80));
        enhancedMetrics.setPredictedErrorRate(predictNextValue(baseMetrics.getErrorRate(), 0.02));
        
        // Performance health score (0-100)
        enhancedMetrics.setHealthScore(calculateHealthScore(baseMetrics));
        
        enhancedMetrics.setTimestamp(System.currentTimeMillis());
        
        return enhancedMetrics;
    }
    
    /**
     * Advanced anomaly detection with pattern recognition
     */
    public EnhancedAnomalyReport detectAdvancedAnomalies(EnhancedMetricsData metrics) {
        // Get base anomalies
        AnomalyReport baseReport = super.detectAnomalies(metrics.getBaseMetrics());
        
        // Create enhanced report
        EnhancedAnomalyReport enhancedReport = new EnhancedAnomalyReport();
        enhancedReport.setBaseReport(baseReport);
        enhancedReport.setTimestamp(System.currentTimeMillis());
        
        // Advanced pattern detection
        List<String> patterns = detectPatterns(metrics);
        enhancedReport.setDetectedPatterns(patterns);
        
        // Risk assessment
        enhancedReport.setRiskLevel(assessRisk(baseReport, metrics));
        
        // Recommendations
        List<String> recommendations = generateRecommendations(baseReport, metrics);
        enhancedReport.setRecommendations(recommendations);
        
        return enhancedReport;
    }
    
    /**
     * Predict next value based on current value and trend factor
     */
    private double predictNextValue(double currentValue, double trendFactor) {
        // Simple prediction model - in reality this would use ML
        double trend = (random.nextDouble() - 0.5) * 0.1; // -5% to +5% variation
        return Math.min(100, Math.max(0, currentValue + (currentValue * trendFactor * trend)));
    }
    
    /**
     * Calculate health score based on metrics
     */
    private int calculateHealthScore(MetricsData metrics) {
        // Weighted scoring system
        double cpuScore = Math.max(0, 100 - metrics.getCpuUsage());
        double memoryScore = Math.max(0, 100 - metrics.getMemoryUsage());
        double errorScore = Math.max(0, 100 - (metrics.getErrorRate() * 20)); // Scale error rate
        double responseScore = Math.max(0, 100 - (metrics.getResponseTime() / 10.0)); // Scale response time
        
        // Weighted average (cpu 30%, memory 30%, error 20%, response 20%)
        return (int) Math.round((cpuScore * 0.3) + (memoryScore * 0.3) + (errorScore * 0.2) + (responseScore * 0.2));
    }
    
    /**
     * Detect patterns in metrics data
     */
    private List<String> detectPatterns(EnhancedMetricsData metrics) {
        List<String> patterns = new ArrayList<>();
        
        MetricsData base = metrics.getBaseMetrics();
        
        // Detect resource usage patterns
        if (base.getCpuUsage() > 80 && metrics.getPredictedCpuUsage() > 85) {
            patterns.add("Consistently high CPU usage with upward trend");
        }
        
        if (base.getMemoryUsage() > 75 && metrics.getPredictedMemoryUsage() > 80) {
            patterns.add("Memory usage increasing, potential leak detected");
        }
        
        if (base.getErrorRate() > 1.0 && metrics.getPredictedErrorRate() > 1.5) {
            patterns.add("Error rate trending upward, stability concerns");
        }
        
        // Detect performance patterns
        if (base.getResponseTime() > 300) {
            patterns.add("Slow response times affecting user experience");
        }
        
        return patterns;
    }
    
    /**
     * Assess risk level based on anomalies and metrics
     */
    private String assessRisk(AnomalyReport report, EnhancedMetricsData metrics) {
        int anomalyCount = report.getAnomalies().size();
        int healthScore = metrics.getHealthScore();
        
        if (anomalyCount > 3 || healthScore < 40) {
            return "CRITICAL";
        } else if (anomalyCount > 1 || healthScore < 70) {
            return "HIGH";
        } else if (anomalyCount > 0 || healthScore < 85) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
    
    /**
     * Generate recommendations based on anomalies and metrics
     */
    private List<String> generateRecommendations(AnomalyReport report, EnhancedMetricsData metrics) {
        List<String> recommendations = new ArrayList<>();
        
        // Add base recommendations
        recommendations.addAll(super.generateInsights(metrics.getBaseMetrics()).getRecommendations());
        
        // Add advanced recommendations
        if (metrics.getHealthScore() < 70) {
            recommendations.add("Consider immediate performance optimization");
            recommendations.add("Review system architecture for bottlenecks");
        }
        
        if (metrics.getPredictedCpuUsage() > 90) {
            recommendations.add("Plan for CPU capacity expansion");
        }
        
        if (metrics.getPredictedMemoryUsage() > 85) {
            recommendations.add("Investigate memory leaks or consider scaling");
        }
        
        return recommendations;
    }
    
    // Enhanced data classes
    public static class EnhancedMetricsData {
        private MetricsData baseMetrics;
        private double predictedCpuUsage;
        private double predictedMemoryUsage;
        private double predictedErrorRate;
        private int healthScore;
        private long timestamp;
        
        // Getters and setters
        public MetricsData getBaseMetrics() { return baseMetrics; }
        public void setBaseMetrics(MetricsData baseMetrics) { this.baseMetrics = baseMetrics; }
        
        public double getPredictedCpuUsage() { return predictedCpuUsage; }
        public void setPredictedCpuUsage(double predictedCpuUsage) { this.predictedCpuUsage = predictedCpuUsage; }
        
        public double getPredictedMemoryUsage() { return predictedMemoryUsage; }
        public void setPredictedMemoryUsage(double predictedMemoryUsage) { this.predictedMemoryUsage = predictedMemoryUsage; }
        
        public double getPredictedErrorRate() { return predictedErrorRate; }
        public void setPredictedErrorRate(double predictedErrorRate) { this.predictedErrorRate = predictedErrorRate; }
        
        public int getHealthScore() { return healthScore; }
        public void setHealthScore(int healthScore) { this.healthScore = healthScore; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
    
    public static class EnhancedAnomalyReport {
        private AnomalyReport baseReport;
        private List<String> detectedPatterns;
        private String riskLevel;
        private List<String> recommendations;
        private long timestamp;
        
        public EnhancedAnomalyReport() {
            this.detectedPatterns = new ArrayList<>();
            this.recommendations = new ArrayList<>();
        }
        
        // Getters and setters
        public AnomalyReport getBaseReport() { return baseReport; }
        public void setBaseReport(AnomalyReport baseReport) { this.baseReport = baseReport; }
        
        public List<String> getDetectedPatterns() { return detectedPatterns; }
        public void setDetectedPatterns(List<String> detectedPatterns) { this.detectedPatterns = detectedPatterns; }
        
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
        
        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
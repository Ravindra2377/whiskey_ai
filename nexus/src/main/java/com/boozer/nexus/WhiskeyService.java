package com.boozer.nexus;

import com.boozer.nexus.WhiskeyTask;
import com.boozer.nexus.WhiskeyResult;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Service
public class WhiskeyService {
    
    private final WhiskeyOrchestrator orchestrator;
    private final MonitoringAgent monitoringAgent;
    private final FeedbackLoop feedbackLoop;
    private final PolicyEngine policyEngine;
    
    public WhiskeyService(WhiskeyOrchestrator orchestrator, 
                         MonitoringAgent monitoringAgent,
                         FeedbackLoop feedbackLoop,
                         PolicyEngine policyEngine) {
        this.orchestrator = orchestrator;
        this.monitoringAgent = monitoringAgent;
        this.feedbackLoop = feedbackLoop;
        this.policyEngine = policyEngine;
    }
    
    /**
     * Execute a task through the WHISKEY orchestrator
     */
    public CompletableFuture<WhiskeyResult> executeTask(WhiskeyTask task) {
        return orchestrator.executeTask(task);
    }
    
    /**
     * Scheduled task to monitor system health
     */
    @Scheduled(fixedRate = 30000) // Run every 30 seconds
    public void monitorSystemHealth() {
        try {
            // Collect metrics
            Map<String, Object> params = new HashMap<>();
            MonitoringAgent.MetricsData metrics = monitoringAgent.collectMetrics(params);
            
            // Detect anomalies
            MonitoringAgent.AnomalyReport anomalies = monitoringAgent.detectAnomalies(metrics);
            
            // If anomalies found, create a task to address them
            if (!anomalies.getAnomalies().isEmpty()) {
                WhiskeyTask task = new WhiskeyTask();
                task.setType(WhiskeyTask.TaskType.PERFORMANCE_OPTIMIZATION);
                task.setDescription("Auto-optimization based on anomaly detection");
                task.setCreatedBy("WHISKEY_SYSTEM");
                task.setParameters(new HashMap<>());
                
                // Execute optimization task
                orchestrator.executeTask(task);
            }
            
            // Send feedback to learning loop
            FeedbackLoop.FeedbackData feedback = new FeedbackLoop.FeedbackData(
                "PERFORMANCE", 
                "MONITORING_AGENT", 
                metrics
            );
            feedbackLoop.processFeedback(feedback);
            
        } catch (Exception e) {
            // Log error but don't fail the scheduled task
            e.printStackTrace();
        }
    }
    
    /**
     * Get system recommendations
     */
    public List<FeedbackLoop.Recommendation> getRecommendations() {
        return feedbackLoop.generateRecommendations();
    }
    
    /**
     * Validate a task against policies
     */
    public boolean validateTask(WhiskeyTask task) {
        return policyEngine.validateTask(task);
    }
    
    /**
     * Check safety compliance for a task
     */
    public PolicyEngine.PolicyCheckResult checkSafetyCompliance(WhiskeyTask task) {
        return policyEngine.checkSafetyCompliance(task);
    }
}

package com.boozer.nexus.service;

import com.boozer.nexus.agent.ProactiveRecommendation;
import com.boozer.nexus.agent.PotentialIssue;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IssuePredictionEngine {
    
    private final ClientEnvironmentService environmentService;
    private final MetricsCollectionService metricsService;
    private final MachineLearningService mlService;
    private final NotificationService notificationService;
    
    public IssuePredictionEngine(ClientEnvironmentService environmentService, 
                                MetricsCollectionService metricsService,
                                MachineLearningService mlService,
                                NotificationService notificationService) {
        this.environmentService = environmentService;
        this.metricsService = metricsService;
        this.mlService = mlService;
        this.notificationService = notificationService;
    }
    
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void predictPotentialIssues() {
        List<ClientEnvironment> environments = environmentService.getMonitoredEnvironments();
        
        environments.parallelStream().forEach(env -> {
            try {
                // Collect current metrics
                SystemMetrics currentMetrics = metricsService.collectCurrentMetrics(env);
                
                // Predict potential issues using ML
                List<PotentialIssue> predictions = mlService.predictIssues(
                    env.getHistoricalData(), currentMetrics, env.getTechnologyStack());
                
                // Generate proactive recommendations
                List<ProactiveRecommendation> recommendations = 
                    generateProactiveRecommendations(predictions);
                
                // Send intelligent alerts
                notificationService.sendIntelligentAlerts(env.getClientId(), recommendations);
            } catch (Exception e) {
                // Log error but continue processing other environments
                System.err.println("Error processing environment " + env.getClientId() + ": " + e.getMessage());
            }
        });
    }
    
    public List<ProactiveRecommendation> generateProactiveRecommendations(List<PotentialIssue> predictions) {
        return predictions.stream()
            .map(prediction -> ProactiveRecommendation.builder()
                .issueType(prediction.getIssueType())
                .severity(prediction.getSeverity())
                .recommendedAction(generateRecommendedAction(prediction))
                .estimatedImpact(prediction.getEstimatedImpact())
                .implementationTime(prediction.getEstimatedResolutionTime())
                .confidenceScore(prediction.getConfidenceScore())
                .build())
            .collect(Collectors.toList());
    }
    
    public String generateRecommendedAction(PotentialIssue prediction) {
        switch (prediction.getIssueType()) {
            case "MEMORY_LEAK":
                return "Implement memory monitoring and garbage collection optimization";
            case "CPU_BOTTLENECK":
                return "Scale compute resources or optimize CPU-intensive operations";
            case "DISK_SPACE":
                return "Clean up unused files and implement automated cleanup procedures";
            case "NETWORK_LATENCY":
                return "Optimize network configuration and implement CDN where applicable";
            case "SECURITY_VULNERABILITY":
                return "Apply security patches and update firewall rules";
            default:
                return "Perform detailed system analysis and implement appropriate optimizations";
        }
    }
}

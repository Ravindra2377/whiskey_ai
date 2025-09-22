package com.boozer.nexus.service;

import com.boozer.nexus.agent.ProactiveRecommendation;
import com.boozer.nexus.agent.PotentialIssue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class IssuePredictionEngineTest {
    
    private IssuePredictionEngine predictionEngine;
    
    @MockBean
    private ClientEnvironmentService environmentService;
    
    @MockBean
    private MetricsCollectionService metricsService;
    
    @MockBean
    private MachineLearningService mlService;
    
    @MockBean
    private NotificationService notificationService;
    
    @BeforeEach
    public void setUp() {
        predictionEngine = new IssuePredictionEngine(
            environmentService, metricsService, mlService, notificationService);
    }
    
    @Test
    public void testGenerateProactiveRecommendations() {
        // Create mock potential issues
        PotentialIssue memoryLeak = new PotentialIssue(
            "MEMORY_LEAK", "HIGH", "High memory usage detected", 
            0.8, "System instability", "4-6 hours");
            
        PotentialIssue cpuBottleneck = new PotentialIssue(
            "CPU_BOTTLENECK", "HIGH", "High CPU usage detected", 
            0.85, "Performance degradation", "2-4 hours");
        
        List<PotentialIssue> predictions = Arrays.asList(memoryLeak, cpuBottleneck);
        
        // Generate recommendations
        List<ProactiveRecommendation> recommendations = 
            predictionEngine.generateProactiveRecommendations(predictions);
        
        // Verify recommendations
        assertEquals(2, recommendations.size());
        assertEquals("MEMORY_LEAK", recommendations.get(0).getIssueType());
        assertEquals("Implement memory monitoring and garbage collection optimization", 
                    recommendations.get(0).getRecommendedAction());
        assertEquals("CPU_BOTTLENECK", recommendations.get(1).getIssueType());
        assertEquals("Scale compute resources or optimize CPU-intensive operations", 
                    recommendations.get(1).getRecommendedAction());
    }
    
    @Test
    public void testGenerateRecommendedAction() {
        // Test different issue types
        PotentialIssue memoryLeak = new PotentialIssue();
        memoryLeak.setIssueType("MEMORY_LEAK");
        assertEquals("Implement memory monitoring and garbage collection optimization",
                    predictionEngine.generateRecommendedAction(memoryLeak));
        
        PotentialIssue cpuBottleneck = new PotentialIssue();
        cpuBottleneck.setIssueType("CPU_BOTTLENECK");
        assertEquals("Scale compute resources or optimize CPU-intensive operations",
                    predictionEngine.generateRecommendedAction(cpuBottleneck));
        
        PotentialIssue diskSpace = new PotentialIssue();
        diskSpace.setIssueType("DISK_SPACE");
        assertEquals("Clean up unused files and implement automated cleanup procedures",
                    predictionEngine.generateRecommendedAction(diskSpace));
        
        PotentialIssue securityVuln = new PotentialIssue();
        securityVuln.setIssueType("SECURITY_VULNERABILITY");
        assertEquals("Apply security patches and update firewall rules",
                    predictionEngine.generateRecommendedAction(securityVuln));
        
        PotentialIssue unknown = new PotentialIssue();
        unknown.setIssueType("UNKNOWN");
        assertEquals("Perform detailed system analysis and implement appropriate optimizations",
                    predictionEngine.generateRecommendedAction(unknown));
    }
}

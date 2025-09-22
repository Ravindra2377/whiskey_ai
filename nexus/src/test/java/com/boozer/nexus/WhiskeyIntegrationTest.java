package com.boozer.nexus;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "nexus.max-concurrent-tasks=5",
    "nexus.task-timeout-seconds=60"
})
public class NexusIntegrationTest {

    @Test
    public void testFullSystemIntegration() throws Exception {
        // Initialize all components
        WhiskeyOrchestrator orchestrator = new WhiskeyOrchestrator();
        PolicyEngine policyEngine = new PolicyEngine();
        MonitoringAgent monitoringAgent = new MonitoringAgent();
        FeedbackLoop feedbackLoop = new FeedbackLoop();
        
        // Verify all components are initialized
        assertNotNull(orchestrator);
        assertNotNull(policyEngine);
        assertNotNull(monitoringAgent);
        assertNotNull(feedbackLoop);
        
        // Test policy engine
        WhiskeyTask task = new NexusTask();
        task.setType(WhiskeyTask.TaskType.CODE_MODIFICATION);
        task.setDescription("Test task");
        task.setCreatedBy("TEST_USER");
        task.setParameters(new HashMap<>());
        
        boolean isValid = policyEngine.validateTask(task);
        assertTrue(isValid, "Task should be valid");
        
        // Test task execution
        CompletableFuture<WhiskeyResult> futureResult = orchestrator.executeTask(task);
        WhiskeyResult result = futureResult.get();
        
        assertNotNull(result);
        assertTrue(result.isSuccessful());
        
        // Test monitoring agent
        Map<String, Object> monitoringParams = new HashMap<>();
        MonitoringAgent.MetricsData metrics = monitoringAgent.collectMetrics(monitoringParams);
        assertNotNull(metrics);
        
        // Test feedback loop
        FeedbackLoop.FeedbackData feedback = new FeedbackLoop.FeedbackData(
            "TEST", "TEST_SOURCE", "test data"
        );
        feedbackLoop.processFeedback(feedback);
        
        // Verify system can generate recommendations
        // Note: In this simple test, we may not have enough data for recommendations
        // but we can verify the method works without errors
        assertDoesNotThrow(() -> {
            feedbackLoop.generateRecommendations();
        });
    }

    @Test
    public void testDifferentTaskTypes() throws Exception {
        WhiskeyOrchestrator orchestrator = new NexusOrchestrator();
        
        // Test different task types
        WhiskeyTask.TaskType[] taskTypes = {
            WhiskeyTask.TaskType.CODE_MODIFICATION,
            WhiskeyTask.TaskType.CI_CD_OPERATION,
            WhiskeyTask.TaskType.INFRASTRUCTURE_OPERATION,
            WhiskeyTask.TaskType.MONITORING_OPERATION,
            WhiskeyTask.TaskType.FEATURE_DEVELOPMENT,
            WhiskeyTask.TaskType.BUG_FIX,
            WhiskeyTask.TaskType.SECURITY_PATCH,
            WhiskeyTask.TaskType.PERFORMANCE_OPTIMIZATION,
            WhiskeyTask.TaskType.DATABASE_MIGRATION,
            WhiskeyTask.TaskType.AUTONOMOUS_MAINTENANCE
        };
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("testParam", "testValue");
        
        for (WhiskeyTask.TaskType taskType : taskTypes) {
            WhiskeyTask task = new NexusTask();
            task.setType(taskType);
            task.setDescription("Test " + taskType + " task");
            task.setCreatedBy("SYSTEM");
            task.setParameters(parameters);
            
            CompletableFuture<WhiskeyResult> futureResult = orchestrator.executeTask(task);
            WhiskeyResult result = futureResult.get();
            
            assertNotNull(result);
            // All tasks should succeed in this simulation
            assertTrue(result.isSuccessful(), "Task " + taskType + " should succeed");
        }
    }
}

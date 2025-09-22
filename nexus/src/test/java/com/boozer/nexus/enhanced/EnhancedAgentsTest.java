package com.boozer.nexus.enhanced;

import com.boozer.nexus.WhiskeyTask;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EnhancedAgentsTest {

    @Test
    public void testEnhancedMonitoringAgentCreation() {
        EnhancedMonitoringAgent agent = new EnhancedMonitoringAgent();
        assertNotNull(agent, "EnhancedMonitoringAgent should be created successfully");
    }

    @Test
    public void testEnhancedCICDAgentCreation() {
        EnhancedCICDAgent agent = new EnhancedCICDAgent();
        assertNotNull(agent, "EnhancedCICDAgent should be created successfully");
    }

    @Test
    public void testEnhancedRepoAgentCreation() {
        EnhancedRepoAgent agent = new EnhancedRepoAgent();
        assertNotNull(agent, "EnhancedRepoAgent should be created successfully");
    }

    @Test
    public void testEnhancedInfraAgentCreation() {
        EnhancedInfraAgent agent = new EnhancedInfraAgent();
        assertNotNull(agent, "EnhancedInfraAgent should be created successfully");
    }

    @Test
    public void testEnhancedPolicyEngineCreation() {
        EnhancedPolicyEngine engine = new EnhancedPolicyEngine();
        assertNotNull(engine, "EnhancedPolicyEngine should be created successfully");
    }

    @Test
    public void testEnhancedWhiskeyOrchestratorCreation() {
        EnhancedWhiskeyOrchestrator orchestrator = new EnhancedWhiskeyOrchestrator();
        assertNotNull(orchestrator, "EnhancedWhiskeyOrchestrator should be created successfully");
    }

    @Test
    public void testEnhancedMonitoringAgentFunctionality() {
        EnhancedMonitoringAgent agent = new EnhancedMonitoringAgent();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("test", "value");

        EnhancedMonitoringAgent.EnhancedMetricsData metrics = agent.collectEnhancedMetrics(parameters);
        assertNotNull(metrics, "EnhancedMetricsData should be collected successfully");
        assertNotNull(metrics.getBaseMetrics(), "Base metrics should be present");
        assertTrue(metrics.getHealthScore() >= 0 && metrics.getHealthScore() <= 100, "Health score should be between 0 and 100");
    }

    @Test
    public void testEnhancedCICDAgentFunctionality() {
        EnhancedCICDAgent agent = new EnhancedCICDAgent();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("test", "value");

        EnhancedCICDAgent.EnhancedTestResult result = agent.runIntelligentTests(parameters);
        assertNotNull(result, "EnhancedTestResult should be generated successfully");
        assertNotNull(result.getBaseResult(), "Base test result should be present");
        assertTrue(result.getTestExecutionTime() > 0, "Test execution time should be positive");
    }
}

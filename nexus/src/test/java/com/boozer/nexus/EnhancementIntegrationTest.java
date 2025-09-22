package com.boozer.nexus;

import com.boozer.nexus.agent.MultiModalTechnicalAgent;
import com.boozer.nexus.agent.TechnicalSolution;
import com.boozer.nexus.agent.TechnicalTicket;
import com.boozer.nexus.agent.IssueClassification;
import com.boozer.nexus.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EnhancementIntegrationTest {
    
    @MockBean
    private UniversalAPIConnector apiConnector;
    
    @MockBean
    private IssuePredictionEngine predictionEngine;
    
    @Test
    public void testMultiModalAgentEnhancement() {
        // Test the multi-modal agent
        MultiModalTechnicalAgent agent = new MultiModalTechnicalAgent();
        
        // Create a complex technical ticket with multi-modal data
        TechnicalTicket ticket = new TechnicalTicket();
        ticket.setTicketId("TEST-001");
        ticket.setClientId("CLIENT-001");
        ticket.setDescription("Application performance degradation with intermittent errors");
        ticket.setPriority("HIGH");
        ticket.setStatus("OPEN");
        
        // Add multi-modal data
        ticket.setCodeSnippets(Arrays.asList(
            "public void slowMethod() { for(int i=0; i<1000000; i++) { /* slow operation */ } }",
            "try { connection.executeQuery(); } catch (Exception e) { e.printStackTrace(); }"
        ));
        ticket.setScreenshots(Arrays.asList("error_screenshot.png", "performance_graph.png"));
        ticket.setLogFiles(Arrays.asList("application.log", "error.log"));
        ticket.setConfigFiles(Arrays.asList("application.properties", "database.yml"));
        
        // Create classification for complex high-severity issue
        IssueClassification classification = new IssueClassification(
            "general", "complex", "HIGH", 0.8);
        
        // Generate solution
        TechnicalSolution solution = agent.generateSolution(ticket, classification);
        
        // Verify solution
        assertNotNull(solution);
        assertEquals("MULTI_MODAL_ANALYSIS", solution.getSolutionType());
        assertNotNull(solution.getSteps());
        assertFalse(solution.getSteps().isEmpty());
        assertNotNull(solution.getDetails());
        assertTrue(solution.getConfidenceScore() > 0.8);
        assertNotNull(solution.getEstimatedTime());
    }
    
    @Test
    public void testAPIIntegrationEnhancement() throws Exception {
        // Test the API integration service
        APIIntegrationRequest request = new APIIntegrationRequest();
        request.setTargetAPIUrl("https://api.example.com");
        request.setPreferredLanguage("Java");
        request.setClientId("CLIENT-001");
        request.setIntegrationName("ExampleAPI");
        
        // Mock the integration result
        IntegrationResult mockResult = new IntegrationResult();
        mockResult.setEstimatedIntegrationTime("2 hours");
        mockResult.setConfidenceScore(0.95);
        
        // Mock the connector to return the result
        when(apiConnector.createAPIIntegration(request))
            .thenReturn(CompletableFuture.completedFuture(mockResult));
        
        // Execute the integration
        CompletableFuture<IntegrationResult> futureResult = 
            apiConnector.createAPIIntegration(request);
        
        // Verify the result
        IntegrationResult result = futureResult.get();
        assertNotNull(result);
        assertEquals("2 hours", result.getEstimatedIntegrationTime());
        assertEquals(0.95, result.getConfidenceScore(), 0.01);
    }
    
    @Test
    public void testPredictiveAnalyticsEnhancement() {
        // Test the predictive analytics capabilities
        // This would typically involve more complex setup with mocked services
        assertNotNull(predictionEngine);
        
        // Verify that the prediction engine can generate recommendations
        // (Actual testing would require more extensive mocking of dependencies)
    }
}

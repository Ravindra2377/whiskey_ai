package com.boozer.nexus.agent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MultiModalTechnicalAgentTest {
    
    @Test
    public void testMultiModalAgentCreation() {
        // Test default constructor
        MultiModalTechnicalAgent agent = new MultiModalTechnicalAgent();
        assertEquals("multimodal", agent.getDomain());
        assertNotNull(agent.getKnowledgeBase());
        
        // Test that agent can handle high severity classifications
        IssueClassification classification = new IssueClassification("general", "complex", "high", 0.75);
        assertTrue(agent.canHandle(classification));
        
        // Test that agent cannot handle low severity classifications
        IssueClassification lowClassification = new IssueClassification("database", "performance", "low", 0.3);
        assertFalse(agent.canHandle(lowClassification));
    }
    
    @Test
    public void testMultiModalSolutionGeneration() {
        MultiModalTechnicalAgent agent = new MultiModalTechnicalAgent();
        
        // Create a technical ticket with multi-modal data
        TechnicalTicket ticket = new TechnicalTicket();
        ticket.setTicketId("MULTI-001");
        ticket.setClientId("CLIENT-001");
        ticket.setDescription("Application is slow and throwing errors");
        ticket.setPriority("HIGH");
        ticket.setStatus("OPEN");
        
        // Add multi-modal data
        ticket.setCodeSnippets(Arrays.asList("public void slowMethod() { // slow code }"));
        ticket.setScreenshots(Arrays.asList("screenshot1.png", "screenshot2.png"));
        ticket.setLogFiles(Arrays.asList("error.log", "application.log"));
        ticket.setConfigFiles(Arrays.asList("application.properties", "database.yml"));
        
        // Create classification
        IssueClassification classification = new IssueClassification("general", "complex", "high", 0.75);
        
        // Generate solution
        TechnicalSolution solution = agent.generateSolution(ticket, classification);
        
        // Verify solution
        assertNotNull(solution);
        assertEquals("MULTI_MODAL_ANALYSIS", solution.getSolutionType());
        assertNotNull(solution.getSteps());
        assertFalse(solution.getSteps().isEmpty());
        assertNotNull(solution.getDetails());
        assertTrue(solution.getConfidenceScore() > 0);
        assertNotNull(solution.getEstimatedTime());
    }
}

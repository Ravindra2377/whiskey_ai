package com.boozer.nexus.agent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class APIIntegrationAgentTest {
    
    @Test
    public void testAPIAgentCreation() {
        // Test default constructor
        APIIntegrationAgent agent = new APIIntegrationAgent();
        assertEquals("api", agent.getDomain());
        assertNotNull(agent.getKnowledgeBase());
        
        // Test that agent can handle api classification
        IssueClassification classification = new IssueClassification("api", "integration", "medium", 0.8);
        assertTrue(agent.canHandle(classification));
        
        // Test that agent cannot handle other classifications
        IssueClassification otherClassification = new IssueClassification("database", "performance", "medium", 0.8);
        assertFalse(agent.canHandle(otherClassification));
    }
    
    @Test
    public void testAPISolutionGeneration() {
        APIIntegrationAgent agent = new APIIntegrationAgent();
        
        // Create a technical ticket
        TechnicalTicket ticket = new TechnicalTicket();
        ticket.setTicketId("TICKET-002");
        ticket.setClientId("CLIENT-002");
        ticket.setDescription("Need to integrate with third-party payment API");
        ticket.setPriority("MEDIUM");
        ticket.setStatus("OPEN");
        
        // Create classification
        IssueClassification classification = new IssueClassification("api", "integration", "medium", 0.8);
        
        // Generate solution
        TechnicalSolution solution = agent.generateSolution(ticket, classification);
        
        // Verify solution
        assertNotNull(solution);
        assertEquals("API_INTEGRATION", solution.getSolutionType());
        assertNotNull(solution.getSteps());
        assertFalse(solution.getSteps().isEmpty());
        assertNotNull(solution.getDetails());
        assertTrue(solution.getConfidenceScore() > 0);
        assertNotNull(solution.getEstimatedTime());
    }
}

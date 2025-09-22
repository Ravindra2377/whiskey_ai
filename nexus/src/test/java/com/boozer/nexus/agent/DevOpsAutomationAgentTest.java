package com.boozer.nexus.agent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DevOpsAutomationAgentTest {
    
    @Test
    public void testDevOpsAgentCreation() {
        // Test default constructor
        DevOpsAutomationAgent agent = new DevOpsAutomationAgent();
        assertEquals("devops", agent.getDomain());
        assertNotNull(agent.getKnowledgeBase());
        
        // Test that agent can handle devops classification
        IssueClassification classification = new IssueClassification("devops", "automation", "medium", 0.8);
        assertTrue(agent.canHandle(classification));
        
        // Test that agent cannot handle other classifications
        IssueClassification otherClassification = new IssueClassification("database", "performance", "medium", 0.8);
        assertFalse(agent.canHandle(otherClassification));
    }
    
    @Test
    public void testDevOpsSolutionGeneration() {
        DevOpsAutomationAgent agent = new DevOpsAutomationAgent();
        
        // Create a technical ticket
        TechnicalTicket ticket = new TechnicalTicket();
        ticket.setTicketId("TICKET-001");
        ticket.setClientId("CLIENT-001");
        ticket.setDescription("CI/CD pipeline is slow and deployments are failing");
        ticket.setPriority("HIGH");
        ticket.setStatus("OPEN");
        
        // Create classification
        IssueClassification classification = new IssueClassification("devops", "pipeline", "high", 0.9);
        
        // Generate solution
        TechnicalSolution solution = agent.generateSolution(ticket, classification);
        
        // Verify solution
        assertNotNull(solution);
        assertEquals("DEVOPS_AUTOMATION", solution.getSolutionType());
        assertNotNull(solution.getSteps());
        assertFalse(solution.getSteps().isEmpty());
        assertNotNull(solution.getDetails());
        assertTrue(solution.getConfidenceScore() > 0);
        assertNotNull(solution.getEstimatedTime());
    }
}

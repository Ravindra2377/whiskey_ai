package com.boozer.nexus.service;

import com.boozer.nexus.agent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TechnicalSupportService {
    
    @Autowired
    private DatabaseSpecialistAgent databaseAgent;
    
    @Autowired
    private CloudInfrastructureAgent cloudAgent;
    
    @Autowired
    private SecurityAnalysisAgent securityAgent;
    
    @Autowired
    private DevOpsAutomationAgent devOpsAgent;
    
    @Autowired
    private APIIntegrationAgent apiAgent;
    
    @Autowired
    private MultiModalTechnicalAgent multiModalAgent;
    
    private List<SpecializedAIAgent> agents;
    
    public TechnicalSupportService() {
        agents = new ArrayList<>();
    }
    
    /**
     * Initialize agents
     */
    public void initializeAgents() {
        agents.add(databaseAgent);
        agents.add(cloudAgent);
        agents.add(securityAgent);
        agents.add(devOpsAgent);
        agents.add(apiAgent);
        agents.add(multiModalAgent);
    }
    
    /**
     * Process a technical ticket and generate a solution
     */
    public TechnicalSolution processTicket(TechnicalTicket ticket) {
        // Classify the issue
        IssueClassification classification = classifyIssue(ticket);
        
        // First try the multi-modal agent for complex issues
        if (multiModalAgent.canHandle(classification)) {
            TechnicalSolution solution = multiModalAgent.generateSolution(ticket, classification);
            if (solution.getConfidenceScore() > 0.8) {
                return solution;
            }
        }
        
        // Find the appropriate agent to handle the issue
        for (SpecializedAIAgent agent : agents) {
            if (agent != multiModalAgent && agent.canHandle(classification)) {
                return agent.generateSolution(ticket, classification);
            }
        }
        
        // If no agent can handle the issue, return a default solution
        return createDefaultSolution(classification);
    }
    
    /**
     * Classify an issue based on the ticket description
     */
    private IssueClassification classifyIssue(TechnicalTicket ticket) {
        String description = ticket.getDescription().toLowerCase();
        
        if (description.contains("database") || description.contains("query") || description.contains("sql")) {
            return new IssueClassification("database", "performance", "medium", 0.8);
        } else if (description.contains("aws") || description.contains("cloud") || description.contains("server")) {
            return new IssueClassification("cloud", "infrastructure", "high", 0.9);
        } else if (description.contains("security") || description.contains("vulnerability") || description.contains("attack")) {
            return new IssueClassification("security", "vulnerability", "critical", 0.95);
        } else if (description.contains("devops") || description.contains("pipeline") || description.contains("deployment")) {
            return new IssueClassification("devops", "automation", "medium", 0.85);
        } else if (description.contains("api") || description.contains("integration") || description.contains("connect")) {
            return new IssueClassification("api", "integration", "medium", 0.8);
        } else {
            // For complex issues, mark as high severity to trigger multi-modal analysis
            return new IssueClassification("general", "complex", "high", 0.75);
        }
    }
    
    /**
     * Create a default solution for unhandled issues
     */
    private TechnicalSolution createDefaultSolution(IssueClassification classification) {
        List<String> steps = Arrays.asList(
            "Review the issue description in detail",
            "Gather additional information from the client",
            "Escalate to human technical support team",
            "Provide regular updates on progress"
        );
        
        Map<String, Object> details = new HashMap<>();
        details.put("escalationRequired", true);
        details.put("supportTeam", "Human Technical Support");
        details.put("estimatedResponseTime", "24 hours");
        
        return TechnicalSolution.builder()
            .solutionType("ESCALATION")
            .steps(steps)
            .details(details)
            .confidenceScore(0.3)
            .estimatedTime("24-48 hours")
            .build();
    }
    
    /**
     * Add a new specialized agent
     */
    public void addAgent(SpecializedAIAgent agent) {
        agents.add(agent);
    }
    
    /**
     * Get all available agents
     */
    public List<SpecializedAIAgent> getAgents() {
        return new ArrayList<>(agents);
    }
}

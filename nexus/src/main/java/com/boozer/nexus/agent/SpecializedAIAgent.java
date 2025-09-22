package com.boozer.nexus.agent;

import com.boozer.nexus.model.AIModel;

public abstract class SpecializedAIAgent {
    
    protected String domain;
    protected AIModel model;
    protected String knowledgeBase;
    
    public SpecializedAIAgent(String domain, AIModel model, String knowledgeBase) {
        this.domain = domain;
        this.model = model;
        this.knowledgeBase = knowledgeBase;
    }
    
    /**
     * Generate a technical solution for a given ticket
     */
    public abstract TechnicalSolution generateSolution(TechnicalTicket ticket, IssueClassification classification);
    
    /**
     * Check if this agent can handle the given issue classification
     */
    public abstract boolean canHandle(IssueClassification classification);
    
    /**
     * Get the domain of this agent
     */
    public String getDomain() {
        return domain;
    }
    
    /**
     * Get the model used by this agent
     */
    public AIModel getModel() {
        return model;
    }
    
    /**
     * Get the knowledge base used by this agent
     */
    public String getKnowledgeBase() {
        return knowledgeBase;
    }
}

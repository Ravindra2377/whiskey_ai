package com.boozer.nexus.agents.models;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Agent memory system models
 */

/**
 * Agent memory container
 */
class AgentMemory {
    private String agentId;
    private LocalDateTime creationTime;
    private LocalDateTime lastUpdate;
    private List<AgentExperience> experiences;
    private List<LearningExperience> learningExperiences;
    private List<KnowledgeItem> knowledgeBase;
    private List<ActionMemory> actionHistory;
    private List<AgentMessage> messageHistory;
    private Map<String, Object> persistentData;
    private double memoryCapacity;
    private double currentUsage;

    public AgentMemory() {
        this.experiences = new ArrayList<>();
        this.learningExperiences = new ArrayList<>();
        this.knowledgeBase = new ArrayList<>();
        this.actionHistory = new ArrayList<>();
        this.messageHistory = new ArrayList<>();
        this.persistentData = new HashMap<>();
        this.memoryCapacity = 1000.0;
        this.currentUsage = 0.0;
    }

    // Getters and Setters
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }

    public LocalDateTime getCreationTime() { return creationTime; }
    public void setCreationTime(LocalDateTime creationTime) { this.creationTime = creationTime; }

    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }

    public List<AgentExperience> getExperiences() { return experiences; }
    public void setExperiences(List<AgentExperience> experiences) { this.experiences = experiences; }

    public List<LearningExperience> getLearningExperiences() { return learningExperiences; }
    public void setLearningExperiences(List<LearningExperience> learningExperiences) { this.learningExperiences = learningExperiences; }

    public List<KnowledgeItem> getKnowledgeBase() { return knowledgeBase; }
    public void setKnowledgeBase(List<KnowledgeItem> knowledgeBase) { this.knowledgeBase = knowledgeBase; }

    public List<ActionMemory> getActionHistory() { return actionHistory; }
    public void setActionHistory(List<ActionMemory> actionHistory) { this.actionHistory = actionHistory; }

    public List<AgentMessage> getMessageHistory() { return messageHistory; }
    public void setMessageHistory(List<AgentMessage> messageHistory) { this.messageHistory = messageHistory; }

    public Map<String, Object> getPersistentData() { return persistentData; }
    public void setPersistentData(Map<String, Object> persistentData) { this.persistentData = persistentData; }

    public double getMemoryCapacity() { return memoryCapacity; }
    public void setMemoryCapacity(double memoryCapacity) { this.memoryCapacity = memoryCapacity; }

    public double getCurrentUsage() { return currentUsage; }
    public void setCurrentUsage(double currentUsage) { this.currentUsage = currentUsage; }
}

/**
 * Agent experience record
 */
class AgentExperience {
    private String experienceId;
    private String agentId;
    private LocalDateTime timestamp;
    private EnvironmentPerception perception;
    private List<AgentAction> actions;
    private String outcome;
    private double significance;
    private Map<String, Object> context;
    private List<String> lessons;

    public AgentExperience() {
        this.experienceId = UUID.randomUUID().toString();
        this.actions = new ArrayList<>();
        this.context = new HashMap<>();
        this.lessons = new ArrayList<>();
        this.significance = 0.5;
    }

    // Getters and Setters
    public String getExperienceId() { return experienceId; }
    public void setExperienceId(String experienceId) { this.experienceId = experienceId; }

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public EnvironmentPerception getPerception() { return perception; }
    public void setPerception(EnvironmentPerception perception) { this.perception = perception; }

    public List<AgentAction> getActions() { return actions; }
    public void setActions(List<AgentAction> actions) { this.actions = actions; }

    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }

    public double getSignificance() { return significance; }
    public void setSignificance(double significance) { this.significance = significance; }

    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }

    public List<String> getLessons() { return lessons; }
    public void setLessons(List<String> lessons) { this.lessons = lessons; }
}

/**
 * Learning experience record
 */
class LearningExperience {
    private String learningId;
    private String agentId;
    private LocalDateTime timestamp;
    private String source;
    private String learningType;
    private String knowledgeGained;
    private double confidence;
    private String validationMethod;
    private Map<String, Object> metadata;

    public LearningExperience() {
        this.learningId = UUID.randomUUID().toString();
        this.metadata = new HashMap<>();
        this.confidence = 0.7;
    }

    // Getters and Setters
    public String getLearningId() { return learningId; }
    public void setLearningId(String learningId) { this.learningId = learningId; }

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getLearningType() { return learningType; }
    public void setLearningType(String learningType) { this.learningType = learningType; }

    public String getKnowledgeGained() { return knowledgeGained; }
    public void setKnowledgeGained(String knowledgeGained) { this.knowledgeGained = knowledgeGained; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public String getValidationMethod() { return validationMethod; }
    public void setValidationMethod(String validationMethod) { this.validationMethod = validationMethod; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}

/**
 * Knowledge item in agent's knowledge base
 */
class KnowledgeItem {
    private String knowledgeId;
    private String content;
    private String source;
    private LocalDateTime timestamp;
    private double reliability;
    private String category;
    private List<String> tags;
    private Map<String, Object> attributes;
    private int accessCount;
    private LocalDateTime lastAccessed;

    public KnowledgeItem() {
        this.knowledgeId = UUID.randomUUID().toString();
        this.tags = new ArrayList<>();
        this.attributes = new HashMap<>();
        this.accessCount = 0;
        this.reliability = 0.5;
    }

    // Getters and Setters
    public String getKnowledgeId() { return knowledgeId; }
    public void setKnowledgeId(String knowledgeId) { this.knowledgeId = knowledgeId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public double getReliability() { return reliability; }
    public void setReliability(double reliability) { this.reliability = reliability; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }

    public int getAccessCount() { return accessCount; }
    public void setAccessCount(int accessCount) { this.accessCount = accessCount; }

    public LocalDateTime getLastAccessed() { return lastAccessed; }
    public void setLastAccessed(LocalDateTime lastAccessed) { this.lastAccessed = lastAccessed; }
}

/**
 * Action memory record
 */
class ActionMemory {
    private String memoryId;
    private AgentAction action;
    private LocalDateTime timestamp;
    private EnvironmentPerception context;
    private double effectiveness;
    private String reflection;
    private List<String> improvements;

    public ActionMemory() {
        this.memoryId = UUID.randomUUID().toString();
        this.improvements = new ArrayList<>();
        this.effectiveness = 0.5;
    }

    // Getters and Setters
    public String getMemoryId() { return memoryId; }
    public void setMemoryId(String memoryId) { this.memoryId = memoryId; }

    public AgentAction getAction() { return action; }
    public void setAction(AgentAction action) { this.action = action; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public EnvironmentPerception getContext() { return context; }
    public void setContext(EnvironmentPerception context) { this.context = context; }

    public double getEffectiveness() { return effectiveness; }
    public void setEffectiveness(double effectiveness) { this.effectiveness = effectiveness; }

    public String getReflection() { return reflection; }
    public void setReflection(String reflection) { this.reflection = reflection; }

    public List<String> getImprovements() { return improvements; }
    public void setImprovements(List<String> improvements) { this.improvements = improvements; }
}
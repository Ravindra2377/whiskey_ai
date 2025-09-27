package com.boozer.nexus.agents;

import com.boozer.nexus.agents.models.*;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Agent Communication Manager
 * 
 * Handles message passing and communication protocols between agents
 */
@Component
class AgentCommunicationManager {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentCommunicationManager.class);
    
    private final Map<String, List<AgentMessage>> agentInboxes = new ConcurrentHashMap<>();
    private final Map<String, AgentMessage> messageRegistry = new ConcurrentHashMap<>();
    
    /**
     * Send message between agents
     */
    public boolean sendMessage(AgentMessage message) {
        try {
            message.setTimestamp(LocalDateTime.now());
            messageRegistry.put(message.getMessageId(), message);
            
            agentInboxes.computeIfAbsent(message.getReceiverId(), k -> new ArrayList<>())
                       .add(message);
            
            logger.debug("Message sent from {} to {}: {}", 
                message.getSenderId(), message.getReceiverId(), message.getContent());
            
            return true;
        } catch (Exception e) {
            logger.error("Failed to send message: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all messages for an agent
     */
    public List<AgentMessage> getMessages(String agentId) {
        return agentInboxes.getOrDefault(agentId, new ArrayList<>());
    }
    
    /**
     * Get unprocessed messages for an agent
     */
    public List<AgentMessage> getUnprocessedMessages(String agentId) {
        return getMessages(agentId).stream()
            .filter(msg -> !msg.isProcessed())
            .toList();
    }
    
    /**
     * Mark message as processed
     */
    public void markMessageProcessed(String messageId) {
        AgentMessage message = messageRegistry.get(messageId);
        if (message != null) {
            message.setProcessed(true);
        }
    }
    
    /**
     * Broadcast message to multiple agents
     */
    public void broadcastMessage(String senderId, List<String> receiverIds, String content, String messageType) {
        for (String receiverId : receiverIds) {
            AgentMessage message = new AgentMessage();
            message.setSenderId(senderId);
            message.setReceiverId(receiverId);
            message.setContent(content);
            message.setMessageType(messageType);
            
            sendMessage(message);
        }
    }
}

/**
 * Task Delegation Engine
 * 
 * Manages task assignment and collaboration between agents
 */
@Component
class TaskDelegationEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskDelegationEngine.class);
    
    private final Map<String, Task> taskRegistry = new ConcurrentHashMap<>();
    private final Map<String, List<Task>> agentTasks = new ConcurrentHashMap<>();
    private final Map<String, CollaborationSession> collaborationSessions = new ConcurrentHashMap<>();
    
    /**
     * Assign task to agent
     */
    public void assignTask(Task task) {
        taskRegistry.put(task.getTaskId(), task);
        agentTasks.computeIfAbsent(task.getAssigneeId(), k -> new ArrayList<>())
                  .add(task);
        
        logger.info("Task {} assigned to agent {}", task.getTaskId(), task.getAssigneeId());
    }
    
    /**
     * Get assigned tasks for agent
     */
    public List<Task> getAssignedTasks(String agentId) {
        return agentTasks.getOrDefault(agentId, new ArrayList<>())
            .stream()
            .filter(task -> task.getStatus() == TaskStatus.ASSIGNED || task.getStatus() == TaskStatus.IN_PROGRESS)
            .toList();
    }
    
    /**
     * Update task status
     */
    public void updateTaskStatus(String taskId, TaskStatus status) {
        Task task = taskRegistry.get(taskId);
        if (task != null) {
            task.setStatus(status);
            if (status == TaskStatus.COMPLETED) {
                task.setCompletionTime(LocalDateTime.now());
            }
        }
    }
    
    /**
     * Initiate collaboration session
     */
    public boolean initiateCollaboration(CollaborationSession session) {
        try {
            collaborationSessions.put(session.getSessionId(), session);
            logger.info("Collaboration session {} initiated by {}", 
                session.getSessionId(), session.getInitiatorId());
            return true;
        } catch (Exception e) {
            logger.error("Failed to initiate collaboration: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get collaboration session
     */
    public CollaborationSession getCollaborationSession(String sessionId) {
        return collaborationSessions.get(sessionId);
    }
}

/**
 * Agent Memory Manager
 * 
 * Manages agent memory, experiences, and knowledge
 */
@Component
class AgentMemoryManager {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentMemoryManager.class);
    
    private final Map<String, AgentMemory> agentMemories = new ConcurrentHashMap<>();
    
    /**
     * Initialize memory for agent
     */
    public AgentMemory initializeMemory(String agentId) {
        AgentMemory memory = new AgentMemory();
        memory.setAgentId(agentId);
        memory.setCreationTime(LocalDateTime.now());
        memory.setLastUpdate(LocalDateTime.now());
        
        agentMemories.put(agentId, memory);
        
        logger.debug("Memory initialized for agent {}", agentId);
        return memory;
    }
    
    /**
     * Record experience in agent memory
     */
    public void recordExperience(String agentId, AgentExperience experience) {
        AgentMemory memory = agentMemories.get(agentId);
        if (memory != null) {
            memory.getExperiences().add(experience);
            memory.setLastUpdate(LocalDateTime.now());
            
            // Maintain memory limits
            maintainMemoryLimits(memory);
        }
    }
    
    /**
     * Record learning experience
     */
    public void recordLearningExperience(String agentId, LearningExperience experience) {
        AgentMemory memory = agentMemories.get(agentId);
        if (memory != null) {
            memory.getLearningExperiences().add(experience);
            memory.setLastUpdate(LocalDateTime.now());
        }
    }
    
    /**
     * Add knowledge to agent's knowledge base
     */
    public void addKnowledge(String agentId, KnowledgeItem knowledge) {
        AgentMemory memory = agentMemories.get(agentId);
        if (memory != null) {
            memory.getKnowledgeBase().add(knowledge);
            memory.setLastUpdate(LocalDateTime.now());
        }
    }
    
    /**
     * Record action in memory
     */
    public void recordActionMemory(String agentId, ActionMemory actionMemory) {
        AgentMemory memory = agentMemories.get(agentId);
        if (memory != null) {
            memory.getActionHistory().add(actionMemory);
            memory.setLastUpdate(LocalDateTime.now());
        }
    }
    
    /**
     * Record message in memory
     */
    public void recordMessage(String agentId, AgentMessage message) {
        AgentMemory memory = agentMemories.get(agentId);
        if (memory != null) {
            memory.getMessageHistory().add(message);
            memory.setLastUpdate(LocalDateTime.now());
        }
    }
    
    /**
     * Search knowledge base
     */
    public List<KnowledgeItem> searchKnowledge(String agentId, String query) {
        AgentMemory memory = agentMemories.get(agentId);
        if (memory == null) return new ArrayList<>();
        
        return memory.getKnowledgeBase().stream()
            .filter(knowledge -> knowledge.getContent().toLowerCase().contains(query.toLowerCase()) ||
                               knowledge.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(query.toLowerCase())))
            .sorted((a, b) -> Double.compare(b.getReliability(), a.getReliability()))
            .toList();
    }
    
    /**
     * Get recent experiences
     */
    public List<AgentExperience> getRecentExperiences(String agentId, int count) {
        AgentMemory memory = agentMemories.get(agentId);
        if (memory == null) return new ArrayList<>();
        
        return memory.getExperiences().stream()
            .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
            .limit(count)
            .toList();
    }
    
    /**
     * Maintain memory limits to prevent overflow
     */
    private void maintainMemoryLimits(AgentMemory memory) {
        // Remove oldest experiences if too many
        if (memory.getExperiences().size() > 1000) {
            memory.getExperiences().sort(Comparator.comparing(AgentExperience::getTimestamp));
            memory.setExperiences(new ArrayList<>(memory.getExperiences().subList(100, memory.getExperiences().size())));
        }
        
        // Remove old messages if too many
        if (memory.getMessageHistory().size() > 500) {
            memory.getMessageHistory().sort(Comparator.comparing(AgentMessage::getTimestamp));
            memory.setMessageHistory(new ArrayList<>(memory.getMessageHistory().subList(100, memory.getMessageHistory().size())));
        }
        
        // Remove old action history if too many
        if (memory.getActionHistory().size() > 500) {
            memory.getActionHistory().sort(Comparator.comparing(ActionMemory::getTimestamp));
            memory.setActionHistory(new ArrayList<>(memory.getActionHistory().subList(100, memory.getActionHistory().size())));
        }
    }
}

/**
 * Goal Planning Engine
 * 
 * Plans actions based on agent goals and current state
 */
@Component
class GoalPlanningEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(GoalPlanningEngine.class);
    
    /**
     * Plan actions for agent based on goals and perception
     */
    public List<AgentAction> planActions(AutonomousAgent agent, EnvironmentPerception perception) {
        List<AgentAction> plannedActions = new ArrayList<>();
        
        // Get active goals sorted by priority
        List<AgentGoal> activeGoals = agent.getGoals().stream()
            .filter(goal -> goal.getStatus() == GoalStatus.ACTIVE)
            .sorted((a, b) -> Double.compare(b.getPriority(), a.getPriority()))
            .toList();
        
        for (AgentGoal goal : activeGoals) {
            List<AgentAction> goalActions = planActionsForGoal(agent, goal, perception);
            plannedActions.addAll(goalActions);
            
            // Limit total actions per cycle
            if (plannedActions.size() >= 3) break;
        }
        
        // Add reactive actions based on perception
        List<AgentAction> reactiveActions = planReactiveActions(agent, perception);
        plannedActions.addAll(reactiveActions);
        
        // Prioritize and limit actions
        return prioritizeActions(plannedActions).stream()
            .limit(5)
            .toList();
    }
    
    /**
     * Plan actions for specific goal
     */
    private List<AgentAction> planActionsForGoal(AutonomousAgent agent, AgentGoal goal, EnvironmentPerception perception) {
        List<AgentAction> actions = new ArrayList<>();
        
        switch (goal.getGoalType()) {
            case "learning":
                actions.addAll(planLearningActions(agent, goal, perception));
                break;
            case "collaboration":
                actions.addAll(planCollaborationActions(agent, goal, perception));
                break;
            case "optimization":
                actions.addAll(planOptimizationActions(agent, goal, perception));
                break;
            case "exploration":
                actions.addAll(planExplorationActions(agent, goal, perception));
                break;
            case "task_completion":
                actions.addAll(planTaskCompletionActions(agent, goal, perception));
                break;
            default:
                actions.addAll(planGenericActions(agent, goal, perception));
        }
        
        return actions;
    }
    
    /**
     * Plan learning actions
     */
    private List<AgentAction> planLearningActions(AutonomousAgent agent, AgentGoal goal, EnvironmentPerception perception) {
        List<AgentAction> actions = new ArrayList<>();
        
        AgentAction learnAction = new AgentAction();
        learnAction.setActionId(UUID.randomUUID().toString());
        learnAction.setActionType("learn");
        learnAction.setDescription("Execute learning based on goal: " + goal.getDescription());
        learnAction.setPriority(goal.getPriority());
        
        Map<String, Object> params = new HashMap<>();
        params.put("source", "environment");
        params.put("type", "observational");
        params.put("goal_id", goal.getGoalId());
        learnAction.setParameters(params);
        
        actions.add(learnAction);
        return actions;
    }
    
    /**
     * Plan collaboration actions
     */
    private List<AgentAction> planCollaborationActions(AutonomousAgent agent, AgentGoal goal, EnvironmentPerception perception) {
        List<AgentAction> actions = new ArrayList<>();
        
        // Find potential collaboration partners
        List<String> availableAgents = perception.getEnvironmentData().keySet().stream()
            .filter(key -> key.startsWith("agent_"))
            .toList();
        
        if (!availableAgents.isEmpty()) {
            AgentAction collaborateAction = new AgentAction();
            collaborateAction.setActionId(UUID.randomUUID().toString());
            collaborateAction.setActionType("collaborate");
            collaborateAction.setDescription("Initiate collaboration for goal: " + goal.getDescription());
            collaborateAction.setPriority(goal.getPriority());
            
            Map<String, Object> params = new HashMap<>();
            params.put("type", "knowledge_sharing");
            params.put("participants", availableAgents.subList(0, Math.min(2, availableAgents.size())));
            params.put("goal_id", goal.getGoalId());
            collaborateAction.setParameters(params);
            
            actions.add(collaborateAction);
        }
        
        return actions;
    }
    
    /**
     * Plan optimization actions
     */
    private List<AgentAction> planOptimizationActions(AutonomousAgent agent, AgentGoal goal, EnvironmentPerception perception) {
        List<AgentAction> actions = new ArrayList<>();
        
        AgentAction optimizeAction = new AgentAction();
        optimizeAction.setActionId(UUID.randomUUID().toString());
        optimizeAction.setActionType("analyze");
        optimizeAction.setDescription("Analyze for optimization opportunities");
        optimizeAction.setPriority(goal.getPriority());
        
        Map<String, Object> params = new HashMap<>();
        params.put("target", "self_performance");
        params.put("type", "optimization");
        params.put("goal_id", goal.getGoalId());
        optimizeAction.setParameters(params);
        
        actions.add(optimizeAction);
        return actions;
    }
    
    /**
     * Plan exploration actions
     */
    private List<AgentAction> planExplorationActions(AutonomousAgent agent, AgentGoal goal, EnvironmentPerception perception) {
        List<AgentAction> actions = new ArrayList<>();
        
        AgentAction exploreAction = new AgentAction();
        exploreAction.setActionId(UUID.randomUUID().toString());
        exploreAction.setActionType("analyze");
        exploreAction.setDescription("Explore environment for new opportunities");
        exploreAction.setPriority(goal.getPriority());
        
        Map<String, Object> params = new HashMap<>();
        params.put("target", "environment");
        params.put("type", "exploration");
        params.put("goal_id", goal.getGoalId());
        exploreAction.setParameters(params);
        
        actions.add(exploreAction);
        return actions;
    }
    
    /**
     * Plan task completion actions
     */
    private List<AgentAction> planTaskCompletionActions(AutonomousAgent agent, AgentGoal goal, EnvironmentPerception perception) {
        List<AgentAction> actions = new ArrayList<>();
        
        // Find assigned tasks
        for (Task task : perception.getAssignedTasks()) {
            if (task.getStatus() == TaskStatus.ASSIGNED) {
                AgentAction taskAction = createTaskAction(task, goal.getPriority());
                actions.add(taskAction);
            }
        }
        
        return actions;
    }
    
    /**
     * Plan generic actions
     */
    private List<AgentAction> planGenericActions(AutonomousAgent agent, AgentGoal goal, EnvironmentPerception perception) {
        List<AgentAction> actions = new ArrayList<>();
        
        AgentAction genericAction = new AgentAction();
        genericAction.setActionId(UUID.randomUUID().toString());
        genericAction.setActionType("analyze");
        genericAction.setDescription("Generic action for goal: " + goal.getDescription());
        genericAction.setPriority(goal.getPriority());
        
        Map<String, Object> params = new HashMap<>();
        params.put("target", "current_state");
        params.put("type", "general");
        params.put("goal_id", goal.getGoalId());
        genericAction.setParameters(params);
        
        actions.add(genericAction);
        return actions;
    }
    
    /**
     * Plan reactive actions based on perception
     */
    private List<AgentAction> planReactiveActions(AutonomousAgent agent, EnvironmentPerception perception) {
        List<AgentAction> actions = new ArrayList<>();
        
        // React to messages
        for (AgentMessage message : perception.getMessages()) {
            if (!message.isProcessed() && message.getPriority() > 0.7) {
                AgentAction replyAction = new AgentAction();
                replyAction.setActionId(UUID.randomUUID().toString());
                replyAction.setActionType("communicate");
                replyAction.setDescription("Reply to high-priority message");
                replyAction.setPriority(message.getPriority());
                
                Map<String, Object> params = new HashMap<>();
                params.put("target_agent", message.getSenderId());
                params.put("message", "Acknowledged: " + message.getContent());
                params.put("message_type", "reply");
                replyAction.setParameters(params);
                
                actions.add(replyAction);
            }
        }
        
        return actions;
    }
    
    /**
     * Create action for task
     */
    private AgentAction createTaskAction(Task task, double priority) {
        AgentAction action = new AgentAction();
        action.setActionId(UUID.randomUUID().toString());
        action.setActionType(task.getTaskType());
        action.setDescription("Execute task: " + task.getDescription());
        action.setPriority(Math.max(priority, task.getPriority()));
        
        Map<String, Object> params = new HashMap<>();
        params.put("task_id", task.getTaskId());
        params.put("task_type", task.getTaskType());
        params.putAll(task.getParameters());
        action.setParameters(params);
        
        return action;
    }
    
    /**
     * Prioritize actions
     */
    private List<AgentAction> prioritizeActions(List<AgentAction> actions) {
        return actions.stream()
            .sorted((a, b) -> Double.compare(b.getPriority(), a.getPriority()))
            .toList();
    }
}
package com.boozer.nexus.learning;

import com.boozer.nexus.learning.models.*;
import com.boozer.nexus.ai.ExternalAIIntegrationService;
import com.boozer.nexus.consciousness.ConsciousnessEngine;
import com.boozer.nexus.consciousness.models.ConsciousnessInput;
import com.boozer.nexus.consciousness.models.ConsciousnessSession;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Continual Learning Engine
 * 
 * Advanced real-time learning system with catastrophic forgetting prevention,
 * experience replay, meta-learning, knowledge distillation, and adaptive architecture.
 */
@Component
public class ContinualLearningEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(ContinualLearningEngine.class);
    
    @Autowired
    private ExternalAIIntegrationService aiIntegrationService;
    
    @Autowired
    private ConsciousnessEngine consciousnessEngine;
    
    @Autowired
    private ExperienceReplayManager replayManager;
    
    @Autowired
    private MetaLearningEngine metaLearningEngine;
    
    @Autowired
    private KnowledgeDistillationEngine distillationEngine;
    
    @Autowired
    private ArchitectureAdaptationEngine adaptationEngine;
    
    @Autowired
    private CatastrophicForgettingPrevention forgettingPrevention;
    
    private final Map<String, LearningSession> activeSessions = new ConcurrentHashMap<>();
    private final ExecutorService learningExecutor = Executors.newFixedThreadPool(8);
    private final AtomicLong sessionIdCounter = new AtomicLong(1);
    private final LearningMetrics globalMetrics = new LearningMetrics();

    /**
     * Start a new continual learning session
     */
    public LearningSession startLearningSession(LearningConfiguration config) {
        logger.info("Starting continual learning session with strategy: {}", config.getLearningStrategy());
        
        String sessionId = "learning-session-" + sessionIdCounter.getAndIncrement();
        
        LearningSession session = new LearningSession();
        session.setSessionId(sessionId);
        session.setConfiguration(config);
        session.setStatus(LearningStatus.ACTIVE);
        session.setStartTime(LocalDateTime.now());
        session.setMetrics(new LearningMetrics());
        session.setKnowledgeBase(new ArrayList<>());
        session.setExperienceBuffer(new ArrayList<>());
        
        // Initialize consciousness session for learning
        ConsciousnessSession consciousnessSession = new ConsciousnessSession();
        consciousnessSession.setSessionId(UUID.randomUUID().toString());
        consciousnessSession.setEntityId(sessionId);
        consciousnessSession.setStartTime(LocalDateTime.now());
        session.setConsciousnessSession(consciousnessSession);
        
        activeSessions.put(sessionId, session);
        
        // Start asynchronous learning loop
        startLearningLoop(session);
        
        return session;
    }
    
    /**
     * Process learning experience
     */
    public LearningResult processExperience(String sessionId, LearningExperience experience) {
        LearningSession session = activeSessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Learning session not found: " + sessionId);
        }
        
        logger.debug("Processing learning experience for session {}: {}", sessionId, experience.getExperienceType());
        
        try {
            // Add experience to buffer
            session.getExperienceBuffer().add(experience);
            
            // Update session metrics
            updateSessionMetrics(session, experience);
            
            // Process immediate learning
            LearningResult result = performImmediateLearning(session, experience);
            
            // Check for catastrophic forgetting and apply prevention
            if (shouldPreventForgetting(session, experience)) {
                forgettingPrevention.applyPreventionStrategies(session, experience);
            }
            
            // Trigger experience replay if needed
            if (shouldTriggerReplay(session)) {
                triggerExperienceReplay(session);
            }
            
            // Update meta-learning if applicable
            if (session.getConfiguration().isMetaLearningEnabled()) {
                metaLearningEngine.updateMetaKnowledge(session, experience, result);
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("Error processing learning experience for session {}: {}", sessionId, e.getMessage(), e);
            
            LearningResult errorResult = new LearningResult();
            errorResult.setSuccess(false);
            errorResult.setErrorMessage(e.getMessage());
            errorResult.setSessionId(sessionId);
            errorResult.setTimestamp(LocalDateTime.now());
            
            return errorResult;
        }
    }
    
    /**
     * Adapt learning architecture based on performance
     */
    public ArchitectureAdaptationResult adaptArchitecture(String sessionId) {
        LearningSession session = activeSessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Learning session not found: " + sessionId);
        }
        
        logger.info("Adapting architecture for learning session {}", sessionId);
        
        return adaptationEngine.adaptArchitecture(session);
    }
    
    /**
     * Perform knowledge distillation
     */
    public KnowledgeDistillationResult distillKnowledge(String sessionId, DistillationConfiguration config) {
        LearningSession session = activeSessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Learning session not found: " + sessionId);
        }
        
        logger.info("Performing knowledge distillation for session {}", sessionId);
        
        return distillationEngine.distillKnowledge(session, config);
    }
    
    /**
     * Start asynchronous learning loop
     */
    private void startLearningLoop(LearningSession session) {
        CompletableFuture.runAsync(() -> {
            try {
                runLearningLoop(session);
            } catch (Exception e) {
                logger.error("Error in learning loop for session {}: {}", session.getSessionId(), e.getMessage(), e);
                session.setStatus(LearningStatus.ERROR);
                session.setErrorMessage(e.getMessage());
            }
        }, learningExecutor);
    }
    
    /**
     * Main learning loop
     */
    private void runLearningLoop(LearningSession session) {
        logger.debug("Starting learning loop for session {}", session.getSessionId());
        
        while (session.getStatus() == LearningStatus.ACTIVE) {
            try {
                // Process buffered experiences
                processBatchedExperiences(session);
                
                // Perform periodic tasks
                performPeriodicTasks(session);
                
                // Update learning strategy if needed
                updateLearningStrategy(session);
                
                // Sleep for a short interval
                Thread.sleep(1000);
                
            } catch (InterruptedException e) {
                logger.info("Learning loop interrupted for session {}", session.getSessionId());
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("Error in learning loop for session {}: {}", session.getSessionId(), e.getMessage(), e);
                Thread.sleep(5000); // Wait before retry
            }
        }
        
        logger.info("Learning loop ended for session {} with status: {}", session.getSessionId(), session.getStatus());
    }
    
    /**
     * Perform immediate learning on experience
     */
    private LearningResult performImmediateLearning(LearningSession session, LearningExperience experience) {
        LearningResult result = new LearningResult();
        result.setSessionId(session.getSessionId());
        result.setExperienceId(experience.getExperienceId());
        result.setTimestamp(LocalDateTime.now());
        
        try {
            // Extract features from experience
            FeatureVector features = extractFeatures(experience);
            
            // Apply learning algorithm based on strategy
            LearningUpdate update = applyLearningAlgorithm(session, experience, features);
            
            // Update knowledge base
            updateKnowledgeBase(session, update);
            
            // Generate learning insights
            List<String> insights = generateLearningInsights(session, experience, update);
            
            result.setSuccess(true);
            result.setLearningUpdate(update);
            result.setInsights(insights);
            result.setConfidenceScore(update.getConfidence());
            result.setKnowledgeGain(calculateKnowledgeGain(update));
            
        } catch (Exception e) {
            logger.error("Error in immediate learning: {}", e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Extract features from learning experience
     */
    private FeatureVector extractFeatures(LearningExperience experience) {
        FeatureVector features = new FeatureVector();
        features.setExperienceId(experience.getExperienceId());
        features.setTimestamp(LocalDateTime.now());
        
        Map<String, Double> featureMap = new HashMap<>();
        
        // Extract content-based features
        String content = experience.getContent();
        if (content != null) {
            featureMap.put("content_length", (double) content.length());
            featureMap.put("word_count", (double) content.split("\\s+").length);
            featureMap.put("complexity_score", calculateComplexityScore(content));
        }
        
        // Extract temporal features
        featureMap.put("timestamp_hour", (double) experience.getTimestamp().getHour());
        featureMap.put("timestamp_day", (double) experience.getTimestamp().getDayOfWeek().getValue());
        
        // Extract context features
        if (experience.getContext() != null) {
            featureMap.put("context_size", (double) experience.getContext().size());
            featureMap.put("context_complexity", calculateContextComplexity(experience.getContext()));
        }
        
        // Extract emotional features
        featureMap.put("emotional_valence", experience.getEmotionalValence());
        featureMap.put("importance_score", experience.getImportanceScore());
        
        features.setFeatures(featureMap);
        features.setDimensions(featureMap.size());
        
        return features;
    }
    
    /**
     * Apply learning algorithm based on session strategy
     */
    private LearningUpdate applyLearningAlgorithm(LearningSession session, LearningExperience experience, FeatureVector features) {
        LearningConfiguration config = session.getConfiguration();
        LearningUpdate update = new LearningUpdate();
        update.setUpdateId(UUID.randomUUID().toString());
        update.setTimestamp(LocalDateTime.now());
        update.setExperienceId(experience.getExperienceId());
        
        try {
            switch (config.getLearningStrategy()) {
                case "incremental":
                    update = applyIncrementalLearning(session, experience, features);
                    break;
                case "episodic":
                    update = applyEpisodicLearning(session, experience, features);
                    break;
                case "meta_learning":
                    update = applyMetaLearning(session, experience, features);
                    break;
                case "transfer_learning":
                    update = applyTransferLearning(session, experience, features);
                    break;
                case "reinforcement":
                    update = applyReinforcementLearning(session, experience, features);
                    break;
                default:
                    update = applyDefaultLearning(session, experience, features);
            }
            
            // Use consciousness engine for complex understanding
            ConsciousnessInput consciousnessInput = new ConsciousnessInput();
            consciousnessInput.setEntityId(session.getSessionId());
            consciousnessInput.setContent(experience.getContent());
            consciousnessInput.setExperienceType("learning");
            consciousnessInput.setIntensityLevel(experience.getImportanceScore());
            consciousnessInput.setTimestamp(LocalDateTime.now());
            
            var consciousnessOutput = consciousnessEngine.processExperience(consciousnessInput, session.getConsciousnessSession());
            
            // Enhance update with consciousness insights
            update.setConsciousnessInsight(consciousnessOutput.getResponse());
            update.setConfidence(Math.max(update.getConfidence(), consciousnessOutput.getConfidenceLevel()));
            
        } catch (Exception e) {
            logger.error("Error applying learning algorithm: {}", e.getMessage(), e);
            update.setConfidence(0.1);
            update.setErrorMessage(e.getMessage());
        }
        
        return update;
    }
    
    /**
     * Apply incremental learning
     */
    private LearningUpdate applyIncrementalLearning(LearningSession session, LearningExperience experience, FeatureVector features) {
        LearningUpdate update = new LearningUpdate();
        update.setUpdateId(UUID.randomUUID().toString());
        update.setLearningType("incremental");
        update.setTimestamp(LocalDateTime.now());
        
        // Incremental learning logic
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("learning_rate", 0.01);
        parameters.put("momentum", 0.9);
        parameters.put("weight_decay", 0.0001);
        
        // Calculate incremental update based on features
        double learningRate = 0.01;
        double significance = experience.getImportanceScore() * features.getFeatures().getOrDefault("complexity_score", 1.0);
        
        Map<String, Double> weightUpdates = new HashMap<>();
        for (Map.Entry<String, Double> feature : features.getFeatures().entrySet()) {
            double update_value = learningRate * significance * feature.getValue();
            weightUpdates.put(feature.getKey(), update_value);
        }
        
        update.setParameters(parameters);
        update.setWeightUpdates(weightUpdates);
        update.setConfidence(0.7 + (significance * 0.3));
        update.setKnowledgeType("incremental_pattern");
        
        return update;
    }
    
    /**
     * Apply episodic learning
     */
    private LearningUpdate applyEpisodicLearning(LearningSession session, LearningExperience experience, FeatureVector features) {
        LearningUpdate update = new LearningUpdate();
        update.setUpdateId(UUID.randomUUID().toString());
        update.setLearningType("episodic");
        update.setTimestamp(LocalDateTime.now());
        
        // Create episodic memory
        EpisodicMemory episode = new EpisodicMemory();
        episode.setEpisodeId(UUID.randomUUID().toString());
        episode.setExperience(experience);
        episode.setFeatures(features);
        episode.setTimestamp(LocalDateTime.now());
        episode.setSignificance(experience.getImportanceScore());
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("episode_id", episode.getEpisodeId());
        parameters.put("memory_strength", episode.getSignificance());
        parameters.put("consolidation_factor", 0.8);
        
        update.setParameters(parameters);
        update.setEpisodicMemory(episode);
        update.setConfidence(0.8);
        update.setKnowledgeType("episodic_memory");
        
        return update;
    }
    
    /**
     * Apply meta-learning
     */
    private LearningUpdate applyMetaLearning(LearningSession session, LearningExperience experience, FeatureVector features) {
        return metaLearningEngine.applyMetaLearning(session, experience, features);
    }
    
    /**
     * Apply transfer learning
     */
    private LearningUpdate applyTransferLearning(LearningSession session, LearningExperience experience, FeatureVector features) {
        LearningUpdate update = new LearningUpdate();
        update.setUpdateId(UUID.randomUUID().toString());
        update.setLearningType("transfer");
        update.setTimestamp(LocalDateTime.now());
        
        // Find similar experiences from knowledge base
        List<KnowledgeItem> similarKnowledge = findSimilarKnowledge(session, features);
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("source_knowledge_count", similarKnowledge.size());
        parameters.put("transfer_confidence", calculateTransferConfidence(similarKnowledge, features));
        parameters.put("adaptation_factor", 0.6);
        
        // Transfer and adapt knowledge
        Map<String, Double> transferredWeights = new HashMap<>();
        for (KnowledgeItem knowledge : similarKnowledge) {
            if (knowledge.getFeatureWeights() != null) {
                for (Map.Entry<String, Double> entry : knowledge.getFeatureWeights().entrySet()) {
                    transferredWeights.merge(entry.getKey(), entry.getValue() * 0.6, Double::sum);
                }
            }
        }
        
        update.setParameters(parameters);
        update.setWeightUpdates(transferredWeights);
        update.setConfidence(0.6 + (similarKnowledge.size() * 0.05));
        update.setKnowledgeType("transferred_pattern");
        
        return update;
    }
    
    /**
     * Apply reinforcement learning
     */
    private LearningUpdate applyReinforcementLearning(LearningSession session, LearningExperience experience, FeatureVector features) {
        LearningUpdate update = new LearningUpdate();
        update.setUpdateId(UUID.randomUUID().toString());
        update.setLearningType("reinforcement");
        update.setTimestamp(LocalDateTime.now());
        
        // Calculate reward based on experience outcome
        double reward = calculateReward(experience);
        double gamma = 0.95; // discount factor
        double alpha = 0.1; // learning rate
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reward", reward);
        parameters.put("discount_factor", gamma);
        parameters.put("learning_rate", alpha);
        parameters.put("exploration_rate", 0.1);
        
        // Update value function
        Map<String, Double> valueUpdates = new HashMap<>();
        for (Map.Entry<String, Double> feature : features.getFeatures().entrySet()) {
            double qValue = feature.getValue();
            double qUpdate = alpha * (reward + gamma * qValue - qValue);
            valueUpdates.put(feature.getKey(), qUpdate);
        }
        
        update.setParameters(parameters);
        update.setWeightUpdates(valueUpdates);
        update.setConfidence(0.6 + Math.abs(reward) * 0.4);
        update.setKnowledgeType("reinforcement_policy");
        
        return update;
    }
    
    /**
     * Apply default learning
     */
    private LearningUpdate applyDefaultLearning(LearningSession session, LearningExperience experience, FeatureVector features) {
        LearningUpdate update = new LearningUpdate();
        update.setUpdateId(UUID.randomUUID().toString());
        update.setLearningType("default");
        update.setTimestamp(LocalDateTime.now());
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("learning_rate", 0.01);
        parameters.put("regularization", 0.001);
        
        // Simple weight updates
        Map<String, Double> weightUpdates = new HashMap<>();
        for (Map.Entry<String, Double> feature : features.getFeatures().entrySet()) {
            weightUpdates.put(feature.getKey(), feature.getValue() * 0.01);
        }
        
        update.setParameters(parameters);
        update.setWeightUpdates(weightUpdates);
        update.setConfidence(0.5);
        update.setKnowledgeType("basic_pattern");
        
        return update;
    }
    
    /**
     * Update knowledge base with learning update
     */
    private void updateKnowledgeBase(LearningSession session, LearningUpdate update) {
        KnowledgeItem knowledge = new KnowledgeItem();
        knowledge.setKnowledgeId(UUID.randomUUID().toString());
        knowledge.setLearningUpdateId(update.getUpdateId());
        knowledge.setKnowledgeType(update.getKnowledgeType());
        knowledge.setConfidence(update.getConfidence());
        knowledge.setCreationTime(LocalDateTime.now());
        knowledge.setFeatureWeights(update.getWeightUpdates());
        knowledge.setParameters(update.getParameters());
        
        if (update.getEpisodicMemory() != null) {
            knowledge.setEpisodicMemory(update.getEpisodicMemory());
        }
        
        session.getKnowledgeBase().add(knowledge);
        
        // Maintain knowledge base size
        if (session.getKnowledgeBase().size() > 10000) {
            // Remove oldest or least confident knowledge
            session.getKnowledgeBase().sort(Comparator.comparing(KnowledgeItem::getConfidence));
            session.setKnowledgeBase(new ArrayList<>(session.getKnowledgeBase().subList(1000, session.getKnowledgeBase().size())));
        }
    }
    
    /**
     * Process batched experiences
     */
    private void processBatchedExperiences(LearningSession session) {
        List<LearningExperience> batch = getBatchFromBuffer(session);
        
        if (!batch.isEmpty()) {
            logger.debug("Processing batch of {} experiences for session {}", batch.size(), session.getSessionId());
            
            // Batch learning processing
            for (LearningExperience experience : batch) {
                try {
                    processExperience(session.getSessionId(), experience);
                } catch (Exception e) {
                    logger.error("Error processing batched experience: {}", e.getMessage(), e);
                }
            }
            
            // Remove processed experiences from buffer
            session.getExperienceBuffer().removeAll(batch);
        }
    }
    
    /**
     * Perform periodic tasks
     */
    private void performPeriodicTasks(LearningSession session) {
        LocalDateTime now = LocalDateTime.now();
        
        // Knowledge consolidation
        if (shouldPerformConsolidation(session, now)) {
            performKnowledgeConsolidation(session);
        }
        
        // Architecture adaptation
        if (shouldAdaptArchitecture(session, now)) {
            adaptArchitecture(session.getSessionId());
        }
        
        // Memory cleanup
        if (shouldCleanupMemory(session, now)) {
            performMemoryCleanup(session);
        }
        
        // Metrics update
        updateGlobalMetrics(session);
    }
    
    // Helper methods
    
    private void updateSessionMetrics(LearningSession session, LearningExperience experience) {
        LearningMetrics metrics = session.getMetrics();
        metrics.setTotalExperiences(metrics.getTotalExperiences() + 1);
        metrics.setLastUpdateTime(LocalDateTime.now());
        
        // Update experience type counts
        Map<String, Integer> typeCounts = metrics.getExperienceTypeCounts();
        typeCounts.merge(experience.getExperienceType(), 1, Integer::sum);
        
        // Update average importance
        double currentAvg = metrics.getAverageImportance();
        int count = metrics.getTotalExperiences();
        double newAvg = ((currentAvg * (count - 1)) + experience.getImportanceScore()) / count;
        metrics.setAverageImportance(newAvg);
    }
    
    private boolean shouldPreventForgetting(LearningSession session, LearningExperience experience) {
        return session.getConfiguration().isForgettingPreventionEnabled() && 
               experience.getImportanceScore() > 0.7;
    }
    
    private boolean shouldTriggerReplay(LearningSession session) {
        return session.getConfiguration().isExperienceReplayEnabled() && 
               session.getExperienceBuffer().size() > 50;
    }
    
    private void triggerExperienceReplay(LearningSession session) {
        CompletableFuture.runAsync(() -> {
            replayManager.performExperienceReplay(session);
        }, learningExecutor);
    }
    
    private double calculateComplexityScore(String content) {
        return Math.min(1.0, content.split("[.!?]").length / 10.0);
    }
    
    private double calculateContextComplexity(Map<String, Object> context) {
        return Math.min(1.0, context.size() / 20.0);
    }
    
    private double calculateKnowledgeGain(LearningUpdate update) {
        return update.getConfidence() * update.getWeightUpdates().values().stream()
            .mapToDouble(Math::abs)
            .average()
            .orElse(0.0);
    }
    
    private List<String> generateLearningInsights(LearningSession session, LearningExperience experience, LearningUpdate update) {
        List<String> insights = new ArrayList<>();
        
        insights.add("Learned from " + experience.getExperienceType() + " experience");
        insights.add("Confidence level: " + String.format("%.2f", update.getConfidence()));
        insights.add("Knowledge type: " + update.getKnowledgeType());
        
        if (update.getConsciousnessInsight() != null) {
            insights.add("Consciousness insight: " + update.getConsciousnessInsight());
        }
        
        return insights;
    }
    
    private double calculateReward(LearningExperience experience) {
        // Simple reward calculation based on importance and emotional valence
        return experience.getImportanceScore() * experience.getEmotionalValence();
    }
    
    private List<KnowledgeItem> findSimilarKnowledge(LearningSession session, FeatureVector features) {
        return session.getKnowledgeBase().stream()
            .filter(knowledge -> knowledge.getFeatureWeights() != null)
            .filter(knowledge -> calculateSimilarity(knowledge.getFeatureWeights(), features.getFeatures()) > 0.7)
            .limit(5)
            .collect(Collectors.toList());
    }
    
    private double calculateSimilarity(Map<String, Double> weights1, Map<String, Double> weights2) {
        Set<String> commonKeys = new HashSet<>(weights1.keySet());
        commonKeys.retainAll(weights2.keySet());
        
        if (commonKeys.isEmpty()) return 0.0;
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (String key : commonKeys) {
            double w1 = weights1.get(key);
            double w2 = weights2.get(key);
            dotProduct += w1 * w2;
            norm1 += w1 * w1;
            norm2 += w2 * w2;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    private double calculateTransferConfidence(List<KnowledgeItem> similarKnowledge, FeatureVector features) {
        if (similarKnowledge.isEmpty()) return 0.0;
        
        return similarKnowledge.stream()
            .mapToDouble(KnowledgeItem::getConfidence)
            .average()
            .orElse(0.0);
    }
    
    private List<LearningExperience> getBatchFromBuffer(LearningSession session) {
        int batchSize = Math.min(10, session.getExperienceBuffer().size());
        return new ArrayList<>(session.getExperienceBuffer().subList(0, batchSize));
    }
    
    private boolean shouldPerformConsolidation(LearningSession session, LocalDateTime now) {
        return session.getKnowledgeBase().size() > 100;
    }
    
    private boolean shouldAdaptArchitecture(LearningSession session, LocalDateTime now) {
        return session.getMetrics().getTotalExperiences() % 1000 == 0;
    }
    
    private boolean shouldCleanupMemory(LearningSession session, LocalDateTime now) {
        return session.getExperienceBuffer().size() > 500;
    }
    
    private void performKnowledgeConsolidation(LearningSession session) {
        logger.debug("Performing knowledge consolidation for session {}", session.getSessionId());
        // Implementation for knowledge consolidation
    }
    
    private void performMemoryCleanup(LearningSession session) {
        logger.debug("Performing memory cleanup for session {}", session.getSessionId());
        // Keep only recent and important experiences
        session.getExperienceBuffer().sort((a, b) -> {
            int timeCompare = b.getTimestamp().compareTo(a.getTimestamp());
            if (timeCompare == 0) {
                return Double.compare(b.getImportanceScore(), a.getImportanceScore());
            }
            return timeCompare;
        });
        
        if (session.getExperienceBuffer().size() > 200) {
            session.setExperienceBuffer(new ArrayList<>(session.getExperienceBuffer().subList(0, 200)));
        }
    }
    
    private void updateGlobalMetrics(LearningSession session) {
        globalMetrics.setTotalExperiences(globalMetrics.getTotalExperiences() + 1);
        globalMetrics.setLastUpdateTime(LocalDateTime.now());
    }
    
    private void updateLearningStrategy(LearningSession session) {
        // Adaptive strategy selection based on performance
        LearningMetrics metrics = session.getMetrics();
        if (metrics.getAverageConfidence() < 0.5) {
            // Switch to more conservative learning
            session.getConfiguration().setLearningStrategy("incremental");
        }
    }
    
    /**
     * Get learning session
     */
    public LearningSession getLearningSession(String sessionId) {
        return activeSessions.get(sessionId);
    }
    
    /**
     * Stop learning session
     */
    public void stopLearningSession(String sessionId) {
        LearningSession session = activeSessions.get(sessionId);
        if (session != null) {
            session.setStatus(LearningStatus.STOPPED);
            session.setEndTime(LocalDateTime.now());
            logger.info("Learning session {} stopped", sessionId);
        }
    }
    
    /**
     * Get global learning metrics
     */
    public LearningMetrics getGlobalMetrics() {
        return globalMetrics;
    }
    
    /**
     * Shutdown the learning engine
     */
    public void shutdown() {
        logger.info("Shutting down continual learning engine");
        
        // Stop all active sessions
        activeSessions.values().forEach(session -> session.setStatus(LearningStatus.STOPPED));
        
        // Shutdown executor
        learningExecutor.shutdown();
        try {
            if (!learningExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                learningExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            learningExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
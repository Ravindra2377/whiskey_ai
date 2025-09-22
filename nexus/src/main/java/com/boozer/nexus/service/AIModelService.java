package com.boozer.nexus.service;

import com.boozer.nexus.model.AIModel;
import com.boozer.nexus.repository.AIModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AIModelService {
    
    @Autowired
    private AIModelRepository aiModelRepository;
    
    /**
     * Create a new AI model
     */
    public AIModel createModel(String modelName, String domain, String version, 
                              String trainingData, String performanceMetrics, String createdBy) {
        AIModel model = new AIModel(modelName, domain, version, trainingData, performanceMetrics, createdBy);
        return aiModelRepository.save(model);
    }
    
    /**
     * Get an AI model by ID
     */
    public Optional<AIModel> getModelById(Long id) {
        return aiModelRepository.findById(id);
    }
    
    /**
     * Get all AI models
     */
    public List<AIModel> getAllModels() {
        return aiModelRepository.findAll();
    }
    
    /**
     * Get AI models by domain
     */
    public List<AIModel> getModelsByDomain(String domain) {
        return aiModelRepository.findByDomain(domain);
    }
    
    /**
     * Search AI models by name
     */
    public List<AIModel> searchModelsByName(String modelName) {
        return aiModelRepository.findByModelNameContainingIgnoreCase(modelName);
    }
    
    /**
     * Get AI models by creator
     */
    public List<AIModel> getModelsByCreator(String createdBy) {
        return aiModelRepository.findByCreatedBy(createdBy);
    }
    
    /**
     * Update an AI model
     */
    public AIModel updateModel(Long id, String modelName, String domain, String version, 
                              String trainingData, String performanceMetrics) {
        Optional<AIModel> existingModel = aiModelRepository.findById(id);
        if (existingModel.isPresent()) {
            AIModel model = existingModel.get();
            model.setModelName(modelName);
            model.setDomain(domain);
            model.setVersion(version);
            model.setTrainingData(trainingData);
            model.setPerformanceMetrics(performanceMetrics);
            model.setUpdatedAt(LocalDateTime.now());
            return aiModelRepository.save(model);
        }
        return null;
    }
    
    /**
     * Delete an AI model
     */
    public void deleteModel(Long id) {
        aiModelRepository.deleteById(id);
    }
    
    /**
     * Start model training
     */
    public String startModelTraining(String domain, String trainingData, String parameters) {
        // In a real implementation, this would start an actual training job
        // For now, we'll just generate a mock job ID
        return "job-" + UUID.randomUUID().toString().substring(0, 8);
    }
}

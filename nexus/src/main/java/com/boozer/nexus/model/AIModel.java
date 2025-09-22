package com.boozer.nexus.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_models")
public class AIModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "model_name", nullable = false)
    private String modelName;
    
    @Column(name = "domain", nullable = false)
    private String domain;
    
    @Column(name = "version")
    private String version;
    
    @Column(name = "training_data")
    private String trainingData;
    
    @Column(name = "performance_metrics")
    private String performanceMetrics;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public AIModel() {}
    
    public AIModel(String modelName, String domain, String version, String trainingData, 
                   String performanceMetrics, String createdBy) {
        this.modelName = modelName;
        this.domain = domain;
        this.version = version;
        this.trainingData = trainingData;
        this.performanceMetrics = performanceMetrics;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getModelName() {
        return modelName;
    }
    
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    
    public String getDomain() {
        return domain;
    }
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getTrainingData() {
        return trainingData;
    }
    
    public void setTrainingData(String trainingData) {
        this.trainingData = trainingData;
    }
    
    public String getPerformanceMetrics() {
        return performanceMetrics;
    }
    
    public void setPerformanceMetrics(String performanceMetrics) {
        this.performanceMetrics = performanceMetrics;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

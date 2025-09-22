package com.boozer.nexus.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "trading_strategies")
public class TradingStrategy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Column(name = "strategy_type", nullable = false)
    private String strategyType;
    
    @Column(name = "parameters", columnDefinition = "TEXT")
    private String parametersJson;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public TradingStrategy() {}
    
    public TradingStrategy(String name, String description, String strategyType, Map<String, Object> parameters) {
        this.name = name;
        this.description = description;
        this.strategyType = strategyType;
        this.setParameters(parameters);
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getStrategyType() {
        return strategyType;
    }
    
    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }
    
    public Map<String, Object> getParameters() {
        if (parametersJson == null || parametersJson.isEmpty()) {
            return null;
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(parametersJson, Map.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    public void setParameters(Map<String, Object> parameters) {
        if (parameters == null) {
            this.parametersJson = null;
            return;
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.parametersJson = mapper.writeValueAsString(parameters);
        } catch (Exception e) {
            this.parametersJson = null;
        }
    }
    
    public String getParametersJson() {
        return parametersJson;
    }
    
    public void setParametersJson(String parametersJson) {
        this.parametersJson = parametersJson;
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

package com.boozer.nexus.entities;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "ai_agents")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class AIAgent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String agentId;
    
    @Column(nullable = false)
    private String agentName;
    
    @Enumerated(EnumType.STRING)
    private AgentType agentType;
    
    @Column(columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private Map<String, Object> capabilities;
    
    private Double performanceScore;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    // Constructors
    public AIAgent() {}
    
    public AIAgent(String agentId, String agentName, AgentType agentType) {
        this.agentId = agentId;
        this.agentName = agentName;
        this.agentType = agentType;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    
    public AgentType getAgentType() { return agentType; }
    public void setAgentType(AgentType agentType) { this.agentType = agentType; }
    
    public Map<String, Object> getCapabilities() { return capabilities; }
    public void setCapabilities(Map<String, Object> capabilities) { this.capabilities = capabilities; }
    
    public Double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(Double performanceScore) { this.performanceScore = performanceScore; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

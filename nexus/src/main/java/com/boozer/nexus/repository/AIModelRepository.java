package com.boozer.nexus.repository;

import com.boozer.nexus.model.AIModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIModelRepository extends JpaRepository<AIModel, Long> {
    
    /**
     * Find AI models by domain
     */
    List<AIModel> findByDomain(String domain);
    
    /**
     * Find AI models by name
     */
    List<AIModel> findByModelNameContainingIgnoreCase(String modelName);
    
    /**
     * Find AI models by creator
     */
    List<AIModel> findByCreatedBy(String createdBy);
}

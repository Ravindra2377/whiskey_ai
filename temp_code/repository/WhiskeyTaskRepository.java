package com.boozer.whiskey.repository;

import com.boozer.whiskey.model.WhiskeyTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WhiskeyTaskRepository extends JpaRepository<WhiskeyTaskEntity, Long> {
    
    Optional<WhiskeyTaskEntity> findByTaskId(String taskId);
    
    List<WhiskeyTaskEntity> findByCreatedBy(String createdBy);
    
    List<WhiskeyTaskEntity> findByStatus(String status);
    
    List<WhiskeyTaskEntity> findByTaskType(String taskType);
    
    List<WhiskeyTaskEntity> findByCreatedByAndStatus(String createdBy, String status);
}
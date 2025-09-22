package com.boozer.whiskey.service;

import com.boozer.whiskey.model.WhiskeyTaskEntity;
import com.boozer.whiskey.repository.WhiskeyTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WhiskeyTaskService {
    
    @Autowired
    private WhiskeyTaskRepository taskRepository;
    
    public WhiskeyTaskEntity saveTask(WhiskeyTaskEntity task) {
        return taskRepository.save(task);
    }
    
    public Optional<WhiskeyTaskEntity> findByTaskId(String taskId) {
        return taskRepository.findByTaskId(taskId);
    }
    
    public List<WhiskeyTaskEntity> findAllTasks() {
        return taskRepository.findAll();
    }
    
    public List<WhiskeyTaskEntity> findByCreatedBy(String createdBy) {
        return taskRepository.findByCreatedBy(createdBy);
    }
    
    public List<WhiskeyTaskEntity> findByStatus(String status) {
        return taskRepository.findByStatus(status);
    }
    
    public List<WhiskeyTaskEntity> findByTaskType(String taskType) {
        return taskRepository.findByTaskType(taskType);
    }
    
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
    
    public long countTasks() {
        return taskRepository.count();
    }
    
    public boolean existsByTaskId(String taskId) {
        return taskRepository.findByTaskId(taskId).isPresent();
    }
}
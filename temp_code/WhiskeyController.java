package com.boozer.whiskey;

import com.boozer.whiskey.WhiskeyTask;
import com.boozer.whiskey.WhiskeyResult;
import com.boozer.whiskey.dto.TaskRequestDTO;
import com.boozer.whiskey.dto.TaskResponseDTO;
import com.boozer.whiskey.model.WhiskeyTaskEntity;
import com.boozer.whiskey.service.WhiskeyTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/whiskey")
public class WhiskeyController {
    
    private final WhiskeyOrchestrator whiskeyOrchestrator;
    
    @Autowired
    private WhiskeyTaskService taskService;
    
    @Autowired
    public WhiskeyController(WhiskeyOrchestrator whiskeyOrchestrator) {
        this.whiskeyOrchestrator = whiskeyOrchestrator;
    }
    
    /**
     * Endpoint to submit a task to WHISKEY
     */
    @PostMapping("/task")
    public ResponseEntity<TaskResponseDTO> submitTask(@RequestBody TaskRequestDTO taskDTO) {
        try {
            // Convert DTO to domain object
            WhiskeyTask task = new WhiskeyTask();
            task.setType(taskDTO.getType());
            task.setDescription(taskDTO.getDescription());
            task.setParameters(taskDTO.getParameters());
            task.setCreatedBy(taskDTO.getCreatedBy());
            
            // Generate task ID
            String taskId = String.valueOf(System.currentTimeMillis());
            
            // Save task to database
            WhiskeyTaskEntity taskEntity = new WhiskeyTaskEntity(
                taskId, 
                task.getType().toString(), 
                task.getDescription(), 
                task.getCreatedBy()
            );
            taskService.saveTask(taskEntity);
            
            // Execute task asynchronously
            CompletableFuture<WhiskeyResult> futureResult = whiskeyOrchestrator.executeTask(task);
            
            // Add callback to update task status when completed
            futureResult.thenAccept(result -> {
                taskService.findByTaskId(taskId).ifPresent(taskStatus -> {
                    if (result.isSuccessful()) {
                        taskStatus.setStatus("COMPLETED");
                        taskStatus.setProgress(100);
                    } else {
                        taskStatus.setStatus("FAILED");
                    }
                    taskService.saveTask(taskStatus);
                });
            }).exceptionally(throwable -> {
                taskService.findByTaskId(taskId).ifPresent(taskStatus -> {
                    taskStatus.setStatus("FAILED");
                    taskService.saveTask(taskStatus);
                });
                return null;
            });
            
            // Return immediate response with task ID
            TaskResponseDTO response = new TaskResponseDTO(
                "ACCEPTED",
                "Task submitted successfully",
                taskId,
                task.getType().toString()
            );
            
            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            TaskResponseDTO response = new TaskResponseDTO(
                "ERROR",
                "Failed to submit task: " + e.getMessage(),
                null,
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Endpoint to trigger autonomous maintenance
     */
    @PostMapping("/maintenance")
    public ResponseEntity<TaskResponseDTO> triggerMaintenance() {
        try {
            // Create a maintenance task
            WhiskeyTask task = new WhiskeyTask();
            task.setType(WhiskeyTask.TaskType.AUTONOMOUS_MAINTENANCE);
            task.setDescription("Autonomous system maintenance");
            task.setCreatedBy("SYSTEM");
            task.setParameters(new HashMap<>());
            
            // Generate task ID
            String taskId = String.valueOf(System.currentTimeMillis());
            
            // Save task to database
            WhiskeyTaskEntity taskEntity = new WhiskeyTaskEntity(
                taskId, 
                task.getType().toString(), 
                task.getDescription(), 
                task.getCreatedBy()
            );
            taskService.saveTask(taskEntity);
            
            // Execute task asynchronously
            CompletableFuture<WhiskeyResult> futureResult = whiskeyOrchestrator.executeTask(task);
            
            // Add callback to update task status when completed
            futureResult.thenAccept(result -> {
                taskService.findByTaskId(taskId).ifPresent(taskStatus -> {
                    if (result.isSuccessful()) {
                        taskStatus.setStatus("COMPLETED");
                        taskStatus.setProgress(100);
                    } else {
                        taskStatus.setStatus("FAILED");
                    }
                    taskService.saveTask(taskStatus);
                });
            }).exceptionally(throwable -> {
                taskService.findByTaskId(taskId).ifPresent(taskStatus -> {
                    taskStatus.setStatus("FAILED");
                    taskService.saveTask(taskStatus);
                });
                return null;
            });
            
            // Return immediate response with task ID
            TaskResponseDTO response = new TaskResponseDTO(
                "ACCEPTED",
                "Autonomous maintenance task submitted successfully",
                taskId,
                task.getType().toString()
            );
            
            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            TaskResponseDTO response = new TaskResponseDTO(
                "ERROR",
                "Failed to trigger maintenance: " + e.getMessage(),
                null,
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Endpoint to get task status
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<WhiskeyTaskEntity> getTaskStatus(@PathVariable String taskId) {
        return taskService.findByTaskId(taskId)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Endpoint to get all tasks
     */
    @GetMapping("/tasks")
    public ResponseEntity<List<WhiskeyTaskEntity>> getAllTasks() {
        return ResponseEntity.ok(taskService.findAllTasks());
    }
    
    /**
     * Endpoint to cancel a task
     */
    @DeleteMapping("/task/{taskId}")
    public ResponseEntity<Map<String, Object>> cancelTask(@PathVariable String taskId) {
        return taskService.findByTaskId(taskId)
                .map(task -> {
                    task.setStatus("CANCELLED");
                    taskService.saveTask(task);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "SUCCESS");
                    response.put("message", "Task cancelled successfully");
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "ERROR");
                    response.put("message", "Task not found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }
    
    /**
     * Endpoint to get system health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("status", "HEALTHY");
        response.put("version", "1.0.0");
        response.put("timestamp", System.currentTimeMillis());
        response.put("databaseConnected", taskService.countTasks() >= 0); // Simple check
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint to get system information and capabilities
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("name", "WHISKEY AI System");
        response.put("version", "1.0.0");
        response.put("description", "Autonomous AI Engineer for Boozer Application");
        
        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("taskTypes", WhiskeyTask.TaskType.values());
        capabilities.put("supportedOperations", new String[]{
            "Code Analysis",
            "Code Generation",
            "Code Modification",
            "Testing",
            "Deployment",
            "Monitoring",
            "Performance Optimization",
            "Bug Fixing",
            "Security Patching"
        });
        
        response.put("capabilities", capabilities);
        
        Map<String, Object> system = new HashMap<>();
        system.put("javaVersion", System.getProperty("java.version"));
        system.put("osName", System.getProperty("os.name"));
        system.put("osVersion", System.getProperty("os.version"));
        system.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        system.put("maxMemory", Runtime.getRuntime().maxMemory());
        system.put("databaseTasks", taskService.countTasks());
        
        response.put("system", system);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint to get system metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> response = new HashMap<>();
        
        // In a real implementation, this would return actual metrics
        response.put("cpuUsage", 45.2);
        response.put("memoryUsage", 67.8);
        response.put("diskUsage", 34.1);
        response.put("activeTasks", taskService.findByStatus("SUBMITTED").size() + taskService.findByStatus("PROCESSING").size());
        response.put("completedTasks", taskService.findByStatus("COMPLETED").size());
        response.put("failedTasks", taskService.findByStatus("FAILED").size());
        response.put("totalTasks", taskService.countTasks());
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint to get recommendations
     */
    @GetMapping("/recommendations")
    public ResponseEntity<Map<String, Object>> getRecommendations() {
        Map<String, Object> response = new HashMap<>();
        
        // In a real implementation, this would return actual recommendations
        response.put("status", "SUCCESS");
        response.put("count", 2);
        response.put("recommendations", new String[]{
            "Optimize database queries in user service",
            "Add circuit breaker for payment service"
        });
        
        return ResponseEntity.ok(response);
    }
}
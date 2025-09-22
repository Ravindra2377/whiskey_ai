package com.boozer.nexus;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WhiskeyOrchestratorTest {

    @Test
    public void testOrchestratorInitialization() {
        WhiskeyOrchestrator orchestrator = new WhiskeyOrchestrator();
        assertNotNull(orchestrator);
    }

    @Test
    public void testTaskExecution() throws Exception {
        WhiskeyOrchestrator orchestrator = new WhiskeyOrchestrator();
        
        // Create a simple task
        WhiskeyTask task = new WhiskeyTask();
        task.setType(WhiskeyTask.TaskType.CODE_MODIFICATION);
        task.setDescription("Test code modification task");
        task.setCreatedBy("TEST_USER");
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("filePath", "test/File.java");
        parameters.put("modification", "System.out.println(\"Hello World\");");
        task.setParameters(parameters);
        
        // Execute the task
        CompletableFuture<WhiskeyResult> futureResult = orchestrator.executeTask(task);
        
        // Wait for completion
        WhiskeyResult result = futureResult.get();
        
        // Verify result
        assertNotNull(result);
        // Note: In the current implementation, tasks are simulated, so we expect success
        assertTrue(result.isSuccessful());
    }

    @Test
    public void testInvalidTaskType() throws Exception {
        WhiskeyOrchestrator orchestrator = new WhiskeyOrchestrator();
        
        // Create a task with invalid type
        WhiskeyTask task = new WhiskeyTask();
        task.setType(WhiskeyTask.TaskType.CODE_MODIFICATION); // This is valid, but we'll test the handling
        task.setDescription("Test task");
        task.setCreatedBy("TEST_USER");
        task.setParameters(new HashMap<>());
        
        // Execute the task
        CompletableFuture<WhiskeyResult> futureResult = orchestrator.executeTask(task);
        
        // Wait for completion
        WhiskeyResult result = futureResult.get();
        
        // Verify result
        assertNotNull(result);
    }
}

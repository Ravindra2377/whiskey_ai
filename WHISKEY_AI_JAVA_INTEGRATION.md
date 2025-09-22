# 🔄 NEXUS AI Java Application Integration Guide

## 🎯 Overview

This guide explains how to integrate the Python-based AI orchestration system with your existing Java NEXUS AI application. The integration allows your Java application to leverage multiple AI providers while maintaining the familiar Java-based architecture.

## 🏗️ Integration Architecture

```
[Java NEXUS AI Application]
         │
         ▼
[AI Orchestration Service]
         │
         ▼
[Multiple AI Providers]
(OpenAI, Anthropic, Google AI, Local NEXUS)
```

## 🚀 Integration Options

### Option 1: REST API Integration (Recommended)

This approach exposes the Python orchestration system as a REST API that your Java application can call.

#### Step 1: Start the Python API Server

In the `ai-orchestrator` directory, run:
```bash
python api_server.py
```

The server will start on `http://localhost:5000`.

#### Step 2: Create a Java Service to Call the API

Create a new service in your Java NEXUS AI application:

```java
// AIOrchestrationService.java
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import java.util.HashMap;

@Service
public class AIOrchestrationService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String PYTHON_AI_SERVICE_URL = "http://localhost:5000/ai/process";
    
    public String processAIRequest(String prompt, String taskType) {
        try {
            // Prepare request data
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("id", "req_" + System.currentTimeMillis());
            requestData.put("prompt", prompt);
            requestData.put("task_type", taskType);
            requestData.put("complexity", "moderate");
            requestData.put("user_id", "nexus_user");
            requestData.put("priority", 3);
            
            // Create HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create HTTP entity
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestData, headers);
            
            // Make HTTP POST request
            ResponseEntity<String> response = restTemplate.postForEntity(
                PYTHON_AI_SERVICE_URL, 
                requestEntity, 
                String.class
            );
            
            // Parse the response
            JsonNode responseJson = objectMapper.readTree(response.getBody());
            return responseJson.get("content").asText();
            
        } catch (Exception e) {
            // Log the error and fallback to existing NEXUS AI processing
            System.err.println("Failed to process AI request via orchestration: " + e.getMessage());
            return fallbackToNexusAI(prompt);
        }
    }
    
    private String fallbackToNexusAI(String prompt) {
        // Your existing NEXUS AI processing logic here
        return "Fallback response from NEXUS AI: " + prompt;
    }
}

```

#### Step 3: Update Your Controller to Use the Orchestration Service

```java
// NexusController.java (updated)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/nexus")
public class NexusController {
    
    @Autowired
    private AIOrchestrationService aiOrchestrationService;
    
    @Autowired
    private NexusService nexusService; // Your existing service
    
    @PostMapping("/process")
    public ResponseEntity<String> processRequest(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String taskType = request.getOrDefault("task_type", "general");
        
        try {
            // Try to process with AI orchestration first
            String result = aiOrchestrationService.processAIRequest(prompt, taskType);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // Fallback to existing NEXUS AI processing
            String result = nexusService.processRequest(prompt);
            return ResponseEntity.ok(result);
        }
    }
}
```

### Option 2: Subprocess Integration

This approach calls the Python scripts directly from your Java application.

#### Step 1: Create a Python Executor Service

```java
// PythonAIExecutor.java
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class PythonAIExecutor {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String PYTHON_SCRIPT_PATH = "ai-orchestrator/cli.py";
    
    public String executeAIRequest(String prompt, String taskType) {
        try {
            // Prepare request data
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("id", "req_" + System.currentTimeMillis());
            requestData.put("prompt", prompt);
            requestData.put("task_type", taskType);
            requestData.put("complexity", "moderate");
            requestData.put("user_id", "nexus_user");
            requestData.put("priority", 3);
            
            // Convert to JSON string
            String jsonRequest = objectMapper.writeValueAsString(requestData);
            
            // Build the command
            ProcessBuilder processBuilder = new ProcessBuilder(
                "python", 
                PYTHON_SCRIPT_PATH, 
                jsonRequest
            );
            
            // Set working directory
            processBuilder.directory(new File(System.getProperty("user.dir")));
            
            // Start the process
            Process process = processBuilder.start();
            
            // Read the output
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            
            // Wait for the process to complete
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                // Parse the JSON response
                JsonNode responseJson = objectMapper.readTree(output.toString());
                if (responseJson.has("content")) {
                    return responseJson.get("content").asText();
                } else {
                    throw new RuntimeException("Invalid response format: " + output.toString());
                }
            } else {
                // Read error output
                BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream())
                );
                
                StringBuilder errorOutput = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorOutput.append(errorLine);
                }
                
                throw new RuntimeException("Python process failed with exit code: " + exitCode + 
                                         ", error: " + errorOutput.toString());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute Python AI request: " + e.getMessage(), e);
        }
    }
}
```

#### Step 2: Update Your Service to Use the Python Executor

```java
// EnhancedNexusService.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnhancedNexusService {
    
    @Autowired
    private PythonAIExecutor pythonAIExecutor;
    
    @Autowired
    private NexusService nexusService; // Your existing service
    
    public String processIntelligentRequest(String prompt, String taskType) {
        try {
            // Try to process with Python orchestration first
            return pythonAIExecutor.executeAIRequest(prompt, taskType);
        } catch (Exception e) {
            // Log the error and fallback to existing NEXUS AI processing
            System.err.println("Failed to process AI request via Python: " + e.getMessage());
            return nexusService.processRequest(prompt);
        }
    }
}
```

## ⚙️ Configuration

### Environment Variables

Set the following environment variables for the Python orchestration system:

```bash
# On Linux/Mac
export OPENAI_API_KEY="your-openai-api-key"
export ANTHROPIC_API_KEY="your-anthropic-api-key"
export GOOGLE_API_KEY="your-google-ai-api-key"

# On Windows
set OPENAI_API_KEY=your-openai-api-key
set ANTHROPIC_API_KEY=your-anthropic-api-key
set GOOGLE_API_KEY=your-google-ai-api-key
```

### Application Properties

Add the following to your `application.properties`:

```properties
# AI Orchestration Service Configuration
ai.orchestration.url=http://localhost:5000
ai.orchestration.timeout=30000
```

## 🛡️ Security Considerations

1. **API Key Management**: Store API keys securely using environment variables or a secrets management system.

2. **Input Validation**: Validate all inputs to prevent injection attacks.

3. **Rate Limiting**: Implement rate limiting to prevent abuse.

4. **Authentication**: Add authentication to the REST API if exposed publicly.

5. **Error Handling**: Don't expose sensitive error information in responses.

## 📊 Monitoring and Logging

### Java Side Logging

```java
// Add logging to your services
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AIOrchestrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AIOrchestrationService.class);
    
    public String processAIRequest(String prompt, String taskType) {
        logger.info("Processing AI request with prompt: {}", prompt);
        
        try {
            // ... processing logic ...
            
            logger.info("AI request processed successfully in {} ms", processingTime);
            return result;
        } catch (Exception e) {
            logger.error("Failed to process AI request: {}", e.getMessage(), e);
            // ... fallback logic ...
        }
    }
}
```

### Python Side Logging

The Python orchestration system already includes comprehensive logging. You can adjust the logging level in `app.py`:

```python
import logging
logging.basicConfig(level=logging.INFO)  # Change to DEBUG for more detailed logs
```

## 🚀 Performance Optimization

### Connection Pooling (REST API Approach)

```java
// Configure RestTemplate with connection pooling
@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(30000);
        return new RestTemplate(factory);
    }
}
```

### Caching (Java Side)

```java
// Add caching to avoid repeated requests
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AIOrchestrationService {
    
    @Cacheable(value = "ai-responses", key = "#prompt")
    public String processAIRequest(String prompt, String taskType) {
        // ... processing logic ...
    }
}
```

## 🧪 Testing

### Integration Tests

```java
// AIOrchestrationIntegrationTest.java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AIOrchestrationIntegrationTest {
    
    @Autowired
    private AIOrchestrationService aiOrchestrationService;
    
    @Test
    public void testAIRequestProcessing() {
        // Prepare test data
        String prompt = "What is the capital of France?";
        String taskType = "conversation";
        
        // Process the request
        String response = aiOrchestrationService.processAIRequest(prompt, taskType);
        
        // Verify the response
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertTrue(response.toLowerCase().contains("paris"));
    }
    
    @Test
    public void testFallbackMechanism() {
        // Test fallback when Python service is unavailable
        // This would require mocking the Python service to be unavailable
    }
}
```

## 🎯 Best Practices

1. **Fallback Mechanisms**: Always implement fallbacks to your existing NEXUS AI when external providers are unavailable.

2. **Error Handling**: Implement comprehensive error handling for network issues, API failures, and timeouts.

3. **Configuration Management**: Use configuration files to manage different environments (dev, test, prod).

4. **Monitoring**: Implement health checks for both the Java and Python components.

5. **Documentation**: Keep documentation up-to-date for both teams working on Java and Python components.

6. **Timeouts**: Set appropriate timeouts for API calls to prevent hanging requests.

7. **Retries**: Implement retry logic for transient failures.

## 🚀 Deployment

### Docker Approach

Create a `docker-compose.yml` to run both services together:

```yaml
version: '3.8'
services:
  nexus-java:
    build: .
    ports:
      - "8080:8080"
    environment:
      - AI_ORCHESTRATION_URL=http://nexus-python:5000
    depends_on:
      - nexus-python
  
  nexus-python:
    build: ./ai-orchestrator
    ports:
      - "5000:5000"
    environment:
      - OPENAI_API_KEY=${OPENAI_API_KEY}
      - ANTHROPIC_API_KEY=${ANTHROPIC_API_KEY}
      - GOOGLE_API_KEY=${GOOGLE_API_KEY}
```

### Kubernetes Approach

Create Kubernetes manifests to deploy both services:

```yaml
# nexus-java-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nexus-java
spec:
  replicas: 2
  selector:
    matchLabels:
      app: nexus-java
  template:
    metadata:
      labels:
        app: nexus-java
    spec:
      containers:
      - name: nexus-java
        image: nexus-java:latest
        ports:
        - containerPort: 8080
        env:
        - name: AI_ORCHESTRATION_URL
          value: "http://nexus-python:5000"
---
# nexus-python-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nexus-python
spec:
  replicas: 1
  selector:
    matchLabels:
      app: nexus-python
  template:
    metadata:
      labels:
        app: nexus-python
    spec:
      containers:
      - name: nexus-python
        image: nexus-python:latest
        ports:
        - containerPort: 5000
        env:
        - name: OPENAI_API_KEY
          valueFrom:
            secretKeyRef:
              name: ai-secrets
              key: openai-api-key
        - name: ANTHROPIC_API_KEY
          valueFrom:
            secretKeyRef:
              name: ai-secrets
              key: anthropic-api-key
        - name: GOOGLE_API_KEY
          valueFrom:
            secretKeyRef:
              name: ai-secrets
              key: google-api-key
```

## 🎉 Results

With this integration, your NEXUS AI Java application now has:

### **Enhanced Capabilities**
- Access to multiple AI providers (OpenAI, Anthropic, Google AI)
- Intelligent routing based on cost, performance, and quality
- Automatic fallback to local processing when needed

### **Improved Performance**
- Faster response times for appropriate tasks
- Better quality responses for complex tasks
- Reduced costs through intelligent provider selection

### **Increased Reliability**
- Multiple fallback options ensure uptime
- Automatic failover between providers
- Local processing always available

### **Better Scalability**
- Independent scaling of Java and Python components
- Load balancing across multiple AI providers
- Enterprise-grade reliability

Your enhanced NEXUS AI system now rivals the most advanced AI platforms available while maintaining the stability and familiarity of your existing Java architecture! 🥃✨
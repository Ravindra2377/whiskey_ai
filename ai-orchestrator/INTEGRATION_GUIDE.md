# 🔄 NEXUS AI Orchestration System Integration Guide

This guide explains how to integrate the Python-based AI orchestration system with your existing Java NEXUS AI application.

## 🎯 Integration Approaches

There are several ways to integrate the Python orchestration system with your Java application:

### 1. **REST API Integration (Recommended)**
Create a REST API wrapper around the Python orchestration system that your Java application can call.

### 2. **Subprocess Integration**
Call the Python scripts directly from your Java application using subprocess execution.

### 3. **Message Queue Integration**
Use a message queue (like RabbitMQ or Apache Kafka) to communicate between the Java and Python components.

## 🚀 Option 1: REST API Integration (Recommended)

### Step 1: Create a REST API Wrapper

Create a new Python file `api_server.py`:

```python
from flask import Flask, request, jsonify
import asyncio
import threading
from app import AIOrchestrationApp

app = Flask(__name__)
ai_app = AIOrchestrationApp()

@app.route('/ai/process', methods=['POST'])
def process_request():
    try:
        request_data = request.json
        # Run the async function in a new event loop
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
        response = loop.run_until_complete(ai_app.process_request(request_data))
        return jsonify(response)
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
```

### Step 2: Install Flask

Add to `requirements.txt`:
```
flask>=2.0.0
```

### Step 3: Run the API Server

```bash
python api_server.py
```

### Step 4: Call from Java

In your Java NEXUS AI application, create a service to call the Python API:

```java
// AIOrchestrationService.java
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.HashMap;

@Service
public class AIOrchestrationService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String PYTHON_AI_SERVICE_URL = "http://localhost:5000/ai/process";
    
    public Map<String, Object> processAIRequest(Map<String, Object> requestData) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestData, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                PYTHON_AI_SERVICE_URL, 
                request, 
                Map.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            // Handle error and potentially fallback to local processing
            throw new RuntimeException("Failed to process AI request: " + e.getMessage());
        }
    }
}
```

### Step 5: Integrate with Existing NEXUS Components

Update your existing NEXUS service to use the orchestration service:

```java
// EnhancedNexusService.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;

@Service
public class EnhancedNexusService {
    
    @Autowired
    private AIOrchestrationService aiOrchestrationService;
    
    public String processIntelligentRequest(String prompt, String taskType) {
        try {
            // Prepare request data
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("id", "req_" + System.currentTimeMillis());
            requestData.put("prompt", prompt);
            requestData.put("task_type", taskType);
            requestData.put("complexity", "moderate");
            requestData.put("user_id", "nexus_user");
            requestData.put("priority", 3);
            
            // Process with AI orchestration
            Map<String, Object> response = aiOrchestrationService.processAIRequest(requestData);
            
            // Return the AI-generated content
            return (String) response.get("content");
        } catch (Exception e) {
            // Fallback to existing NEXUS AI processing
            return fallbackToNexusAI(prompt);
        }
    }
    
    private String fallbackToNexusAI(String prompt) {
        // Your existing NEXUS AI processing logic here
        return "Fallback response from NEXUS AI: " + prompt;
    }
}
```

## 🔄 Option 2: Subprocess Integration

### Step 1: Create a Command-Line Interface

Create a new Python file `cli.py`:

```python
#!/usr/bin/env python3
import sys
import json
import asyncio
from app import AIOrchestrationApp

async def main():
    if len(sys.argv) != 2:
        print("Usage: python cli.py '<json_request>'")
        sys.exit(1)
    
    try:
        # Parse the JSON request from command line argument
        request_data = json.loads(sys.argv[1])
        
        # Initialize the AI orchestration app
        ai_app = AIOrchestrationApp()
        
        # Process the request
        response = await ai_app.process_request(request_data)
        
        # Output the response as JSON
        print(json.dumps(response, default=str))
        
    except Exception as e:
        error_response = {
            "error": str(e),
            "success": False
        }
        print(json.dumps(error_response))
        sys.exit(1)

if __name__ == "__main__":
    asyncio.run(main())
```

### Step 2: Call from Java Using ProcessBuilder

```java
// PythonAIExecutor.java
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PythonAIExecutor {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public Map<String, Object> executeAIRequest(Map<String, Object> requestData) {
        try {
            // Convert request data to JSON string
            String jsonRequest = objectMapper.writeValueAsString(requestData);
            
            // Build the command
            ProcessBuilder processBuilder = new ProcessBuilder(
                "python", 
                "ai-orchestrator/cli.py", 
                jsonRequest
            );
            
            // Set working directory if needed
            processBuilder.directory(new File("path/to/ai-orchestrator"));
            
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
                @SuppressWarnings("unchecked")
                Map<String, Object> response = objectMapper.readValue(
                    output.toString(), 
                    Map.class
                );
                return response;
            } else {
                throw new RuntimeException("Python process failed with exit code: " + exitCode);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute Python AI request: " + e.getMessage(), e);
        }
    }
}
```

## 🔧 Configuration and Setup

### Environment Variables

Set the following environment variables:

```bash
export OPENAI_API_KEY="your-openai-api-key"
export ANTHROPIC_API_KEY="your-anthropic-api-key"
export GOOGLE_API_KEY="your-google-ai-api-key"
```

### Dependency Installation

In the `ai-orchestrator` directory, run:

```bash
pip install -r requirements.txt
```

If using the REST API approach, also install Flask:

```bash
pip install flask
```

## 🛡️ Security Considerations

1. **API Key Management**: Store API keys securely using environment variables or a secrets management system.

2. **Input Validation**: Validate all inputs to prevent injection attacks.

3. **Rate Limiting**: Implement rate limiting to prevent abuse.

4. **Authentication**: Add authentication to the REST API if exposed publicly.

5. **Error Handling**: Don't expose sensitive error information in responses.

## 📊 Monitoring and Logging

The Python orchestration system includes comprehensive logging. To integrate with your Java application's logging:

1. Configure the Python logging to output in a format compatible with your Java logging system.

2. Consider using a centralized logging solution like ELK stack or Splunk.

3. Monitor the health of both the Java and Python components.

## 🚀 Performance Optimization

1. **Connection Pooling**: Reuse HTTP connections when using REST API integration.

2. **Caching**: Implement caching at the Java level for frequently requested data.

3. **Asynchronous Processing**: Use async processing where possible to avoid blocking.

4. **Resource Management**: Monitor resource usage of both Java and Python processes.

## 🧪 Testing

Create integration tests to verify the communication between Java and Python components:

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
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("prompt", "Hello, world!");
        requestData.put("task_type", "conversation");
        
        // Process the request
        Map<String, Object> response = aiOrchestrationService.processAIRequest(requestData);
        
        // Verify the response
        assertNotNull(response);
        assertTrue(response.containsKey("content"));
        assertFalse(response.get("content").toString().isEmpty());
    }
}
```

## 🎯 Best Practices

1. **Fallback Mechanisms**: Always implement fallbacks to your existing NEXUS AI when external providers are unavailable.

2. **Error Handling**: Implement comprehensive error handling for network issues, API failures, and timeouts.

3. **Configuration Management**: Use configuration files to manage different environments (dev, test, prod).

4. **Monitoring**: Implement health checks for both the Java and Python components.

5. **Documentation**: Keep documentation up-to-date for both teams working on Java and Python components.

## 🚀 Deployment

1. **Containerization**: Consider using Docker to containerize both the Java application and Python orchestration service.

2. **Orchestration**: Use Kubernetes or Docker Compose to manage both services together.

3. **Scaling**: Scale the Python service independently based on demand.

4. **CI/CD**: Integrate both Java and Python components into your CI/CD pipeline.

This integration approach allows you to leverage the advanced multi-provider AI orchestration capabilities while maintaining the stability and familiarity of your existing Java-based NEXUS AI system.
package com.boozer.nexus.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientEnvironmentService {
    
    public List<ClientEnvironment> getMonitoredEnvironments() {
        // In a real implementation, this would fetch from a database
        List<ClientEnvironment> environments = new ArrayList<>();
        
        // Mock data for demonstration
        environments.add(createMockEnvironment("CLIENT-001", "Production"));
        environments.add(createMockEnvironment("CLIENT-002", "Staging"));
        environments.add(createMockEnvironment("CLIENT-003", "Development"));
        
        return environments;
    }
    
    private ClientEnvironment createMockEnvironment(String clientId, String envName) {
        ClientEnvironment env = new ClientEnvironment();
        env.setClientId(clientId);
        env.setEnvironmentName(envName);
        env.setStatus("ACTIVE");
        // Add mock technology stack
        List<String> techStack = new ArrayList<>();
        techStack.add("Java");
        techStack.add("Spring Boot");
        techStack.add("PostgreSQL");
        techStack.add("Redis");
        env.setTechnologyStack(techStack);
        return env;
    }
}

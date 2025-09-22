package com.boozer.nexus;

import com.boozer.nexus.enhanced.UniversalEnterpriseIntegrationEngine;
import com.boozer.nexus.enhanced.UniversalEnterpriseIntegrationEngine.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UniversalEnterpriseIntegrationEngineTest {
    
    private UniversalEnterpriseIntegrationEngine enterpriseEngine;
    
    @BeforeEach
    public void setUp() {
        enterpriseEngine = new UniversalEnterpriseIntegrationEngine();
    }
    
    @Test
    public void testSystemDiscovery() throws Exception {
        // Given
        Map<String, Object> discoveryParams = new HashMap<>();
        discoveryParams.put("scan_network", true);
        discoveryParams.put("scan_apis", true);
        discoveryParams.put("scan_databases", true);
        
        // When
        CompletableFuture<SystemDiscoveryResult> futureResult = enterpriseEngine.discoverEnterpriseSystems(discoveryParams);
        SystemDiscoveryResult result = futureResult.get();
        
        // Then
        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        assertTrue(result.getDiscoveredSystems().size() > 0);
        assertNotNull(result.getDiscoveryId());
    }
    
    @Test
    public void testConnectionConfiguration() throws Exception {
        // Given
        Map<String, Object> discoveryParams = new HashMap<>();
        CompletableFuture<SystemDiscoveryResult> discoveryFuture = enterpriseEngine.discoverEnterpriseSystems(discoveryParams);
        SystemDiscoveryResult discoveryResult = discoveryFuture.get();
        
        Map<String, Object> configParams = new HashMap<>();
        configParams.put("auto_configure", true);
        
        // When
        CompletableFuture<java.util.List<ConnectionConfiguration>> futureConfigurations = 
            enterpriseEngine.configureSystemConnections(discoveryResult.getDiscoveredSystems(), configParams);
        java.util.List<ConnectionConfiguration> configurations = futureConfigurations.get();
        
        // Then
        assertNotNull(configurations);
        assertFalse(configurations.isEmpty());
        assertEquals(discoveryResult.getDiscoveredSystems().size(), configurations.size());
    }
    
    @Test
    public void testAgentDeployment() throws Exception {
        // Given
        Map<String, Object> discoveryParams = new HashMap<>();
        CompletableFuture<SystemDiscoveryResult> discoveryFuture = enterpriseEngine.discoverEnterpriseSystems(discoveryParams);
        SystemDiscoveryResult discoveryResult = discoveryFuture.get();
        
        Map<String, Object> deploymentParams = new HashMap<>();
        deploymentParams.put("region", "us-west-2");
        
        // When
        CompletableFuture<java.util.List<SupportAgentDeployment>> futureDeployments = 
            enterpriseEngine.deploySupportAgents(discoveryResult.getDiscoveredSystems(), deploymentParams);
        java.util.List<SupportAgentDeployment> deployments = futureDeployments.get();
        
        // Then
        assertNotNull(deployments);
        assertFalse(deployments.isEmpty());
        assertEquals(discoveryResult.getDiscoveredSystems().size(), deployments.size());
        assertEquals("DEPLOYED", deployments.get(0).getStatus());
    }
    
    @Test
    public void testTechnicalSupport() throws Exception {
        // Given
        TechnicalSupportRequest request = new TechnicalSupportRequest();
        request.setRequestId("TEST_SUPPORT_001");
        request.setSystemId("SYS_WEB_001");
        request.setSupportType("performance_optimization");
        request.setDescription("Application running slowly");
        
        // When
        CompletableFuture<TechnicalSupportResponse> futureResponse = 
            enterpriseEngine.provideTechnicalSupport(request);
        TechnicalSupportResponse response = futureResponse.get();
        
        // Then
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("TEST_SUPPORT_001", response.getRequestId());
        assertNotNull(response.getResult());
    }
    
    @Test
    public void testEnterpriseClientRegistration() throws Exception {
        // Given
        String clientName = "TestCorp";
        String tier = "Enterprise";
        Map<String, Object> config = new HashMap<>();
        config.put("industry", "Technology");
        config.put("employees", 1000);
        
        // When
        CompletableFuture<EnterpriseClient> futureClient = 
            enterpriseEngine.registerEnterpriseClient(clientName, tier, config);
        EnterpriseClient client = futureClient.get();
        
        // Then
        assertNotNull(client);
        assertEquals("TestCorp", client.getClientName());
        assertEquals("Enterprise", client.getTier());
        assertEquals("ACTIVE", client.getStatus());
        assertNotNull(client.getClientId());
    }
    
    @Test
    public void testCompleteEnterpriseIntegrationWorkflow() throws Exception {
        // Given
        String clientName = "TestCorp";
        String tier = "Enterprise";
        Map<String, Object> config = new HashMap<>();
        config.put("industry", "Technology");
        config.put("employees", 1000);
        
        Map<String, Object> discoveryParams = new HashMap<>();
        discoveryParams.put("scan_network", true);
        
        Map<String, Object> configParams = new HashMap<>();
        configParams.put("auto_configure", true);
        
        Map<String, Object> deploymentParams = new HashMap<>();
        deploymentParams.put("region", "us-west-2");
        
        // When
        CompletableFuture<EnterpriseClient> clientFuture = 
            enterpriseEngine.registerEnterpriseClient(clientName, tier, config);
        EnterpriseClient client = clientFuture.get();
        
        CompletableFuture<SystemDiscoveryResult> discoveryFuture = 
            enterpriseEngine.discoverEnterpriseSystems(discoveryParams);
        SystemDiscoveryResult discoveryResult = discoveryFuture.get();
        
        CompletableFuture<java.util.List<ConnectionConfiguration>> configFuture = 
            enterpriseEngine.configureSystemConnections(discoveryResult.getDiscoveredSystems(), configParams);
        java.util.List<ConnectionConfiguration> configurations = configFuture.get();
        
        CompletableFuture<java.util.List<SupportAgentDeployment>> deploymentFuture = 
            enterpriseEngine.deploySupportAgents(discoveryResult.getDiscoveredSystems(), deploymentParams);
        java.util.List<SupportAgentDeployment> deployments = deploymentFuture.get();
        
        // Then
        assertNotNull(client);
        assertNotNull(discoveryResult);
        assertFalse(configurations.isEmpty());
        assertFalse(deployments.isEmpty());
        
        assertEquals("SUCCESS", discoveryResult.getStatus());
        assertEquals(discoveryResult.getDiscoveredSystems().size(), configurations.size());
        assertEquals(discoveryResult.getDiscoveredSystems().size(), deployments.size());
    }
}

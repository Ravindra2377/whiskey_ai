package com.boozer.nexus;

import com.boozer.nexus.enhanced.UniversalEnterpriseIntegrationEngine;
import com.boozer.nexus.service.UniversalEnterpriseServiceImpl;
import com.boozer.nexus.service.EnterpriseMonitoringService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EnterpriseIntegrationTest {

    @Test
    public void testSystemDiscovery() {
        // Create the engine and services
        UniversalEnterpriseIntegrationEngine engine = new UniversalEnterpriseIntegrationEngine();
        EnterpriseMonitoringService monitoringService = new EnterpriseMonitoringService();
        UniversalEnterpriseServiceImpl service = new UniversalEnterpriseServiceImpl(engine);
        
        // Test discovery parameters
        Map<String, Object> discoveryParams = new HashMap<>();
        discoveryParams.put("scan_network", true);
        discoveryParams.put("scan_apis", true);
        discoveryParams.put("scan_databases", true);
        
        // Execute discovery
        CompletableFuture<UniversalEnterpriseIntegrationEngine.SystemDiscoveryResult> future = 
            service.discoverEnterpriseSystems(discoveryParams);
        
        // Get result
        UniversalEnterpriseIntegrationEngine.SystemDiscoveryResult result = future.join();
        
        // Verify result
        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        assertNotNull(result.getDiscoveredSystems());
        assertTrue(result.getDiscoveredSystems().size() > 0);
        assertTrue(result.getMessage().contains("Discovered"));
        
        System.out.println("Discovered " + result.getDiscoveredSystems().size() + " systems");
        result.getDiscoveredSystems().forEach(system -> 
            System.out.println("- " + system.getSystemName() + " (" + system.getSystemType() + ")"));
    }
    
    @Test
    public void testClientRegistration() {
        // Create the engine and services
        UniversalEnterpriseIntegrationEngine engine = new UniversalEnterpriseIntegrationEngine();
        EnterpriseMonitoringService monitoringService = new EnterpriseMonitoringService();
        UniversalEnterpriseServiceImpl service = new UniversalEnterpriseServiceImpl(engine);
        
        // Test client registration
        Map<String, Object> config = new HashMap<>();
        config.put("industry", "Technology");
        config.put("employee_count", 1000);
        
        CompletableFuture<UniversalEnterpriseIntegrationEngine.EnterpriseClient> future = 
            service.registerEnterpriseClient("TestCorp", "Enterprise", config);
        
        // Get result
        UniversalEnterpriseIntegrationEngine.EnterpriseClient client = future.join();
        
        // Verify result
        assertNotNull(client);
        assertEquals("TestCorp", client.getClientName());
        assertEquals("Enterprise", client.getTier());
        assertEquals("ACTIVE", client.getStatus());
        assertNotNull(client.getClientId());
        
        System.out.println("Registered client: " + client.getClientName());
        System.out.println("Client ID: " + client.getClientId());
        System.out.println("Tier: " + client.getTier());
    }
    
    @Test
    public void testTechnicalSupport() {
        // Create the engine and services
        UniversalEnterpriseIntegrationEngine engine = new UniversalEnterpriseIntegrationEngine();
        EnterpriseMonitoringService monitoringService = new EnterpriseMonitoringService();
        UniversalEnterpriseServiceImpl service = new UniversalEnterpriseServiceImpl(engine);
        
        // Create support request
        UniversalEnterpriseIntegrationEngine.TechnicalSupportRequest request = 
            new UniversalEnterpriseIntegrationEngine.TechnicalSupportRequest();
        request.setSystemId("SYS_WEB_001");
        request.setSupportType("performance_optimization");
        request.setDescription("Application running slowly");
        request.setPriority("HIGH");
        
        System.out.println("Creating support request with:");
        System.out.println("  System ID: " + request.getSystemId());
        System.out.println("  Support Type: " + request.getSupportType());
        System.out.println("  Description: " + request.getDescription());
        System.out.println("  Priority: " + request.getPriority());
        
        // Execute support request
        CompletableFuture<UniversalEnterpriseIntegrationEngine.TechnicalSupportResponse> future = 
            service.provideTechnicalSupport(request);
        
        // Get result
        UniversalEnterpriseIntegrationEngine.TechnicalSupportResponse response = null;
        try {
            System.out.println("Calling future.join()...");
            response = future.join();
            System.out.println("future.join() completed");
        } catch (Exception e) {
            System.err.println("Exception during future.join(): " + e.getMessage());
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
        
        // Verify result
        System.out.println("Response object: " + response);
        assertNotNull(response, "Response should not be null");
        
        System.out.println("Response status: " + response.getStatus());
        System.out.println("Response message: " + response.getMessage());
        System.out.println("Response result: " + response.getResult());
        System.out.println("Response request ID: " + response.getRequestId());
        
        assertEquals("SUCCESS", response.getStatus(), "Status should be SUCCESS");
        assertNotNull(response.getResult(), "Result should not be null");
        assertTrue(response.getMessage().contains("completed"), "Message should contain 'completed': " + response.getMessage());
        assertNotNull(response.getRequestId(), "Request ID should not be null");
        
        System.out.println("Support request ID: " + response.getRequestId());
        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
    }
}

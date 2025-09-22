package com.boozer.nexus;

import com.boozer.nexus.enhanced.UniversalEnterpriseIntegrationEngine;

import java.util.concurrent.CompletableFuture;

public class DebugTechnicalSupportTest {
    public static void main(String[] args) {
        // Create the engine
        UniversalEnterpriseIntegrationEngine engine = new UniversalEnterpriseIntegrationEngine();
        
        // Create support request
        UniversalEnterpriseIntegrationEngine.TechnicalSupportRequest request = 
            new UniversalEnterpriseIntegrationEngine.TechnicalSupportRequest();
        request.setSystemId("SYS_WEB_001");
        request.setSupportType("performance_optimization");
        request.setDescription("Application running slowly");
        request.setPriority("HIGH");
        
        System.out.println("Creating support request...");
        System.out.println("System ID: " + request.getSystemId());
        System.out.println("Support Type: " + request.getSupportType());
        
        // Execute support request
        System.out.println("Calling provideTechnicalSupport...");
        CompletableFuture<UniversalEnterpriseIntegrationEngine.TechnicalSupportResponse> future = 
            engine.provideTechnicalSupport(request);
        
        // Get result
        System.out.println("Getting result...");
        UniversalEnterpriseIntegrationEngine.TechnicalSupportResponse response = future.join();
        
        System.out.println("Response: " + response);
        if (response != null) {
            System.out.println("Status: " + response.getStatus());
            System.out.println("Message: " + response.getMessage());
            System.out.println("Request ID: " + response.getRequestId());
        } else {
            System.out.println("Response is null!");
        }
    }
}

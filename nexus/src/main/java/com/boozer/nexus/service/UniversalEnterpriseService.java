package com.boozer.nexus.service;

import com.boozer.nexus.enhanced.UniversalEnterpriseIntegrationEngine.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public interface UniversalEnterpriseService {
    
    /**
     * Discover enterprise systems
     */
    CompletableFuture<SystemDiscoveryResult> discoverEnterpriseSystems(Map<String, Object> discoveryParams);
    
    /**
     * Configure system connections
     */
    CompletableFuture<List<ConnectionConfiguration>> configureSystemConnections(
            List<DiscoveredSystem> systems, Map<String, Object> configParams);
    
    /**
     * Deploy support agents
     */
    CompletableFuture<List<SupportAgentDeployment>> deploySupportAgents(
            List<DiscoveredSystem> systems, Map<String, Object> deploymentParams);
    
    /**
     * Provide technical support
     */
    CompletableFuture<TechnicalSupportResponse> provideTechnicalSupport(TechnicalSupportRequest request);
    
    /**
     * Register enterprise client
     */
    CompletableFuture<EnterpriseClient> registerEnterpriseClient(String clientName, String tier, Map<String, Object> config);
}

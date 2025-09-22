package com.boozer.nexus.service;

import com.boozer.nexus.enhanced.UniversalEnterpriseIntegrationEngine;
import com.boozer.nexus.enhanced.UniversalEnterpriseIntegrationEngine.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class UniversalEnterpriseServiceImpl implements UniversalEnterpriseService {
    
    private static final Logger logger = LoggerFactory.getLogger(UniversalEnterpriseServiceImpl.class);
    
    private final UniversalEnterpriseIntegrationEngine enterpriseEngine;
    
    @Autowired
    public UniversalEnterpriseServiceImpl(UniversalEnterpriseIntegrationEngine enterpriseEngine) {
        this.enterpriseEngine = enterpriseEngine;
    }
    
    @Override
    public CompletableFuture<SystemDiscoveryResult> discoverEnterpriseSystems(Map<String, Object> discoveryParams) {
        try {
            logger.info("Service: Discovering enterprise systems");
            
            // Validate input parameters
            if (discoveryParams == null) {
                discoveryParams = new java.util.HashMap<>();
            }
            
            return enterpriseEngine.discoverEnterpriseSystems(discoveryParams);
        } catch (Exception e) {
            logger.error("Service: Failed to discover enterprise systems", e);
            throw new RuntimeException("Failed to discover enterprise systems: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CompletableFuture<List<ConnectionConfiguration>> configureSystemConnections(
            List<DiscoveredSystem> systems, Map<String, Object> configParams) {
        try {
            logger.info("Service: Configuring system connections for {} systems", 
                systems != null ? systems.size() : 0);
            
            // Validate input parameters
            if (systems == null || systems.isEmpty()) {
                logger.warn("Service: No systems provided for connection configuration");
                throw new IllegalArgumentException("Systems list cannot be null or empty");
            }
            
            if (configParams == null) {
                configParams = new java.util.HashMap<>();
            }
            
            return enterpriseEngine.configureSystemConnections(systems, configParams);
        } catch (Exception e) {
            logger.error("Service: Failed to configure system connections", e);
            throw new RuntimeException("Failed to configure system connections: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CompletableFuture<List<SupportAgentDeployment>> deploySupportAgents(
            List<DiscoveredSystem> systems, Map<String, Object> deploymentParams) {
        try {
            logger.info("Service: Deploying support agents for {} systems", 
                systems != null ? systems.size() : 0);
            
            // Validate input parameters
            if (systems == null || systems.isEmpty()) {
                logger.warn("Service: No systems provided for agent deployment");
                throw new IllegalArgumentException("Systems list cannot be null or empty");
            }
            
            if (deploymentParams == null) {
                deploymentParams = new java.util.HashMap<>();
            }
            
            return enterpriseEngine.deploySupportAgents(systems, deploymentParams);
        } catch (Exception e) {
            logger.error("Service: Failed to deploy support agents", e);
            throw new RuntimeException("Failed to deploy support agents: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CompletableFuture<TechnicalSupportResponse> provideTechnicalSupport(TechnicalSupportRequest request) {
        try {
            logger.info("Service: Providing technical support for system: {} with type: {}", 
                request.getSystemId(), request.getSupportType());
            
            // Validate input parameters
            if (request == null) {
                logger.warn("Service: Technical support request cannot be null");
                throw new IllegalArgumentException("Technical support request cannot be null");
            }
            
            if (!StringUtils.hasText(request.getSystemId())) {
                logger.warn("Service: System ID is required for technical support");
                throw new IllegalArgumentException("System ID is required for technical support");
            }
            
            if (!StringUtils.hasText(request.getSupportType())) {
                logger.warn("Service: Support type is required for technical support");
                throw new IllegalArgumentException("Support type is required for technical support");
            }
            
            return enterpriseEngine.provideTechnicalSupport(request);
        } catch (Exception e) {
            logger.error("Service: Failed to provide technical support", e);
            throw new RuntimeException("Failed to provide technical support: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CompletableFuture<EnterpriseClient> registerEnterpriseClient(String clientName, String tier, Map<String, Object> config) {
        try {
            logger.info("Service: Registering enterprise client: {}", clientName);
            
            // Validate input parameters
            if (!StringUtils.hasText(clientName)) {
                logger.warn("Service: Client name is required for registration");
                throw new IllegalArgumentException("Client name is required for registration");
            }
            
            if (config == null) {
                config = new java.util.HashMap<>();
            }
            
            return enterpriseEngine.registerEnterpriseClient(clientName, tier, config);
        } catch (Exception e) {
            logger.error("Service: Failed to register enterprise client", e);
            throw new RuntimeException("Failed to register enterprise client: " + e.getMessage(), e);
        }
    }
}

package com.boozer.nexus.service;

import com.boozer.nexus.enhanced.UniversalEnterpriseIntegrationEngine.SystemDiscoveryResult;
import com.boozer.nexus.enhanced.UniversalEnterpriseIntegrationEngine.DiscoveredSystem;
import com.boozer.nexus.enhanced.UniversalEnterpriseIntegrationEngine.ConnectionConfiguration;
import com.boozer.nexus.enhanced.UniversalEnterpriseIntegrationEngine.SupportAgentDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// Removed @Service annotation to avoid conflict with MonitoringConfig

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

// Service bean is configured in MonitoringConfig
public class EnterpriseMonitoringService {
    
    private static final Logger logger = LoggerFactory.getLogger(EnterpriseMonitoringService.class);
    
    // Metrics storage
    private final AtomicInteger totalSystemsDiscovered = new AtomicInteger(0);
    private final AtomicInteger totalConnectionsConfigured = new AtomicInteger(0);
    private final AtomicInteger totalAgentsDeployed = new AtomicInteger(0);
    private final AtomicInteger totalSupportRequests = new AtomicInteger(0);
    private final AtomicInteger totalClientsRegistered = new AtomicInteger(0);
    private final AtomicLong totalProcessingTime = new AtomicLong(0);
    private final AtomicInteger totalOperations = new AtomicInteger(0);
    
    // Error tracking
    private final AtomicInteger totalErrors = new AtomicInteger(0);
    private final Map<String, AtomicInteger> errorByType = new ConcurrentHashMap<>();
    
    // Performance metrics
    private final Map<String, Long> operationDurations = new ConcurrentHashMap<>();
    
    /**
     * Record system discovery metrics
     */
    public void recordSystemDiscovery(SystemDiscoveryResult result) {
        try {
            if (result != null && "SUCCESS".equals(result.getStatus())) {
                int count = result.getDiscoveredSystems() != null ? result.getDiscoveredSystems().size() : 0;
                totalSystemsDiscovered.addAndGet(count);
                logger.info("Monitoring: Recorded {} discovered systems", count);
            } else if (result != null && "ERROR".equals(result.getStatus())) {
                totalErrors.incrementAndGet();
                errorByType.computeIfAbsent("SYSTEM_DISCOVERY", k -> new AtomicInteger(0)).incrementAndGet();
                logger.warn("Monitoring: Recorded system discovery error");
            }
        } catch (Exception e) {
            logger.error("Monitoring: Failed to record system discovery metrics", e);
        }
    }
    
    /**
     * Record connection configuration metrics
     */
    public void recordConnectionConfiguration(List<ConnectionConfiguration> configurations) {
        try {
            if (configurations != null) {
                totalConnectionsConfigured.addAndGet(configurations.size());
                logger.info("Monitoring: Recorded {} configured connections", configurations.size());
            }
        } catch (Exception e) {
            logger.error("Monitoring: Failed to record connection configuration metrics", e);
        }
    }
    
    /**
     * Record agent deployment metrics
     */
    public void recordAgentDeployment(List<SupportAgentDeployment> deployments) {
        try {
            if (deployments != null) {
                totalAgentsDeployed.addAndGet(deployments.size());
                logger.info("Monitoring: Recorded {} deployed agents", deployments.size());
            }
        } catch (Exception e) {
            logger.error("Monitoring: Failed to record agent deployment metrics", e);
        }
    }
    
    /**
     * Record technical support metrics
     */
    public void recordTechnicalSupport(String status, long processingTimeMs) {
        try {
            totalSupportRequests.incrementAndGet();
            totalProcessingTime.addAndGet(processingTimeMs);
            totalOperations.incrementAndGet();
            
            if ("ERROR".equals(status)) {
                totalErrors.incrementAndGet();
                errorByType.computeIfAbsent("TECHNICAL_SUPPORT", k -> new AtomicInteger(0)).incrementAndGet();
                logger.warn("Monitoring: Recorded technical support error");
            } else {
                logger.info("Monitoring: Recorded technical support request with processing time: {}ms", processingTimeMs);
            }
        } catch (Exception e) {
            logger.error("Monitoring: Failed to record technical support metrics", e);
        }
    }
    
    /**
     * Record client registration metrics
     */
    public void recordClientRegistration(String status) {
        try {
            totalClientsRegistered.incrementAndGet();
            
            if ("ERROR".equals(status)) {
                totalErrors.incrementAndGet();
                errorByType.computeIfAbsent("CLIENT_REGISTRATION", k -> new AtomicInteger(0)).incrementAndGet();
                logger.warn("Monitoring: Recorded client registration error");
            } else {
                logger.info("Monitoring: Recorded client registration");
            }
        } catch (Exception e) {
            logger.error("Monitoring: Failed to record client registration metrics", e);
        }
    }
    
    /**
     * Record operation duration
     */
    public void recordOperationDuration(String operation, long durationMs) {
        try {
            operationDurations.put(operation, durationMs);
            logger.info("Monitoring: Recorded operation '{}' duration: {}ms", operation, durationMs);
        } catch (Exception e) {
            logger.error("Monitoring: Failed to record operation duration", e);
        }
    }
    
    /**
     * Get current metrics summary
     */
    public Map<String, Object> getMetricsSummary() {
        Map<String, Object> summary = new java.util.HashMap<>();
        
        try {
            summary.put("totalSystemsDiscovered", totalSystemsDiscovered.get());
            summary.put("totalConnectionsConfigured", totalConnectionsConfigured.get());
            summary.put("totalAgentsDeployed", totalAgentsDeployed.get());
            summary.put("totalSupportRequests", totalSupportRequests.get());
            summary.put("totalClientsRegistered", totalClientsRegistered.get());
            summary.put("totalErrors", totalErrors.get());
            
            // Calculate average processing time
            int operations = totalOperations.get();
            long totalTime = totalProcessingTime.get();
            double avgProcessingTime = operations > 0 ? (double) totalTime / operations : 0;
            summary.put("averageProcessingTimeMs", avgProcessingTime);
            
            // Error breakdown
            Map<String, Integer> errorBreakdown = new java.util.HashMap<>();
            errorByType.forEach((key, value) -> errorBreakdown.put(key, value.get()));
            summary.put("errorBreakdown", errorBreakdown);
            
            // Operation durations
            summary.put("operationDurations", new java.util.HashMap<>(operationDurations));
            
            // Health status
            String healthStatus = totalErrors.get() == 0 ? "HEALTHY" : 
                                 totalErrors.get() < 5 ? "DEGRADED" : "UNHEALTHY";
            summary.put("healthStatus", healthStatus);
            
            logger.info("Monitoring: Generated metrics summary");
        } catch (Exception e) {
            logger.error("Monitoring: Failed to generate metrics summary", e);
            summary.put("error", "Failed to generate metrics summary: " + e.getMessage());
        }
        
        return summary;
    }
    
    /**
     * Reset metrics (for testing purposes)
     */
    public void resetMetrics() {
        totalSystemsDiscovered.set(0);
        totalConnectionsConfigured.set(0);
        totalAgentsDeployed.set(0);
        totalSupportRequests.set(0);
        totalClientsRegistered.set(0);
        totalProcessingTime.set(0);
        totalOperations.set(0);
        totalErrors.set(0);
        errorByType.clear();
        operationDurations.clear();
        logger.info("Monitoring: Metrics reset");
    }
    
    /**
     * Get error rate percentage
     */
    public double getErrorRate() {
        int totalOps = totalOperations.get();
        return totalOps > 0 ? (double) totalErrors.get() / totalOps * 100 : 0;
    }
    
    /**
     * Get throughput (operations per second)
     */
    public double getThroughput() {
        // This is a simplified calculation - in a real implementation, 
        // you would track operations over time windows
        return totalOperations.get() / 60.0; // Operations per minute
    }
}

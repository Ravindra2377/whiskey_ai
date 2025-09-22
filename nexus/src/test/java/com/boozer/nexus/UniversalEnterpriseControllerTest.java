package com.boozer.nexus;

import com.boozer.nexus.enhanced.UniversalEnterpriseIntegrationEngine;
import com.boozer.nexus.service.UniversalEnterpriseService;
import com.boozer.nexus.service.UniversalEnterpriseServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UniversalEnterpriseController.class)
public class UniversalEnterpriseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UniversalEnterpriseService enterpriseService;

    @MockBean
    private UniversalEnterpriseIntegrationEngine enterpriseEngine;

    // Mock all other services and repositories that might be autowired
    @MockBean
    private com.boozer.nexus.service.BoozerFileService boozerFileService;
    
    @MockBean
    private com.boozer.nexus.service.NexusTaskService nexusTaskService;
    
    @MockBean
    private com.boozer.nexus.repository.BoozerFileRepository boozerFileRepository;
    
    @MockBean
    private com.boozer.nexus.repository.NexusTaskRepository nexusTaskRepository;
    
    @MockBean
    private com.boozer.nexus.repository.ClientTenantRepository clientTenantRepository;
    
    @MockBean
    private com.boozer.nexus.repository.AIModelRepository aiModelRepository;
    
    @MockBean
    private com.boozer.nexus.repository.FinancialDataRepository financialDataRepository;
    
    @MockBean
    private com.boozer.nexus.repository.TradingStrategyRepository tradingStrategyRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Setup the service implementation
        UniversalEnterpriseServiceImpl serviceImpl = new UniversalEnterpriseServiceImpl(enterpriseEngine);
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/nexus/enterprise/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Universal Enterprise Integration Engine"));
    }

    @Test
    void testRegisterClient() throws Exception {
        // Prepare test data
        Map<String, Object> request = new HashMap<>();
        request.put("clientName", "Test Corp");
        request.put("tier", "Enterprise");
        request.put("configuration", new HashMap<>());

        // Mock the service response
        UniversalEnterpriseIntegrationEngine.EnterpriseClient client = new UniversalEnterpriseIntegrationEngine.EnterpriseClient();
        client.setClientName("Test Corp");
        client.setTier("Enterprise");
        
        // Fix: Return CompletableFuture with the client
        when(enterpriseService.registerEnterpriseClient(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(client));

        MvcResult mvcResult = mockMvc.perform(post("/api/nexus/enterprise/register-client")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(request().asyncStarted())
                .andReturn();
                
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientName").value("Test Corp"));
    }

    @Test
    void testDiscoverSystems() throws Exception {
        // Prepare test data
        Map<String, Object> request = new HashMap<>();
        request.put("scan_network", true);
        request.put("scan_apis", true);
        request.put("scan_databases", true);

        // Mock the service response
        UniversalEnterpriseIntegrationEngine.SystemDiscoveryResult result = new UniversalEnterpriseIntegrationEngine.SystemDiscoveryResult();
        result.setStatus("SUCCESS");
        
        // Fix: Return CompletableFuture with the result
        when(enterpriseService.discoverEnterpriseSystems(any()))
                .thenReturn(CompletableFuture.completedFuture(result));

        MvcResult mvcResult = mockMvc.perform(post("/api/nexus/enterprise/discover-systems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(request().asyncStarted())
                .andReturn();
                
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void testIntegrateEnterprise() throws Exception {
        // Prepare test data
        Map<String, Object> request = new HashMap<>();
        request.put("clientName", "Test Corp");
        request.put("tier", "Enterprise");
        request.put("configuration", new HashMap<>());
        request.put("discoveryParams", new HashMap<>());
        request.put("configParams", new HashMap<>());
        request.put("deploymentParams", new HashMap<>());

        // Mock the service responses
        UniversalEnterpriseIntegrationEngine.EnterpriseClient client = new UniversalEnterpriseIntegrationEngine.EnterpriseClient();
        client.setClientName("Test Corp");
        client.setTier("Enterprise");
        
        UniversalEnterpriseIntegrationEngine.SystemDiscoveryResult discoveryResult = new UniversalEnterpriseIntegrationEngine.SystemDiscoveryResult();
        discoveryResult.setStatus("SUCCESS");
        
        // Fix: Return CompletableFuture with the responses
        when(enterpriseService.registerEnterpriseClient(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(client));
                
        when(enterpriseService.discoverEnterpriseSystems(any()))
                .thenReturn(CompletableFuture.completedFuture(discoveryResult));
                
        when(enterpriseService.configureSystemConnections(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(new java.util.ArrayList<>()));
                
        when(enterpriseService.deploySupportAgents(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(new java.util.ArrayList<>()));

        MvcResult mvcResult = mockMvc.perform(post("/api/nexus/enterprise/integrate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(request().asyncStarted())
                .andReturn();
                
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}

package com.boozer.nexus.test.integration;

import com.boozer.nexus.ai.ExternalAIIntegrationService;
import com.boozer.nexus.ai.providers.OpenAIProvider;
import com.boozer.nexus.ai.providers.AnthropicProvider;
import com.boozer.nexus.ai.providers.GoogleAIProvider;
import com.boozer.nexus.ai.IntelligentAIRouter;
import com.boozer.nexus.ai.models.*;
import com.boozer.nexus.consciousness.ConsciousnessEngine;
import com.boozer.nexus.consciousness.models.*;
import com.boozer.nexus.quantum.QuantumProcessor;
import com.boozer.nexus.quantum.models.*;
import com.boozer.nexus.neuromorphic.NeuromorphicProcessor;
import com.boozer.nexus.neuromorphic.models.*;
import com.boozer.nexus.security.JWTUtil;
import com.boozer.nexus.entities.User;
import com.boozer.nexus.entities.Role;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive Integration Tests for NEXUS AI Platform
 * 
 * Tests the complete functionality and integration of all major components
 * including AI integration, consciousness engine, quantum processing, 
 * neuromorphic computing, and security framework.
 */
@SpringBootTest
@ActiveProfiles("test")
@SpringJUnitConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NexusIntegrationTest {

    @Autowired
    private ExternalAIIntegrationService aiIntegrationService;
    
    @Autowired
    private OpenAIProvider openAIProvider;
    
    @Autowired
    private AnthropicProvider anthropicProvider;
    
    @Autowired
    private GoogleAIProvider googleAIProvider;
    
    @Autowired
    private IntelligentAIRouter aiRouter;
    
    @Autowired
    private ConsciousnessEngine consciousnessEngine;
    
    @Autowired
    private QuantumProcessor quantumProcessor;
    
    @Autowired
    private NeuromorphicProcessor neuromorphicProcessor;
    
    @Autowired
    private JWTUtil jwtUtil;

    @BeforeAll
    void setupTest() {
        System.out.println("Starting NEXUS AI Platform Integration Tests");
    }

    @Test
    @DisplayName("AI Integration Service - Multi-Provider Test")
    void testAIIntegrationService() {
        // Test request creation
        AIRequest request = new AIRequest();
        request.setPrompt("Explain quantum computing in simple terms");
        request.setTaskType("explanation");
        request.setMaxTokens(150);
        request.setTemperature(0.7);
        request.setUserId("test-user");
        request.setTimestamp(LocalDateTime.now());

        // Test AI processing
        assertDoesNotThrow(() -> {
            AIResponse response = aiIntegrationService.processRequest(request);
            
            assertNotNull(response);
            assertNotNull(response.getContent());
            assertFalse(response.getContent().isEmpty());
            assertEquals("test-user", response.getUserId());
            assertTrue(response.getProcessingTime() > 0);
            assertNotNull(response.getProvider());
        });
    }

    @Test
    @DisplayName("AI Router - Provider Selection Test")
    void testIntelligentAIRouter() {
        // Test different task types
        String[] taskTypes = {"creative", "analytical", "conversational", "coding"};
        
        for (String taskType : taskTypes) {
            AIRequest request = new AIRequest();
            request.setTaskType(taskType);
            request.setPrompt("Test prompt for " + taskType);
            request.setUserId("test-user");
            
            assertDoesNotThrow(() -> {
                String selectedProvider = aiRouter.selectProvider(request);
                assertNotNull(selectedProvider);
                assertTrue(Arrays.asList("openai", "anthropic", "google").contains(selectedProvider));
            });
        }
    }

    @Test
    @DisplayName("Consciousness Engine - Complete Workflow Test")
    @Transactional
    void testConsciousnessEngine() {
        // Create consciousness session
        ConsciousnessSession session = new ConsciousnessSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setEntityId("test-entity");
        session.setStartTime(LocalDateTime.now());
        session.setSessionType("integration_test");

        // Create consciousness input
        ConsciousnessInput input = new ConsciousnessInput();
        input.setEntityId("test-entity");
        input.setContent("I need to understand the relationship between quantum mechanics and consciousness");
        input.setExperienceType("learning");
        input.setIntensityLevel(0.8);
        input.setTimestamp(LocalDateTime.now());
        
        List<Stimulus> stimuli = Arrays.asList(
            createStimulus("textual", "learning request", 0.7),
            createStimulus("conceptual", "quantum consciousness", 0.9)
        );
        input.setStimuli(stimuli);

        // Test consciousness processing
        assertDoesNotThrow(() -> {
            ConsciousnessOutput output = consciousnessEngine.processExperience(input, session);
            
            assertNotNull(output);
            assertNotNull(output.getResponse());
            assertFalse(output.getResponse().isEmpty());
            assertTrue(output.getConsciousnessLevel() > 0);
            assertTrue(output.getConfidenceLevel() > 0);
            assertNotNull(output.getTimestamp());
            assertNotNull(output.getExplanation());
        });

        // Test reasoning capability
        ReasoningInput reasoningInput = new ReasoningInput();
        reasoningInput.setQuery("How might quantum effects influence conscious experience?");
        reasoningInput.setContextType("philosophical");
        reasoningInput.setReasoningType("analytical");
        reasoningInput.setUserId("test-entity");

        assertDoesNotThrow(() -> {
            ReasoningResult reasoningResult = consciousnessEngine.performReasoning(reasoningInput, session);
            
            assertNotNull(reasoningResult);
            assertNotNull(reasoningResult.getReasoningChain());
            assertFalse(reasoningResult.getReasoningChain().isEmpty());
            assertTrue(reasoningResult.getConfidenceLevel() > 0);
            assertNotNull(reasoningResult.getConclusion());
        });
    }

    @Test
    @DisplayName("Quantum Processing - Algorithm Test")
    void testQuantumProcessor() {
        // Test QAOA algorithm
        QAOAParameters qaoaParams = new QAOAParameters();
        qaoaParams.setP(2);
        qaoaParams.setIterations(10);
        
        Map<String, Object> problem = new HashMap<>();
        problem.put("vertices", 4);
        problem.put("edges", Arrays.asList(
            Map.of("source", 0, "target", 1, "weight", 1.0),
            Map.of("source", 1, "target", 2, "weight", 1.0),
            Map.of("source", 2, "target", 3, "weight", 1.0),
            Map.of("source", 3, "target", 0, "weight", 1.0)
        ));
        qaoaParams.setProblemGraph(problem);

        assertDoesNotThrow(() -> {
            QuantumResult result = quantumProcessor.runQAOA(qaoaParams);
            
            assertNotNull(result);
            assertNotNull(result.getResultData());
            assertTrue(result.getExecutionTime() > 0);
            assertTrue(result.isSuccessful());
        });

        // Test Grover's algorithm
        GroverParameters groverParams = new GroverParameters();
        groverParams.setSearchSpace(16);
        groverParams.setTargetItems(Arrays.asList(5, 10));
        groverParams.setIterations(3);

        assertDoesNotThrow(() -> {
            QuantumResult result = quantumProcessor.runGrover(groverParams);
            
            assertNotNull(result);
            assertNotNull(result.getResultData());
            assertTrue(result.isSuccessful());
        });
    }

    @Test
    @DisplayName("Neuromorphic Processing - Spike Network Test")
    void testNeuromorphicProcessor() {
        // Create network configuration
        NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setNetworkSize(100);
        networkConfig.setConnectionProbability(0.1);
        networkConfig.setLearningRate(0.01);
        networkConfig.setPlasticityEnabled(true);

        // Create spike pattern
        SpikePattern pattern = new SpikePattern();
        pattern.setPatternId("test-pattern");
        pattern.setTimestamps(Arrays.asList(0.0, 0.01, 0.02, 0.03, 0.04));
        pattern.setNeuronIds(Arrays.asList(0, 1, 2, 3, 4));
        pattern.setIntensities(Arrays.asList(1.0, 0.8, 0.9, 0.7, 0.6));

        assertDoesNotThrow(() -> {
            NetworkState initialState = neuromorphicProcessor.initializeNetwork(networkConfig);
            assertNotNull(initialState);
            assertEquals(100, initialState.getNeuronCount());
            assertTrue(initialState.getConnectionCount() > 0);

            TemporalResult result = neuromorphicProcessor.processTemporalSequence(pattern, initialState);
            assertNotNull(result);
            assertNotNull(result.getOutputSpikes());
            assertTrue(result.getProcessingTime() > 0);
        });
    }

    @Test
    @DisplayName("Security Framework - JWT Authentication Test")
    void testSecurityFramework() {
        // Create test user
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedpassword");
        
        Role userRole = new Role();
        userRole.setName("USER");
        testUser.setRoles(Set.of(userRole));

        // Test JWT generation
        assertDoesNotThrow(() -> {
            String token = jwtUtil.generateToken(testUser);
            
            assertNotNull(token);
            assertFalse(token.isEmpty());
            assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
            
            // Test token validation
            assertTrue(jwtUtil.validateToken(token, testUser));
            
            // Test username extraction
            assertEquals("testuser", jwtUtil.getUsernameFromToken(token));
            
            // Test token expiration check
            assertFalse(jwtUtil.isTokenExpired(token));
        });
    }

    @Test
    @DisplayName("End-to-End Integration - AI to Consciousness Pipeline")
    void testEndToEndIntegration() {
        // Test complete pipeline from AI request to consciousness processing
        
        // 1. AI Request
        AIRequest aiRequest = new AIRequest();
        aiRequest.setPrompt("Analyze the philosophical implications of artificial consciousness");
        aiRequest.setTaskType("analytical");
        aiRequest.setUserId("integration-test-user");
        aiRequest.setTimestamp(LocalDateTime.now());

        assertDoesNotThrow(() -> {
            // 2. Process AI request
            AIResponse aiResponse = aiIntegrationService.processRequest(aiRequest);
            assertNotNull(aiResponse);

            // 3. Create consciousness session
            ConsciousnessSession session = new ConsciousnessSession();
            session.setSessionId(UUID.randomUUID().toString());
            session.setEntityId("integration-test-entity");
            session.setStartTime(LocalDateTime.now());

            // 4. Convert AI response to consciousness input
            ConsciousnessInput consciousnessInput = new ConsciousnessInput();
            consciousnessInput.setEntityId("integration-test-entity");
            consciousnessInput.setContent(aiResponse.getContent());
            consciousnessInput.setExperienceType("ai_analysis");
            consciousnessInput.setIntensityLevel(0.8);
            consciousnessInput.setTimestamp(LocalDateTime.now());

            // 5. Process through consciousness engine
            ConsciousnessOutput consciousnessOutput = consciousnessEngine.processExperience(consciousnessInput, session);
            
            assertNotNull(consciousnessOutput);
            assertNotNull(consciousnessOutput.getResponse());
            assertTrue(consciousnessOutput.getConsciousnessLevel() > 0);
            assertTrue(consciousnessOutput.getConfidenceLevel() > 0);
        });
    }

    @Test
    @DisplayName("Performance Benchmark - Concurrent Processing")
    void testConcurrentPerformance() {
        int concurrentRequests = 10;
        List<CompletableFuture<AIResponse>> futures = new ArrayList<>();

        // Create concurrent AI requests
        for (int i = 0; i < concurrentRequests; i++) {
            AIRequest request = new AIRequest();
            request.setPrompt("Test concurrent request " + i);
            request.setTaskType("conversational");
            request.setUserId("perf-test-user-" + i);
            request.setTimestamp(LocalDateTime.now());

            CompletableFuture<AIResponse> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return aiIntegrationService.processRequest(request);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            futures.add(future);
        }

        // Wait for all requests to complete
        assertDoesNotThrow(() -> {
            CompletableFuture<Void> allOf = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
            );
            allOf.get(30, TimeUnit.SECONDS);

            // Verify all responses
            for (CompletableFuture<AIResponse> future : futures) {
                AIResponse response = future.get();
                assertNotNull(response);
                assertNotNull(response.getContent());
                assertTrue(response.getProcessingTime() > 0);
            }
        });
    }

    @Test
    @DisplayName("Error Handling and Resilience Test")
    void testErrorHandling() {
        // Test invalid AI request
        AIRequest invalidRequest = new AIRequest();
        invalidRequest.setPrompt(""); // Empty prompt
        invalidRequest.setTaskType("invalid");
        invalidRequest.setUserId(null); // Null user ID

        assertDoesNotThrow(() -> {
            try {
                AIResponse response = aiIntegrationService.processRequest(invalidRequest);
                // Should handle gracefully
                assertNotNull(response);
                assertTrue(response.getContent().contains("error") || response.getContent().contains("invalid"));
            } catch (Exception e) {
                // Expected exception handling
                assertNotNull(e.getMessage());
            }
        });

        // Test invalid consciousness input
        ConsciousnessInput invalidInput = new ConsciousnessInput();
        invalidInput.setEntityId("");
        invalidInput.setContent(null);
        invalidInput.setIntensityLevel(-1); // Invalid intensity

        ConsciousnessSession session = new ConsciousnessSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setEntityId("test-entity");

        assertDoesNotThrow(() -> {
            try {
                ConsciousnessOutput output = consciousnessEngine.processExperience(invalidInput, session);
                // Should handle gracefully
                assertNotNull(output);
            } catch (Exception e) {
                // Expected exception handling
                assertNotNull(e.getMessage());
            }
        });
    }

    private Stimulus createStimulus(String type, String data, double intensity) {
        Stimulus stimulus = new Stimulus();
        stimulus.setType(type);
        stimulus.setData(data);
        stimulus.setIntensity(intensity);
        stimulus.setTimestamp(LocalDateTime.now());
        return stimulus;
    }

    @AfterAll
    void tearDown() {
        System.out.println("Completed NEXUS AI Platform Integration Tests");
    }
}
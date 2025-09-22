package com.boozer.nexus.test.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;

import com.boozer.nexus.ai.providers.OpenAIProvider;
import com.boozer.nexus.ai.providers.AnthropicProvider;
import com.boozer.nexus.ai.providers.GoogleAIProvider;
import com.boozer.nexus.consciousness.ConsciousnessEngine;
import com.boozer.nexus.quantum.QuantumProcessor;
import com.boozer.nexus.neuromorphic.NeuromorphicProcessor;

import org.mockito.Mockito;

/**
 * Test Configuration for NEXUS AI Platform
 * 
 * Provides test-specific configurations, mock beans, and test properties
 * for comprehensive testing of the NEXUS AI platform.
 */
@TestConfiguration
@Profile("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "logging.level.com.boozer.nexus=DEBUG",
    "nexus.ai.openai.api-key=test-openai-key",
    "nexus.ai.anthropic.api-key=test-anthropic-key",
    "nexus.ai.google.api-key=test-google-key",
    "nexus.quantum.ibm.api-key=test-ibm-quantum-key",
    "nexus.security.jwt.secret=test-jwt-secret-key-for-testing-purposes",
    "nexus.security.jwt.expiration=3600000",
    "nexus.cache.enabled=false",
    "nexus.rate-limiting.enabled=true",
    "nexus.rate-limiting.requests-per-minute=100"
})
public class TestConfig {

    /**
     * Mock OpenAI Provider for testing
     */
    @Bean
    @Primary
    @Profile("test")
    public OpenAIProvider mockOpenAIProvider() {
        OpenAIProvider mockProvider = Mockito.mock(OpenAIProvider.class);
        
        // Configure mock behavior
        Mockito.when(mockProvider.calculateCost(Mockito.any()))
            .thenReturn(0.001);
        
        Mockito.when(mockProvider.assessQuality(Mockito.any()))
            .thenReturn(0.85);
        
        return mockProvider;
    }

    /**
     * Mock Anthropic Provider for testing
     */
    @Bean
    @Primary
    @Profile("test")
    public AnthropicProvider mockAnthropicProvider() {
        AnthropicProvider mockProvider = Mockito.mock(AnthropicProvider.class);
        
        // Configure mock behavior
        Mockito.when(mockProvider.calculateCost(Mockito.any()))
            .thenReturn(0.002);
        
        Mockito.when(mockProvider.assessQuality(Mockito.any()))
            .thenReturn(0.90);
        
        return mockProvider;
    }

    /**
     * Mock Google AI Provider for testing
     */
    @Bean
    @Primary
    @Profile("test")
    public GoogleAIProvider mockGoogleAIProvider() {
        GoogleAIProvider mockProvider = Mockito.mock(GoogleAIProvider.class);
        
        // Configure mock behavior
        Mockito.when(mockProvider.calculateCost(Mockito.any()))
            .thenReturn(0.0015);
        
        Mockito.when(mockProvider.assessQuality(Mockito.any()))
            .thenReturn(0.80);
        
        return mockProvider;
    }
}

/**
 * Test Data Factory for creating test entities and models
 */
public class TestDataFactory {
    
    /**
     * Create test AI request
     */
    public static com.boozer.nexus.ai.models.AIRequest createTestAIRequest(String prompt, String taskType, String userId) {
        com.boozer.nexus.ai.models.AIRequest request = new com.boozer.nexus.ai.models.AIRequest();
        request.setPrompt(prompt);
        request.setTaskType(taskType);
        request.setUserId(userId);
        request.setMaxTokens(150);
        request.setTemperature(0.7);
        request.setTimestamp(java.time.LocalDateTime.now());
        return request;
    }
    
    /**
     * Create test consciousness input
     */
    public static com.boozer.nexus.consciousness.models.ConsciousnessInput createTestConsciousnessInput(String entityId, String content) {
        com.boozer.nexus.consciousness.models.ConsciousnessInput input = new com.boozer.nexus.consciousness.models.ConsciousnessInput();
        input.setEntityId(entityId);
        input.setContent(content);
        input.setExperienceType("test");
        input.setIntensityLevel(0.7);
        input.setTimestamp(java.time.LocalDateTime.now());
        
        java.util.List<com.boozer.nexus.consciousness.models.Stimulus> stimuli = java.util.Arrays.asList(
            createTestStimulus("textual", content, 0.8),
            createTestStimulus("contextual", "test context", 0.6)
        );
        input.setStimuli(stimuli);
        
        return input;
    }
    
    /**
     * Create test stimulus
     */
    public static com.boozer.nexus.consciousness.models.Stimulus createTestStimulus(String type, String data, double intensity) {
        com.boozer.nexus.consciousness.models.Stimulus stimulus = new com.boozer.nexus.consciousness.models.Stimulus();
        stimulus.setType(type);
        stimulus.setData(data);
        stimulus.setIntensity(intensity);
        stimulus.setTimestamp(java.time.LocalDateTime.now());
        return stimulus;
    }
    
    /**
     * Create test consciousness session
     */
    public static com.boozer.nexus.consciousness.models.ConsciousnessSession createTestConsciousnessSession(String entityId) {
        com.boozer.nexus.consciousness.models.ConsciousnessSession session = new com.boozer.nexus.consciousness.models.ConsciousnessSession();
        session.setSessionId(java.util.UUID.randomUUID().toString());
        session.setEntityId(entityId);
        session.setStartTime(java.time.LocalDateTime.now());
        session.setSessionType("test");
        return session;
    }
    
    /**
     * Create test quantum parameters
     */
    public static com.boozer.nexus.quantum.models.QAOAParameters createTestQAOAParameters() {
        com.boozer.nexus.quantum.models.QAOAParameters params = new com.boozer.nexus.quantum.models.QAOAParameters();
        params.setP(2);
        params.setIterations(10);
        
        java.util.Map<String, Object> problem = new java.util.HashMap<>();
        problem.put("vertices", 4);
        problem.put("edges", java.util.Arrays.asList(
            java.util.Map.of("source", 0, "target", 1, "weight", 1.0),
            java.util.Map.of("source", 1, "target", 2, "weight", 1.0),
            java.util.Map.of("source", 2, "target", 3, "weight", 1.0),
            java.util.Map.of("source", 3, "target", 0, "weight", 1.0)
        ));
        params.setProblemGraph(problem);
        
        return params;
    }
    
    /**
     * Create test Grover parameters
     */
    public static com.boozer.nexus.quantum.models.GroverParameters createTestGroverParameters() {
        com.boozer.nexus.quantum.models.GroverParameters params = new com.boozer.nexus.quantum.models.GroverParameters();
        params.setSearchSpace(16);
        params.setTargetItems(java.util.Arrays.asList(5, 10));
        params.setIterations(3);
        return params;
    }
    
    /**
     * Create test network configuration
     */
    public static com.boozer.nexus.neuromorphic.models.NetworkConfig createTestNetworkConfig() {
        com.boozer.nexus.neuromorphic.models.NetworkConfig config = new com.boozer.nexus.neuromorphic.models.NetworkConfig();
        config.setNetworkSize(100);
        config.setConnectionProbability(0.1);
        config.setLearningRate(0.01);
        config.setPlasticityEnabled(true);
        return config;
    }
    
    /**
     * Create test spike pattern
     */
    public static com.boozer.nexus.neuromorphic.models.SpikePattern createTestSpikePattern() {
        com.boozer.nexus.neuromorphic.models.SpikePattern pattern = new com.boozer.nexus.neuromorphic.models.SpikePattern();
        pattern.setPatternId("test-pattern");
        pattern.setTimestamps(java.util.Arrays.asList(0.0, 0.01, 0.02, 0.03, 0.04));
        pattern.setNeuronIds(java.util.Arrays.asList(0, 1, 2, 3, 4));
        pattern.setIntensities(java.util.Arrays.asList(1.0, 0.8, 0.9, 0.7, 0.6));
        return pattern;
    }
    
    /**
     * Create test user
     */
    public static com.boozer.nexus.entities.User createTestUser(String username, String email, String roleName) {
        com.boozer.nexus.entities.User user = new com.boozer.nexus.entities.User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("hashedpassword123");
        
        com.boozer.nexus.entities.Role role = new com.boozer.nexus.entities.Role();
        role.setName(roleName);
        user.setRoles(java.util.Set.of(role));
        
        return user;
    }
}

/**
 * Test Utilities for common test operations
 */
public class TestUtils {
    
    /**
     * Wait for async operation with timeout
     */
    public static void waitForAsync(java.util.concurrent.CompletableFuture<?> future, long timeoutSeconds) throws Exception {
        future.get(timeoutSeconds, java.util.concurrent.TimeUnit.SECONDS);
    }
    
    /**
     * Verify response time is within acceptable range
     */
    public static void assertResponseTime(long startTime, long maxDurationMs) {
        long duration = System.currentTimeMillis() - startTime;
        org.junit.jupiter.api.Assertions.assertTrue(duration <= maxDurationMs, 
            String.format("Response time %dms exceeded maximum %dms", duration, maxDurationMs));
    }
    
    /**
     * Generate random test data
     */
    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        java.util.Random random = new java.util.Random();
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        
        return sb.toString();
    }
    
    /**
     * Create test properties map
     */
    public static java.util.Map<String, Object> createTestProperties() {
        java.util.Map<String, Object> properties = new java.util.HashMap<>();
        properties.put("test.enabled", true);
        properties.put("test.mode", "integration");
        properties.put("test.timestamp", java.time.LocalDateTime.now());
        return properties;
    }
    
    /**
     * Verify all required fields are not null
     */
    public static void assertAllFieldsNotNull(Object object, String... excludedFields) {
        java.util.Set<String> excluded = java.util.Set.of(excludedFields);
        java.lang.reflect.Field[] fields = object.getClass().getDeclaredFields();
        
        for (java.lang.reflect.Field field : fields) {
            if (!excluded.contains(field.getName())) {
                field.setAccessible(true);
                try {
                    Object value = field.get(object);
                    org.junit.jupiter.api.Assertions.assertNotNull(value, 
                        "Field " + field.getName() + " should not be null");
                } catch (IllegalAccessException e) {
                    // Skip fields that can't be accessed
                }
            }
        }
    }
    
    /**
     * Measure execution time of a runnable
     */
    public static long measureExecutionTime(Runnable operation) {
        long startTime = System.currentTimeMillis();
        operation.run();
        return System.currentTimeMillis() - startTime;
    }
    
    /**
     * Create concurrent test executor
     */
    public static java.util.concurrent.ExecutorService createTestExecutor(int threadCount) {
        return java.util.concurrent.Executors.newFixedThreadPool(threadCount);
    }
    
    /**
     * Shutdown executor gracefully
     */
    public static void shutdownExecutor(java.util.concurrent.ExecutorService executor, long timeoutSeconds) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(timeoutSeconds, java.util.concurrent.TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
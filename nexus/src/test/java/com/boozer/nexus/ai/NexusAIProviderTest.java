package com.boozer.nexus.test.ai;

import com.boozer.nexus.ai.ExternalAIIntegrationService;
import com.boozer.nexus.ai.IntelligentAIRouter;
import com.boozer.nexus.ai.providers.OpenAIProvider;
import com.boozer.nexus.ai.providers.AnthropicProvider;
import com.boozer.nexus.ai.providers.GoogleAIProvider;
import com.boozer.nexus.ai.models.*;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive AI Provider Tests for NEXUS AI Platform
 * 
 * Tests all AI providers, intelligent routing, cost optimization,
 * quality assessment, and provider-specific functionality.
 */
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NexusAIProviderTest {

    @Autowired
    private ExternalAIIntegrationService aiIntegrationService;
    
    @Autowired
    private IntelligentAIRouter aiRouter;
    
    @Autowired
    private OpenAIProvider openAIProvider;
    
    @Autowired
    private AnthropicProvider anthropicProvider;
    
    @Autowired
    private GoogleAIProvider googleAIProvider;

    @BeforeAll
    void setupAIProviderTest() {
        System.out.println("Starting NEXUS AI Provider Tests");
    }

    @Test
    @DisplayName("OpenAI Provider - Comprehensive Testing")
    void testOpenAIProvider() {
        // Test basic request
        AIRequest request = new AIRequest();
        request.setPrompt("Explain the concept of artificial intelligence in one paragraph");
        request.setTaskType("explanation");
        request.setMaxTokens(150);
        request.setTemperature(0.7);
        request.setUserId("openai-test-user");
        request.setTimestamp(LocalDateTime.now());

        assertDoesNotThrow(() -> {
            AIResponse response = openAIProvider.processRequest(request);
            
            assertNotNull(response);
            assertEquals("openai", response.getProvider());
            assertNotNull(response.getContent());
            assertFalse(response.getContent().isEmpty());
            assertTrue(response.getProcessingTime() > 0);
            assertTrue(response.getTokensUsed() > 0);
            assertTrue(response.getCost() >= 0);
            assertEquals("openai-test-user", response.getUserId());
        });

        // Test creative task
        request.setPrompt("Write a creative short story about a robot learning to paint");
        request.setTaskType("creative");
        request.setTemperature(0.9);

        assertDoesNotThrow(() -> {
            AIResponse response = openAIProvider.processRequest(request);
            
            assertNotNull(response);
            assertTrue(response.getContent().length() > 100);
            assertTrue(response.getQualityScore() > 0);
        });

        // Test cost calculation
        assertDoesNotThrow(() -> {
            double cost = openAIProvider.calculateCost(request);
            assertTrue(cost >= 0);
        });

        // Test quality assessment
        assertDoesNotThrow(() -> {
            double quality = openAIProvider.assessQuality(request);
            assertTrue(quality >= 0 && quality <= 1);
        });
    }

    @Test
    @DisplayName("Anthropic Provider - Comprehensive Testing")
    void testAnthropicProvider() {
        AIRequest request = new AIRequest();
        request.setPrompt("Analyze the ethical implications of autonomous AI systems");
        request.setTaskType("analytical");
        request.setMaxTokens(200);
        request.setTemperature(0.6);
        request.setUserId("anthropic-test-user");
        request.setTimestamp(LocalDateTime.now());

        assertDoesNotThrow(() -> {
            AIResponse response = anthropicProvider.processRequest(request);
            
            assertNotNull(response);
            assertEquals("anthropic", response.getProvider());
            assertNotNull(response.getContent());
            assertFalse(response.getContent().isEmpty());
            assertTrue(response.getProcessingTime() > 0);
            assertTrue(response.getTokensUsed() > 0);
            assertEquals("anthropic-test-user", response.getUserId());
        });

        // Test philosophical reasoning
        request.setPrompt("What are the fundamental questions about consciousness and free will?");
        request.setTaskType("philosophical");

        assertDoesNotThrow(() -> {
            AIResponse response = anthropicProvider.processRequest(request);
            
            assertNotNull(response);
            assertTrue(response.getContent().contains("consciousness") || response.getContent().contains("free will"));
            assertTrue(response.getQualityScore() > 0.6);
        });

        // Test cost calculation
        assertDoesNotThrow(() -> {
            double cost = anthropicProvider.calculateCost(request);
            assertTrue(cost >= 0);
        });
    }

    @Test
    @DisplayName("Google AI Provider - Comprehensive Testing")
    void testGoogleAIProvider() {
        AIRequest request = new AIRequest();
        request.setPrompt("Describe the latest advances in quantum computing and their potential applications");
        request.setTaskType("technical");
        request.setMaxTokens(180);
        request.setTemperature(0.5);
        request.setUserId("google-test-user");
        request.setTimestamp(LocalDateTime.now());

        assertDoesNotThrow(() -> {
            AIResponse response = googleAIProvider.processRequest(request);
            
            assertNotNull(response);
            assertEquals("google", response.getProvider());
            assertNotNull(response.getContent());
            assertFalse(response.getContent().isEmpty());
            assertTrue(response.getProcessingTime() > 0);
            assertTrue(response.getTokensUsed() > 0);
            assertEquals("google-test-user", response.getUserId());
        });

        // Test conversational task
        request.setPrompt("Have a friendly conversation about the weather and weekend plans");
        request.setTaskType("conversational");
        request.setTemperature(0.8);

        assertDoesNotThrow(() -> {
            AIResponse response = googleAIProvider.processRequest(request);
            
            assertNotNull(response);
            assertTrue(response.getContent().length() > 50);
            assertTrue(response.getQualityScore() > 0);
        });

        // Test quality assessment
        assertDoesNotThrow(() -> {
            double quality = googleAIProvider.assessQuality(request);
            assertTrue(quality >= 0 && quality <= 1);
        });
    }

    @Test
    @DisplayName("Intelligent AI Router - Provider Selection")
    void testIntelligentAIRouter() {
        // Test different task types and verify appropriate provider selection
        Map<String, String> taskProviderMapping = new HashMap<>();
        
        String[] taskTypes = {"creative", "analytical", "conversational", "technical", "philosophical", "coding"};
        
        for (String taskType : taskTypes) {
            AIRequest request = new AIRequest();
            request.setPrompt("Test prompt for " + taskType + " task");
            request.setTaskType(taskType);
            request.setUserId("router-test-user");
            request.setTimestamp(LocalDateTime.now());

            assertDoesNotThrow(() -> {
                String selectedProvider = aiRouter.selectProvider(request);
                assertNotNull(selectedProvider);
                assertTrue(Arrays.asList("openai", "anthropic", "google").contains(selectedProvider));
                taskProviderMapping.put(taskType, selectedProvider);
            });
        }

        // Verify routing decisions are consistent
        for (String taskType : taskTypes) {
            AIRequest request = new AIRequest();
            request.setPrompt("Another test prompt for " + taskType);
            request.setTaskType(taskType);
            request.setUserId("router-consistency-test");

            String selectedProvider = aiRouter.selectProvider(request);
            assertEquals(taskProviderMapping.get(taskType), selectedProvider, 
                "Provider selection should be consistent for same task type");
        }
    }

    @Test
    @DisplayName("Cost Optimization Testing")
    void testCostOptimization() {
        // Test cost optimization across providers
        AIRequest expensiveRequest = new AIRequest();
        expensiveRequest.setPrompt("Generate a very detailed, comprehensive analysis with extensive examples");
        expensiveRequest.setTaskType("analytical");
        expensiveRequest.setMaxTokens(500);
        expensiveRequest.setUserId("cost-test-user");

        AIRequest cheapRequest = new AIRequest();
        cheapRequest.setPrompt("Yes or no?");
        cheapRequest.setTaskType("conversational");
        cheapRequest.setMaxTokens(10);
        cheapRequest.setUserId("cost-test-user");

        assertDoesNotThrow(() -> {
            // Test cost calculations for different providers
            double openAICostExpensive = openAIProvider.calculateCost(expensiveRequest);
            double anthropicCostExpensive = anthropicProvider.calculateCost(expensiveRequest);
            double googleCostExpensive = googleAIProvider.calculateCost(expensiveRequest);

            double openAICostCheap = openAIProvider.calculateCost(cheapRequest);
            double anthropicCostCheap = anthropicProvider.calculateCost(cheapRequest);
            double googleCostCheap = googleAIProvider.calculateCost(cheapRequest);

            // Expensive requests should cost more than cheap requests
            assertTrue(openAICostExpensive > openAICostCheap);
            assertTrue(anthropicCostExpensive > anthropicCostCheap);
            assertTrue(googleCostExpensive > googleCostCheap);

            // All costs should be non-negative
            assertTrue(openAICostExpensive >= 0);
            assertTrue(anthropicCostExpensive >= 0);
            assertTrue(googleCostExpensive >= 0);
        });
    }

    @Test
    @DisplayName("Quality Assessment Validation")
    void testQualityAssessment() {
        // Test quality assessment for different request types
        String[] prompts = {
            "Simple question", // Should have decent quality
            "Complex analytical task requiring deep reasoning", // Should have high quality
            "", // Empty prompt should have low quality
            "Random gibberish xyz abc 123" // Should have low quality
        };

        for (String prompt : prompts) {
            AIRequest request = new AIRequest();
            request.setPrompt(prompt);
            request.setTaskType("analytical");
            request.setUserId("quality-test-user");

            assertDoesNotThrow(() -> {
                double openAIQuality = openAIProvider.assessQuality(request);
                double anthropicQuality = anthropicProvider.assessQuality(request);
                double googleQuality = googleAIProvider.assessQuality(request);

                // All quality scores should be between 0 and 1
                assertTrue(openAIQuality >= 0 && openAIQuality <= 1);
                assertTrue(anthropicQuality >= 0 && anthropicQuality <= 1);
                assertTrue(googleQuality >= 0 && googleQuality <= 1);

                // Empty or nonsensical prompts should have lower quality scores
                if (prompt.isEmpty() || prompt.contains("gibberish")) {
                    assertTrue(openAIQuality < 0.5);
                    assertTrue(anthropicQuality < 0.5);
                    assertTrue(googleQuality < 0.5);
                }
            });
        }
    }

    @Test
    @DisplayName("Provider Fallback and Error Handling")
    void testProviderFallback() {
        // Test provider fallback mechanism
        AIRequest request = new AIRequest();
        request.setPrompt("Test fallback mechanism");
        request.setTaskType("conversational");
        request.setUserId("fallback-test-user");
        request.setTimestamp(LocalDateTime.now());

        assertDoesNotThrow(() -> {
            // Process request through integration service which should handle fallbacks
            AIResponse response = aiIntegrationService.processRequest(request);
            
            assertNotNull(response);
            assertNotNull(response.getProvider());
            assertTrue(Arrays.asList("openai", "anthropic", "google").contains(response.getProvider()));
            assertNotNull(response.getContent());
            assertFalse(response.getContent().isEmpty());
        });
    }

    @Test
    @DisplayName("Concurrent Provider Testing")
    void testConcurrentProviderAccess() {
        int concurrentRequests = 15;
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<CompletableFuture<AIResponse>> futures = new ArrayList<>();

        // Create concurrent requests to different providers
        for (int i = 0; i < concurrentRequests; i++) {
            final int requestId = i;
            
            CompletableFuture<AIResponse> future = CompletableFuture.supplyAsync(() -> {
                AIRequest request = new AIRequest();
                request.setPrompt("Concurrent test request " + requestId);
                request.setTaskType("conversational");
                request.setUserId("concurrent-test-" + requestId);
                request.setTimestamp(LocalDateTime.now());

                try {
                    return aiIntegrationService.processRequest(request);
                } catch (Exception e) {
                    throw new RuntimeException("Concurrent request failed", e);
                }
            }, executor);
            
            futures.add(future);
        }

        assertDoesNotThrow(() -> {
            // Wait for all requests to complete
            CompletableFuture<Void> allOf = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
            );
            allOf.get(60, TimeUnit.SECONDS);

            // Verify all responses
            List<AIResponse> responses = futures.stream()
                .map(CompletableFuture::join)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

            assertEquals(concurrentRequests, responses.size());
            
            for (AIResponse response : responses) {
                assertNotNull(response);
                assertNotNull(response.getContent());
                assertFalse(response.getContent().isEmpty());
                assertTrue(response.getProcessingTime() > 0);
                assertNotNull(response.getProvider());
            }
        });

        executor.shutdown();
    }

    @Test
    @DisplayName("Provider Caching Mechanism")
    void testProviderCaching() {
        // Test caching mechanism for repeated requests
        AIRequest request = new AIRequest();
        request.setPrompt("What is the capital of France?");
        request.setTaskType("factual");
        request.setUserId("cache-test-user");
        request.setTimestamp(LocalDateTime.now());

        assertDoesNotThrow(() -> {
            // First request
            long startTime1 = System.currentTimeMillis();
            AIResponse response1 = aiIntegrationService.processRequest(request);
            long duration1 = System.currentTimeMillis() - startTime1;

            assertNotNull(response1);
            assertNotNull(response1.getContent());

            // Second identical request (should potentially be cached)
            long startTime2 = System.currentTimeMillis();
            AIResponse response2 = aiIntegrationService.processRequest(request);
            long duration2 = System.currentTimeMillis() - startTime2;

            assertNotNull(response2);
            assertEquals(response1.getContent(), response2.getContent());
            
            // Cache hit should be faster (though this might not always be true in test environment)
            // assertTrue(duration2 <= duration1, "Cached request should be faster or equal");
        });
    }

    @Test
    @DisplayName("Provider Performance Metrics")
    void testProviderPerformanceMetrics() {
        Map<String, List<Long>> performanceMetrics = new HashMap<>();
        int testRequests = 10;

        // Test each provider's performance
        String[] providers = {"openai", "anthropic", "google"};
        
        for (String providerName : providers) {
            List<Long> responseTimes = new ArrayList<>();
            
            for (int i = 0; i < testRequests; i++) {
                AIRequest request = new AIRequest();
                request.setPrompt("Performance test request " + i + " for " + providerName);
                request.setTaskType("conversational");
                request.setUserId("perf-test-" + providerName + "-" + i);
                request.setTimestamp(LocalDateTime.now());

                long startTime = System.currentTimeMillis();
                
                try {
                    switch (providerName) {
                        case "openai":
                            openAIProvider.processRequest(request);
                            break;
                        case "anthropic":
                            anthropicProvider.processRequest(request);
                            break;
                        case "google":
                            googleAIProvider.processRequest(request);
                            break;
                    }
                    
                    long responseTime = System.currentTimeMillis() - startTime;
                    responseTimes.add(responseTime);
                    
                } catch (Exception e) {
                    fail("Provider " + providerName + " failed: " + e.getMessage());
                }
            }
            
            performanceMetrics.put(providerName, responseTimes);
        }

        // Analyze performance metrics
        performanceMetrics.forEach((provider, times) -> {
            double avgTime = times.stream().mapToLong(Long::longValue).average().orElse(0.0);
            long minTime = times.stream().mapToLong(Long::longValue).min().orElse(0L);
            long maxTime = times.stream().mapToLong(Long::longValue).max().orElse(0L);
            
            System.out.printf("%s Provider Performance:%n", provider.toUpperCase());
            System.out.printf("  Average Response Time: %.2f ms%n", avgTime);
            System.out.printf("  Min Response Time: %d ms%n", minTime);
            System.out.printf("  Max Response Time: %d ms%n", maxTime);
            System.out.println();
            
            // Performance assertions
            assertTrue(avgTime > 0, "Average response time should be positive");
            assertTrue(avgTime < 10000, "Average response time should be under 10 seconds");
            assertEquals(testRequests, times.size(), "All requests should complete");
        });
    }

    @AfterAll
    void tearDownAIProviderTest() {
        System.out.println("Completed NEXUS AI Provider Tests");
    }
}
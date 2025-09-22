package com.boozer.nexus.test.performance;

import com.boozer.nexus.ai.ExternalAIIntegrationService;
import com.boozer.nexus.ai.models.AIRequest;
import com.boozer.nexus.ai.models.AIResponse;
import com.boozer.nexus.consciousness.ConsciousnessEngine;
import com.boozer.nexus.consciousness.models.*;
import com.boozer.nexus.quantum.QuantumProcessor;
import com.boozer.nexus.quantum.models.*;
import com.boozer.nexus.neuromorphic.NeuromorphicProcessor;
import com.boozer.nexus.neuromorphic.models.*;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance Benchmark Tests for NEXUS AI Platform
 * 
 * Measures performance characteristics, throughput, latency, and scalability
 * of all major system components under various load conditions.
 */
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NexusPerformanceTest {

    @Autowired
    private ExternalAIIntegrationService aiIntegrationService;
    
    @Autowired
    private ConsciousnessEngine consciousnessEngine;
    
    @Autowired
    private QuantumProcessor quantumProcessor;
    
    @Autowired
    private NeuromorphicProcessor neuromorphicProcessor;

    private ExecutorService executorService;
    private Map<String, List<Long>> performanceMetrics;

    @BeforeAll
    void setupPerformanceTest() {
        executorService = Executors.newFixedThreadPool(20);
        performanceMetrics = new ConcurrentHashMap<>();
        System.out.println("Starting NEXUS AI Platform Performance Tests");
    }

    @Test
    @DisplayName("AI Integration Service - Throughput Benchmark")
    void benchmarkAIThroughput() {
        int requestCount = 50;
        int concurrentUsers = 10;
        
        List<CompletableFuture<Long>> futures = IntStream.range(0, requestCount)
            .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                long startTime = System.currentTimeMillis();
                
                AIRequest request = new AIRequest();
                request.setPrompt("Benchmark test request " + i);
                request.setTaskType("conversational");
                request.setUserId("perf-test-user-" + (i % concurrentUsers));
                request.setMaxTokens(100);
                request.setTimestamp(LocalDateTime.now());
                
                try {
                    AIResponse response = aiIntegrationService.processRequest(request);
                    assertNotNull(response);
                    assertNotNull(response.getContent());
                    
                    return System.currentTimeMillis() - startTime;
                } catch (Exception e) {
                    fail("AI request failed: " + e.getMessage());
                    return -1L;
                }
            }, executorService))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        List<Long> responseTimes = futures.stream()
            .map(CompletableFuture::join)
            .filter(time -> time > 0)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        // Performance analysis
        double averageResponseTime = responseTimes.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0);
        
        long minResponseTime = responseTimes.stream()
            .mapToLong(Long::longValue)
            .min()
            .orElse(0L);
        
        long maxResponseTime = responseTimes.stream()
            .mapToLong(Long::longValue)
            .max()
            .orElse(0L);
        
        double throughput = (double) responseTimes.size() / (maxResponseTime - minResponseTime) * 1000;

        System.out.printf("AI Integration Throughput Benchmark:%n");
        System.out.printf("  Requests: %d%n", responseTimes.size());
        System.out.printf("  Average Response Time: %.2f ms%n", averageResponseTime);
        System.out.printf("  Min Response Time: %d ms%n", minResponseTime);
        System.out.printf("  Max Response Time: %d ms%n", maxResponseTime);
        System.out.printf("  Throughput: %.2f requests/second%n", throughput);

        // Performance assertions
        assertTrue(averageResponseTime < 5000, "Average response time should be under 5 seconds");
        assertTrue(responseTimes.size() >= requestCount * 0.95, "At least 95% of requests should succeed");
        
        performanceMetrics.put("ai_integration", responseTimes);
    }

    @Test
    @DisplayName("Consciousness Engine - Processing Benchmark")
    void benchmarkConsciousnessProcessing() {
        int processCount = 30;
        List<Long> processingTimes = new ArrayList<>();
        
        // Create consciousness session
        ConsciousnessSession session = new ConsciousnessSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setEntityId("perf-test-entity");
        session.setStartTime(LocalDateTime.now());

        for (int i = 0; i < processCount; i++) {
            long startTime = System.currentTimeMillis();
            
            ConsciousnessInput input = new ConsciousnessInput();
            input.setEntityId("perf-test-entity");
            input.setContent("Performance test input " + i + ": Analyze complex philosophical question about consciousness and reality");
            input.setExperienceType("analysis");
            input.setIntensityLevel(0.7 + (i % 3) * 0.1);
            input.setTimestamp(LocalDateTime.now());
            
            List<Stimulus> stimuli = Arrays.asList(
                createStimulus("textual", "complex analysis", 0.8),
                createStimulus("conceptual", "philosophical", 0.9)
            );
            input.setStimuli(stimuli);

            try {
                ConsciousnessOutput output = consciousnessEngine.processExperience(input, session);
                assertNotNull(output);
                assertTrue(output.getConsciousnessLevel() > 0);
                
                long processingTime = System.currentTimeMillis() - startTime;
                processingTimes.add(processingTime);
                
            } catch (Exception e) {
                fail("Consciousness processing failed: " + e.getMessage());
            }
        }

        // Performance analysis
        double averageProcessingTime = processingTimes.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0);
        
        long minProcessingTime = processingTimes.stream()
            .mapToLong(Long::longValue)
            .min()
            .orElse(0L);
        
        long maxProcessingTime = processingTimes.stream()
            .mapToLong(Long::longValue)
            .max()
            .orElse(0L);

        System.out.printf("Consciousness Engine Processing Benchmark:%n");
        System.out.printf("  Processes: %d%n", processingTimes.size());
        System.out.printf("  Average Processing Time: %.2f ms%n", averageProcessingTime);
        System.out.printf("  Min Processing Time: %d ms%n", minProcessingTime);
        System.out.printf("  Max Processing Time: %d ms%n", maxProcessingTime);

        // Performance assertions
        assertTrue(averageProcessingTime < 3000, "Average processing time should be under 3 seconds");
        assertEquals(processCount, processingTimes.size(), "All processes should complete successfully");
        
        performanceMetrics.put("consciousness_processing", processingTimes);
    }

    @Test
    @DisplayName("Quantum Processing - Algorithm Performance")
    void benchmarkQuantumPerformance() {
        List<Long> qaoaTimes = new ArrayList<>();
        List<Long> groverTimes = new ArrayList<>();
        
        // QAOA Performance Test
        for (int i = 0; i < 10; i++) {
            long startTime = System.currentTimeMillis();
            
            QAOAParameters qaoaParams = new QAOAParameters();
            qaoaParams.setP(2);
            qaoaParams.setIterations(5 + i);
            
            Map<String, Object> problem = new HashMap<>();
            problem.put("vertices", 6 + (i % 4));
            problem.put("edges", generateRandomEdges(6 + (i % 4)));
            qaoaParams.setProblemGraph(problem);

            try {
                QuantumResult result = quantumProcessor.runQAOA(qaoaParams);
                assertTrue(result.isSuccessful());
                
                long processingTime = System.currentTimeMillis() - startTime;
                qaoaTimes.add(processingTime);
                
            } catch (Exception e) {
                fail("QAOA processing failed: " + e.getMessage());
            }
        }
        
        // Grover's Algorithm Performance Test
        for (int i = 0; i < 10; i++) {
            long startTime = System.currentTimeMillis();
            
            GroverParameters groverParams = new GroverParameters();
            groverParams.setSearchSpace(16 + i * 4);
            groverParams.setTargetItems(Arrays.asList(5 + i, 10 + i));
            groverParams.setIterations(3);

            try {
                QuantumResult result = quantumProcessor.runGrover(groverParams);
                assertTrue(result.isSuccessful());
                
                long processingTime = System.currentTimeMillis() - startTime;
                groverTimes.add(processingTime);
                
            } catch (Exception e) {
                fail("Grover processing failed: " + e.getMessage());
            }
        }

        // Performance analysis
        double avgQaoaTime = qaoaTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double avgGroverTime = groverTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);

        System.out.printf("Quantum Processing Performance Benchmark:%n");
        System.out.printf("  QAOA Average Time: %.2f ms%n", avgQaoaTime);
        System.out.printf("  Grover Average Time: %.2f ms%n", avgGroverTime);

        // Performance assertions
        assertTrue(avgQaoaTime < 2000, "QAOA average time should be under 2 seconds");
        assertTrue(avgGroverTime < 1500, "Grover average time should be under 1.5 seconds");
        
        performanceMetrics.put("quantum_qaoa", qaoaTimes);
        performanceMetrics.put("quantum_grover", groverTimes);
    }

    @Test
    @DisplayName("Neuromorphic Processing - Spike Network Performance")
    void benchmarkNeuromorphicPerformance() {
        List<Long> processingTimes = new ArrayList<>();
        
        // Network configuration
        NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setNetworkSize(200);
        networkConfig.setConnectionProbability(0.15);
        networkConfig.setLearningRate(0.01);
        networkConfig.setPlasticityEnabled(true);

        NetworkState networkState = null;
        try {
            networkState = neuromorphicProcessor.initializeNetwork(networkConfig);
            assertNotNull(networkState);
        } catch (Exception e) {
            fail("Network initialization failed: " + e.getMessage());
        }

        // Process multiple spike patterns
        for (int i = 0; i < 20; i++) {
            long startTime = System.currentTimeMillis();
            
            SpikePattern pattern = new SpikePattern();
            pattern.setPatternId("perf-pattern-" + i);
            
            // Generate temporal sequence
            List<Double> timestamps = new ArrayList<>();
            List<Integer> neuronIds = new ArrayList<>();
            List<Double> intensities = new ArrayList<>();
            
            for (int j = 0; j < 50 + i * 2; j++) {
                timestamps.add(j * 0.001);
                neuronIds.add(j % networkConfig.getNetworkSize());
                intensities.add(0.5 + Math.random() * 0.5);
            }
            
            pattern.setTimestamps(timestamps);
            pattern.setNeuronIds(neuronIds);
            pattern.setIntensities(intensities);

            try {
                TemporalResult result = neuromorphicProcessor.processTemporalSequence(pattern, networkState);
                assertNotNull(result);
                assertNotNull(result.getOutputSpikes());
                
                long processingTime = System.currentTimeMillis() - startTime;
                processingTimes.add(processingTime);
                
            } catch (Exception e) {
                fail("Neuromorphic processing failed: " + e.getMessage());
            }
        }

        // Performance analysis
        double averageProcessingTime = processingTimes.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0);
        
        long maxProcessingTime = processingTimes.stream()
            .mapToLong(Long::longValue)
            .max()
            .orElse(0L);

        System.out.printf("Neuromorphic Processing Performance Benchmark:%n");
        System.out.printf("  Patterns Processed: %d%n", processingTimes.size());
        System.out.printf("  Average Processing Time: %.2f ms%n", averageProcessingTime);
        System.out.printf("  Max Processing Time: %d ms%n", maxProcessingTime);

        // Performance assertions
        assertTrue(averageProcessingTime < 1000, "Average neuromorphic processing time should be under 1 second");
        assertEquals(20, processingTimes.size(), "All patterns should be processed successfully");
        
        performanceMetrics.put("neuromorphic_processing", processingTimes);
    }

    @Test
    @DisplayName("Memory Usage and Resource Utilization")
    void benchmarkResourceUtilization() {
        Runtime runtime = Runtime.getRuntime();
        
        // Baseline memory
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // Stress test with multiple concurrent operations
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    // AI processing
                    AIRequest aiRequest = new AIRequest();
                    aiRequest.setPrompt("Resource test " + taskId);
                    aiRequest.setTaskType("conversational");
                    aiRequest.setUserId("resource-test-" + taskId);
                    aiIntegrationService.processRequest(aiRequest);
                    
                    // Consciousness processing
                    ConsciousnessSession session = new ConsciousnessSession();
                    session.setSessionId(UUID.randomUUID().toString());
                    session.setEntityId("resource-test-entity-" + taskId);
                    
                    ConsciousnessInput input = new ConsciousnessInput();
                    input.setEntityId("resource-test-entity-" + taskId);
                    input.setContent("Resource utilization test");
                    input.setExperienceType("test");
                    input.setIntensityLevel(0.5);
                    input.setTimestamp(LocalDateTime.now());
                    
                    consciousnessEngine.processExperience(input, session);
                    
                } catch (Exception e) {
                    System.err.println("Resource test task failed: " + e.getMessage());
                }
            }, executorService);
            
            futures.add(future);
        }
        
        // Wait for completion
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        // Force garbage collection
        System.gc();
        
        // Final memory measurement
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = finalMemory - initialMemory;
        
        System.out.printf("Resource Utilization Benchmark:%n");
        System.out.printf("  Initial Memory: %d MB%n", initialMemory / (1024 * 1024));
        System.out.printf("  Final Memory: %d MB%n", finalMemory / (1024 * 1024));
        System.out.printf("  Memory Increase: %d MB%n", memoryIncrease / (1024 * 1024));
        System.out.printf("  Available Processors: %d%n", runtime.availableProcessors());
        
        // Resource assertions
        assertTrue(memoryIncrease < 500 * 1024 * 1024, "Memory increase should be under 500MB");
    }

    @Test
    @DisplayName("End-to-End Performance Summary")
    void performanceSummary() {
        System.out.println("\n=== NEXUS AI Platform Performance Summary ===");
        
        performanceMetrics.forEach((component, times) -> {
            double avgTime = times.stream().mapToLong(Long::longValue).average().orElse(0.0);
            long minTime = times.stream().mapToLong(Long::longValue).min().orElse(0L);
            long maxTime = times.stream().mapToLong(Long::longValue).max().orElse(0L);
            
            System.out.printf("%s:%n", component.toUpperCase().replace("_", " "));
            System.out.printf("  Operations: %d%n", times.size());
            System.out.printf("  Average: %.2f ms%n", avgTime);
            System.out.printf("  Min: %d ms%n", minTime);
            System.out.printf("  Max: %d ms%n", maxTime);
            System.out.printf("  95th Percentile: %.2f ms%n", calculatePercentile(times, 95));
            System.out.println();
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

    private List<Map<String, Object>> generateRandomEdges(int vertices) {
        List<Map<String, Object>> edges = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < vertices; i++) {
            for (int j = i + 1; j < vertices; j++) {
                if (random.nextDouble() < 0.3) { // 30% edge probability
                    Map<String, Object> edge = new HashMap<>();
                    edge.put("source", i);
                    edge.put("target", j);
                    edge.put("weight", random.nextDouble());
                    edges.add(edge);
                }
            }
        }
        return edges;
    }

    private double calculatePercentile(List<Long> values, int percentile) {
        List<Long> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        
        int index = (int) Math.ceil(sorted.size() * percentile / 100.0) - 1;
        return sorted.get(Math.max(0, Math.min(index, sorted.size() - 1)));
    }

    @AfterAll
    void tearDownPerformanceTest() {
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
        System.out.println("Completed NEXUS AI Platform Performance Tests");
    }
}
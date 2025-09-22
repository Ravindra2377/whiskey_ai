package com.boozer.nexus.billing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class RevenueEngineTest {

    @InjectMocks
    private RevenueEngine revenueEngine;

    private ExecutorService testExecutor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testExecutor = Executors.newSingleThreadExecutor();
        
        // Use reflection to set the executor
        try {
            java.lang.reflect.Field executorField = RevenueEngine.class.getDeclaredField("billingExecutor");
            executorField.setAccessible(true);
            executorField.set(revenueEngine, testExecutor);
        } catch (Exception e) {
            // Ignore for testing
        }
    }

    @Test
    void testTrackUsage() {
        // Arrange
        String tenantId = "test-tenant";
        Map<String, Object> usageEvent = new HashMap<>();
        usageEvent.put("event_type", "api_call");
        usageEvent.put("count", 100);

        // Act
        CompletableFuture<Map<String, Object>> futureResult = revenueEngine.trackUsage(tenantId, usageEvent);
        Map<String, Object> result = futureResult.join();

        // Assert
        assertEquals("SUCCESS", result.get("status"));
        assertEquals("Usage tracked successfully", result.get("message"));
        assertTrue(result.containsKey("billable_amount"));
    }

    @Test
    void testGenerateMonthlyBill() {
        // Arrange
        String tenantId = "test-tenant";

        // Act
        CompletableFuture<Map<String, Object>> futureResult = revenueEngine.generateMonthlyBill(tenantId);
        Map<String, Object> result = futureResult.join();

        // Assert
        assertEquals("SUCCESS", result.get("status"));
        assertEquals("Monthly bill generated successfully", result.get("message"));
        assertTrue(result.containsKey("invoice"));
        
        Map<String, Object> invoice = (Map<String, Object>) result.get("invoice");
        assertNotNull(invoice.get("invoice_id"));
        assertEquals(tenantId, invoice.get("tenant_id"));
        assertTrue(invoice.containsKey("charges"));
    }
}

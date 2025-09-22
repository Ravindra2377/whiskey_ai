package com.boozer.nexus;

import com.boozer.nexus.dto.AlgorithmicTradeRequest;
import com.boozer.nexus.dto.TradingResult;
import com.boozer.nexus.service.QuantitativeDevelopmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class QuantitativeDevelopmentTest {
    
    @Autowired
    private QuantitativeDevelopmentService quantService;
    
    @Test
    public void testAlgorithmicTradeExecution() throws Exception {
        // Create a test trade request
        AlgorithmicTradeRequest request = new AlgorithmicTradeRequest();
        request.setAsset("BTCUSD");
        request.setQuantity(1.5);
        request.setOrderType("MARKET");
        request.setStrategy("HFT");
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("risk_tolerance", "HIGH");
        parameters.put("time_horizon", "SHORT");
        request.setParameters(parameters);
        
        // Execute the trade
        CompletableFuture<TradingResult> futureResult = quantService.executeAlgorithmicTrade(request);
        TradingResult result = futureResult.get();
        
        // Verify the result
        assertNotNull(result);
        assertTrue(result.isSuccessful());
        assertNotNull(result.getMessage());
        assertNotNull(result.getOptimization());
        assertNotNull(result.getTiming());
        assertNotNull(result.getExecution());
        assertNotNull(result.getEvolution());
    }
    
    @Test
    public void testHighFrequencyTradeExecution() throws Exception {
        // Create mock market data
        Map<String, Object> marketData = new HashMap<>();
        marketData.put("asset", "BTCUSD");
        marketData.put("price", 50000.0);
        marketData.put("volume", 1000L);
        marketData.put("timestamp", System.currentTimeMillis());
        
        // Execute HFT trade
        CompletableFuture<Map<String, Object>> futureResult = quantService.executeHighFrequencyTrade(marketData);
        Map<String, Object> result = futureResult.get();
        
        // Verify the result
        assertNotNull(result);
        // The result might be empty if no arbitrage opportunity is detected
        // but it should not be null
    }
}

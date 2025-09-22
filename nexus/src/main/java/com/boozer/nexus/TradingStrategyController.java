package com.boozer.nexus;

import com.boozer.nexus.model.TradingStrategy;
import com.boozer.nexus.service.TradingStrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/nexus/strategies")
public class TradingStrategyController {
    
    @Autowired
    private TradingStrategyService tradingStrategyService;
    
    /**
     * Create a new trading strategy
     */
    @PostMapping
    public ResponseEntity<TradingStrategy> createStrategy(@RequestBody TradingStrategy strategy) {
        try {
            TradingStrategy savedStrategy = tradingStrategyService.saveStrategy(strategy);
            return ResponseEntity.ok(savedStrategy);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Get a trading strategy by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TradingStrategy> getStrategyById(@PathVariable Long id) {
        Optional<TradingStrategy> strategy = tradingStrategyService.getStrategyById(id);
        return strategy.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get all trading strategies
     */
    @GetMapping
    public ResponseEntity<List<TradingStrategy>> getAllStrategies() {
        List<TradingStrategy> strategies = tradingStrategyService.getAllStrategies();
        return ResponseEntity.ok(strategies);
    }
    
    /**
     * Get trading strategies by type
     */
    @GetMapping("/type/{strategyType}")
    public ResponseEntity<List<TradingStrategy>> getStrategiesByType(@PathVariable String strategyType) {
        List<TradingStrategy> strategies = tradingStrategyService.getStrategiesByType(strategyType);
        return ResponseEntity.ok(strategies);
    }
    
    /**
     * Search trading strategies by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<TradingStrategy>> searchStrategiesByName(@RequestParam String name) {
        List<TradingStrategy> strategies = tradingStrategyService.searchStrategiesByName(name);
        return ResponseEntity.ok(strategies);
    }
    
    /**
     * Update a trading strategy
     */
    @PutMapping("/{id}")
    public ResponseEntity<TradingStrategy> updateStrategy(@PathVariable Long id, @RequestBody TradingStrategy strategy) {
        Optional<TradingStrategy> existingStrategy = tradingStrategyService.getStrategyById(id);
        if (existingStrategy.isPresent()) {
            strategy.setId(id);
            TradingStrategy updatedStrategy = tradingStrategyService.saveStrategy(strategy);
            return ResponseEntity.ok(updatedStrategy);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Delete a trading strategy
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStrategy(@PathVariable Long id) {
        Optional<TradingStrategy> strategy = tradingStrategyService.getStrategyById(id);
        if (strategy.isPresent()) {
            tradingStrategyService.deleteStrategy(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

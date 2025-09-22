package com.boozer.nexus.service;

import com.boozer.nexus.model.TradingStrategy;
import com.boozer.nexus.repository.TradingStrategyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TradingStrategyService {
    
    @Autowired
    private TradingStrategyRepository tradingStrategyRepository;
    
    /**
     * Save a trading strategy
     */
    public TradingStrategy saveStrategy(TradingStrategy strategy) {
        return tradingStrategyRepository.save(strategy);
    }
    
    /**
     * Get a trading strategy by ID
     */
    public Optional<TradingStrategy> getStrategyById(Long id) {
        return tradingStrategyRepository.findById(id);
    }
    
    /**
     * Get all trading strategies
     */
    public List<TradingStrategy> getAllStrategies() {
        return tradingStrategyRepository.findAll();
    }
    
    /**
     * Get trading strategies by type
     */
    public List<TradingStrategy> getStrategiesByType(String strategyType) {
        return tradingStrategyRepository.findByStrategyType(strategyType);
    }
    
    /**
     * Search trading strategies by name
     */
    public List<TradingStrategy> searchStrategiesByName(String name) {
        return tradingStrategyRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Delete a trading strategy
     */
    public void deleteStrategy(Long id) {
        tradingStrategyRepository.deleteById(id);
    }
}

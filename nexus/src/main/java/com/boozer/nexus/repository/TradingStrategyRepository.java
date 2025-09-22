package com.boozer.nexus.repository;

import com.boozer.nexus.model.TradingStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradingStrategyRepository extends JpaRepository<TradingStrategy, Long> {
    
    /**
     * Find trading strategies by type
     */
    List<TradingStrategy> findByStrategyType(String strategyType);
    
    /**
     * Find trading strategies by name
     */
    List<TradingStrategy> findByNameContainingIgnoreCase(String name);
}

package com.boozer.nexus.repository;

import com.boozer.nexus.model.FinancialData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FinancialDataRepository extends JpaRepository<FinancialData, Long> {
    
    /**
     * Find financial data by asset symbol
     */
    List<FinancialData> findByAssetSymbol(String assetSymbol);
    
    /**
     * Find financial data by asset symbol and timestamp range
     */
    List<FinancialData> findByAssetSymbolAndTimestampBetween(String assetSymbol, LocalDateTime start, LocalDateTime end);
    
    /**
     * Find the latest financial data for an asset
     */
    List<FinancialData> findFirst10ByAssetSymbolOrderByTimestampDesc(String assetSymbol);
}

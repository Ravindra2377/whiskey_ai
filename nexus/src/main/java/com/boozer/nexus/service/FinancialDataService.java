package com.boozer.nexus.service;

import com.boozer.nexus.model.FinancialData;
import com.boozer.nexus.repository.FinancialDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FinancialDataService {
    
    @Autowired
    private FinancialDataRepository financialDataRepository;
    
    /**
     * Save financial data
     */
    public FinancialData saveFinancialData(FinancialData financialData) {
        return financialDataRepository.save(financialData);
    }
    
    /**
     * Get financial data by ID
     */
    public Optional<FinancialData> getFinancialDataById(Long id) {
        return financialDataRepository.findById(id);
    }
    
    /**
     * Get all financial data for an asset
     */
    public List<FinancialData> getFinancialDataByAssetSymbol(String assetSymbol) {
        return financialDataRepository.findByAssetSymbol(assetSymbol);
    }
    
    /**
     * Get financial data for an asset within a time range
     */
    public List<FinancialData> getFinancialDataByAssetSymbolAndTimeRange(
            String assetSymbol, LocalDateTime start, LocalDateTime end) {
        return financialDataRepository.findByAssetSymbolAndTimestampBetween(assetSymbol, start, end);
    }
    
    /**
     * Get latest financial data for an asset
     */
    public List<FinancialData> getLatestFinancialData(String assetSymbol) {
        return financialDataRepository.findFirst10ByAssetSymbolOrderByTimestampDesc(assetSymbol);
    }
    
    /**
     * Calculate simple moving average for an asset
     */
    public BigDecimal calculateSimpleMovingAverage(String assetSymbol, int periods) {
        List<FinancialData> latestData = financialDataRepository.findFirst10ByAssetSymbolOrderByTimestampDesc(assetSymbol);
        
        if (latestData.size() < periods) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < periods; i++) {
            sum = sum.add(latestData.get(i).getClosePrice());
        }
        
        return sum.divide(BigDecimal.valueOf(periods));
    }
    
    /**
     * Calculate volatility for an asset
     */
    public BigDecimal calculateVolatility(String assetSymbol, int periods) {
        List<FinancialData> latestData = financialDataRepository.findFirst10ByAssetSymbolOrderByTimestampDesc(assetSymbol);
        
        if (latestData.size() < periods) {
            return BigDecimal.ZERO;
        }
        
        // Calculate average
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < periods; i++) {
            sum = sum.add(latestData.get(i).getClosePrice());
        }
        BigDecimal average = sum.divide(BigDecimal.valueOf(periods));
        
        // Calculate variance
        BigDecimal varianceSum = BigDecimal.ZERO;
        for (int i = 0; i < periods; i++) {
            BigDecimal diff = latestData.get(i).getClosePrice().subtract(average);
            varianceSum = varianceSum.add(diff.multiply(diff));
        }
        
        BigDecimal variance = varianceSum.divide(BigDecimal.valueOf(periods));
        
        // Return standard deviation (square root of variance)
        return BigDecimal.valueOf(Math.sqrt(variance.doubleValue()));
    }
}

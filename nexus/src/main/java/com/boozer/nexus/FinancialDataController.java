package com.boozer.nexus;

import com.boozer.nexus.model.FinancialData;
import com.boozer.nexus.service.FinancialDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/nexus/financial-data")
public class FinancialDataController {
    
    @Autowired
    private FinancialDataService financialDataService;
    
    /**
     * Save financial data
     */
    @PostMapping
    public ResponseEntity<FinancialData> saveFinancialData(@RequestBody FinancialData financialData) {
        try {
            FinancialData savedData = financialDataService.saveFinancialData(financialData);
            return ResponseEntity.ok(savedData);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Get financial data by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<FinancialData> getFinancialDataById(@PathVariable Long id) {
        Optional<FinancialData> financialData = financialDataService.getFinancialDataById(id);
        return financialData.map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get all financial data for an asset
     */
    @GetMapping("/asset/{assetSymbol}")
    public ResponseEntity<List<FinancialData>> getFinancialDataByAssetSymbol(@PathVariable String assetSymbol) {
        List<FinancialData> financialData = financialDataService.getFinancialDataByAssetSymbol(assetSymbol);
        return ResponseEntity.ok(financialData);
    }
    
    /**
     * Get financial data for an asset within a time range
     */
    @GetMapping("/asset/{assetSymbol}/range")
    public ResponseEntity<List<FinancialData>> getFinancialDataByAssetSymbolAndTimeRange(
            @PathVariable String assetSymbol,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        List<FinancialData> financialData = financialDataService.getFinancialDataByAssetSymbolAndTimeRange(
            assetSymbol, start, end);
        return ResponseEntity.ok(financialData);
    }
    
    /**
     * Get latest financial data for an asset
     */
    @GetMapping("/asset/{assetSymbol}/latest")
    public ResponseEntity<List<FinancialData>> getLatestFinancialData(@PathVariable String assetSymbol) {
        List<FinancialData> financialData = financialDataService.getLatestFinancialData(assetSymbol);
        return ResponseEntity.ok(financialData);
    }
    
    /**
     * Calculate simple moving average for an asset
     */
    @GetMapping("/asset/{assetSymbol}/sma")
    public ResponseEntity<BigDecimal> calculateSimpleMovingAverage(
            @PathVariable String assetSymbol,
            @RequestParam int periods) {
        BigDecimal sma = financialDataService.calculateSimpleMovingAverage(assetSymbol, periods);
        return ResponseEntity.ok(sma);
    }
    
    /**
     * Calculate volatility for an asset
     */
    @GetMapping("/asset/{assetSymbol}/volatility")
    public ResponseEntity<BigDecimal> calculateVolatility(
            @PathVariable String assetSymbol,
            @RequestParam int periods) {
        BigDecimal volatility = financialDataService.calculateVolatility(assetSymbol, periods);
        return ResponseEntity.ok(volatility);
    }
}

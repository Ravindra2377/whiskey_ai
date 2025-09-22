package com.boozer.nexus.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_data")
public class FinancialData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "asset_symbol")
    private String assetSymbol;
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    @Column(name = "open_price")
    private BigDecimal openPrice;
    
    @Column(name = "high_price")
    private BigDecimal highPrice;
    
    @Column(name = "low_price")
    private BigDecimal lowPrice;
    
    @Column(name = "close_price")
    private BigDecimal closePrice;
    
    @Column(name = "volume")
    private Long volume;
    
    @Column(name = "data_source")
    private String dataSource;
    
    // Constructors
    public FinancialData() {}
    
    public FinancialData(String assetSymbol, LocalDateTime timestamp, BigDecimal openPrice, 
                        BigDecimal highPrice, BigDecimal lowPrice, BigDecimal closePrice, 
                        Long volume, String dataSource) {
        this.assetSymbol = assetSymbol;
        this.timestamp = timestamp;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
        this.dataSource = dataSource;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAssetSymbol() {
        return assetSymbol;
    }
    
    public void setAssetSymbol(String assetSymbol) {
        this.assetSymbol = assetSymbol;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public BigDecimal getOpenPrice() {
        return openPrice;
    }
    
    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }
    
    public BigDecimal getHighPrice() {
        return highPrice;
    }
    
    public void setHighPrice(BigDecimal highPrice) {
        this.highPrice = highPrice;
    }
    
    public BigDecimal getLowPrice() {
        return lowPrice;
    }
    
    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }
    
    public BigDecimal getClosePrice() {
        return closePrice;
    }
    
    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }
    
    public Long getVolume() {
        return volume;
    }
    
    public void setVolume(Long volume) {
        this.volume = volume;
    }
    
    public String getDataSource() {
        return dataSource;
    }
    
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}

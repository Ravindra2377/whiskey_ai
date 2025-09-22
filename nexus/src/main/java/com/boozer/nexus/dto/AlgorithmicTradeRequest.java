package com.boozer.nexus.dto;

import java.util.Map;

public class AlgorithmicTradeRequest {
    private String asset;
    private double quantity;
    private String orderType; // MARKET, LIMIT, STOP
    private Double price; // For limit and stop orders
    private String strategy; // HFT, STAT_ARBITRAGE, MARKET_MAKING
    private Map<String, Object> parameters;
    
    // Constructors
    public AlgorithmicTradeRequest() {}
    
    public AlgorithmicTradeRequest(String asset, double quantity, String orderType, Double price, String strategy, Map<String, Object> parameters) {
        this.asset = asset;
        this.quantity = quantity;
        this.orderType = orderType;
        this.price = price;
        this.strategy = strategy;
        this.parameters = parameters;
    }
    
    // Getters and Setters
    public String getAsset() {
        return asset;
    }
    
    public void setAsset(String asset) {
        this.asset = asset;
    }
    
    public double getQuantity() {
        return quantity;
    }
    
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    
    public String getOrderType() {
        return orderType;
    }
    
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public String getStrategy() {
        return strategy;
    }
    
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}

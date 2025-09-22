package com.boozer.nexus.dto;

import java.util.List;
import java.util.Map;

public class PortfolioRiskRequest {
    private List<PortfolioAsset> portfolio;
    private String riskModel; // VAR, MONTE_CARLO, STRESS_TESTING
    private Map<String, Object> parameters;
    private String timeframe; // DAILY, WEEKLY, MONTHLY, YEARLY
    
    // Constructors
    public PortfolioRiskRequest() {}
    
    public PortfolioRiskRequest(List<PortfolioAsset> portfolio, String riskModel, Map<String, Object> parameters, String timeframe) {
        this.portfolio = portfolio;
        this.riskModel = riskModel;
        this.parameters = parameters;
        this.timeframe = timeframe;
    }
    
    // Getters and Setters
    public List<PortfolioAsset> getPortfolio() {
        return portfolio;
    }
    
    public void setPortfolio(List<PortfolioAsset> portfolio) {
        this.portfolio = portfolio;
    }
    
    public String getRiskModel() {
        return riskModel;
    }
    
    public void setRiskModel(String riskModel) {
        this.riskModel = riskModel;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public String getTimeframe() {
        return timeframe;
    }
    
    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }
    
    // Inner class for portfolio assets
    public static class PortfolioAsset {
        private String asset;
        private double quantity;
        private double price;
        private double weight;
        
        // Constructors
        public PortfolioAsset() {}
        
        public PortfolioAsset(String asset, double quantity, double price, double weight) {
            this.asset = asset;
            this.quantity = quantity;
            this.price = price;
            this.weight = weight;
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
        
        public double getPrice() {
            return price;
        }
        
        public void setPrice(double price) {
            this.price = price;
        }
        
        public double getWeight() {
            return weight;
        }
        
        public void setWeight(double weight) {
            this.weight = weight;
        }
    }
}

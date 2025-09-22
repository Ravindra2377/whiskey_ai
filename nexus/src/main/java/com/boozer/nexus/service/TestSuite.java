package com.boozer.nexus.service;

import java.util.List;

public class TestSuite {
    private int testCount;
    private double coveragePercentage;
    private List<String> testCases;
    private String framework;
    
    // Constructors
    public TestSuite() {}
    
    public TestSuite(int testCount, double coveragePercentage, List<String> testCases, String framework) {
        this.testCount = testCount;
        this.coveragePercentage = coveragePercentage;
        this.testCases = testCases;
        this.framework = framework;
    }
    
    // Getters and Setters
    public int getTestCount() {
        return testCount;
    }
    
    public void setTestCount(int testCount) {
        this.testCount = testCount;
    }
    
    public double getCoveragePercentage() {
        return coveragePercentage;
    }
    
    public void setCoveragePercentage(double coveragePercentage) {
        this.coveragePercentage = coveragePercentage;
    }
    
    public List<String> getTestCases() {
        return testCases;
    }
    
    public void setTestCases(List<String> testCases) {
        this.testCases = testCases;
    }
    
    public String getFramework() {
        return framework;
    }
    
    public void setFramework(String framework) {
        this.framework = framework;
    }
}

package com.boozer.nexus.learning.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistillationConfiguration {
    private String distillationType = "standard";
    private double temperature = 3.0;
    private double alpha = 0.7;
    private int studentModelSize = 100;
    private List<String> targetKnowledgeTypes = new ArrayList<>();
    private Map<String, Object> parameters = new HashMap<>();

    public String getDistillationType() { return distillationType; }
    public void setDistillationType(String distillationType) { this.distillationType = distillationType; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public double getAlpha() { return alpha; }
    public void setAlpha(double alpha) { this.alpha = alpha; }
    public int getStudentModelSize() { return studentModelSize; }
    public void setStudentModelSize(int studentModelSize) { this.studentModelSize = studentModelSize; }
    public List<String> getTargetKnowledgeTypes() { return targetKnowledgeTypes; }
    public void setTargetKnowledgeTypes(List<String> targetKnowledgeTypes) { this.targetKnowledgeTypes = targetKnowledgeTypes; }
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
}

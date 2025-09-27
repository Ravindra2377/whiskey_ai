package com.boozer.nexus.learning.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EpisodicMemory {
    private String episodeId;
    private LearningExperience experience;
    private FeatureVector features;
    private LocalDateTime timestamp;
    private double significance;
    private String context;
    private List<String> associations = new ArrayList<>();
    private double consolidationStrength = 0.5;

    public String getEpisodeId() { return episodeId; }
    public void setEpisodeId(String episodeId) { this.episodeId = episodeId; }
    public LearningExperience getExperience() { return experience; }
    public void setExperience(LearningExperience experience) { this.experience = experience; }
    public FeatureVector getFeatures() { return features; }
    public void setFeatures(FeatureVector features) { this.features = features; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public double getSignificance() { return significance; }
    public void setSignificance(double significance) { this.significance = significance; }
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    public List<String> getAssociations() { return associations; }
    public void setAssociations(List<String> associations) { this.associations = associations; }
    public double getConsolidationStrength() { return consolidationStrength; }
    public void setConsolidationStrength(double consolidationStrength) { this.consolidationStrength = consolidationStrength; }
}

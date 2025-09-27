package com.boozer.nexus.consciousness.models;

public class ConsciousnessMetrics {
    private long processedExperiences;

    public void updateWithResult(Object result) {
        processedExperiences++;
    }

    public long getProcessedExperiences() { return processedExperiences; }
}

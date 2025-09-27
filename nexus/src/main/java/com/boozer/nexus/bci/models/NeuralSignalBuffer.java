package com.boozer.nexus.bci.models;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class NeuralSignalBuffer {
    private final int bufferSize;
    private final double samplingRate;
    private final Queue<NeuralSignalData> buffer;
    private LocalDateTime lastUpdate;

    public NeuralSignalBuffer(int bufferSize, double samplingRate) {
        this.bufferSize = bufferSize;
        this.samplingRate = samplingRate;
        this.buffer = new LinkedList<>();
        this.lastUpdate = LocalDateTime.now();
    }

    public void addSignalData(NeuralSignalData data) {
        if (buffer.size() >= bufferSize) buffer.poll();
        buffer.offer(data);
        lastUpdate = LocalDateTime.now();
    }

    public List<NeuralSignalData> getRecentSignals(int count) {
        return buffer.stream().skip(Math.max(0, buffer.size() - count)).collect(Collectors.toList());
    }

    public int getBufferSize() { return bufferSize; }
    public double getSamplingRate() { return samplingRate; }
    public int getCurrentSize() { return buffer.size(); }
    public LocalDateTime getLastUpdate() { return lastUpdate; }
}

package com.boozer.nexus.bci.models;

import com.boozer.nexus.consciousness.models.ConsciousnessSession;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Extracted from monolithic BCIModels. Represents an active BCI session.
 * Only minimal fields and methods required by BCISimulationEngine retained for now.
 */
public class BCISession {
    private final String sessionId;
    private final String sessionType;
    private final String participantId;
    private final LocalDateTime startTime;
    private LocalDateTime endTime;
    private final BCIConfiguration configuration;
    private CalibrationResult calibrationResult;
    private MotorCalibration motorCalibration;
    private ConsciousnessSession consciousnessSession;
    private final List<LearnedPattern> learnedPatterns;
    private final List<MotorIntention> movementHistory;
    private final List<BCICommand> executedCommands;
    private final Set<String> permissions;
    private LocalDateTime lastActivity;
    private boolean active;
    private int commandCount;
    private BCIMetrics baselineMetrics;

    public BCISession(String sessionType, String participantId, BCIConfiguration configuration) {
        this.sessionId = UUID.randomUUID().toString();
        this.sessionType = sessionType;
        this.participantId = participantId;
        this.startTime = LocalDateTime.now();
        this.configuration = configuration != null ? configuration : new BCIConfiguration();
        this.learnedPatterns = new ArrayList<>();
        this.movementHistory = new ArrayList<>();
        this.executedCommands = new ArrayList<>();
        this.permissions = new HashSet<>();
        this.lastActivity = LocalDateTime.now();
        this.active = true;
        permissions.addAll(Arrays.asList("CURSOR_MOVE","CLICK","TYPE_TEXT"));
    }

    public void addMotorPrediction(RefinedMotorIntention intention) {
        if (intention != null) {
            movementHistory.add(intention);
            touch();
        }
    }

    public void addExecutedCommand(BCICommand command, CommandExecutionResult result) {
        if (command != null) {
            executedCommands.add(command);
            commandCount++;
            touch();
        }
    }

    public void terminate() {
        this.active = false;
        this.endTime = LocalDateTime.now();
    }

    private void touch() { this.lastActivity = LocalDateTime.now(); }

    // Getters / Setters
    public String getSessionId() { return sessionId; }
    public String getSessionType() { return sessionType; }
    public String getParticipantId() { return participantId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public BCIConfiguration getConfiguration() { return configuration; }
    public CalibrationResult getCalibrationResult() { return calibrationResult; }
    public void setCalibrationResult(CalibrationResult calibrationResult) { this.calibrationResult = calibrationResult; }
    public MotorCalibration getMotorCalibration() { return motorCalibration; }
    public void setMotorCalibration(MotorCalibration motorCalibration) { this.motorCalibration = motorCalibration; }
    public ConsciousnessSession getConsciousnessSession() { return consciousnessSession; }
    public void setConsciousnessSession(ConsciousnessSession consciousnessSession) { this.consciousnessSession = consciousnessSession; }
    public List<LearnedPattern> getLearnedPatterns() { return new ArrayList<>(learnedPatterns); }
    public List<MotorIntention> getMovementHistory() { return new ArrayList<>(movementHistory); }
    public Set<String> getPermissions() { return new HashSet<>(permissions); }
    public LocalDateTime getLastActivity() { return lastActivity; }
    public boolean isActive() { return active; }
    public int getCommandCount() { return commandCount; }
    public BCIMetrics getBaselineMetrics() { return baselineMetrics; }
    public void setBaselineMetrics(BCIMetrics baselineMetrics) { this.baselineMetrics = baselineMetrics; }
}

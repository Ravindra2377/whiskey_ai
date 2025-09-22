package com.boozer.nexus.bci.models;

import com.boozer.nexus.consciousness.models.ConsciousnessSession;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Comprehensive BCI Models for NEXUS AI Platform
 * 
 * This file contains all data models required for Brain-Computer Interface operations,
 * including neural signal processing, thought pattern recognition, motor intention
 * prediction, and bidirectional brain-computer communication.
 */

/**
 * BCI Session - Main session container for BCI operations
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
    private final FeedbackPreferences feedbackPreferences;
    private LocalDateTime lastActivity;
    private boolean active;
    private int maxSafetyLevel;
    private int maxCommandsPerMinute;
    private int commandCount;
    private final Map<String, Object> sessionData;
    private BCIMetrics baselineMetrics;
    
    public BCISession(String sessionType, String participantId, BCIConfiguration configuration) {
        this.sessionId = UUID.randomUUID().toString();
        this.sessionType = sessionType;
        this.participantId = participantId;
        this.startTime = LocalDateTime.now();
        this.configuration = configuration;
        this.learnedPatterns = new ArrayList<>();
        this.movementHistory = new ArrayList<>();
        this.executedCommands = new ArrayList<>();
        this.permissions = new HashSet<>();
        this.feedbackPreferences = new FeedbackPreferences();
        this.lastActivity = LocalDateTime.now();
        this.active = true;
        this.maxSafetyLevel = 3;
        this.maxCommandsPerMinute = 60;
        this.commandCount = 0;
        this.sessionData = new HashMap<>();
        
        // Default permissions
        permissions.addAll(Arrays.asList("CURSOR_MOVE", "CLICK", "TYPE_TEXT"));
    }
    
    public void addMotorPrediction(RefinedMotorIntention intention) {
        movementHistory.add(intention);
        updateLastActivity();
    }
    
    public void addExecutedCommand(BCICommand command, CommandExecutionResult result) {
        executedCommands.add(command);
        commandCount++;
        updateLastActivity();
    }
    
    public void addNeurofeedbackSession(NeurofeedbackSession trainingSession, NeurofeedbackAnalysis analysis) {
        sessionData.put("lastNeurofeedback", trainingSession);
        sessionData.put("lastAnalysis", analysis);
        updateLastActivity();
    }
    
    public void terminate() {
        this.active = false;
        this.endTime = LocalDateTime.now();
    }
    
    private void updateLastActivity() {
        this.lastActivity = LocalDateTime.now();
    }
    
    // Getters and Setters
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
    public FeedbackPreferences getFeedbackPreferences() { return feedbackPreferences; }
    public LocalDateTime getLastActivity() { return lastActivity; }
    public boolean isActive() { return active; }
    public int getMaxSafetyLevel() { return maxSafetyLevel; }
    public int getMaxCommandsPerMinute() { return maxCommandsPerMinute; }
    public int getCommandCount() { return commandCount; }
    public BCIMetrics getBaselineMetrics() { return baselineMetrics; }
    public void setBaselineMetrics(BCIMetrics baselineMetrics) { this.baselineMetrics = baselineMetrics; }
}

/**
 * BCI Configuration - Configuration settings for BCI sessions
 */
public static class BCIConfiguration {
    private final int bufferSize;
    private final double samplingRate;
    private final String calibrationType;
    private final List<String> electrodePositions;
    private final Map<String, Double> filterSettings;
    private final boolean realTimeProcessing;
    private final int processingDelay;
    
    public BCIConfiguration() {
        this.bufferSize = 1000;
        this.samplingRate = 256.0; // 256 Hz
        this.calibrationType = "STANDARD";
        this.electrodePositions = Arrays.asList("C3", "C4", "Cz", "F3", "F4", "P3", "P4");
        this.filterSettings = createDefaultFilterSettings();
        this.realTimeProcessing = true;
        this.processingDelay = 50; // 50ms
    }
    
    private Map<String, Double> createDefaultFilterSettings() {
        Map<String, Double> settings = new HashMap<>();
        settings.put("highPass", 1.0);
        settings.put("lowPass", 50.0);
        settings.put("notch", 50.0);
        return settings;
    }
    
    // Getters
    public int getBufferSize() { return bufferSize; }
    public double getSamplingRate() { return samplingRate; }
    public String getCalibrationType() { return calibrationType; }
    public List<String> getElectrodePositions() { return new ArrayList<>(electrodePositions); }
    public Map<String, Double> getFilterSettings() { return new HashMap<>(filterSettings); }
    public boolean isRealTimeProcessing() { return realTimeProcessing; }
    public int getProcessingDelay() { return processingDelay; }
}

/**
 * Neural Signal Data - Raw neural signal data from electrodes
 */
public static class NeuralSignalData {
    private final String signalId;
    private final double[] signalData;
    private final String[] electrodeLabels;
    private final double samplingRate;
    private final long timestamp;
    private final Map<String, Double> signalQualityMetrics;
    
    public NeuralSignalData(double[] signalData, String[] electrodeLabels, double samplingRate) {
        this.signalId = UUID.randomUUID().toString();
        this.signalData = Arrays.copyOf(signalData, signalData.length);
        this.electrodeLabels = Arrays.copyOf(electrodeLabels, electrodeLabels.length);
        this.samplingRate = samplingRate;
        this.timestamp = System.currentTimeMillis();
        this.signalQualityMetrics = new HashMap<>();
        calculateQualityMetrics();
    }
    
    private void calculateQualityMetrics() {
        // Calculate signal-to-noise ratio
        double snr = calculateSNR(signalData);
        signalQualityMetrics.put("snr", snr);
        
        // Calculate impedance estimate
        double impedance = estimateImpedance(signalData);
        signalQualityMetrics.put("impedance", impedance);
        
        // Calculate artifact level
        double artifactLevel = detectArtifacts(signalData);
        signalQualityMetrics.put("artifactLevel", artifactLevel);
    }
    
    private double calculateSNR(double[] signal) {
        // Signal-to-noise ratio calculation
        double signalPower = Arrays.stream(signal).map(x -> x * x).average().orElse(0.0);
        double noisePower = estimateNoisePower(signal);
        return 10 * Math.log10(signalPower / noisePower);
    }
    
    private double estimateImpedance(double[] signal) {
        // Electrode impedance estimation
        return 5000.0; // Placeholder: 5kΩ
    }
    
    private double detectArtifacts(double[] signal) {
        // Artifact detection (eye blinks, muscle activity, etc.)
        return 0.1; // Placeholder: 10% artifact level
    }
    
    private double estimateNoisePower(double[] signal) {
        // Noise power estimation
        return 0.01; // Placeholder implementation
    }
    
    // Getters
    public String getSignalId() { return signalId; }
    public double[] getSignalData() { return Arrays.copyOf(signalData, signalData.length); }
    public String[] getElectrodeLabels() { return Arrays.copyOf(electrodeLabels, electrodeLabels.length); }
    public double getSamplingRate() { return samplingRate; }
    public long getTimestamp() { return timestamp; }
    public Map<String, Double> getSignalQualityMetrics() { return new HashMap<>(signalQualityMetrics); }
}

/**
 * Processed Signal - Signal after initial processing
 */
public static class ProcessedSignal {
    private NeuralSignalData originalSignal;
    private FrequencyFeatures frequencyFeatures;
    private TimeFeatures timeFeatures;
    private SpatialFeatures spatialFeatures;
    private LocalDateTime timestamp;
    
    // Getters and Setters
    public NeuralSignalData getOriginalSignal() { return originalSignal; }
    public void setOriginalSignal(NeuralSignalData originalSignal) { this.originalSignal = originalSignal; }
    public FrequencyFeatures getFrequencyFeatures() { return frequencyFeatures; }
    public void setFrequencyFeatures(FrequencyFeatures frequencyFeatures) { this.frequencyFeatures = frequencyFeatures; }
    public TimeFeatures getTimeFeatures() { return timeFeatures; }
    public void setTimeFeatures(TimeFeatures timeFeatures) { this.timeFeatures = timeFeatures; }
    public SpatialFeatures getSpatialFeatures() { return spatialFeatures; }
    public void setSpatialFeatures(SpatialFeatures spatialFeatures) { this.spatialFeatures = spatialFeatures; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}

/**
 * Signal Features Classes
 */
public static class FrequencyFeatures {
    private double alphaPower;
    private double betaPower;
    private double gammaPower;
    private double deltaPower;
    private double thetaPower;
    
    // Getters and Setters
    public double getAlphaPower() { return alphaPower; }
    public void setAlphaPower(double alphaPower) { this.alphaPower = alphaPower; }
    public double getBetaPower() { return betaPower; }
    public void setBetaPower(double betaPower) { this.betaPower = betaPower; }
    public double getGammaPower() { return gammaPower; }
    public void setGammaPower(double gammaPower) { this.gammaPower = gammaPower; }
    public double getDeltaPower() { return deltaPower; }
    public void setDeltaPower(double deltaPower) { this.deltaPower = deltaPower; }
    public double getThetaPower() { return thetaPower; }
    public void setThetaPower(double thetaPower) { this.thetaPower = thetaPower; }
}

public static class TimeFeatures {
    private double mean;
    private double variance;
    private double skewness;
    private double kurtosis;
    private double rms;
    
    // Getters and Setters
    public double getMean() { return mean; }
    public void setMean(double mean) { this.mean = mean; }
    public double getVariance() { return variance; }
    public void setVariance(double variance) { this.variance = variance; }
    public double getSkewness() { return skewness; }
    public void setSkewness(double skewness) { this.skewness = skewness; }
    public double getKurtosis() { return kurtosis; }
    public void setKurtosis(double kurtosis) { this.kurtosis = kurtosis; }
    public double getRms() { return rms; }
    public void setRms(double rms) { this.rms = rms; }
}

public static class SpatialFeatures {
    private double[] cspFeatures;
    private double[] laplacianFeatures;
    
    // Getters and Setters
    public double[] getCspFeatures() { return cspFeatures != null ? Arrays.copyOf(cspFeatures, cspFeatures.length) : null; }
    public void setCspFeatures(double[] cspFeatures) { this.cspFeatures = cspFeatures != null ? Arrays.copyOf(cspFeatures, cspFeatures.length) : null; }
    public double[] getLaplacianFeatures() { return laplacianFeatures != null ? Arrays.copyOf(laplacianFeatures, laplacianFeatures.length) : null; }
    public void setLaplacianFeatures(double[] laplacianFeatures) { this.laplacianFeatures = laplacianFeatures != null ? Arrays.copyOf(laplacianFeatures, laplacianFeatures.length) : null; }
}

/**
 * Filtered Signal - Signal after filtering
 */
public static class FilteredSignal {
    private ProcessedSignal originalSignal;
    private double[] filteredData;
    private double noiseLevel;
    private LocalDateTime timestamp;
    
    // Getters and Setters
    public ProcessedSignal getOriginalSignal() { return originalSignal; }
    public void setOriginalSignal(ProcessedSignal originalSignal) { this.originalSignal = originalSignal; }
    public double[] getFilteredData() { return filteredData != null ? Arrays.copyOf(filteredData, filteredData.length) : null; }
    public void setFilteredData(double[] filteredData) { this.filteredData = filteredData != null ? Arrays.copyOf(filteredData, filteredData.length) : null; }
    public double getNoiseLevel() { return noiseLevel; }
    public void setNoiseLevel(double noiseLevel) { this.noiseLevel = noiseLevel; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}

/**
 * Thought Pattern and Recognition Models
 */
public static class ThoughtPattern {
    private String patternType;
    private double confidence;
    private double intensity;
    private long duration;
    private FrequencySignature frequencySignature;
    private List<String> associatedConcepts;
    private LocalDateTime timestamp;
    
    // Getters and Setters
    public String getPatternType() { return patternType; }
    public void setPatternType(String patternType) { this.patternType = patternType; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public double getIntensity() { return intensity; }
    public void setIntensity(double intensity) { this.intensity = intensity; }
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    public FrequencySignature getFrequencySignature() { return frequencySignature; }
    public void setFrequencySignature(FrequencySignature frequencySignature) { this.frequencySignature = frequencySignature; }
    public List<String> getAssociatedConcepts() { return associatedConcepts != null ? new ArrayList<>(associatedConcepts) : null; }
    public void setAssociatedConcepts(List<String> associatedConcepts) { this.associatedConcepts = associatedConcepts != null ? new ArrayList<>(associatedConcepts) : null; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}

public static class FrequencySignature {
    private double dominantFrequency;
    private double peakAmplitude;
    private double bandwidth;
    
    // Getters and Setters
    public double getDominantFrequency() { return dominantFrequency; }
    public void setDominantFrequency(double dominantFrequency) { this.dominantFrequency = dominantFrequency; }
    public double getPeakAmplitude() { return peakAmplitude; }
    public void setPeakAmplitude(double peakAmplitude) { this.peakAmplitude = peakAmplitude; }
    public double getBandwidth() { return bandwidth; }
    public void setBandwidth(double bandwidth) { this.bandwidth = bandwidth; }
}

public static class LearnedPattern {
    private String patternType;
    private double[] featureVector;
    private double accuracy;
    private LocalDateTime learnedAt;
    
    // Getters and Setters
    public String getPatternType() { return patternType; }
    public void setPatternType(String patternType) { this.patternType = patternType; }
    public double[] getFeatureVector() { return featureVector != null ? Arrays.copyOf(featureVector, featureVector.length) : null; }
    public void setFeatureVector(double[] featureVector) { this.featureVector = featureVector != null ? Arrays.copyOf(featureVector, featureVector.length) : null; }
    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
    public LocalDateTime getLearnedAt() { return learnedAt; }
    public void setLearnedAt(LocalDateTime learnedAt) { this.learnedAt = learnedAt; }
}

/**
 * Motor Intention and Movement Models
 */
public static class MotorCortexSignal {
    private NeuralSignalData sourceSignal;
    private double[] motorBandSignal;
    private double[] movementPotentials;
    
    // Getters and Setters
    public NeuralSignalData getSourceSignal() { return sourceSignal; }
    public void setSourceSignal(NeuralSignalData sourceSignal) { this.sourceSignal = sourceSignal; }
    public double[] getMotorBandSignal() { return motorBandSignal != null ? Arrays.copyOf(motorBandSignal, motorBandSignal.length) : null; }
    public void setMotorBandSignal(double[] motorBandSignal) { this.motorBandSignal = motorBandSignal != null ? Arrays.copyOf(motorBandSignal, motorBandSignal.length) : null; }
    public double[] getMovementPotentials() { return movementPotentials != null ? Arrays.copyOf(movementPotentials, movementPotentials.length) : null; }
    public void setMovementPotentials(double[] movementPotentials) { this.movementPotentials = movementPotentials != null ? Arrays.copyOf(movementPotentials, movementPotentials.length) : null; }
}

public static class MotorIntention {
    private MovementVector movementVector;
    private String action;
    private double confidence;
    private LocalDateTime timestamp;
    
    // Getters and Setters
    public MovementVector getMovementVector() { return movementVector; }
    public void setMovementVector(MovementVector movementVector) { this.movementVector = movementVector; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}

public static class RefinedMotorIntention extends MotorIntention {
    private MotorIntention originalIntention;
    private boolean isContinuation;
    
    // Getters and Setters
    public MotorIntention getOriginalIntention() { return originalIntention; }
    public void setOriginalIntention(MotorIntention originalIntention) { this.originalIntention = originalIntention; }
    public boolean isContinuation() { return isContinuation; }
    public void setContinuation(boolean continuation) { isContinuation = continuation; }
}

public static class MovementVector {
    private double x;
    private double y;
    private double z;
    private double magnitude;
    
    // Getters and Setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getZ() { return z; }
    public void setZ(double z) { this.z = z; }
    public double getMagnitude() { return magnitude; }
    public void setMagnitude(double magnitude) { this.magnitude = magnitude; }
}

/**
 * BCI Commands and Execution
 */
public static class BCICommand {
    private final String commandId;
    private final String commandType;
    private final Map<String, Object> parameters;
    private final int safetyLevel;
    private final LocalDateTime timestamp;
    
    public BCICommand(String commandType, Map<String, Object> parameters, int safetyLevel) {
        this.commandId = UUID.randomUUID().toString();
        this.commandType = commandType;
        this.parameters = new HashMap<>(parameters);
        this.safetyLevel = safetyLevel;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters
    public String getCommandId() { return commandId; }
    public String getCommandType() { return commandType; }
    public Map<String, Object> getParameters() { return new HashMap<>(parameters); }
    public int getSafetyLevel() { return safetyLevel; }
    public LocalDateTime getTimestamp() { return timestamp; }
}

public static class CommandExecutionResult {
    private String commandId;
    private boolean successful;
    private String message;
    private LocalDateTime executionTime;
    
    // Getters and Setters
    public String getCommandId() { return commandId; }
    public void setCommandId(String commandId) { this.commandId = commandId; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getExecutionTime() { return executionTime; }
    public void setExecutionTime(LocalDateTime executionTime) { this.executionTime = executionTime; }
}

/**
 * Request and Result Models
 */
public static class BCISessionRequest {
    private String sessionType;
    private String participantId;
    private BCIConfiguration configuration;
    private boolean consciousnessIntegration;
    
    // Getters and Setters
    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
    public String getParticipantId() { return participantId; }
    public void setParticipantId(String participantId) { this.participantId = participantId; }
    public BCIConfiguration getConfiguration() { return configuration; }
    public void setConfiguration(BCIConfiguration configuration) { this.configuration = configuration; }
    public boolean isConsciousnessIntegration() { return consciousnessIntegration; }
    public void setConsciousnessIntegration(boolean consciousnessIntegration) { this.consciousnessIntegration = consciousnessIntegration; }
}

public static class BCISessionResult {
    private String sessionId;
    private boolean successful;
    private String message;
    private double calibrationAccuracy;
    private LocalDateTime timestamp;
    
    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public double getCalibrationAccuracy() { return calibrationAccuracy; }
    public void setCalibrationAccuracy(double calibrationAccuracy) { this.calibrationAccuracy = calibrationAccuracy; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}

public static class ThoughtRecognitionResult {
    private String sessionId;
    private ThoughtPattern recognizedPattern;
    private double confidence;
    private long processingTime;
    private boolean successful;
    private String errorMessage;
    
    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public ThoughtPattern getRecognizedPattern() { return recognizedPattern; }
    public void setRecognizedPattern(ThoughtPattern recognizedPattern) { this.recognizedPattern = recognizedPattern; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public long getProcessingTime() { return processingTime; }
    public void setProcessingTime(long processingTime) { this.processingTime = processingTime; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}

public static class MotorIntentionResult {
    private String sessionId;
    private RefinedMotorIntention motorIntention;
    private double confidence;
    private MovementVector movementVector;
    private String predictedAction;
    private boolean successful;
    private String errorMessage;
    
    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public RefinedMotorIntention getMotorIntention() { return motorIntention; }
    public void setMotorIntention(RefinedMotorIntention motorIntention) { this.motorIntention = motorIntention; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public MovementVector getMovementVector() { return movementVector; }
    public void setMovementVector(MovementVector movementVector) { this.movementVector = movementVector; }
    public String getPredictedAction() { return predictedAction; }
    public void setPredictedAction(String predictedAction) { this.predictedAction = predictedAction; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}

public static class BCICommandResult {
    private String sessionId;
    private String commandId;
    private CommandExecutionResult executionResult;
    private boolean successful;
    private String errorMessage;
    private boolean feedbackProvided;
    
    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getCommandId() { return commandId; }
    public void setCommandId(String commandId) { this.commandId = commandId; }
    public CommandExecutionResult getExecutionResult() { return executionResult; }
    public void setExecutionResult(CommandExecutionResult executionResult) { this.executionResult = executionResult; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public boolean isFeedbackProvided() { return feedbackProvided; }
    public void setFeedbackProvided(boolean feedbackProvided) { this.feedbackProvided = feedbackProvided; }
}

/**
 * Supporting Models for BCI Operations
 */
public static class CalibrationResult {
    private String sessionId;
    private String calibrationType;
    private double accuracy;
    private LocalDateTime timestamp;
    
    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getCalibrationType() { return calibrationType; }
    public void setCalibrationType(String calibrationType) { this.calibrationType = calibrationType; }
    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}

public static class MotorCalibration {
    private Map<String, Double> axisCalibration;
    private double sensitivity;
    private LocalDateTime calibratedAt;
    
    public MotorCalibration() {
        this.axisCalibration = new HashMap<>();
        this.sensitivity = 1.0;
        this.calibratedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Map<String, Double> getAxisCalibration() { return new HashMap<>(axisCalibration); }
    public void setAxisCalibration(Map<String, Double> axisCalibration) { this.axisCalibration = new HashMap<>(axisCalibration); }
    public double getSensitivity() { return sensitivity; }
    public void setSensitivity(double sensitivity) { this.sensitivity = sensitivity; }
    public LocalDateTime getCalibratedAt() { return calibratedAt; }
    public void setCalibratedAt(LocalDateTime calibratedAt) { this.calibratedAt = calibratedAt; }
}

// Additional model classes for comprehensive BCI functionality
public static class NeuralSignalBuffer {
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
        if (buffer.size() >= bufferSize) {
            buffer.poll(); // Remove oldest
        }
        buffer.offer(data);
        lastUpdate = LocalDateTime.now();
    }
    
    public List<NeuralSignalData> getRecentSignals(int count) {
        return buffer.stream().skip(Math.max(0, buffer.size() - count)).collect(java.util.stream.Collectors.toList());
    }
    
    // Getters
    public int getBufferSize() { return bufferSize; }
    public double getSamplingRate() { return samplingRate; }
    public int getCurrentSize() { return buffer.size(); }
    public LocalDateTime getLastUpdate() { return lastUpdate; }
}

public static class BCIMetrics {
    private long totalSignalsProcessed;
    private double averageLatency;
    private double averageSignalQuality;
    private int commandsExecuted;
    private double accuracy;
    
    public void incrementSignalsProcessed() { totalSignalsProcessed++; }
    public void updateLatency(long latency) { 
        averageLatency = (averageLatency + latency) / 2.0; 
    }
    public void updateSignalQuality(double quality) { 
        averageSignalQuality = (averageSignalQuality + quality) / 2.0; 
    }
    
    // Getters and Setters
    public long getTotalSignalsProcessed() { return totalSignalsProcessed; }
    public double getAverageLatency() { return averageLatency; }
    public double getAverageSignalQuality() { return averageSignalQuality; }
    public int getCommandsExecuted() { return commandsExecuted; }
    public void setCommandsExecuted(int commandsExecuted) { this.commandsExecuted = commandsExecuted; }
    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
}

public static class SignalQuality {
    private double overallScore;
    private double noiseLevel;
    private double artifactLevel;
    
    // Getters and Setters
    public double getOverallScore() { return overallScore; }
    public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
    public double getNoiseLevel() { return noiseLevel; }
    public void setNoiseLevel(double noiseLevel) { this.noiseLevel = noiseLevel; }
    public double getArtifactLevel() { return artifactLevel; }
    public void setArtifactLevel(double artifactLevel) { this.artifactLevel = artifactLevel; }
}

public static class FeedbackPreferences {
    private FeedbackType feedbackType;
    private int intensity;
    private boolean adaptiveFeedback;
    
    public FeedbackPreferences() {
        this.feedbackType = FeedbackType.VISUAL;
        this.intensity = 50;
        this.adaptiveFeedback = true;
    }
    
    // Getters and Setters
    public FeedbackType getFeedbackType() { return feedbackType; }
    public void setFeedbackType(FeedbackType feedbackType) { this.feedbackType = feedbackType; }
    public int getIntensity() { return intensity; }
    public void setIntensity(int intensity) { this.intensity = intensity; }
    public boolean isAdaptiveFeedback() { return adaptiveFeedback; }
    public void setAdaptiveFeedback(boolean adaptiveFeedback) { this.adaptiveFeedback = adaptiveFeedback; }
}

public enum FeedbackType {
    VISUAL, AUDITORY, HAPTIC, NEURAL
}

// Feedback signal classes
public static class FeedbackSignal {
    private FeedbackType feedbackType;
    private boolean successful;
    private String message;
    private LocalDateTime timestamp;
    private VisualFeedback visualFeedback;
    private AuditoryFeedback auditoryFeedback;
    private HapticFeedback hapticFeedback;
    private NeuralFeedback neuralFeedback;
    
    // Getters and Setters
    public FeedbackType getFeedbackType() { return feedbackType; }
    public void setFeedbackType(FeedbackType feedbackType) { this.feedbackType = feedbackType; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public VisualFeedback getVisualFeedback() { return visualFeedback; }
    public void setVisualFeedback(VisualFeedback visualFeedback) { this.visualFeedback = visualFeedback; }
    public AuditoryFeedback getAuditoryFeedback() { return auditoryFeedback; }
    public void setAuditoryFeedback(AuditoryFeedback auditoryFeedback) { this.auditoryFeedback = auditoryFeedback; }
    public HapticFeedback getHapticFeedback() { return hapticFeedback; }
    public void setHapticFeedback(HapticFeedback hapticFeedback) { this.hapticFeedback = hapticFeedback; }
    public NeuralFeedback getNeuralFeedback() { return neuralFeedback; }
    public void setNeuralFeedback(NeuralFeedback neuralFeedback) { this.neuralFeedback = neuralFeedback; }
}

public static class VisualFeedback {
    private String color;
    private int brightness;
    private String pattern;
    
    // Getters and Setters
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public int getBrightness() { return brightness; }
    public void setBrightness(int brightness) { this.brightness = brightness; }
    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }
}

public static class AuditoryFeedback {
    private String tone;
    private int volume;
    private int duration;
    
    // Getters and Setters
    public String getTone() { return tone; }
    public void setTone(String tone) { this.tone = tone; }
    public int getVolume() { return volume; }
    public void setVolume(int volume) { this.volume = volume; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
}

public static class HapticFeedback {
    private String vibrationPattern;
    private int intensity;
    private int duration;
    
    // Getters and Setters
    public String getVibrationPattern() { return vibrationPattern; }
    public void setVibrationPattern(String vibrationPattern) { this.vibrationPattern = vibrationPattern; }
    public int getIntensity() { return intensity; }
    public void setIntensity(int intensity) { this.intensity = intensity; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
}

public static class NeuralFeedback {
    private String stimulationType;
    private int intensity;
    private String targetRegion;
    
    // Getters and Setters
    public String getStimulationType() { return stimulationType; }
    public void setStimulationType(String stimulationType) { this.stimulationType = stimulationType; }
    public int getIntensity() { return intensity; }
    public void setIntensity(int intensity) { this.intensity = intensity; }
    public String getTargetRegion() { return targetRegion; }
    public void setTargetRegion(String targetRegion) { this.targetRegion = targetRegion; }
}

// Neurofeedback Models
public static class NeurofeedbackProtocol {
    private String protocolId;
    private String protocolName;
    private int numberOfCycles;
    private String feedbackType;
    private Map<String, Object> parameters;
    
    public NeurofeedbackProtocol() {
        this.protocolId = UUID.randomUUID().toString();
        this.parameters = new HashMap<>();
    }
    
    // Getters and Setters
    public String getProtocolId() { return protocolId; }
    public void setProtocolId(String protocolId) { this.protocolId = protocolId; }
    public String getProtocolName() { return protocolName; }
    public void setProtocolName(String protocolName) { this.protocolName = protocolName; }
    public int getNumberOfCycles() { return numberOfCycles; }
    public void setNumberOfCycles(int numberOfCycles) { this.numberOfCycles = numberOfCycles; }
    public String getFeedbackType() { return feedbackType; }
    public void setFeedbackType(String feedbackType) { this.feedbackType = feedbackType; }
    public Map<String, Object> getParameters() { return new HashMap<>(parameters); }
    public void setParameters(Map<String, Object> parameters) { this.parameters = new HashMap<>(parameters); }
}

public static class NeurofeedbackSession {
    private String sessionId;
    private String protocolId;
    private LocalDateTime startTime;
    
    public NeurofeedbackSession(String protocolId, LocalDateTime startTime) {
        this.sessionId = UUID.randomUUID().toString();
        this.protocolId = protocolId;
        this.startTime = startTime;
    }
    
    // Getters
    public String getSessionId() { return sessionId; }
    public String getProtocolId() { return protocolId; }
    public LocalDateTime getStartTime() { return startTime; }
}

public static class NeurofeedbackCycle {
    private int cycleNumber;
    private double performanceScore;
    private boolean targetAchieved;
    private String improvementDirection;
    
    // Getters and Setters
    public int getCycleNumber() { return cycleNumber; }
    public void setCycleNumber(int cycleNumber) { this.cycleNumber = cycleNumber; }
    public double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(double performanceScore) { this.performanceScore = performanceScore; }
    public boolean isTargetAchieved() { return targetAchieved; }
    public void setTargetAchieved(boolean targetAchieved) { this.targetAchieved = targetAchieved; }
    public String getImprovementDirection() { return improvementDirection; }
    public void setImprovementDirection(String improvementDirection) { this.improvementDirection = improvementDirection; }
}

public static class NeurofeedbackAnalysis {
    private double improvementScore;
    private String nextSessionRecommendation;
    
    // Getters and Setters
    public double getImprovementScore() { return improvementScore; }
    public void setImprovementScore(double improvementScore) { this.improvementScore = improvementScore; }
    public String getNextSessionRecommendation() { return nextSessionRecommendation; }
    public void setNextSessionRecommendation(String nextSessionRecommendation) { this.nextSessionRecommendation = nextSessionRecommendation; }
}

public static class NeurofeedbackResult {
    private String sessionId;
    private String protocolId;
    private NeurofeedbackAnalysis trainingAnalysis;
    private double improvementScore;
    private String recommendedNextSession;
    private boolean successful;
    private String errorMessage;
    
    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getProtocolId() { return protocolId; }
    public void setProtocolId(String protocolId) { this.protocolId = protocolId; }
    public NeurofeedbackAnalysis getTrainingAnalysis() { return trainingAnalysis; }
    public void setTrainingAnalysis(NeurofeedbackAnalysis trainingAnalysis) { this.trainingAnalysis = trainingAnalysis; }
    public double getImprovementScore() { return improvementScore; }
    public void setImprovementScore(double improvementScore) { this.improvementScore = improvementScore; }
    public String getRecommendedNextSession() { return recommendedNextSession; }
    public void setRecommendedNextSession(String recommendedNextSession) { this.recommendedNextSession = recommendedNextSession; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}

public static class RealtimeFeedback {
    private int cycleNumber;
    private double performanceScore;
    private boolean targetAchieved;
    private String feedbackType;
    private LocalDateTime timestamp;
    private boolean positiveReinforcement;
    private double reinforcementStrength;
    private boolean correctiveGuidance;
    private String guidanceDirection;
    
    // Getters and Setters
    public int getCycleNumber() { return cycleNumber; }
    public void setCycleNumber(int cycleNumber) { this.cycleNumber = cycleNumber; }
    public double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(double performanceScore) { this.performanceScore = performanceScore; }
    public boolean isTargetAchieved() { return targetAchieved; }
    public void setTargetAchieved(boolean targetAchieved) { this.targetAchieved = targetAchieved; }
    public String getFeedbackType() { return feedbackType; }
    public void setFeedbackType(String feedbackType) { this.feedbackType = feedbackType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isPositiveReinforcement() { return positiveReinforcement; }
    public void setPositiveReinforcement(boolean positiveReinforcement) { this.positiveReinforcement = positiveReinforcement; }
    public double getReinforcementStrength() { return reinforcementStrength; }
    public void setReinforcementStrength(double reinforcementStrength) { this.reinforcementStrength = reinforcementStrength; }
    public boolean isCorrectiveGuidance() { return correctiveGuidance; }
    public void setCorrectiveGuidance(boolean correctiveGuidance) { this.correctiveGuidance = correctiveGuidance; }
    public String getGuidanceDirection() { return guidanceDirection; }
    public void setGuidanceDirection(String guidanceDirection) { this.guidanceDirection = guidanceDirection; }
}

// System Status and Information Models
public static class BCISystemStatus {
    private int activeSessionCount;
    private double systemHealth;
    private double averageLatency;
    private long totalSignalsProcessed;
    private long uptimeHours;
    private LocalDateTime lastUpdate;
    
    // Getters and Setters
    public int getActiveSessionCount() { return activeSessionCount; }
    public void setActiveSessionCount(int activeSessionCount) { this.activeSessionCount = activeSessionCount; }
    public double getSystemHealth() { return systemHealth; }
    public void setSystemHealth(double systemHealth) { this.systemHealth = systemHealth; }
    public double getAverageLatency() { return averageLatency; }
    public void setAverageLatency(double averageLatency) { this.averageLatency = averageLatency; }
    public long getTotalSignalsProcessed() { return totalSignalsProcessed; }
    public void setTotalSignalsProcessed(long totalSignalsProcessed) { this.totalSignalsProcessed = totalSignalsProcessed; }
    public long getUptimeHours() { return uptimeHours; }
    public void setUptimeHours(long uptimeHours) { this.uptimeHours = uptimeHours; }
    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }
}

public static class BCISessionInfo {
    private String sessionId;
    private String sessionType;
    private String participantId;
    private LocalDateTime startTime;
    private BCIMetrics currentMetrics;
    private SignalQuality signalQuality;
    private double calibrationAccuracy;
    private boolean isActive;
    
    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
    public String getParticipantId() { return participantId; }
    public void setParticipantId(String participantId) { this.participantId = participantId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public BCIMetrics getCurrentMetrics() { return currentMetrics; }
    public void setCurrentMetrics(BCIMetrics currentMetrics) { this.currentMetrics = currentMetrics; }
    public SignalQuality getSignalQuality() { return signalQuality; }
    public void setSignalQuality(SignalQuality signalQuality) { this.signalQuality = signalQuality; }
    public double getCalibrationAccuracy() { return calibrationAccuracy; }
    public void setCalibrationAccuracy(double calibrationAccuracy) { this.calibrationAccuracy = calibrationAccuracy; }
    public boolean isActive() { return isActive; }
    public void setIsActive(boolean active) { isActive = active; }
}

public static class SystemPerformanceMetrics {
    private double averageLatency;
    private double averageAccuracy;
    
    // Getters and Setters
    public double getAverageLatency() { return averageLatency; }
    public void setAverageLatency(double averageLatency) { this.averageLatency = averageLatency; }
    public double getAverageAccuracy() { return averageAccuracy; }
    public void setAverageAccuracy(double averageAccuracy) { this.averageAccuracy = averageAccuracy; }
}

public static class PatternClassification {
    private String patternType;
    private double confidence;
    
    // Getters and Setters
    public String getPatternType() { return patternType; }
    public void setPatternType(String patternType) { this.patternType = patternType; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
}
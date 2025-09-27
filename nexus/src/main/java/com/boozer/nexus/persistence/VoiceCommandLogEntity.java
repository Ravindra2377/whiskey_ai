package com.boozer.nexus.persistence;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "voice_command_log")
public class VoiceCommandLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "wake_word", length = 100)
    private String wakeWord;

    @Column(name = "transcript", columnDefinition = "TEXT")
    private String transcript;

    @Column(name = "command_text", columnDefinition = "TEXT")
    private String commandText;

    @Column(name = "intent_label", length = 100)
    private String intentLabel;

    @Column(name = "successful")
    private boolean successful;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    protected VoiceCommandLogEntity() {
    }

    public VoiceCommandLogEntity(String wakeWord, String transcript, String commandText, String intentLabel, boolean successful, String errorMessage) {
        this.wakeWord = wakeWord;
        this.transcript = transcript;
        this.commandText = commandText;
        this.intentLabel = (intentLabel == null || intentLabel.isBlank()) ? "unknown" : intentLabel;
        this.successful = successful;
        this.errorMessage = errorMessage;
    }

    public UUID getId() {
        return id;
    }

    public String getWakeWord() {
        return wakeWord;
    }

    public String getTranscript() {
        return transcript;
    }

    public String getCommandText() {
        return commandText;
    }

    public String getIntentLabel() {
        return intentLabel;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}

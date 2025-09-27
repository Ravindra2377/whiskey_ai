package com.boozer.nexus.persistence;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnBean(VoiceCommandLogRepository.class)
public class VoiceCommandLogService {
    private final VoiceCommandLogRepository repository;

    public VoiceCommandLogService(VoiceCommandLogRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void record(String wakeWord, String transcript, String commandText, String intentLabel, boolean successful, String errorMessage) {
        repository.save(new VoiceCommandLogEntity(wakeWord, transcript, commandText, intentLabel, successful, errorMessage));
    }
}

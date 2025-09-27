package com.boozer.nexus.persistence;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@ConditionalOnBean(VoiceCommandLogRepository.class)
public class VoiceCommandAnalyticsService {
    private final VoiceCommandLogRepository repository;

    public VoiceCommandAnalyticsService(VoiceCommandLogRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public VoiceCommandSummary summarize() {
        var entries = repository.findAll();
        if (entries.isEmpty()) {
        return new VoiceCommandSummary(0, 0, Map.of(), Map.of(), Map.of(), 0, 0, 0, 0, 0, null, null);
        }

        long total = entries.size();
        long successful = entries.stream().filter(VoiceCommandLogEntity::isSuccessful).count();

    long awaiting = entries.stream()
        .map(VoiceCommandLogEntity::getErrorMessage)
        .filter(s -> "awaiting confirmation".equalsIgnoreCase(normalize(s)))
        .count();

    long wakeWordMisses = entries.stream()
        .map(VoiceCommandLogEntity::getIntentLabel)
        .filter(s -> "wake-word-miss".equalsIgnoreCase(normalize(s)))
        .count();

    long noSpeech = entries.stream()
        .map(VoiceCommandLogEntity::getIntentLabel)
        .filter(s -> "no-speech".equalsIgnoreCase(normalize(s)))
        .count();

    long resumed = entries.stream()
        .filter(e -> "resume-pending".equalsIgnoreCase(normalize(e.getIntentLabel())) && e.isSuccessful())
        .count();

    long repeated = entries.stream()
        .map(VoiceCommandLogEntity::getIntentLabel)
        .filter(s -> "repeat-last".equalsIgnoreCase(normalize(s)))
        .count();

        Map<String, Long> commandCounts = entries.stream()
                .map(VoiceCommandLogEntity::getCommandText)
                .filter(s -> s != null && !s.isBlank())
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(20)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b)->a, LinkedHashMap::new));

    Map<String, Long> errorCounts = entries.stream()
                .map(VoiceCommandLogEntity::getErrorMessage)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b)->a, LinkedHashMap::new));

    Map<String, Long> intentCounts = entries.stream()
        .map(VoiceCommandLogEntity::getIntentLabel)
        .filter(s -> s != null && !s.isBlank())
        .collect(Collectors.groupingBy(s -> s.toLowerCase(Locale.ROOT), Collectors.counting()))
        .entrySet().stream()
        .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
        .limit(20)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b)->a, LinkedHashMap::new));

        var first = entries.stream().map(VoiceCommandLogEntity::getCreatedAt).min(OffsetDateTime::compareTo).orElse(null);
        var last = entries.stream().map(VoiceCommandLogEntity::getCreatedAt).max(OffsetDateTime::compareTo).orElse(null);

        return new VoiceCommandSummary(total, successful, commandCounts, errorCounts, intentCounts, awaiting,
                wakeWordMisses, noSpeech, resumed, repeated, first, last);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}

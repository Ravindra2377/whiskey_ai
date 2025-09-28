package com.boozer.nexus.voice.intelligence;

import com.boozer.nexus.voice.WhisperClient;
import com.boozer.nexus.voice.processing.AudioEnhancementService;
import com.boozer.nexus.voice.processing.EmotionAnalysisService;
import com.boozer.nexus.voice.processing.EmotionalContext;
import com.boozer.nexus.voice.processing.LanguageContext;
import com.boozer.nexus.voice.processing.LanguageDetectionService;
import com.boozer.nexus.voice.processing.SpeakerProfile;
import com.boozer.nexus.voice.processing.SpeechRecognitionClient;
import com.boozer.nexus.voice.processing.TranscriptionResult;
import com.boozer.nexus.voice.processing.VoiceActivityDetector;
import com.boozer.nexus.voice.processing.VoiceActivityResult;
import com.boozer.nexus.voice.processing.VoiceBiometricsService;
import com.boozer.nexus.voice.processing.VoiceProcessingResult;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Orchestrates the advanced multi-stage voice processing pipeline used by the NEXUS voice assistant.
 */
public final class IntelligentVoicePipeline {
    private final AudioEnhancementService audioEnhancer;
    private final VoiceActivityDetector vad;
    private final SpeechRecognitionClient speechRecognition;
    private final VoiceBiometricsService biometricsService;
    private final EmotionAnalysisService emotionAnalysisService;
    private final LanguageDetectionService languageDetectionService;

    private IntelligentVoicePipeline(Builder builder) {
        this.audioEnhancer = Objects.requireNonNull(builder.audioEnhancer, "audioEnhancer");
        this.vad = Objects.requireNonNull(builder.voiceActivityDetector, "voiceActivityDetector");
        this.speechRecognition = Objects.requireNonNull(builder.speechRecognitionClient, "speechRecognitionClient");
        this.biometricsService = Objects.requireNonNull(builder.voiceBiometricsService, "voiceBiometricsService");
        this.emotionAnalysisService = Objects.requireNonNull(builder.emotionAnalysisService, "emotionAnalysisService");
        this.languageDetectionService = Objects.requireNonNull(builder.languageDetectionService, "languageDetectionService");
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Convenience factory for the default production pipeline (Whisper + lightweight heuristics).
     */
    public static IntelligentVoicePipeline createDefault(String openAiKey) {
        WhisperClient whisperClient = new WhisperClient(openAiKey);
        return builder()
                .audioEnhancer(new PassthroughAudioEnhancementService())
                .voiceActivityDetector(new RmsVoiceActivityDetector())
                .speechRecognitionClient(new WhisperSpeechRecognitionAdapter(whisperClient))
                .voiceBiometricsService(new HashVoiceBiometricsService())
                .emotionAnalysisService(new EnergyEmotionAnalysisService())
                .languageDetectionService(new HeuristicLanguageDetectionService())
                .build();
    }

    public VoiceProcessingResult process(byte[] audio, AudioFormat format) {
        if (audio == null || audio.length == 0 || format == null) {
            return VoiceProcessingResult.noVoice();
        }

        byte[] enhanced = safeEnhance(audio, format);
        VoiceActivityResult vadResult = safeDetect(enhanced, format);
        if (!vadResult.hasVoice()) {
            return VoiceProcessingResult.builder()
                    .voiceDetected(false)
                    .overallConfidence(vadResult.getConfidence())
                    .build();
        }

        TranscriptionResult transcription = safeTranscribe(enhanced, format);
        if (transcription == null) {
            transcription = TranscriptionResult.failure("unknown", "transcription unavailable");
        }

        SpeakerProfile speakerProfile = safeIdentify(enhanced, format);
        EmotionalContext emotionalContext = safeEmotion(enhanced, format);
        LanguageContext languageContext = safeLanguage(transcription.getText());

        double overallConfidence = average(
                vadResult.getConfidence(),
                transcription.getConfidence(),
                speakerProfile.getConfidence(),
                emotionalContext.getConfidence(),
                languageContext.getConfidence());

        return VoiceProcessingResult.builder()
                .voiceDetected(true)
                .transcription(transcription)
                .speakerProfile(speakerProfile)
                .emotionalContext(emotionalContext)
                .languageContext(languageContext)
                .overallConfidence(overallConfidence)
                .build();
    }

    private byte[] safeEnhance(byte[] audio, AudioFormat format) {
        try {
            return Optional.ofNullable(audioEnhancer.enhance(audio, format)).orElse(audio);
        } catch (Exception ex) {
            return audio;
        }
    }

    private VoiceActivityResult safeDetect(byte[] audio, AudioFormat format) {
        try {
            VoiceActivityResult result = vad.detect(audio, format);
            return result == null ? VoiceActivityResult.noVoice() : result;
        } catch (Exception ex) {
            return VoiceActivityResult.voiceDetected(0.1);
        }
    }

    private TranscriptionResult safeTranscribe(byte[] audio, AudioFormat format) {
        try {
            return speechRecognition.transcribe(audio, format);
        } catch (Exception ex) {
            return TranscriptionResult.failure("speech", ex.getMessage());
        }
    }

    private SpeakerProfile safeIdentify(byte[] audio, AudioFormat format) {
        try {
            SpeakerProfile profile = biometricsService.identify(audio, format);
            return profile == null ? SpeakerProfile.unknown() : profile;
        } catch (Exception ex) {
            return SpeakerProfile.unknown();
        }
    }

    private EmotionalContext safeEmotion(byte[] audio, AudioFormat format) {
        try {
            EmotionalContext ctx = emotionAnalysisService.analyze(audio, format);
            return ctx == null ? EmotionalContext.neutral() : ctx;
        } catch (Exception ex) {
            return EmotionalContext.neutral();
        }
    }

    private LanguageContext safeLanguage(String text) {
        try {
            LanguageContext ctx = languageDetectionService.detect(text == null ? "" : text);
            return ctx == null ? LanguageContext.unknown() : ctx;
        } catch (Exception ex) {
            return LanguageContext.unknown();
        }
    }

    private double average(double... values) {
        double sum = 0.0;
        int count = 0;
        for (double v : values) {
            if (Double.isFinite(v) && v > 0.0) {
                sum += Math.min(1.0, Math.max(0.0, v));
                count++;
            }
        }
        if (count == 0) {
            return 0.0;
        }
        return sum / count;
    }

    public static final class Builder {
        private AudioEnhancementService audioEnhancer;
        private VoiceActivityDetector voiceActivityDetector;
        private SpeechRecognitionClient speechRecognitionClient;
        private VoiceBiometricsService voiceBiometricsService;
        private EmotionAnalysisService emotionAnalysisService;
        private LanguageDetectionService languageDetectionService;

        public Builder audioEnhancer(AudioEnhancementService audioEnhancer) {
            this.audioEnhancer = audioEnhancer;
            return this;
        }

        public Builder voiceActivityDetector(VoiceActivityDetector voiceActivityDetector) {
            this.voiceActivityDetector = voiceActivityDetector;
            return this;
        }

        public Builder speechRecognitionClient(SpeechRecognitionClient speechRecognitionClient) {
            this.speechRecognitionClient = speechRecognitionClient;
            return this;
        }

        public Builder voiceBiometricsService(VoiceBiometricsService voiceBiometricsService) {
            this.voiceBiometricsService = voiceBiometricsService;
            return this;
        }

        public Builder emotionAnalysisService(EmotionAnalysisService emotionAnalysisService) {
            this.emotionAnalysisService = emotionAnalysisService;
            return this;
        }

        public Builder languageDetectionService(LanguageDetectionService languageDetectionService) {
            this.languageDetectionService = languageDetectionService;
            return this;
        }

        public IntelligentVoicePipeline build() {
            return new IntelligentVoicePipeline(this);
        }
    }

    private static final class PassthroughAudioEnhancementService implements AudioEnhancementService {
        @Override
        public byte[] enhance(byte[] audio, AudioFormat format) {
            return audio;
        }
    }

    private static final class RmsVoiceActivityDetector implements VoiceActivityDetector {
        private static final double BASE_THRESHOLD = 0.015;

        @Override
        public VoiceActivityResult detect(byte[] audio, AudioFormat format) {
            if (audio == null || audio.length < 2) {
                return VoiceActivityResult.noVoice();
            }
            double rms = computeRms(audio);
            if (rms <= BASE_THRESHOLD) {
                return VoiceActivityResult.noVoice();
            }
            double confidence = Math.min(1.0, (rms - BASE_THRESHOLD) / 0.2 + 0.1);
            return VoiceActivityResult.voiceDetected(confidence);
        }

        private static double computeRms(byte[] audio) {
            double sum = 0.0;
            int samples = audio.length / 2;
            if (samples == 0) {
                return 0.0;
            }
            for (int i = 0; i + 1 < audio.length; i += 2) {
                int sample = ((audio[i + 1] << 8) | (audio[i] & 0xFF));
                double normalized = sample / 32768.0;
                sum += normalized * normalized;
            }
            return Math.sqrt(sum / samples);
        }
    }

    private static final class WhisperSpeechRecognitionAdapter implements SpeechRecognitionClient {
        private final WhisperClient whisperClient;

        private WhisperSpeechRecognitionAdapter(WhisperClient whisperClient) {
            this.whisperClient = whisperClient;
        }

        @Override
        public String name() {
            return "whisper";
        }

        @Override
        public TranscriptionResult transcribe(byte[] audio, AudioFormat format) {
            Instant start = Instant.now();
            try {
                String text = whisperClient.transcribe(audio, format);
                boolean success = text != null && !text.isBlank();
                double confidence = success ? Math.min(0.95, Math.max(0.35, text.length() / 120.0)) : 0.0;
                return TranscriptionResult.builder(name())
                        .text(text == null ? "" : text.trim())
                        .confidence(confidence)
                        .success(success)
                        .latency(Duration.between(start, Instant.now()))
                        .build();
            } catch (IOException ex) {
                return TranscriptionResult.failure(name(), ex.getMessage());
            }
        }
    }

    private static final class HashVoiceBiometricsService implements VoiceBiometricsService {
        @Override
        public SpeakerProfile identify(byte[] audio, AudioFormat format) {
            if (audio == null || audio.length == 0) {
                return SpeakerProfile.unknown();
            }
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                byte[] hash = digest.digest(audio);
                byte[] shortHash = new byte[6];
                System.arraycopy(hash, 0, shortHash, 0, shortHash.length);
                String speakerId = Base64.getUrlEncoder().withoutPadding().encodeToString(shortHash);
                return SpeakerProfile.of(speakerId, 0.6);
            } catch (NoSuchAlgorithmException ex) {
                return SpeakerProfile.unknown();
            }
        }
    }

    private static final class EnergyEmotionAnalysisService implements EmotionAnalysisService {
        @Override
        public EmotionalContext analyze(byte[] audio, AudioFormat format) {
            if (audio == null || audio.length < 2) {
                return EmotionalContext.neutral();
            }
            double rms = RmsVoiceActivityDetector.computeRms(audio);
            if (rms > 0.18) {
                return EmotionalContext.of("excited", Math.min(1.0, rms));
            }
            if (rms > 0.08) {
                return EmotionalContext.of("engaged", Math.min(1.0, rms));
            }
            return EmotionalContext.neutral();
        }
    }

    private static final class HeuristicLanguageDetectionService implements LanguageDetectionService {
        @Override
        public LanguageContext detect(String text) {
            if (text == null || text.isBlank()) {
                return LanguageContext.unknown();
            }
            String normalized = text.toLowerCase(Locale.ROOT);
            if (containsAny(normalized, "hola", "gracias", "mañana", "por favor", "buenos")) {
                return LanguageContext.of(new Locale("es"), 0.65);
            }
            if (containsAny(normalized, "bonjour", "merci", "demain", "s'il vous plaît")) {
                return LanguageContext.of(Locale.FRENCH, 0.6);
            }
            return LanguageContext.of(Locale.ENGLISH, 0.9);
        }

        private boolean containsAny(String text, String... tokens) {
            for (String token : tokens) {
                if (text.contains(token)) {
                    return true;
                }
            }
            return false;
        }
    }
}

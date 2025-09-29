package com.boozer.nexus.desktop.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/** Minimal Whisper transcription client for voice-to-app workflows. */
public final class VoiceToTextService {
    private static final String TRANSCRIPTION_URL = "https://api.openai.com/v1/audio/transcriptions";

    private final String apiKey;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public VoiceToTextService(String apiKey) {
        this.apiKey = apiKey;
    }

    public CompletableFuture<String> transcribeAsync(byte[] wavBytes) {
        Objects.requireNonNull(wavBytes, "wavBytes");
        if (apiKey == null || apiKey.isBlank()) {
            return CompletableFuture.failedFuture(new IllegalStateException("OpenAI API key missing"));
        }
        return CompletableFuture.supplyAsync(() -> transcribeInternal(wavBytes));
    }

    private String transcribeInternal(byte[] wavBytes) {
        try {
            String boundary = "----NEXUS-VOICE-" + UUID.randomUUID();
            byte[] body = buildMultipart(boundary, wavBytes);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TRANSCRIPTION_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IOException("Whisper API error: status=" + response.statusCode() + " body=" + response.body());
            }
            JsonNode root = mapper.readTree(response.body());
            return root.path("text").asText("");
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to transcribe audio: " + ex.getMessage(), ex);
        }
    }

    private byte[] buildMultipart(String boundary, byte[] wavBytes) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String lineBreak = "\r\n";
        String boundaryPrefix = "--" + boundary + lineBreak;

        output.write(boundaryPrefix.getBytes(StandardCharsets.UTF_8));
        output.write("Content-Disposition: form-data; name=\"model\"".getBytes(StandardCharsets.UTF_8));
        output.write((lineBreak + lineBreak + "whisper-1" + lineBreak).getBytes(StandardCharsets.UTF_8));

        output.write(boundaryPrefix.getBytes(StandardCharsets.UTF_8));
        output.write("Content-Disposition: form-data; name=\"response_format\"".getBytes(StandardCharsets.UTF_8));
        output.write((lineBreak + lineBreak + "text" + lineBreak).getBytes(StandardCharsets.UTF_8));

        output.write(boundaryPrefix.getBytes(StandardCharsets.UTF_8));
        output.write("Content-Disposition: form-data; name=\"file\"; filename=\"voice.wav\"".getBytes(StandardCharsets.UTF_8));
        output.write((lineBreak + "Content-Type: audio/wav" + lineBreak + lineBreak).getBytes(StandardCharsets.UTF_8));
        output.write(wavBytes);
        output.write(lineBreak.getBytes(StandardCharsets.UTF_8));

        output.write(("--" + boundary + "--" + lineBreak).getBytes(StandardCharsets.UTF_8));
        return output.toByteArray();
    }
}

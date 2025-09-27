package com.boozer.nexus.voice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Thin HTTP client wrapper for OpenAI Whisper transcription API.
 */
public class WhisperClient {
    private static final String WHISPER_ENDPOINT = "https://api.openai.com/v1/audio/transcriptions";
    private static final String MODEL_NAME = "whisper-1";

    private final ObjectMapper mapper = new ObjectMapper();
    private final String apiKey;
    private final PoolingHttpClientConnectionManager connectionManager;

    public WhisperClient(String apiKey) {
        this.apiKey = apiKey;
        this.connectionManager = new PoolingHttpClientConnectionManager();
        this.connectionManager.setDefaultMaxPerRoute(4);
        this.connectionManager.setMaxTotal(8);
    }

    public String transcribe(Path audioFile) throws IOException {
    try (CloseableHttpClient client = HttpClients.custom()
        .setConnectionManager(connectionManager)
        .setConnectionManagerShared(true)
        .build()) {
            HttpPost post = new HttpPost(WHISPER_ENDPOINT);
            post.setHeader("Authorization", "Bearer " + apiKey);

            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", audioFile.toFile(), ContentType.DEFAULT_BINARY, audioFile.getFileName().toString())
                    .addTextBody("model", MODEL_NAME)
                    .addTextBody("response_format", "json")
                    .build();
            post.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(post)) {
                String payload = EntityUtils.toString(response.getEntity());
                if (response.getStatusLine().getStatusCode() >= 300) {
                    throw new IOException("Whisper API error: " + response.getStatusLine() + " -> " + payload);
                }
                JsonNode json = mapper.readTree(payload);
                JsonNode text = json.get("text");
                return text != null ? text.asText("") : "";
            }
        }
    }

    public String transcribe(byte[] pcm, AudioFormat format) throws IOException {
        if (pcm == null || pcm.length == 0) {
            return "";
        }
        Path temp = Files.createTempFile("nexus-voice", ".wav");
        try {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(pcm);
                 AudioInputStream audioStream = new AudioInputStream(inputStream, format, pcm.length / format.getFrameSize())) {
                AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, temp.toFile());
            }
            return transcribe(temp);
        } finally {
            Files.deleteIfExists(temp);
        }
    }
}

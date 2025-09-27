package com.boozer.nexus.codegen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class OpenAiChatClient {
    private static final String CHAT_COMPLETIONS_URL = "https://api.openai.com/v1/chat/completions";

    private final String apiKey;
    private final ObjectMapper mapper = new ObjectMapper();

    public OpenAiChatClient(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("OpenAI API key is required");
        }
        this.apiKey = apiKey;
    }

    public String chat(String model, List<Message> messages, double temperature) throws IOException {
        ObjectNode payload = mapper.createObjectNode();
        payload.put("model", model);
        payload.put("temperature", temperature);
        ArrayNode messageArray = payload.putArray("messages");
        for (Message message : messages) {
            ObjectNode node = mapper.createObjectNode();
            node.put("role", message.role);
            node.put("content", message.content);
            messageArray.add(node);
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(CHAT_COMPLETIONS_URL);
            request.setHeader("Authorization", "Bearer " + apiKey);
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(payload.toString(), StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int status = response.getStatusLine().getStatusCode();
                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                if (status < 200 || status >= 300) {
                    throw new IOException("OpenAI API request failed: status=" + status + " body=" + body);
                }
                JsonNode root = mapper.readTree(body);
                JsonNode choices = root.path("choices");
                if (!choices.isArray() || choices.isEmpty()) {
                    throw new IOException("OpenAI API returned no choices");
                }
                JsonNode first = choices.get(0).path("message");
                return first.path("content").asText("");
            }
        }
    }

    public static class Message {
        final String role;
        final String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public static Message system(String content) {
            return new Message("system", content);
        }

        public static Message user(String content) {
            return new Message("user", content);
        }

        public static Message assistant(String content) {
            return new Message("assistant", content);
        }
    }
}

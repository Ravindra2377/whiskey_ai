package com.boozer.nexus.desktop.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Minimal GPT-4 integration used by the desktop shell to generate full projects.
 */
public final class GPT4IntegrationService implements AutoCloseable {
    private static final String CHAT_COMPLETIONS_URL = "https://api.openai.com/v1/chat/completions";
    private static final String DEFAULT_MODEL = "gpt-4o";

    private final AtomicReference<String> apiKeyRef = new AtomicReference<>();
    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final ObjectMapper mapper = new ObjectMapper();
    private final ExecutorService executor = Executors.newFixedThreadPool(2, new DaemonFactory());

    public GPT4IntegrationService() {
        String key = System.getenv("NEXUS_OPENAI_KEY");
        if (key == null || key.isBlank()) {
            key = System.getenv("OPENAI_API_KEY");
        }
        if (key != null && !key.isBlank()) {
            apiKeyRef.set(key.trim());
        }
    }

    public Optional<String> getApiKey() {
        String key = apiKeyRef.get();
        return key == null || key.isBlank() ? Optional.empty() : Optional.of(key);
    }

    public void setApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            apiKeyRef.set(null);
        } else {
            apiKeyRef.set(apiKey.trim());
        }
    }

    public CompletableFuture<ProjectGenerationResult> generateProject(ProjectGenerationRequest request) {
        Objects.requireNonNull(request, "request");
        return CompletableFuture.supplyAsync(() -> executeGeneration(request), executor);
    }

    private ProjectGenerationResult executeGeneration(ProjectGenerationRequest request) {
        String apiKey = apiKeyRef.get();
        if (apiKey == null || apiKey.isBlank()) {
            throw new MissingApiKeyException("OpenAI API key not configured. Set the key before generating projects.");
        }

        ObjectNode payload = mapper.createObjectNode();
        payload.put("model", DEFAULT_MODEL);
        payload.put("temperature", request.isIncludeTests() ? 0.35 : 0.2);

        ArrayNode messages = payload.putArray("messages");
        messages.addObject()
                .put("role", "system")
                .put("content", systemPrompt());
        messages.addObject()
                .put("role", "user")
                .put("content", buildUserPrompt(request));

        ObjectNode responseFormat = payload.putObject("response_format");
        responseFormat.put("type", "json_schema");
        ObjectNode schemaWrapper = responseFormat.putObject("json_schema");
        schemaWrapper.put("name", "nexus_project_spec");
        schemaWrapper.set("schema", buildSchema());

        HttpRequest requestPayload = HttpRequest.newBuilder()
                .uri(URI.create(CHAT_COMPLETIONS_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(requestPayload, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IOException("OpenAI API error: status=" + response.statusCode() + " body=" + response.body());
            }
            return parseResponse(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("GPT-4 generation interrupted", ex);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to execute GPT-4 generation: " + ex.getMessage(), ex);
        }
    }

    private String systemPrompt() {
        return "You are NEXUS AI, an enterprise software architect. Generate full project scaffolds that ship" +
                " production-ready code, scripts, and documentation. Output must follow the provided JSON schema" +
                " with UTF-8 encoded file contents. Include realistic build tooling, tests if requested, and" +
                " concise release notes.";
    }

    private String buildUserPrompt(ProjectGenerationRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append("Generate a complete project based on this description:\n\n");
        builder.append(request.getDescription().trim()).append("\n\n");
        if (!request.getFeatureTags().isEmpty()) {
            builder.append("Key capabilities to emphasise: ");
            builder.append(String.join(", ", request.getFeatureTags()));
            builder.append(".\n\n");
        }
        if (request.getProjectNameHint() != null && !request.getProjectNameHint().isBlank()) {
            builder.append("Project name hint: ").append(request.getProjectNameHint().trim()).append("\n");
        }
        if (request.getTargetStack() != null && !request.getTargetStack().isBlank()) {
            builder.append("Primary technology stack preference: ").append(request.getTargetStack().trim()).append("\n");
        }
        builder.append("Include tests: ").append(request.isIncludeTests() ? "yes" : "no").append(".\n");
        builder.append("Source channel: ").append(request.getSource().name()).append(".\n");
        builder.append("Deliver production-ready quality suitable for enterprise deployment.");
        return builder.toString();
    }

    private ObjectNode buildSchema() {
        ObjectNode schema = mapper.createObjectNode();
        schema.put("type", "object");
        ObjectNode properties = schema.putObject("properties");
        properties.putObject("projectName").put("type", "string");
        properties.putObject("summary").put("type", "string");
        properties.putObject("model").put("type", "string");
        properties.putObject("notes").put("type", "array").set("items", mapper.createObjectNode().put("type", "string"));

        ObjectNode files = properties.putObject("files");
        files.put("type", "array");
        ObjectNode fileItems = mapper.createObjectNode();
        fileItems.put("type", "object");
        ObjectNode fileProps = fileItems.putObject("properties");
        fileProps.putObject("path").put("type", "string");
        fileProps.putObject("content").put("type", "string");
        fileItems.putArray("required").add("path").add("content");
        files.set("items", fileItems);

        schema.putArray("required").add("projectName").add("summary").add("files");
        return schema;
    }

    private ProjectGenerationResult parseResponse(String responseBody) throws IOException {
        JsonNode root = mapper.readTree(responseBody);
        ArrayNode choices = (ArrayNode) root.path("choices");
        if (choices == null || choices.isEmpty()) {
            throw new IOException("OpenAI response missing choices");
        }
        JsonNode message = choices.get(0).path("message");
        String content = message.path("content").asText(null);
        if (content == null || content.isBlank()) {
            throw new IOException("OpenAI response missing message content");
        }

        JsonNode payload;
        try {
            payload = mapper.readTree(content);
        } catch (JsonProcessingException ex) {
            // Some responses might stream as JSON string with escaped newline sequences – attempt to decode from base64 fallback
            payload = tryDecodeEmbeddedJson(content);
        }

        String projectName = textOr(payload, "projectName", "GeneratedApplication");
        String summary = textOr(payload, "summary", "");
        String model = textOr(payload, "model", root.path("model").asText(DEFAULT_MODEL));

        List<GeneratedFile> files = new ArrayList<>();
        JsonNode filesNode = payload.path("files");
        if (filesNode.isArray()) {
            for (JsonNode node : filesNode) {
                String path = textOr(node, "path", null);
                String contentValue = textOr(node, "content", "");
                if (path != null && !path.isBlank()) {
                    files.add(new GeneratedFile(path, contentValue));
                }
            }
        }
        if (files.isEmpty()) {
            throw new IOException("OpenAI response did not include any files");
        }

        List<String> notes = new ArrayList<>();
        JsonNode notesNode = payload.path("notes");
        if (notesNode.isArray()) {
            for (JsonNode note : notesNode) {
                if (note.isTextual()) {
                    notes.add(note.asText());
                }
            }
        }

        return new ProjectGenerationResult(projectName, summary, model, Instant.now(), files, notes);
    }

    private JsonNode tryDecodeEmbeddedJson(String content) throws IOException {
        // Attempt to decode scenarios where the assistant wraps JSON in triple backticks or base64.
        String trimmed = content.trim();
        if (trimmed.startsWith("```")) {
            int first = trimmed.indexOf('\n');
            if (first > 0) {
                trimmed = trimmed.substring(first + 1);
            }
            int last = trimmed.lastIndexOf("```");
            if (last > 0) {
                trimmed = trimmed.substring(0, last);
            }
        }

        try {
            return mapper.readTree(trimmed);
        } catch (JsonProcessingException ignored) {
            // try base64
            try {
                byte[] decoded = Base64.getDecoder().decode(trimmed);
                return mapper.readTree(new String(decoded, StandardCharsets.UTF_8));
            } catch (IllegalArgumentException ex) {
                throw new IOException("Failed to parse OpenAI JSON payload", ex);
            }
        }
    }

    private String textOr(JsonNode node, String field, String fallback) {
        if (node == null) {
            return fallback;
        }
        JsonNode value = node.path(field);
        if (value.isMissingNode() || value.isNull()) {
            return fallback;
        }
        return value.asText(fallback);
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }

    public static final class MissingApiKeyException extends RuntimeException {
        public MissingApiKeyException(String message) {
            super(message);
        }
    }

    private static final class DaemonFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "gpt4-desktop-worker-" + UUID.randomUUID());
            thread.setDaemon(true);
            return thread;
        }
    }
}

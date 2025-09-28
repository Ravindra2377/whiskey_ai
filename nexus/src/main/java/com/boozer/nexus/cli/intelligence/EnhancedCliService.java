package com.boozer.nexus.cli.intelligence;

import com.boozer.nexus.codegen.OpenAiChatClient;
import com.boozer.nexus.codegen.OpenAiChatClient.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnhancedCliService {
    private static final int MAX_OUTPUT_CHARS = 4000;
    private static final String DEFAULT_MODEL = "gpt-4o-mini";
    private static final double DEFAULT_TEMPERATURE = 0.2;
    private static final String MISSING_KEY_HINT =
            "Intelligent insights require an OpenAI API key. Set NEXUS_OPENAI_KEY / OPENAI_API_KEY or pass --openai-key=KEY.";

    private final ObjectMapper mapper = new ObjectMapper();

    public EnhancedCliResult enhance(EnhancedCliRequest request) {
        String apiKey = resolveApiKey(request.apiKey());
        if (apiKey == null || apiKey.isBlank()) {
            return EnhancedCliResult.failure(MISSING_KEY_HINT);
        }

        String prompt;
        try {
            prompt = composePrompt(request);
        } catch (JsonProcessingException e) {
            return EnhancedCliResult.failure("Failed to prepare intelligent prompt: " + e.getMessage());
        }

    String model = request.model() == null ? DEFAULT_MODEL : request.model();
    double requestedTemperature = request.temperature();
    double temperature = (Double.isNaN(requestedTemperature) || requestedTemperature < 0 || requestedTemperature > 1)
        ? DEFAULT_TEMPERATURE
        : requestedTemperature;

        try {
            OpenAiChatClient client = new OpenAiChatClient(apiKey);
            List<Message> messages = new ArrayList<>();
            messages.add(Message.system("You are NEXUS, an enterprise DevOps and automation expert assisting operators via CLI." +
                    " Provide concise, actionable insights, highlight anomalies, and suggest next best steps."));
            messages.add(Message.user(prompt));
            String raw = client.chat(model, messages, temperature);
            if (raw == null || raw.isBlank()) {
                return EnhancedCliResult.failure("OpenAI returned an empty response.");
            }
            return EnhancedCliResult.success(raw.trim());
        } catch (Exception e) {
            return EnhancedCliResult.failure("OpenAI request failed: " + e.getMessage());
        }
    }

    private String composePrompt(EnhancedCliRequest request) throws JsonProcessingException {
        ObjectNode root = mapper.createObjectNode();
        root.put("command", request.command());
        if (request.mode() != null) {
            root.put("mode", request.mode());
        }
        root.put("base_output", truncate(request.baseOutput()));
        Map<String, Object> context = request.context();
        if (context != null && !context.isEmpty()) {
            root.set("context", mapper.valueToTree(context));
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Analyze the recent CLI execution and produce:\n")
          .append("1. 2-3 bullet highlights that surface key findings or anomalies.\n")
          .append("2. Recommended next steps or follow-up commands with short reasons.\n")
          .append("3. If any risk, dependency, or data gap is detected, call it out explicitly.\n\n")
          .append("CLI payload:\n")
          .append(root.toPrettyString());
        return sb.toString();
    }

    private String truncate(String text) {
        if (text == null) {
            return "";
        }
        String trimmed = text.trim();
        if (trimmed.length() <= MAX_OUTPUT_CHARS) {
            return trimmed;
        }
        return trimmed.substring(0, MAX_OUTPUT_CHARS) + "... [truncated]";
    }

    private String resolveApiKey(String explicit) {
        if (explicit != null && !explicit.isBlank()) {
            return explicit.trim();
        }
        String key = System.getenv("NEXUS_OPENAI_KEY");
        if (key == null || key.isBlank()) {
            key = System.getenv("OPENAI_API_KEY");
        }
        return key;
    }
}

package com.boozer.nexus.codegen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CodeGenerationService {
    private static final String SYSTEM_PROMPT = "You are NEXUS AI, an expert software engineer. " +
            "Generate high-quality, production-ready code. " +
            "Always respond with JSON containing keys: code, tests, notes. " +
            "The value for code must contain only the primary implementation. " +
            "tests may be empty. notes should include validation or deployment hints.";

    private final OpenAiChatClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public CodeGenerationService(OpenAiChatClient client) {
        this.client = client;
    }

    public CodeGenerationResult generate(CodeGenerationRequest request) throws IOException {
        if (request == null) {
            throw new IllegalArgumentException("request is required");
        }
        if (request.specification() == null || request.specification().isBlank()) {
            throw new IllegalArgumentException("specification is required");
        }

        List<OpenAiChatClient.Message> messages = new ArrayList<>();
        messages.add(OpenAiChatClient.Message.system(SYSTEM_PROMPT));
        messages.add(OpenAiChatClient.Message.user(buildUserPrompt(request)));

        String content = client.chat(request.resolvedModel(), messages, request.resolvedTemperature());
        return parse(content, request.resolvedModel());
    }

    private String buildUserPrompt(CodeGenerationRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Specification:\n").append(request.specification().trim()).append("\n\n");
        sb.append("Target language: ").append(request.resolvedLanguage()).append('\n');
        sb.append("Include inline comments: ").append(request.includeComments() ? "yes" : "minimal").append('\n');
        sb.append("Include tests: ").append(request.includeTests() ? "yes" : "optional").append('\n');
        sb.append("Return JSON with fields {code, tests, notes}.");
        return sb.toString();
    }

    private CodeGenerationResult parse(String content, String model) {
        if (content == null) {
            return new CodeGenerationResult("", "", "No response received", model);
        }
        String trimmed = stripCodeFences(content.trim());
        try {
            JsonNode root = mapper.readTree(trimmed);
            String code = root.path("code").asText("");
            String tests = root.path("tests").asText("");
            String notes = root.path("notes").asText("");
            if (code.isBlank()) {
                code = trimmed;
            }
            return new CodeGenerationResult(code, tests, notes, model);
        } catch (Exception ex) {
            String notes = "Raw response (failed to parse JSON): " + ex.getMessage();
            return new CodeGenerationResult(trimmed, "", notes, model);
        }
    }

    private String stripCodeFences(String content) {
        if (content.startsWith("```")) {
            int firstBreak = content.indexOf('\n');
            if (firstBreak > 0 && firstBreak < content.length() - 1) {
                content = content.substring(firstBreak + 1);
            }
            if (content.endsWith("```")) {
                content = content.substring(0, content.length() - 3);
            }
        }
        return content.trim();
    }
}

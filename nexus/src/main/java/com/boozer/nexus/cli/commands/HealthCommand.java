package com.boozer.nexus.cli.commands;

import com.boozer.nexus.cli.intelligence.EnhancedCliRequest;
import com.boozer.nexus.cli.intelligence.EnhancedCliResult;
import com.boozer.nexus.cli.intelligence.EnhancedCliService;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class HealthCommand implements Command {
    private final EnhancedCliService enhancedCliService;

    public HealthCommand() {
        this(new EnhancedCliService());
    }

    public HealthCommand(EnhancedCliService enhancedCliService) {
        this.enhancedCliService = enhancedCliService;
    }

    @Override
    public String name() { return "health"; }

    @Override
    public String description() { return "Print OK and exit (add --intelligent for GPT-4 insights)"; }

    @Override
    public int run(String[] args) {
        boolean intelligent = false;
        String openAiKey = null;

        if (args != null) {
            for (String arg : args) {
                if ("--intelligent".equalsIgnoreCase(arg)) {
                    intelligent = true;
                } else if (arg.startsWith("--intelligent=")) {
                    String value = arg.substring("--intelligent=".length());
                    intelligent = value.equalsIgnoreCase("true") || value.isBlank();
                } else if (arg.startsWith("--openai-key=")) {
                    openAiKey = arg.substring("--openai-key=".length()).trim();
                }
            }
        }

        System.out.println("OK");

        if (intelligent) {
            Map<String, Object> context = new LinkedHashMap<>();
            context.put("status", "OK");
            context.put("checkedAt", Instant.now().toString());
            EnhancedCliResult aiResult = enhancedCliService.enhance(
                    EnhancedCliRequest.builder()
                            .command("health")
                            .mode("status")
                            .baseOutput("OK")
                            .context(context)
                            .apiKey(openAiKey)
                            .build());
            if (aiResult.successful() && aiResult.insight() != null && !aiResult.insight().isBlank()) {
                System.out.println();
                System.out.println("=== GPT-4 Insight ===");
                System.out.println(aiResult.insight());
            } else if (!aiResult.successful() && aiResult.message() != null && !aiResult.message().isBlank()) {
                System.out.println();
                System.out.println("[Intelligent mode] " + aiResult.message());
            }
        }

        return 0;
    }
}

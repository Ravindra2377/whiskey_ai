package com.boozer.nexus.cli.commands;

import com.boozer.nexus.cli.model.OperationDescriptor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.boozer.nexus.cli.intelligence.EnhancedCliService;
import com.boozer.nexus.cli.intelligence.EnhancedCliRequest;
import com.boozer.nexus.cli.intelligence.EnhancedCliResult;
import com.boozer.nexus.cli.server.VoiceAnalyticsHttpServer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AnalyticsCommand implements Command {
    private final com.boozer.nexus.persistence.VoiceCommandAnalyticsService voiceAnalyticsService;
    private final EnhancedCliService enhancedCliService;

    public AnalyticsCommand() {
        this(null, new EnhancedCliService());
    }

    public AnalyticsCommand(com.boozer.nexus.persistence.VoiceCommandAnalyticsService voiceAnalyticsService) {
        this(voiceAnalyticsService, new EnhancedCliService());
    }

    public AnalyticsCommand(com.boozer.nexus.persistence.VoiceCommandAnalyticsService voiceAnalyticsService, EnhancedCliService enhancedCliService) {
        this.voiceAnalyticsService = voiceAnalyticsService;
        this.enhancedCliService = enhancedCliService;
    }
    @Override
    public String name() { return "analytics"; }

    @Override
    public String description() {
        return "Predictive analytics: default catalog insights or --voice for live voice metrics (add --intelligent for GPT-4 insights)";
    }

    @Override
    public int run(String[] args) throws Exception {
        boolean voice = false;
        boolean server = false;
        Integer serverPort = null;
        boolean intelligent = false;
        String openAiKey = null;
        String workspaceCode = null;

        for (String arg : args) {
            if ("--voice".equals(arg)) {
                voice = true;
            }
            if (arg.startsWith("--server")) {
                server = true;
                int eq = arg.indexOf('=');
                if (eq > 0) {
                    try {
                        serverPort = Integer.parseInt(arg.substring(eq + 1));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            if ("--intelligent".equals(arg)) {
                intelligent = true;
            }
            if (arg.startsWith("--intelligent=")) {
                String value = arg.substring("--intelligent=".length());
                intelligent = value.equalsIgnoreCase("true") || value.isBlank();
            }
            if (arg.startsWith("--openai-key=")) {
                openAiKey = arg.substring("--openai-key=".length()).trim();
            }
            if (arg.startsWith("--workspace=")) {
                workspaceCode = arg.substring("--workspace=".length()).trim();
            }
        }

        if (server) {
            voice = true; // implicit
        }

        if (voice) {
            if (voiceAnalyticsService == null) {
                System.err.println("Voice analytics requires database support. Enable nexus.db.enabled=true and rerun.");
                return 2;
            }
            return renderVoiceAnalytics(server, serverPort);
        }

    Path catalogPath = workspaceCode != null ? resolveCatalogForWorkspace(workspaceCode) : resolveCatalog(args);
    private Path resolveCatalogForWorkspace(String workspaceCode) {
        // Try to find workspace-specific catalog file
        Path workspaceDir = Paths.get(Objects.requireNonNullElse(System.getProperty("nexus.root"), Paths.get("").toAbsolutePath().toString()));
        Path candidate = workspaceDir.resolve(workspaceCode).resolve("operations-catalog.json");
        if (Files.exists(candidate)) {
            return candidate;
        }
        // Fallback: search for catalog file in workspace root
        candidate = workspaceDir.resolve(workspaceCode + "-operations-catalog.json");
        if (Files.exists(candidate)) {
            return candidate;
        }
        // Fallback: default catalog
        return resolveCatalog(new String[0]);
    }
        List<OperationDescriptor> ops = readCatalog(catalogPath);
        StringBuilder sb = new StringBuilder();
        sb.append("Analytics for catalog: ").append(catalogPath.toAbsolutePath()).append(System.lineSeparator());
        sb.append("Total operations: ").append(ops.size()).append(System.lineSeparator());

        Map<String, Long> byType = ops.stream().collect(Collectors.groupingBy(o -> opt(o.type), LinkedHashMap::new, Collectors.counting()));
        sb.append("By type:").append(System.lineSeparator());
        byType.forEach((k,v) -> sb.append(String.format("  %-12s %d%n", k, v)));

        Map<String, Long> byTag = new LinkedHashMap<>();
        for (OperationDescriptor o : ops) {
            if (o.tags == null) continue;
            for (String t : o.tags) byTag.merge(t.toLowerCase(Locale.ROOT), 1L, Long::sum);
        }
        sb.append("Top tags:").append(System.lineSeparator());
        byTag.entrySet().stream().sorted((a,b)->Long.compare(b.getValue(), a.getValue())).limit(10)
                .forEach(e -> sb.append(String.format("  %-12s %d%n", e.getKey(), e.getValue())));

        int days = 30;
        sb.append("\n30-day Projection (naive):").append(System.lineSeparator());
        for (Map.Entry<String, Long> e : byType.entrySet()) {
            long current = e.getValue();
            long projected = current + Math.max(1, current / 10); // +10%
            sb.append(String.format("  %-12s %d -> ~%d%n", e.getKey(), current, projected));
        }

        sb.append("\nHotspots (top folders):").append(System.lineSeparator());
        Map<String, Long> byFolder = ops.stream().collect(Collectors.groupingBy(o -> topFolder(opt(o.path)), LinkedHashMap::new, Collectors.counting()));
        byFolder.entrySet().stream().sorted((a,b)->Long.compare(b.getValue(), a.getValue())).limit(10)
                .forEach(e -> sb.append(String.format("  %-20s %d%n", e.getKey(), e.getValue())));

        sb.append("\nTimeline (stub): today=").append(LocalDate.now()).append(", next checkpoint=").append(LocalDate.now().plusDays(30)).append(System.lineSeparator());

        String output = sb.toString();
        System.out.print(output);

        if (intelligent) {
            Map<String, Object> context = new LinkedHashMap<>();
            context.put("catalogPath", catalogPath.toAbsolutePath().toString());
            context.put("totalOperations", ops.size());
            context.put("byType", byType);
            context.put("byTag", byTag);
            context.put("byFolder", byFolder);
            context.put("projectionDays", days);
            EnhancedCliResult aiResult = enhancedCliService.enhance(
                    EnhancedCliRequest.builder()
                            .command("analytics")
                            .mode("summary")
                            .baseOutput(output)
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

    private int renderVoiceAnalytics(boolean startServer, Integer serverPort) {
        var summary = voiceAnalyticsService.summarize();
        boolean hasData = summary.getTotal() > 0;
        System.out.println("Voice Command Analytics");
        if (!hasData) {
            System.out.println("No voice commands logged yet. Run voice --live with database enabled.");
        } else {
            System.out.println("Total commands: " + summary.getTotal());
            System.out.println("Successful: " + summary.getSuccessful());
            long failed = summary.getTotal() - summary.getSuccessful();
            System.out.println("Failed: " + failed);
            System.out.printf("Success rate: %.1f%%%n", summary.getSuccessRate() * 100.0);
            if (summary.getAwaitingConfirmation() > 0) {
                System.out.println("Awaiting confirmation events: " + summary.getAwaitingConfirmation());
            }
            if (summary.getResumedCommands() > 0 || summary.getRepeatedCommands() > 0) {
                System.out.println("Follow-ups: resumed=" + summary.getResumedCommands() + ", repeated=" + summary.getRepeatedCommands());
            }
            if (summary.getWakeWordMisses() > 0 || summary.getNoSpeechEvents() > 0) {
                System.out.println("Signal quality: wake word misses=" + summary.getWakeWordMisses() + ", no-speech=" + summary.getNoSpeechEvents());
            }
            if (summary.getFirst() != null && summary.getLast() != null) {
                System.out.println("Range: " + summary.getFirst() + " -> " + summary.getLast());
            }

            System.out.println("\nTop Commands:");
            summary.getCommandCounts().forEach((cmd, count) -> System.out.printf("  %-25s %d%n", cmd, count));

            if (!summary.getIntentCounts().isEmpty()) {
                System.out.println("\nTop Intents:");
                summary.getIntentCounts().forEach((intent, count) ->
                        System.out.printf("  %-25s %d%n", intent, count));
            }

            if (!summary.getErrorCounts().isEmpty()) {
                System.out.println("\nTop Errors:");
                summary.getErrorCounts().forEach((err, count) -> System.out.printf("  %-40s %d%n", err, count));
            }
        }

        if (startServer) {
            int port = serverPort != null ? serverPort : 8088;
            try (VoiceAnalyticsHttpServer server = new VoiceAnalyticsHttpServer(voiceAnalyticsService)) {
                server.start(port);
                System.out.println("\nVoice analytics API available at http://localhost:" + port + "/");
                System.out.println("Endpoints: / (html), /metrics (json), /health");
                System.out.println("Press Ctrl+C to stop.");
                server.blockUntilShutdown();
            } catch (Exception ex) {
                System.err.println("Failed to start analytics server: " + ex.getMessage());
                return 1;
            }
            return 0;
        }

        return 0;
    }

    private String opt(String s) { return s == null ? "unknown" : s; }

    private String topFolder(String path) {
        String norm = path.replace('\\','/');
        int slash = norm.indexOf('/');
        return slash > 0 ? norm.substring(0, slash) : norm;
    }

    private Path resolveCatalog(String[] args) {
        for (String a : args) if (a.startsWith("--file=")) return Paths.get(a.substring("--file=".length()));
        Path cwd = Paths.get("").toAbsolutePath();
        Path a = cwd.resolve("operations-catalog.json");
        if (Files.exists(a)) return a;
        Path b = cwd.resolve("nexus").resolve("operations-catalog.json");
        return Files.exists(b) ? b : a;
    }

    private List<OperationDescriptor> readCatalog(Path p) throws Exception {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(Files.readAllBytes(p), new TypeReference<List<OperationDescriptor>>(){});
    }
}

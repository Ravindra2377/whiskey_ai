package com.boozer.nexus.cli.commands;

import com.boozer.nexus.cli.intelligence.EnhancedCliRequest;
import com.boozer.nexus.cli.intelligence.EnhancedCliResult;
import com.boozer.nexus.cli.intelligence.EnhancedCliService;
import com.boozer.nexus.cli.model.OperationDescriptor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CatalogCommand implements Command {
    private final EnhancedCliService enhancedCliService;

    public CatalogCommand() {
        this(new EnhancedCliService());
    }

    public CatalogCommand(EnhancedCliService enhancedCliService) {
        this.enhancedCliService = enhancedCliService;
    }

    @Override
    public String name() { return "catalog"; }

    @Override
    public String description() {
        return "Inspect operations catalog: summary, list, or export (usage: catalog [summary|list|export] [--file=<path>] [--type=<t>] [--tag=<k>] [--limit=N] [--out=<file>] [--intelligent] [--openai-key=KEY])";
    }

    @Override
    public int run(String[] args) throws Exception {
        String mode = args.length == 0 || args[0].startsWith("--") ? "summary" : args[0].toLowerCase(Locale.ROOT);
        Map<String, String> flags = parseFlags(args, mode.equals("summary") ? 0 : 1);
        Path catalogPath = resolveCatalog(flags.get("file"));
        List<OperationDescriptor> ops = readCatalog(catalogPath);

        boolean intelligentRequested = isIntelligentEnabled(flags);
        String openAiKey = flags.get("openai-key");

        CatalogResult result;
        switch (mode) {
            case "summary":
                result = buildSummary(ops, catalogPath);
                break;
            case "list":
                result = buildList(ops, flags, catalogPath);
                break;
            case "export":
                String out = flags.get("out");
                if (out == null || out.isBlank()) {
                    System.err.println("--out=<file> is required for export");
                    return 2;
                }
                result = buildExport(ops, flags, catalogPath, out);
                break;
            default:
                System.err.println("Unknown subcommand: " + mode);
                return 2;
        }

        if (!result.output().isBlank()) {
            printBlock(result.output());
        }

        if (intelligentRequested) {
            EnhancedCliResult aiResult = enhancedCliService.enhance(
                    EnhancedCliRequest.builder()
                            .command("catalog")
                            .mode(mode)
                            .baseOutput(result.output())
                            .context(result.context())
                            .apiKey(openAiKey)
                            .build());
            emitInsight(aiResult);
        }

        return 0;
    }

    private CatalogResult buildSummary(List<OperationDescriptor> ops, Path catalogPath) {
        StringBuilder sb = new StringBuilder();
        sb.append("Catalog: ").append(catalogPath.toAbsolutePath()).append(System.lineSeparator());
        sb.append("Total operations: ").append(ops.size()).append(System.lineSeparator());

        Map<String, Long> byType = ops.stream()
                .collect(Collectors.groupingBy(o -> opt(o.type), LinkedHashMap::new, Collectors.counting()));
        sb.append("By type:").append(System.lineSeparator());
        byType.forEach((t, c) -> sb.append(String.format("  %-12s %d%n", t, c)));

        Map<String, Long> byTop = ops.stream()
                .collect(Collectors.groupingBy(o -> topFolder(opt(o.path)), LinkedHashMap::new, Collectors.counting()));
        sb.append("By top folder:").append(System.lineSeparator());
        byTop.forEach((t, c) -> sb.append(String.format("  %-20s %d%n", t, c)));

        Map<String, Object> context = new LinkedHashMap<>();
        context.put("catalogPath", catalogPath.toAbsolutePath().toString());
        context.put("totalOperations", ops.size());
        context.put("byType", byType);
        context.put("byTopFolder", byTop);

        return new CatalogResult(sb.toString(), context);
    }

    private CatalogResult buildList(List<OperationDescriptor> ops, Map<String, String> flags, Path catalogPath) {
        String type = flags.get("type");
        String tag = flags.get("tag");
        int limit = parseIntOrDefault(flags.get("limit"), Integer.MAX_VALUE);

        StringBuilder sb = new StringBuilder();
        sb.append("Catalog: ").append(catalogPath.toAbsolutePath()).append(System.lineSeparator());

        List<Map<String, Object>> listed = new ArrayList<>();
        long count = 0;
        for (OperationDescriptor o : ops) {
            if (type != null && !type.equalsIgnoreCase(o.type)) continue;
            if (tag != null && (o.tags == null || o.tags.stream().noneMatch(t -> t.equalsIgnoreCase(tag)))) continue;
            String tags = (o.tags == null || o.tags.isEmpty()) ? "" : String.join("|", o.tags);
            sb.append(String.format("%-10s  %-8s  %-25s  %s%n", safe(o.type), basename(safe(o.name)), tags, safe(o.path)));
            listed.add(Map.of(
                    "type", safe(o.type),
                    "name", basename(safe(o.name)),
                    "tags", tags,
                    "path", safe(o.path)));
            count++;
            if (count >= limit) break;
        }

        long total = ops.stream()
                .filter(o -> type == null || type.equalsIgnoreCase(o.type))
                .filter(o -> tag == null || (o.tags != null && o.tags.stream().anyMatch(t -> t.equalsIgnoreCase(tag))))
                .count();
        sb.append("Shown: ").append(count).append("/").append(total).append(System.lineSeparator());

        Map<String, Object> context = new LinkedHashMap<>();
        context.put("catalogPath", catalogPath.toAbsolutePath().toString());
        context.put("filterType", type);
        context.put("filterTag", tag);
        context.put("limit", limit == Integer.MAX_VALUE ? "unbounded" : limit);
        context.put("returnedCount", count);
        context.put("totalMatching", total);
        context.put("entries", listed);

        return new CatalogResult(sb.toString(), context);
    }

    private CatalogResult buildExport(List<OperationDescriptor> ops, Map<String, String> flags, Path catalogPath, String out) throws IOException {
        String type = flags.get("type");
        String tag = flags.get("tag");

        List<OperationDescriptor> filtered = ops.stream()
                .filter(o -> type == null || type.equalsIgnoreCase(o.type))
                .filter(o -> tag == null || (o.tags != null && o.tags.stream().anyMatch(t -> t.equalsIgnoreCase(tag))))
                .collect(Collectors.toList());

        ObjectMapper om = new ObjectMapper();
        Path outPath = Paths.get(out);
        om.writerWithDefaultPrettyPrinter().writeValue(outPath.toFile(), filtered);

        StringBuilder sb = new StringBuilder();
        sb.append("Exported ").append(filtered.size()).append(" operations to ")
                .append(outPath.toAbsolutePath()).append(System.lineSeparator());

        Map<String, Object> context = new LinkedHashMap<>();
        context.put("catalogPath", catalogPath.toAbsolutePath().toString());
        context.put("filterType", type);
        context.put("filterTag", tag);
        context.put("exportPath", outPath.toAbsolutePath().toString());
        context.put("exportedCount", filtered.size());

        return new CatalogResult(sb.toString(), context);
    }

    private void emitInsight(EnhancedCliResult aiResult) {
        if (aiResult.successful() && aiResult.insight() != null && !aiResult.insight().isBlank()) {
            System.out.println();
            System.out.println("=== GPT-4 Insight ===");
            System.out.println(aiResult.insight());
        } else if (!aiResult.successful() && aiResult.message() != null && !aiResult.message().isBlank()) {
            System.out.println();
            System.out.println("[Intelligent mode] " + aiResult.message());
        }
    }

    private boolean isIntelligentEnabled(Map<String, String> flags) {
        String value = flags.get("intelligent");
        if (value == null) {
            return false;
        }
        return value.equalsIgnoreCase("true") || value.isBlank();
    }

    private void printBlock(String text) {
        if (text.endsWith(System.lineSeparator())) {
            System.out.print(text);
        } else {
            System.out.println(text);
        }
    }

    private String basename(String name) {
        int i = name.lastIndexOf('/') >= 0 ? name.lastIndexOf('/') : name.lastIndexOf('\\');
        return i >= 0 ? name.substring(i + 1) : name;
    }

    private String opt(String s) { return s == null ? "unknown" : s; }
    private String safe(String s) { return s == null ? "" : s; }

    private String topFolder(String path) {
        String norm = path.replace('\\', '/');
        int slash = norm.indexOf('/');
        return slash > 0 ? norm.substring(0, slash) : norm;
    }

    private Map<String, String> parseFlags(String[] args, int startIdx) {
        Map<String, String> m = new HashMap<>();
        for (int i = startIdx; i < args.length; i++) {
            String a = args[i];
            if (a.startsWith("--")) {
                int eq = a.indexOf('=');
                if (eq > 2) m.put(a.substring(2, eq), a.substring(eq + 1));
                else m.put(a.substring(2), "true");
            }
        }
        return m;
    }

    private int parseIntOrDefault(String s, int def) {
        if (s == null || s.isEmpty()) return def;
        try { return Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return def; }
    }

    private Path resolveCatalog(String flagFile) {
        if (flagFile != null) return Paths.get(flagFile);
        Path cwd = Paths.get("").toAbsolutePath();
        Path a = cwd.resolve("operations-catalog.json");
        if (Files.exists(a)) return a;
        Path b = cwd.resolve("nexus").resolve("operations-catalog.json");
        return Files.exists(b) ? b : a;
    }

    private List<OperationDescriptor> readCatalog(Path p) throws IOException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(Files.readAllBytes(p), new TypeReference<List<OperationDescriptor>>(){});
    }

    private static class CatalogResult {
        private final String output;
        private final Map<String, Object> context;

        private CatalogResult(String output, Map<String, Object> context) {
            this.output = output == null ? "" : output;
            this.context = context == null ? new LinkedHashMap<>() : new LinkedHashMap<>(context);
        }

        public String output() {
            return output;
        }

        public Map<String, Object> context() {
            return context;
        }
    }
}

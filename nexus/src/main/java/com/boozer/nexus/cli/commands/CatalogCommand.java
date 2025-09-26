package com.boozer.nexus.cli.commands;

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
    @Override
    public String name() { return "catalog"; }

    @Override
    public String description() {
    return "Inspect operations catalog: summary, list, or export (usage: catalog [summary|list|export] [--file=<path>] [--type=<t>] [--tag=<k>] [--limit=N] [--out=<file>])";
    }

    @Override
    public int run(String[] args) throws Exception {
        String mode = args.length == 0 || args[0].startsWith("--") ? "summary" : args[0].toLowerCase(Locale.ROOT);
        Map<String, String> flags = parseFlags(args, mode.equals("summary") ? 0 : 1);
        Path catalogPath = resolveCatalog(flags.get("file"));
        List<OperationDescriptor> ops = readCatalog(catalogPath);

        switch (mode) {
            case "summary":
                printSummary(ops, catalogPath);
                break;
            case "list":
                listOps(ops, flags, catalogPath);
                break;
            case "export":
                exportOps(ops, flags, catalogPath);
                break;
            default:
                System.err.println("Unknown subcommand: " + mode);
                return 2;
        }
        return 0;
    }

    private void printSummary(List<OperationDescriptor> ops, Path catalogPath) {
        System.out.println("Catalog: " + catalogPath.toAbsolutePath());
        System.out.println("Total operations: " + ops.size());
        Map<String, Long> byType = ops.stream().collect(Collectors.groupingBy(o -> opt(o.type), LinkedHashMap::new, Collectors.counting()));
        System.out.println("By type:");
        byType.forEach((t, c) -> System.out.printf("  %-12s %d%n", t, c));

        // Top-level folders insight
        Map<String, Long> byTop = ops.stream().collect(Collectors.groupingBy(o -> topFolder(opt(o.path)), LinkedHashMap::new, Collectors.counting()));
        System.out.println("By top folder:");
        byTop.forEach((t, c) -> System.out.printf("  %-20s %d%n", t, c));
    }

    private void listOps(List<OperationDescriptor> ops, Map<String, String> flags, Path catalogPath) {
        String type = flags.get("type");
        String tag = flags.get("tag");
    int limit = parseIntOrDefault(flags.get("limit"), Integer.MAX_VALUE);
        System.out.println("Catalog: " + catalogPath.toAbsolutePath());
        long count = 0;
        for (OperationDescriptor o : ops) {
            if (type != null && !type.equalsIgnoreCase(o.type)) continue;
            if (tag != null && (o.tags == null || o.tags.stream().noneMatch(t -> t.equalsIgnoreCase(tag)))) continue;
            String tags = (o.tags == null || o.tags.isEmpty()) ? "" : String.join("|", o.tags);
            System.out.printf("%-10s  %-8s  %-25s  %s%n", safe(o.type), basename(safe(o.name)), tags, safe(o.path));
            count++;
            if (count >= limit) break;
        }
        long total = ops.stream()
                .filter(o -> type == null || type.equalsIgnoreCase(o.type))
                .filter(o -> tag == null || (o.tags != null && o.tags.stream().anyMatch(t -> t.equalsIgnoreCase(tag))))
                .count();
        System.out.println("Shown: " + count + "/" + total);
    }

    private void exportOps(List<OperationDescriptor> ops, Map<String, String> flags, Path catalogPath) throws IOException {
        String type = flags.get("type");
        String tag = flags.get("tag");
        String out = flags.get("out");
        if (out == null || out.isBlank()) {
            System.err.println("--out=<file> is required for export");
            System.exit(2);
            return;
        }
        List<OperationDescriptor> filtered = ops.stream()
                .filter(o -> type == null || type.equalsIgnoreCase(o.type))
                .filter(o -> tag == null || (o.tags != null && o.tags.stream().anyMatch(t -> t.equalsIgnoreCase(tag))))
                .collect(Collectors.toList());
        ObjectMapper om = new ObjectMapper();
        Path outPath = Paths.get(out);
        om.writerWithDefaultPrettyPrinter().writeValue(outPath.toFile(), filtered);
        System.out.println("Exported " + filtered.size() + " operations to " + outPath.toAbsolutePath());
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
}

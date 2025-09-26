package com.boozer.nexus.cli.commands;

import com.boozer.nexus.cli.model.OperationDescriptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class IngestCommand implements Command {
    @Override
    public String name() { return "ingest"; }

    @Override
    public String description() { return "Scan the repo for operations and generate operations-catalog.json (flags: --all, --root=<path>, --out=<file>)"; }

    @Override
    public int run(String[] args) throws Exception {
    // Parse flags
    boolean includeAll = Arrays.stream(args).anyMatch(a -> a.equalsIgnoreCase("--all"));
    Optional<String> rootArg = Arrays.stream(args)
        .filter(a -> a.startsWith("--root="))
        .map(a -> a.substring("--root=".length()))
        .findFirst();
    Optional<String> outArg = Arrays.stream(args)
        .filter(a -> a.startsWith("--out="))
        .map(a -> a.substring("--out=".length()))
        .findFirst();

    Path root = rootArg.map(Paths::get)
        .orElseGet(() -> Paths.get(Objects.requireNonNullElse(System.getProperty("nexus.root"), findRepoRoot())));
    Path output = outArg.map(Paths::get).orElse(root.resolve("operations-catalog.json"));

    Set<String> ignoreDirs = includeAll ? Collections.emptySet() : defaultIgnoreDirs();

    // Aggregate and de-duplicate by relative path
    Map<String, OperationDescriptor> byPath = new LinkedHashMap<>();
    for (OperationDescriptor d : scanJsonOperations(root, ignoreDirs)) byPath.putIfAbsent(d.path, d);
    for (OperationDescriptor d : scanJavaOperations(root, ignoreDirs)) byPath.putIfAbsent(d.path, d);
    for (OperationDescriptor d : scanScriptOperations(root, ignoreDirs)) byPath.putIfAbsent(d.path, d);

    List<OperationDescriptor> ops = new ArrayList<>(byPath.values());

    String json = toJson(ops);
        Files.writeString(output, json, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("Wrote catalog: " + output.toAbsolutePath());
        System.out.println("Total operations: " + ops.size());
        return 0;
    }

    private String findRepoRoot() {
        // Default to current working directory
        return Paths.get("").toAbsolutePath().toString();
    }

    private List<OperationDescriptor> scanJsonOperations(Path root, Set<String> ignoreDirs) throws IOException {
        List<String> candidates = List.of(
                "**/nexus_task.json",
                "**/*operations*.json",
                "**/*tasks*.json"
        );
        return scanByGlobs(root, candidates, ignoreDirs, p -> {
            OperationDescriptor d = new OperationDescriptor();
            d.id = UUID.randomUUID().toString();
            d.name = p.getFileName().toString();
            d.type = "json";
            d.path = root.relativize(p).toString();
            d.summary = "JSON operation definition";
            d.tags = inferTags(d.path, d.type);
            return d;
        });
    }

    private List<OperationDescriptor> scanJavaOperations(Path root, Set<String> ignoreDirs) throws IOException {
        List<String> candidates = List.of(
                "**/src/main/java/**/service/**.java",
                "**/src/main/java/**/engine/**.java",
                "**/src/main/java/**/processor/**.java",
                "**/src/main/java/**/orchestrator/**.java"
        );
        return scanByGlobs(root, candidates, ignoreDirs, p -> {
            OperationDescriptor d = new OperationDescriptor();
            d.id = UUID.randomUUID().toString();
            d.name = p.getFileName().toString();
            d.type = "java";
            d.path = root.relativize(p).toString();
            d.summary = "Java operation candidate";
            d.tags = inferTags(d.path, d.type);
            return d;
        });
    }

    private List<OperationDescriptor> scanScriptOperations(Path root, Set<String> ignoreDirs) throws IOException {
        List<String> candidates = List.of(
                "**/*.sh",
                "**/*.ps1",
                "**/*.bat",
                "**/*.py"
        );
        return scanByGlobs(root, candidates, ignoreDirs, p -> {
            OperationDescriptor d = new OperationDescriptor();
            d.id = UUID.randomUUID().toString();
            d.name = p.getFileName().toString();
            d.type = guessScriptType(p.getFileName().toString());
            d.path = root.relativize(p).toString();
            d.summary = "Script operation";
            d.tags = inferTags(d.path, d.type);
            return d;
        });
    }

    private String guessScriptType(String name) {
        String n = name.toLowerCase();
        if (n.endsWith(".sh")) return "shell";
        if (n.endsWith(".ps1")) return "powershell";
        if (n.endsWith(".bat")) return "batch";
        if (n.endsWith(".py")) return "python";
        return "script";
    }

    private List<OperationDescriptor> scanByGlobs(Path root, List<String> globs, Set<String> ignoreDirs, java.util.function.Function<Path, OperationDescriptor> mapper) throws IOException {
        List<PathMatcher> matchers = globs.stream()
                .map(g -> FileSystems.getDefault().getPathMatcher("glob:" + g.replace("**/", "**/").replace("**", "**")))
                .collect(Collectors.toList());
        List<OperationDescriptor> results = new ArrayList<>();
        Files.walk(root)
                .filter(Files::isRegularFile)
                .filter(p -> {
                    // Avoid self-inclusion of generated catalogs
                    if (p.getFileName().toString().equalsIgnoreCase("operations-catalog.json")) return false;
                    if (!ignoreDirs.isEmpty() && isIgnored(root, p, ignoreDirs)) return false;
                    String rel = root.relativize(p).toString().replace('\\','/');
                    for (PathMatcher m : matchers) {
                        if (m.matches(Paths.get(rel))) return true;
                    }
                    return false;
                })
                .forEach(p -> results.add(mapper.apply(p)));
        return results;
    }

    private boolean isIgnored(Path root, Path p, Set<String> ignoreDirs) {
        Path rel = root.relativize(p);
        for (Path part : rel) {
            String name = part.toString();
            if (ignoreDirs.contains(name)) return true;
        }
        return false;
    }

    private Set<String> defaultIgnoreDirs() {
        return new HashSet<>(Arrays.asList(
                ".git", ".idea", ".vscode", "node_modules", "target", "build", "dist", "out",
                ".gradle", ".mvn", "tmp", "temp", "logs", "coverage", "__pycache__", ".pytest_cache",
                ".venv", "venv", ".next"
        ));
    }

    private String toJson(List<OperationDescriptor> ops) {
    String items = ops.stream().map(o -> String.format(Locale.ROOT,
        "{\"id\":\"%s\",\"name\":\"%s\",\"type\":\"%s\",\"path\":\"%s\",\"summary\":\"%s\",\"tags\":[%s]}",
        escape(o.id), escape(o.name), escape(o.type), escape(o.path), escape(o.summary), tagsJson(o.tags)
    )).collect(Collectors.joining(","));
        return "[" + items + "]";
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String tagsJson(java.util.List<String> tags) {
        if (tags == null || tags.isEmpty()) return "";
        return tags.stream().map(this::escape).map(t -> "\"" + t + "\"").collect(Collectors.joining(","));
    }

    private java.util.List<String> inferTags(String path, String type) {
        String p = path.toLowerCase(Locale.ROOT).replace('\\','/');
        java.util.LinkedHashSet<String> tags = new java.util.LinkedHashSet<>();
        tags.add(type);
        if (p.contains("test")) tags.add("test");
        if (p.contains("setup") || p.contains("install") || p.contains("deploy")) tags.add("infra");
        if (p.contains("migrate") || p.contains("database") || p.contains("postgres")) tags.add("data");
        if (p.contains("orchestrator") || p.contains("engine") || p.contains("service")) tags.add("core");
        // top folder as tag
        int slash = p.indexOf('/');
        if (slash > 0) tags.add(p.substring(0, slash));
        return new java.util.ArrayList<>(tags);
    }
}

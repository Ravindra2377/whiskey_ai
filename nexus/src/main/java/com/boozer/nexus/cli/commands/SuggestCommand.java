package com.boozer.nexus.cli.commands;

import com.boozer.nexus.cli.model.OperationDescriptor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class SuggestCommand implements Command {
    @Override
    public String name() { return "suggest"; }

    @Override
    public String description() { return "Suggest likely next commands based on the catalog and simple heuristics"; }

    @Override
    public int run(String[] args) throws Exception {
        Path catalog = resolveCatalog();
        List<OperationDescriptor> ops = readCatalog(catalog);
        if (ops.isEmpty()) {
            System.out.println("No operations found. Run: ingest");
            return 0;
        }

        Map<String, Long> byType = ops.stream().collect(Collectors.groupingBy(o -> opt(o.type), Collectors.counting()));
        String topType = byType.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("script");

        System.out.println("Suggestions (based on " + ops.size() + " ops in catalog):");
        // Suggest catalog exploration
        System.out.println("- catalog summary     # high-level view");
        System.out.println("- catalog list --type=" + topType + " --limit=10");
        // Suggest running infra/data scripts in dry-run
        long dataCount = ops.stream().filter(o -> hasTag(o, "data")).count();
        if (dataCount > 0) {
            System.out.println("- run --tag=data --limit=3   # dry-run database/data scripts");
        }
        long infraCount = ops.stream().filter(o -> hasTag(o, "infra")).count();
        if (infraCount > 0) {
            System.out.println("- run --tag=infra --limit=3  # dry-run setup/deploy scripts");
        }
        // Suggest exports
        System.out.println("- catalog export --type=" + topType + " --out=export-" + topType + ".json");
        return 0;
    }

    private boolean hasTag(OperationDescriptor o, String tag) {
        return o.tags != null && o.tags.stream().anyMatch(t -> t.equalsIgnoreCase(tag));
    }

    private String opt(String s) { return s == null ? "unknown" : s; }

    private Path resolveCatalog() {
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

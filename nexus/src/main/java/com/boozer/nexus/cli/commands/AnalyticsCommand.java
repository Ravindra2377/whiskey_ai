package com.boozer.nexus.cli.commands;

import com.boozer.nexus.cli.model.OperationDescriptor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AnalyticsCommand implements Command {
    @Override
    public String name() { return "analytics"; }

    @Override
    public String description() {
        return "Predictive analytics (stub): shows counts, hotspots, and simple projections (flags: --file=<catalog.json>)";
    }

    @Override
    public int run(String[] args) throws Exception {
        Path catalogPath = resolveCatalog(args);
        List<OperationDescriptor> ops = readCatalog(catalogPath);
        System.out.println("Analytics for catalog: " + catalogPath.toAbsolutePath());
        System.out.println("Total operations: " + ops.size());

        Map<String, Long> byType = ops.stream().collect(Collectors.groupingBy(o -> opt(o.type), LinkedHashMap::new, Collectors.counting()));
        System.out.println("By type:");
        byType.forEach((k,v) -> System.out.printf("  %-12s %d%n", k, v));

        Map<String, Long> byTag = new LinkedHashMap<>();
        for (OperationDescriptor o : ops) {
            if (o.tags == null) continue;
            for (String t : o.tags) byTag.merge(t.toLowerCase(Locale.ROOT), 1L, Long::sum);
        }
        System.out.println("Top tags:");
        byTag.entrySet().stream().sorted((a,b)->Long.compare(b.getValue(), a.getValue())).limit(10)
                .forEach(e -> System.out.printf("  %-12s %d%n", e.getKey(), e.getValue()));

        // Very naive 30-day projection: assume linear growth of ops by tag based on current composition
        int days = 30;
        System.out.println("\n30-day Projection (naive):");
        for (Map.Entry<String, Long> e : byType.entrySet()) {
            long current = e.getValue();
            long projected = current + Math.max(1, current / 10); // +10%
            System.out.printf("  %-12s %d -> ~%d%n", e.getKey(), current, projected);
        }

        // Hotspots by folder
        System.out.println("\nHotspots (top folders):");
        Map<String, Long> byFolder = ops.stream().collect(Collectors.groupingBy(o -> topFolder(opt(o.path)), LinkedHashMap::new, Collectors.counting()));
        byFolder.entrySet().stream().sorted((a,b)->Long.compare(b.getValue(), a.getValue())).limit(10)
                .forEach(e -> System.out.printf("  %-20s %d%n", e.getKey(), e.getValue()));

        // Timeline stub (since we have no commit history here)
        System.out.println("\nTimeline (stub): today=" + LocalDate.now() + ", next checkpoint=" + LocalDate.now().plusDays(30));
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

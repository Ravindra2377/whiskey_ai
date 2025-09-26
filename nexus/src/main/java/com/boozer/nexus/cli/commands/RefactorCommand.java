package com.boozer.nexus.cli.commands;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class RefactorCommand implements Command {
    @Override
    public String name() { return "refactor"; }

    @Override
    public String description() {
        return "Analyze repository for basic code smells and propose refactorings (flags: --root=<path>)";
    }

    @Override
    public int run(String[] args) throws Exception {
        Path root = parseRoot(args);
        System.out.println("Refactor analysis for: " + root.toAbsolutePath());
        List<String> smells = new ArrayList<>();

        // 1) Large files (> 2,000 lines)
        Files.walk(root)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java") || p.toString().endsWith(".py"))
                .forEach(p -> {
                    try {
                        long lines = Files.lines(p).count();
                        if (lines > 2000) {
                            smells.add("LARGE_FILE:" + p + " (" + lines + " lines)");
                        }
                    } catch (IOException ignored) {}
                });

        // 2) God classes (many methods) - naive heuristic by counting 'class ' and 'public ' methods
        Files.walk(root)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(p -> {
                    try {
                        String content = new String(Files.readAllBytes(p));
                        int methods = countOccurrences(content, "public ") + countOccurrences(content, "private ") + countOccurrences(content, "protected ");
                        if (methods > 60) smells.add("GOD_CLASS:" + p + " (" + methods + " method markers)");
                    } catch (IOException ignored) {}
                });

        // 3) Long scripts without functions (python)
        Files.walk(root)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".py"))
                .forEach(p -> {
                    try {
                        String content = new String(Files.readAllBytes(p));
                        boolean hasDef = content.contains("def ");
                        long lines = content.lines().count();
                        if (lines > 300 && !hasDef) smells.add("LONG_SCRIPT_NO_FUNCTIONS:" + p + " (" + lines + " lines)");
                    } catch (IOException ignored) {}
                });

        if (smells.isEmpty()) {
            System.out.println("No obvious smells found (based on simple heuristics).");
            return 0;
        }

        System.out.println("Smells found: " + smells.size());
        for (String s : smells) System.out.println(" - " + s);
        System.out.println("\nRecommended next steps:\n - Extract smaller classes/modules from LARGE_FILE/GOD_CLASS items\n - Introduce functions for long Python scripts\n - Add tests before making changes\n - Create a backup branch before refactoring");
        return 0;
    }

    private Path parseRoot(String[] args) {
        for (String a : args) {
            if (a.startsWith("--root=")) return Paths.get(a.substring("--root=".length()));
        }
        return Paths.get("");
    }

    private int countOccurrences(String haystack, String needle) {
        int count = 0, idx = 0;
        while ((idx = haystack.indexOf(needle, idx)) >= 0) { count++; idx += needle.length(); }
        return count;
    }
}

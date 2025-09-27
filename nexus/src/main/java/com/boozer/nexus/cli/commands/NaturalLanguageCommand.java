package com.boozer.nexus.cli.commands;

import java.util.*;

public class NaturalLanguageCommand implements Command {
    @Override
    public String name() { return "nl"; }

    @Override
    public String description() {
        return "Natural language interface (stub). Examples: nl \"ingest operations\", nl \"list python scripts\"";
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: nl \"your request...\"");
            return 2;
        }
        String original = String.join(" ", args);
        String query = original.toLowerCase(Locale.ROOT);

        if (query.contains("ingest")) {
            System.out.println("[NL] -> ingest");
            return new IngestCommand().run(new String[]{});
        }
        if (query.contains("list") || query.contains("show") || query.contains("catalog")) {
            List<String> flags = new ArrayList<>();
            flags.add("list");
            if (query.contains("python")) flags.add("--type=python");
            if (query.contains("powershell") || query.contains("ps1")) flags.add("--type=powershell");
            if (query.contains("shell") || query.contains("bash") || query.contains("sh")) flags.add("--type=shell");
            if (query.contains("java")) flags.add("--type=java");
            if (query.contains("data")) flags.add("--tag=data");
            System.out.println("[NL] -> catalog " + String.join(" ", flags));
            return new CatalogCommand().run(flags.toArray(new String[0]));
        }
        if (query.contains("run") || query.contains("execute")) {
            List<String> flags = new ArrayList<>();
            if (query.contains("python")) flags.add("--type=python");
            if (query.contains("powershell") || query.contains("ps1")) flags.add("--type=powershell");
            if (query.contains("shell") || query.contains("bash") || query.contains("sh")) flags.add("--type=shell");
            if (query.contains("java")) flags.add("--type=java");
            if (query.contains("data")) flags.add("--tag=data");
            System.out.println("[NL] -> run (dry-run) " + String.join(" ", flags));
            return new RunCommand().run(flags.toArray(new String[0]));
        }
        if (query.contains("analytics")) {
            if (query.contains("voice")) {
                System.out.println("[NL] -> analytics --voice");
                return new AnalyticsCommand().run(new String[]{"--voice"});
            }
            System.out.println("[NL] -> analytics");
            return new AnalyticsCommand().run(new String[]{});
        }
        if (query.contains("generate")) {
            List<String> flags = new ArrayList<>();
            flags.add("--spec=" + buildSpec(original));
            if (query.contains("python")) flags.add("--language=python");
            if (query.contains("java")) flags.add("--language=java");
            if (query.contains("kotlin")) flags.add("--language=kotlin");
            if (query.contains("typescript")) flags.add("--language=typescript");
            if (query.contains("test")) flags.add("--include-tests");
            System.out.println("[NL] -> generate " + String.join(" ", flags));
            return new GenerateCommand().run(flags.toArray(new String[0]));
        }

        System.out.println("[NL] Sorry, I didn't understand. Try: 'ingest operations', 'list python scripts', or 'run database migrations (dry run)'.");
        return 2;
    }

    private String buildSpec(String original) {
        String cleaned = original.replaceAll("(?i)hey nexus", "").trim();
        int idx = cleaned.toLowerCase(Locale.ROOT).indexOf("generate");
        if (idx >= 0) {
            cleaned = cleaned.substring(idx + "generate".length()).trim();
        }
        if (cleaned.isBlank()) {
            return original.trim();
        }
        return cleaned;
    }
}

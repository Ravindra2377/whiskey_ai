package com.boozer.nexus.cli.commands;

import com.boozer.nexus.cli.model.OperationDescriptor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class RunCommand implements Command {
    @Override
    public String name() { return "run"; }

    @Override
    public String description() {
        return "Execute cataloged operations (safe by default). Usage: run [--file=<catalog.json>] [--type=<t>] [--tag=<k>] [--name=<substr>] [--limit=N] [--root=<path>] [--ext=.ps1,.bat,.py,.sh] [--confirm]";
    }

    @Override
    public int run(String[] args) throws Exception {
        Map<String, String> flags = parseFlags(args, 0);
    Path catalogPath = resolveCatalog(flags.get("file"));
    Path root = resolveRoot(flags.get("root"), catalogPath);
        boolean confirm = flags.containsKey("confirm");
        int limit = parseIntOrDefault(flags.get("limit"), Integer.MAX_VALUE);
        String type = flags.get("type");
        String tag = flags.get("tag");
        String name = flags.get("name");
        Set<String> allowedExt = parseExt(flags.get("ext"));

        List<OperationDescriptor> ops = readCatalog(catalogPath).stream()
                .filter(o -> type == null || (o.type != null && o.type.equalsIgnoreCase(type)))
                .filter(o -> tag == null || (o.tags != null && o.tags.stream().anyMatch(t -> t.equalsIgnoreCase(tag))))
                .filter(o -> name == null || (o.name != null && o.name.toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT))))
                .limit(limit)
                .collect(Collectors.toList());

        if (ops.isEmpty()) {
            System.out.println("No matching operations to run.");
            return 0;
        }

        System.out.println((confirm ? "Executing" : "Dry-run") + " " + ops.size() + " operation(s)\nRoot: " + root.toAbsolutePath() + "\nCatalog: " + catalogPath.toAbsolutePath());

        int executed = 0;
        for (OperationDescriptor o : ops) {
            Path abs = root.resolve(o.path).normalize();
            // Ensure within root
            if (!abs.toAbsolutePath().startsWith(root.toAbsolutePath())) {
                System.err.println("[SKIP] Outside root: " + abs);
                continue;
            }
            if (!Files.exists(abs)) {
                System.err.println("[SKIP] Missing file: " + abs);
                continue;
            }
            String ext = getExt(abs.getFileName().toString());
            if (!allowedExt.isEmpty() && !allowedExt.contains(ext)) {
                System.err.println("[SKIP] Disallowed extension (" + ext + "): " + abs);
                continue;
            }
            List<String> cmd = buildCommand(abs, ext);
            if (cmd == null) {
                System.err.println("[SKIP] Unsupported file type: " + abs);
                continue;
            }
            System.out.println(formatCmd(cmd));
            if (confirm) {
                int code = exec(cmd, root.toFile());
                System.out.println("  -> Exit code: " + code);
                if (code != 0) return code;
                executed++;
            }
        }
        if (!confirm) {
            System.out.println("\nNo commands executed. Re-run with --confirm to execute.");
        } else {
            System.out.println("\nExecuted: " + executed + "/" + ops.size());
        }
        return 0;
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

    private Path resolveCatalog(String flagFile) {
        if (flagFile != null) return Paths.get(flagFile);
        Path cwd = Paths.get("").toAbsolutePath();
        Path a = cwd.resolve("operations-catalog.json");
        if (Files.exists(a)) return a;
        Path b = cwd.resolve("nexus").resolve("operations-catalog.json");
        return Files.exists(b) ? b : a;
    }

    private Path resolveRoot(String flagRoot, Path catalogPath) {
        if (flagRoot != null && !flagRoot.isBlank()) return Paths.get(flagRoot);
        Path parent = catalogPath.getParent();
        if (parent != null) return parent;
        return Paths.get("");
    }

    private int parseIntOrDefault(String s, int def) {
        if (s == null || s.isEmpty()) return def;
        try { return Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return def; }
    }

    private Set<String> parseExt(String extList) {
        if (extList == null || extList.isBlank()) return new LinkedHashSet<>(Arrays.asList(".ps1", ".bat", ".py", ".sh"));
        return Arrays.stream(extList.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String getExt(String name) {
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(dot).toLowerCase(Locale.ROOT) : "";
    }

    private List<String> buildCommand(Path abs, String ext) {
        String p = abs.toAbsolutePath().toString();
        // Windows-friendly defaults
        switch (ext) {
            case ".ps1":
                return Arrays.asList("powershell.exe", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", p);
            case ".bat":
                return Arrays.asList("cmd.exe", "/c", p);
            case ".py":
                return Arrays.asList("python", p);
            case ".sh":
                // On Windows, user may have Git Bash or WSL; default to bash if present in PATH
                return Arrays.asList("bash", p);
            default:
                return null;
        }
    }

    private String formatCmd(List<String> cmd) {
        return "> " + cmd.stream().map(this::quoteIfNeeded).collect(Collectors.joining(" "));
    }

    private String quoteIfNeeded(String s) {
        if (s.indexOf(' ') >= 0 && !(s.startsWith("\"") && s.endsWith("\""))) {
            return '"' + s + '"';
        }
        return s;
    }

    private int exec(List<String> cmd, File cwd) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(cwd);
        pb.inheritIO();
        Process p = pb.start();
        return p.waitFor();
    }

    private List<OperationDescriptor> readCatalog(Path p) throws IOException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(Files.readAllBytes(p), new TypeReference<List<OperationDescriptor>>(){});
    }
}

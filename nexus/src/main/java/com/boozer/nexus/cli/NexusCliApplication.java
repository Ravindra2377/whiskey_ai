package com.boozer.nexus.cli;

import com.boozer.nexus.persistence.OperationCatalogPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(
        scanBasePackages = "com.boozer.nexus",
        exclude = {
                // Disable DB/JPA/Redis for CLI runs unless explicitly enabled
                DataSourceAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                JpaRepositoriesAutoConfiguration.class,
                RedisAutoConfiguration.class,
                RedisRepositoriesAutoConfiguration.class
        }
)
public class NexusCliApplication implements CommandLineRunner {
    @Autowired(required = false)
    private OperationCatalogPersistenceService persistenceService;

    public static void main(String[] args) {
        new SpringApplicationBuilder(NexusCliApplication.class)
                .web(WebApplicationType.NONE) // disable web server
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .run(args);
    }

    @Override
    public void run(String... args) {
        var registry = new java.util.LinkedHashMap<String, com.boozer.nexus.cli.commands.Command>();
        registry.put("health", new com.boozer.nexus.cli.commands.HealthCommand());
        registry.put("version", new com.boozer.nexus.cli.commands.VersionCommand("NEXUS AI CLI v1.0.0"));
        registry.put("ingest", new com.boozer.nexus.cli.commands.IngestCommand(persistenceService));
        registry.put("catalog", new com.boozer.nexus.cli.commands.CatalogCommand());
        registry.put("run", new com.boozer.nexus.cli.commands.RunCommand());
        // New advanced commands
        registry.put("nl", new com.boozer.nexus.cli.commands.NaturalLanguageCommand());
        registry.put("suggest", new com.boozer.nexus.cli.commands.SuggestCommand());
        registry.put("refactor", new com.boozer.nexus.cli.commands.RefactorCommand());
        registry.put("analytics", new com.boozer.nexus.cli.commands.AnalyticsCommand());
    registry.put("voice", new com.boozer.nexus.cli.commands.VoiceCommand());

        if (args == null || args.length == 0) {
            printHelp(registry);
            return;
        }

        String cmd = args[0];
        if (equalsAny(cmd, "--help", "-h", "help")) {
            printHelp(registry);
            return;
        }
        if (equalsAny(cmd, "--version", "-v")) {
            cmd = "version";
        }

        var command = registry.get(cmd.toLowerCase());
        if (command == null) {
            System.err.println("Unknown command: " + cmd + "\n");
            printHelp(registry);
            System.exit(2);
        }
        try {
            int code = command.run(java.util.Arrays.copyOfRange(args, 1, args.length));
            System.exit(code);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static boolean equalsAny(String value, String... options) {
        for (String o : options) if (value.equalsIgnoreCase(o)) return true;
        return false;
    }

    private static void printHelp(java.util.Map<String, com.boozer.nexus.cli.commands.Command> registry) {
        System.out.println("NEXUS AI CLI");
        System.out.println("Usage: java -jar nexus-1.0.0.jar [command] [options]\n");
        System.out.println("Commands:");
        for (var c : registry.values()) {
            System.out.printf("  %-20s %s%n", c.name(), c.description());
        }
        System.out.println("  --version, -v         Print version and exit");
        System.out.println("  --help, -h, help      Show this help");
    }
}

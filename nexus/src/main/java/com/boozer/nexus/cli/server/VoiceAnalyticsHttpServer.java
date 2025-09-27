package com.boozer.nexus.cli.server;

import com.boozer.nexus.persistence.VoiceCommandAnalyticsService;
import com.boozer.nexus.persistence.VoiceCommandSummary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Lightweight HTTP server exposing voice analytics for dashboards or automation.
 */
public class VoiceAnalyticsHttpServer implements AutoCloseable {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final VoiceCommandAnalyticsService analyticsService;
    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final CountDownLatch latch = new CountDownLatch(1);

    private HttpServer server;
    private ExecutorService executor;

    public VoiceAnalyticsHttpServer(VoiceCommandAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    public void start(int port) throws IOException {
        if (analyticsService == null) {
            throw new IllegalStateException("Voice analytics service is not available");
        }

        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/metrics", new MetricsHandler());
        server.createContext("/health", exchange -> respond(exchange, 200, "ok"));
        server.createContext("/", new HtmlHandler());
        executor = Executors.newCachedThreadPool();
        server.setExecutor(executor);
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void blockUntilShutdown() throws InterruptedException {
        latch.await();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
        latch.countDown();
    }

    @Override
    public void close() {
        stop();
    }

    private void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private final class MetricsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            VoiceCommandSummary summary = analyticsService.summarize();
            byte[] payload = objectMapper.writeValueAsBytes(summary);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        }
    }

    private final class HtmlHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            VoiceCommandSummary summary = analyticsService.summarize();
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html><head><title>Nexus Voice Analytics</title>");
            html.append("<style>body{font-family:Segoe UI,Arial, sans-serif;margin:40px;}h1{color:#1f6feb;}table{border-collapse:collapse;margin-top:20px;}th,td{border:1px solid #d0d7de;padding:8px 12px;}th{background:#f6f8fa;text-align:left;}</style>");
            html.append("</head><body><h1>NEXUS Voice Analytics</h1>");
            html.append("<p>Total commands: ").append(summary.getTotal()).append("</p>");
            html.append("<p>Successful: ").append(summary.getSuccessful()).append("</p>");
            long failed = summary.getTotal() - summary.getSuccessful();
            html.append("<p>Failed: ").append(failed).append("</p>");
            html.append("<p>Success rate: ").append(percent(summary.getSuccessRate())).append("</p>");
            if (summary.getAwaitingConfirmation() > 0) {
                html.append("<p>Awaiting confirmations: ").append(summary.getAwaitingConfirmation()).append("</p>");
            }
            if (summary.getResumedCommands() > 0 || summary.getRepeatedCommands() > 0) {
                html.append("<p>Follow-ups → resumed: ").append(summary.getResumedCommands())
                        .append(", repeated: ").append(summary.getRepeatedCommands()).append("</p>");
            }
            if (summary.getWakeWordMisses() > 0 || summary.getNoSpeechEvents() > 0) {
                html.append("<p>Signal quality → wake word misses: ").append(summary.getWakeWordMisses())
                        .append(", no speech: ").append(summary.getNoSpeechEvents()).append("</p>");
            }
            if (summary.getFirst() != null && summary.getLast() != null) {
        html.append("<p>Window: ")
            .append(DATE_FMT.format(summary.getFirst()))
            .append(" &rarr; ")
            .append(DATE_FMT.format(summary.getLast()))
            .append("</p>");
            }

            appendTable(html, "Top Commands", summary.getCommandCounts());
            appendTable(html, "Top Errors", summary.getErrorCounts());
            appendTable(html, "Top Intents", summary.getIntentCounts());

            html.append("<p><a href=\"/metrics\">Download JSON</a> | <a href=\"/health\">Health</a></p>");
            html.append("</body></html>");

            byte[] payload = html.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        }

        private void appendTable(StringBuilder html, String title, java.util.Map<String, Long> data) {
            if (data == null || data.isEmpty()) {
                return;
            }
            html.append("<h2>").append(title).append("</h2><table><tr><th>Key</th><th>Count</th></tr>");
            data.forEach((key, value) -> html.append("<tr><td>").append(escape(key)).append("</td><td>").append(value).append("</td></tr>"));
            html.append("</table>");
        }

        private String escape(String value) {
            return value == null ? "" : value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }

    private String percent(double ratio) {
        return String.format("%.1f%%", ratio * 100.0);
    }
}

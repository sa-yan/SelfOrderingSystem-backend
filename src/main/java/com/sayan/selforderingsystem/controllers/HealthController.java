package com.sayan.selforderingsystem.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class HealthController {

    private final DataSource dataSource;

    @GetMapping(value = "/health", produces = MediaType.TEXT_HTML_VALUE)
    public String health() {
        boolean dbUp = isDatabaseUp();
        boolean allUp = dbUp;

        Duration uptime = Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime());
        String uptimeText = "%dh %dm %ds".formatted(
                uptime.toHours(), uptime.toMinutesPart(), uptime.toSecondsPart());
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm:ss a"));

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Health | Self Ordering System</title>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body {
                            font-family: 'Segoe UI', system-ui, sans-serif;
                            min-height: 100vh;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            background: linear-gradient(135deg, #1f2428 0%%, #2d3436 100%%);
                            color: #e8eaed;
                            padding: 1rem;
                        }
                        .card {
                            background: #24292e;
                            border: 1px solid rgba(255, 255, 255, 0.08);
                            border-radius: 16px;
                            padding: 2rem 2.5rem;
                            width: 100%%;
                            max-width: 420px;
                            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.4);
                        }
                        h1 { font-size: 1.25rem; margin-bottom: 0.25rem; }
                        .subtitle { color: #9aa0a6; font-size: 0.85rem; margin-bottom: 1.5rem; }
                        .overall {
                            display: inline-flex;
                            align-items: center;
                            gap: 0.5rem;
                            font-weight: 700;
                            font-size: 1rem;
                            padding: 0.4rem 1rem;
                            border-radius: 999px;
                            margin-bottom: 1.5rem;
                            background: %s;
                            color: %s;
                        }
                        .dot {
                            width: 10px; height: 10px; border-radius: 50%%;
                            background: currentColor;
                            animation: pulse 1.6s ease-in-out infinite;
                        }
                        @keyframes pulse {
                            0%%, 100%% { opacity: 1; }
                            50%% { opacity: 0.35; }
                        }
                        .row {
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            padding: 0.7rem 0;
                            border-top: 1px solid rgba(255, 255, 255, 0.07);
                            font-size: 0.92rem;
                        }
                        .row .label { color: #9aa0a6; }
                        .badge {
                            font-weight: 600;
                            font-size: 0.8rem;
                            padding: 0.2rem 0.7rem;
                            border-radius: 999px;
                        }
                        .up { background: rgba(22, 163, 74, 0.18); color: #4ade80; }
                        .down { background: rgba(220, 38, 38, 0.18); color: #f87171; }
                        .value { color: #e8eaed; }
                        .footer { margin-top: 1.25rem; text-align: center; color: #6b7280; font-size: 0.75rem; }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <h1>🍽️ Self Ordering System</h1>
                        <p class="subtitle">Backend health check</p>
                        <div class="overall"><span class="dot"></span>%s</div>
                        <div class="row">
                            <span class="label">API Server</span>
                            <span class="badge up">UP</span>
                        </div>
                        <div class="row">
                            <span class="label">Database (PostgreSQL)</span>
                            <span class="badge %s">%s</span>
                        </div>
                        <div class="row">
                            <span class="label">Uptime</span>
                            <span class="value">%s</span>
                        </div>
                        <div class="row">
                            <span class="label">Java</span>
                            <span class="value">%s</span>
                        </div>
                        <div class="footer">Checked at %s</div>
                    </div>
                </body>
                </html>
                """.formatted(
                allUp ? "rgba(22, 163, 74, 0.18)" : "rgba(220, 38, 38, 0.18)",
                allUp ? "#4ade80" : "#f87171",
                allUp ? "All Systems Operational" : "Service Degraded",
                dbUp ? "up" : "down",
                dbUp ? "UP" : "DOWN",
                uptimeText,
                System.getProperty("java.version"),
                now
        );
    }

    private boolean isDatabaseUp() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }
}

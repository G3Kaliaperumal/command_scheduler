package main.java.scheduler;

import main.java.scheduler.domain.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CommandLoader {
    private static final Logger logger = Logger.getLogger(CommandLoader.class.getName());
    private final String filepath;

    public CommandLoader(String filepath) {
        this.filepath = filepath;
    }

    public List<Command> loadCommands() {
        List<Command> commands = new ArrayList<>();
        logInfo("Loading commands from file: " + filepath);
        try {
            Files.readAllLines(Paths.get(filepath)).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .forEach(line -> processLine(line, commands));
        } catch (IOException e) {
            logSevere("Error reading file: " + filepath + ". " + e.getMessage());
        }
        logInfo("Loaded " + commands.size() + " commands.");
        return commands;
    }

    private void processLine(String line, List<Command> commands) {
        if (line.startsWith("*/")) {
            logInfo("Parsing recurring command: " + line);
            if (isValidRecurringCommand(line)) {
                parseRecurringCommand(line, commands);
            } else {
                logWarning("Invalid recurring command format: " + line);
            }
        } else {
            logInfo("Parsing scheduled command: " + line);
            if (isValidScheduledCommand(line)) {
                parseScheduledCommand(line, commands);
            } else {
                logWarning("Invalid scheduled command format: " + line);
            }
        }
    }

    private boolean isValidRecurringCommand(String line) {
        try {
            String[] parts = line.split(" ", 2);
            int frequency = Integer.parseInt(parts[0].substring(2));
            return parts.length == 2 && frequency > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidScheduledCommand(String line) {
        try {
            String[] parts = line.split(" ", 6);
            if (parts.length < 6) return false;
            int minute = Integer.parseInt(parts[0]);
            int hour = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);
            int month = Integer.parseInt(parts[3]);
            int year = Integer.parseInt(parts[4]);
            return minute >= 0 && minute < 60 &&
                   hour >= 0 && hour < 24 &&
                   day >= 1 && day <= 31 &&
                   month >= 1 && month <= 12 &&
                   year >= 1970;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void parseRecurringCommand(String line, List<Command> commands) {
        try {
            String[] parts = line.split(" ", 2);
            long frequency = Integer.parseInt(parts[0].substring(2)) * 60 * 1000L;
            commands.add(new Command(parts[1], frequency, true));
            logInfo("Added recurring command: " + parts[1] + " with frequency " + frequency + " ms");
        } catch (Exception e) {
            logWarning("Failed to parse recurring command: " + line + ". " + e.getMessage());
        }
    }

    private void parseScheduledCommand(String line, List<Command> commands) {
        try {
            String[] parts = line.split(" ", 6);
            LocalDateTime scheduledTime = LocalDateTime.of(
                Integer.parseInt(parts[4]), Integer.parseInt(parts[3]),
                Integer.parseInt(parts[2]), Integer.parseInt(parts[1]),
                Integer.parseInt(parts[0])
            );
            long delayInMillis = Math.max(0, Duration.between(LocalDateTime.now(), scheduledTime).toMillis());
            commands.add(new Command(parts[5], delayInMillis, false));
            logInfo("Added scheduled command: " + parts[5] + " with delay " + delayInMillis + " ms");
        } catch (Exception e) {
            logWarning("Failed to parse scheduled command: " + line + ". " + e.getMessage());
        }
    }

    private void logInfo(String message) {
        logger.info(message);
    }

    private void logWarning(String message) {
        logger.warning(message);
    }

    private void logSevere(String message) {
        logger.severe(message);
    }
}
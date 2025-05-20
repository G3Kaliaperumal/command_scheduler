package main.java.scheduler;

import main.java.scheduler.domain.Command;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.logging.Logger;

public class CommandExecutor {
    private static final Logger logger = Logger.getLogger(CommandExecutor.class.getName());
    private final String outputPath;
    private static final Object fileWriteLock = new Object();

    public CommandExecutor(String outputPath) {
        this.outputPath = outputPath;
        logInfo("Initializing CommandExecutor with output path: " + outputPath);
        initializeOutputFile();
    }

    private void initializeOutputFile() {
        try {
            Files.writeString(Path.of(outputPath), "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            logInfo("Output file initialized.");
        } catch (IOException e) {
            logSevere("Failed to initialize output file: " + e.getMessage());
        }
    }

    public void execute(Command cmd) {
        logInfo("Executing command: " + cmd.getCommandText());
        try {
            String output = runCommand(cmd.getCommandText());
            writeToFile(output);
            logInfo("Command executed successfully.");
        } catch (IOException e) {
            logSevere("Error executing command: " + e.getMessage());
        }
    }

    private String runCommand(String commandText) throws IOException {
        logInfo("Running command: " + commandText);
        ProcessBuilder builder = createProcessBuilder(commandText);
        builder.redirectErrorStream(true);
        Process process = builder.start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        logInfo("Command output: " + output);
        return output;
    }

    private ProcessBuilder createProcessBuilder(String commandText) {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win")
            ? new ProcessBuilder("cmd.exe", "/c", commandText)
            : new ProcessBuilder("bash", "-c", commandText);
    }

    private void writeToFile(String output) throws IOException {
        logInfo("Writing output to file.");
        synchronized (fileWriteLock) {
            Files.writeString(Path.of(outputPath), output, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            logInfo("Output written successfully.");
        }
    }

    private void logInfo(String message) {
        logger.info(message);
    }

    private void logSevere(String message) {
        logger.severe(message);
    }
}
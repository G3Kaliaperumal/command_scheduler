package main.java.scheduler;

import main.java.scheduler.domain.Command;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class CommandExecutor {
    private final String outputPath;
    private static final Object fileWriteLock = new Object();

    public CommandExecutor(String outputPath) {
        this.outputPath = outputPath;
        // Truncate the output file once at startup
        try {
            Files.writeString(Path.of(outputPath), "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void execute(Command cmd) {
        try {
            ProcessBuilder builder = new ProcessBuilder("bash", "-c", cmd.getCommandText());
            builder.redirectErrorStream(true);
            Process process = builder.start();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            synchronized (fileWriteLock) {
                Files.writeString(Path.of(outputPath), output, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                cmd.markExecuted();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
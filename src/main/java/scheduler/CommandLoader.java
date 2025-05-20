package main.java.scheduler;

import main.java.scheduler.domain.Command;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class CommandLoader {
    private final String filepath;

    public CommandLoader(String filepath) {
        this.filepath = filepath;
    }

    public List<Command> loadCommands() {
        List<Command> commands = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filepath));
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                if (line.startsWith("*/")) {
                    long frequency = Integer.parseInt(line.split(" ", 2)[0].substring(2));
                    String commandText = line.split(" ", 2)[1];
                    long delayInMillis = frequency * 60 * 1000;
                    commands.add(new Command(commandText, delayInMillis, true));
                } else {
                    String[] parts = line.split(" ", 6);
                    int minute = Integer.parseInt(parts[0]);
                    int hour = Integer.parseInt(parts[1]);
                    int day = Integer.parseInt(parts[2]);
                    int month = Integer.parseInt(parts[3]);
                    int year = Integer.parseInt(parts[4]);
                    String commandText = parts[5];
                    LocalDateTime scheduledTime = LocalDateTime.of(year, month, day, hour, minute);
                    long delayInMillis = Math.max(0, java.time.Duration.between(LocalDateTime.now(), scheduledTime).toMillis());
                    commands.add(new Command(commandText, delayInMillis, false));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return commands;
    }
}
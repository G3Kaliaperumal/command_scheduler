package main.test.scheduler;

import main.java.scheduler.CommandScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandSchedulerIntegrationTest {

    private static final String COMMANDS_FILE_PATH = "tmp/commands.txt";
    private static final String OUTPUT_FILE_PATH = "tmp/sample-output.txt";

    @BeforeEach
    void setUp() throws IOException {
        // Create the commands file
        File commandsFile = new File(COMMANDS_FILE_PATH);
        commandsFile.getParentFile().mkdirs();
        commandsFile.createNewFile();

        // Clear the output file
        File outputFile = new File(OUTPUT_FILE_PATH);
        outputFile.getParentFile().mkdirs();
        if (outputFile.exists()) {
            outputFile.delete();
        }
        outputFile.createNewFile();
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up files
        Files.deleteIfExists(new File(COMMANDS_FILE_PATH).toPath());
        Files.deleteIfExists(new File(OUTPUT_FILE_PATH).toPath());
    }

    @Test
    void schedulesAndExecutesOneTimeCommand() throws IOException, InterruptedException {
        // Schedule for 2 minutes in the future
        LocalDateTime now = LocalDateTime.now().plusMinutes(2);
        String command = String.format("%d %d %d %d %d echo 'One-time command executed'\n",
                now.getMinute(), now.getHour(), now.getDayOfMonth(), now.getMonthValue(), now.getYear());

        // Write a one-time command to the commands file
        try (FileWriter writer = new FileWriter(COMMANDS_FILE_PATH)) {
            writer.write(command);
        }

        // Run the scheduler
        CommandScheduler.main(new String[]{});

        // Wait for 2 minutes
        Thread.sleep((now.getMinute() + 3) * 60 * 1000);

        // Verify the output file contains the command output
        String output = Files.readString(new File(OUTPUT_FILE_PATH).toPath());
        assertTrue(output.contains("One-time command executed"));
    }

    @Test
    void schedulesAndExecutesRecurringCommand() throws IOException, InterruptedException {
        // Write a recurring command to the commands file
        try (FileWriter writer = new FileWriter(COMMANDS_FILE_PATH)) {
            writer.write("*/1 echo 'Recurring command executed'\n");
        }

        // Run the scheduler
        CommandScheduler.main(new String[]{});

        // Wait for the command to execute multiple times
        Thread.sleep(65000);

        // Verify the output file contains the command output multiple times
        String output = Files.readString(new File(OUTPUT_FILE_PATH).toPath());
        long occurrences = output.lines().filter(line -> line.contains("Recurring command executed")).count();
        assertTrue(occurrences > 1, "Recurring command should execute multiple times.");
    }

    @Test
    void skipsOneTimeCommandScheduledInThePast() throws IOException, InterruptedException {
        // Write a past one-time command to the commands file
        try (FileWriter writer = new FileWriter(COMMANDS_FILE_PATH)) {
            writer.write("0 0 1 1 2000 echo 'Past command should not execute'\n");
        }

        // Run the scheduler
        CommandScheduler.main(new String[]{});

        // Wait for the scheduler to process
        Thread.sleep(2000);

        // Verify the output file does not contain the command output
        String output = Files.readString(new File(OUTPUT_FILE_PATH).toPath());
        assertFalse(output.contains("Past command should not execute"));
    }
}
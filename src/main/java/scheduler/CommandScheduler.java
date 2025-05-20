package main.java.scheduler;

import main.java.scheduler.domain.Command;

import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class CommandScheduler {
    private static final Logger logger = Logger.getLogger(CommandScheduler.class.getName());

    public static void main(String[] args) {
        CommandLoader loader = new CommandLoader("tmp/commands.txt");
        List<Command> commands = loader.loadCommands();
        logInfo("Loaded " + commands.size() + " commands.");

        CommandExecutor executor = new CommandExecutor("tmp/sample-output.txt");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(commands.size());
        logInfo("Scheduler initialized with pool size: " + commands.size());

        commands.forEach(cmd -> scheduleCommand(cmd, executor, scheduler));

        logInfo("All commands scheduled.");
    }

    private static void scheduleCommand(Command cmd, CommandExecutor executor, ScheduledExecutorService scheduler) {
        cmd.setCommandExecutor(executor);
        if (cmd.isRecurring()) {
            logInfo("Scheduling recurring command: " + cmd.getCommandText() + " every " + cmd.getDelayInMillis() + " ms");
            scheduler.scheduleWithFixedDelay(cmd, 0, cmd.getDelayInMillis(), TimeUnit.MILLISECONDS);
        } else if (cmd.getDelayInMillis() > 0) {
            logInfo("Scheduling one-time command: " + cmd.getCommandText() + " after " + cmd.getDelayInMillis() + " ms");
            scheduler.schedule(cmd, cmd.getDelayInMillis(), TimeUnit.MILLISECONDS);
        } else {
            logWarning("Skipping command (scheduled time in the past): " + cmd.getCommandText());
        }
    }

    private static void logInfo(String message) {
        logger.info(message);
    }

    private static void logWarning(String message) {
        logger.warning(message);
    }
}

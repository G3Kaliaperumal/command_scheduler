package main.java.scheduler;

import main.java.scheduler.domain.Command;

import java.util.List;
import java.util.concurrent.*;

public class CommandScheduler {
    public static void main(String[] args) {
        CommandLoader loader = new CommandLoader("tmp/commands.txt");
        List<Command> commands = loader.loadCommands();

        CommandExecutor executor = new CommandExecutor("/tmp/sample-output.txt");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(commands.size());

        for (Command cmd: commands) {
            cmd.setCommandExecutor(executor);
            if (cmd.getIsRecurring()) {
                scheduler.scheduleWithFixedDelay(cmd, 0, cmd.getDelayInMillis(), TimeUnit.MILLISECONDS);
            } else {
                if (cmd.getDelayInMillis() > 0)
                    scheduler.schedule(cmd, cmd.getDelayInMillis(), TimeUnit.MILLISECONDS);
            }

        }
    }
}
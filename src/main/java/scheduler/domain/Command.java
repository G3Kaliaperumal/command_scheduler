package main.java.scheduler.domain;

import main.java.scheduler.CommandExecutor;

public class Command implements Runnable {
    private final String commandText;
    private CommandExecutor commandExecutor;
    private final long delayInMillis;
    private final boolean isRecurring;

    public Command(String commandText, long delayInMillis, boolean isRecurring) {
        this.commandText = commandText;
        this.delayInMillis = delayInMillis;
        this.isRecurring = isRecurring;
    }

    public String getCommandText() {
        return commandText;
    }

    public long getDelayInMillis() {
        return delayInMillis;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setCommandExecutor(CommandExecutor executor) {
        this.commandExecutor = executor;
    }

    @Override
    public void run() {
        if (commandExecutor != null) {
            commandExecutor.execute(this);
        }
    }
}
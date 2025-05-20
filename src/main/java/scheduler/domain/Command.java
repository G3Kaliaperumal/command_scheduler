package main.java.scheduler.domain;

import main.java.scheduler.CommandExecutor;

public class Command implements Runnable {
    protected String commandText;
    protected CommandExecutor commandExecutor;
    protected long delayInMillis;
    protected boolean isRecurring;

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

    public boolean getIsRecurring() {
        return isRecurring;
    }

    public void markExecuted() {}

    public void setCommandExecutor(CommandExecutor executor) {
        this.commandExecutor = executor;
    }

    @Override
    public void run() {
        this.commandExecutor.execute(this);
    }
}

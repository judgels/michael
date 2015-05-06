package org.iatoki.judgels.michael;

public class SingleMachineOperationCommand {

    private final String command;

    public SingleMachineOperationCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}

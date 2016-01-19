package org.iatoki.judgels.michael.machine.watcher;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class MachineWatcherNotFoundException extends EntityNotFoundException {

    public MachineWatcherNotFoundException() {
        super();
    }

    public MachineWatcherNotFoundException(String s) {
        super(s);
    }

    public MachineWatcherNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MachineWatcherNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Machine Watcher";
    }
}

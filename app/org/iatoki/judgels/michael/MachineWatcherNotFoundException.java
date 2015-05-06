package org.iatoki.judgels.michael;

import org.iatoki.judgels.commons.EntityNotFoundException;

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

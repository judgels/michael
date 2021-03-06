package org.iatoki.judgels.michael.machine.access;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class MachineAccessNotFoundException extends EntityNotFoundException {

    public MachineAccessNotFoundException() {
        super();
    }

    public MachineAccessNotFoundException(String s) {
        super(s);
    }

    public MachineAccessNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MachineAccessNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Machine Access";
    }
}

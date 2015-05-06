package org.iatoki.judgels.michael;

import org.iatoki.judgels.commons.EntityNotFoundException;

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

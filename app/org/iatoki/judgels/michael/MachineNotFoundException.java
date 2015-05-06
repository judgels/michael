package org.iatoki.judgels.michael;

import org.iatoki.judgels.commons.EntityNotFoundException;

public final class MachineNotFoundException extends EntityNotFoundException {

    public MachineNotFoundException() {
        super();
    }

    public MachineNotFoundException(String s) {
        super(s);
    }

    public MachineNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MachineNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Machine";
    }
}

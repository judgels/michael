package org.iatoki.judgels.michael.operation;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class OperationNotFoundException extends EntityNotFoundException {

    public OperationNotFoundException() {
        super();
    }

    public OperationNotFoundException(String s) {
        super(s);
    }

    public OperationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OperationNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Operation";
    }
}

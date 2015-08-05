package org.iatoki.judgels.michael;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class ApplicationNotFoundException extends EntityNotFoundException {

    public ApplicationNotFoundException() {
        super();
    }

    public ApplicationNotFoundException(String s) {
        super(s);
    }

    public ApplicationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Application";
    }
}

package org.iatoki.judgels.michael;

import org.iatoki.judgels.commons.EntityNotFoundException;

public final class ApplicationVersionNotFoundException extends EntityNotFoundException {

    public ApplicationVersionNotFoundException() {
        super();
    }

    public ApplicationVersionNotFoundException(String s) {
        super(s);
    }

    public ApplicationVersionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationVersionNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Application Version";
    }
}

package org.iatoki.judgels.michael;

import org.iatoki.judgels.commons.EntityNotFoundException;

public final class DashboardNotFoundException extends EntityNotFoundException {

    public DashboardNotFoundException() {
        super();
    }

    public DashboardNotFoundException(String s) {
        super(s);
    }

    public DashboardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DashboardNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Dashboard";
    }
}

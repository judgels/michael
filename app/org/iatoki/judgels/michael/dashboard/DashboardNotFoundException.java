package org.iatoki.judgels.michael.dashboard;

import org.iatoki.judgels.play.EntityNotFoundException;

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

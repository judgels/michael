package org.iatoki.judgels.michael;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class DashboardMachineNotFoundException extends EntityNotFoundException {

    public DashboardMachineNotFoundException() {
        super();
    }

    public DashboardMachineNotFoundException(String s) {
        super(s);
    }

    public DashboardMachineNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DashboardMachineNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Dashboard Machine";
    }
}

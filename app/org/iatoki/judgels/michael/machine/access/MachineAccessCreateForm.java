package org.iatoki.judgels.michael.machine.access;

import play.data.validation.Constraints;

public final class MachineAccessCreateForm {

    @Constraints.Required
    public String machineAccessType;
}

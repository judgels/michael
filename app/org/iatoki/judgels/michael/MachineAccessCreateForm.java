package org.iatoki.judgels.michael;

import play.data.validation.Constraints;

public final class MachineAccessCreateForm {

    @Constraints.Required
    public String machineAccessType;

}

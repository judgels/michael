package org.iatoki.judgels.michael.controllers.forms;

import play.data.validation.Constraints;

public final class MachineAccessCreateForm {

    @Constraints.Required
    public String machineAccessType;

}

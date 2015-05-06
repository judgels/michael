package org.iatoki.judgels.michael;

import play.data.validation.Constraints;

public final class SingleMachineOperationUpsertForm {

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String type;

    @Constraints.Required
    public String command;
}

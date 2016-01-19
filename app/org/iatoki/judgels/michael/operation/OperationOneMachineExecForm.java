package org.iatoki.judgels.michael.operation;

import play.data.validation.Constraints;

public final class OperationOneMachineExecForm {

    @Constraints.Required
    public String machineJid;

    @Constraints.Required
    public long accessId;
}

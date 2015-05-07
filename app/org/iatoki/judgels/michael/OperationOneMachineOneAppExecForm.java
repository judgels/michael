package org.iatoki.judgels.michael;

import play.data.validation.Constraints;

public final class OperationOneMachineOneAppExecForm {

    @Constraints.Required
    public String machineJid;

    @Constraints.Required
    public long accessId;

    @Constraints.Required
    public String applicationJid;

    @Constraints.Required
    public long versionId;
}
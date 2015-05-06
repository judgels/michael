package org.iatoki.judgels.michael;

import play.data.validation.Constraints;

public final class CopyOperationRunForm {

    @Constraints.Required
    public String machineJid1;

    @Constraints.Required
    public long accessId1;

    @Constraints.Required
    public String machineJid2;

    @Constraints.Required
    public long accessId2;

    @Constraints.Required
    public String applicationJid;

    @Constraints.Required
    public long versionId;
}

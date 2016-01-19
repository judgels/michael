package org.iatoki.judgels.michael.machine;

import play.data.validation.Constraints;

public final class MachineUpsertForm {

    @Constraints.Required
    public String instanceName;

    @Constraints.Required
    public String displayName;

    @Constraints.Required
    public String baseDir;

    @Constraints.Required
    public String type;

    @Constraints.Required
    public String ipAddress;
}

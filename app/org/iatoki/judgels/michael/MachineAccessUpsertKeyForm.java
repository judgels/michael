package org.iatoki.judgels.michael;

import play.data.validation.Constraints;

public final class MachineAccessUpsertKeyForm {

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String username;

    @Constraints.Required
    public String key;

    @Constraints.Required
    public int port;
}

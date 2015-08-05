package org.iatoki.judgels.michael.forms;

import play.data.validation.Constraints;

public final class MachineAccessKeyConfForm {

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String username;

    @Constraints.Required
    public String key;

    @Constraints.Required
    public int port;
}

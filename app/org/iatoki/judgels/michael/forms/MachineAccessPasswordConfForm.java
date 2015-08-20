package org.iatoki.judgels.michael.forms;

import play.data.validation.Constraints;

public final class MachineAccessPasswordConfForm {

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String username;

    @Constraints.Required
    public String password;

    @Constraints.Required
    public int port;
}

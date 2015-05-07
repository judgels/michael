package org.iatoki.judgels.michael;

import play.data.validation.Constraints;

public final class OperationOneMachineExecConfForm {

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String command;

    @Constraints.Required
    public String terminationType;

    public String terminationValue;

}

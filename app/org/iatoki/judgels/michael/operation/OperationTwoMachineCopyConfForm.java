package org.iatoki.judgels.michael.operation;

import play.data.validation.Constraints;

public final class OperationTwoMachineCopyConfForm {

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String sourceFile;

    @Constraints.Required
    public String targetFile;
}

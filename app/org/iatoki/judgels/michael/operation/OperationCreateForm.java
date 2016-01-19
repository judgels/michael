package org.iatoki.judgels.michael.operation;

import play.data.validation.Constraints;

public final class OperationCreateForm {

    @Constraints.Required
    public String operationType;
}

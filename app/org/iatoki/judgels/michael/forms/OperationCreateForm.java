package org.iatoki.judgels.michael.forms;

import play.data.validation.Constraints;

public final class OperationCreateForm {

    @Constraints.Required
    public String operationType;
}

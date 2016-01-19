package org.iatoki.judgels.michael.application;

import play.data.validation.Constraints;

public final class ApplicationUpsertForm {

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String type;
}

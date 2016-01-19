package org.iatoki.judgels.michael.application.version;

import play.data.validation.Constraints;

public final class ApplicationVersionUpsertForm {

    @Constraints.Required
    public String name;
}

package org.iatoki.judgels.michael;

import play.data.validation.Constraints;

public final class ApplicationVersionUpsertForm {

    @Constraints.Required
    public String name;

}

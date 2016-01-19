package org.iatoki.judgels.michael.dashboard;

import play.data.validation.Constraints;

public final class DashboardUpsertForm {

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String description;
}

package org.iatoki.judgels.michael.dashboard.machine;

import play.data.validation.Constraints;

public final class DashboardMachineCreateForm {

    @Constraints.Required
    public String machineJid;
}

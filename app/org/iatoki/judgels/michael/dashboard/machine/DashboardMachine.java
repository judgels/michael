package org.iatoki.judgels.michael.dashboard.machine;

public final class DashboardMachine {

    private final long id;
    private final String dashboardJid;
    private final String machineJid;

    public DashboardMachine(long id, String dashboardJid, String machineJid) {
        this.id = id;
        this.dashboardJid = dashboardJid;
        this.machineJid = machineJid;
    }

    public long getId() {
        return id;
    }

    public String getDashboardJid() {
        return dashboardJid;
    }

    public String getMachineJid() {
        return machineJid;
    }
}

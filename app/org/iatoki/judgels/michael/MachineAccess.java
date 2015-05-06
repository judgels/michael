package org.iatoki.judgels.michael;

public final class MachineAccess {

    private final long id;
    private final String machineJid;
    private final String name;
    private final String type;

    public MachineAccess(long id, String machineJid, String name, String type) {
        this.id = id;
        this.machineJid = machineJid;
        this.name = name;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getMachineJid() {
        return machineJid;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}

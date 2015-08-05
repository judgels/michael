package org.iatoki.judgels.michael;

public final class MachineWatcher {

    private final long id;
    private final String machineJid;
    private final MachineWatcherType type;
    private final String conf;

    public MachineWatcher(long id, String machineJid, MachineWatcherType type, String conf) {
        this.id = id;
        this.machineJid = machineJid;
        this.type = type;
        this.conf = conf;
    }

    public long getId() {
        return id;
    }

    public String getMachineJid() {
        return machineJid;
    }

    public MachineWatcherType getType() {
        return type;
    }

    public String getConf() {
        return conf;
    }
}

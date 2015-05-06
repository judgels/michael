package org.iatoki.judgels.michael;

public final class MachineWatcher {

    private final long id;
    private final String machineJid;
    private final MachineWatcherTypes type;
    private final String conf;

    public MachineWatcher(long id, String machineJid, MachineWatcherTypes type, String conf) {
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

    public MachineWatcherTypes getType() {
        return type;
    }

    public String getConf() {
        return conf;
    }
}

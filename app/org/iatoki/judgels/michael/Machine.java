package org.iatoki.judgels.michael;

public final class Machine {

    private final long id;
    private final String jid;
    private final String instanceName;
    private final String displayName;
    private final String baseDir;
    private final MachineTypes type;
    private final String ipAddress;

    public Machine(long id, String jid, String instanceName, String displayName, String baseDir, MachineTypes type, String ipAddress) {
        this.id = id;
        this.jid = jid;
        this.instanceName = instanceName;
        this.displayName = displayName;
        this.baseDir = baseDir;
        this.type = type;
        this.ipAddress = ipAddress;
    }

    public long getId() {
        return id;
    }

    public String getJid() {
        return jid;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public MachineTypes getType() {
        return type;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}

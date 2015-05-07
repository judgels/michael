package org.iatoki.judgels.michael;

public final class Operation {

    private final long id;
    private final String jid;
    private final String name;
    private final String type;
    private final String conf;

    public Operation(long id, String jid, String name, String type, String conf) {
        this.id = id;
        this.jid = jid;
        this.name = name;
        this.type = type;
        this.conf = conf;
    }

    public long getId() {
        return id;
    }

    public String getJid() {
        return jid;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getConf() {
        return conf;
    }
}

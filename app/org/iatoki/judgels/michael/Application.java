package org.iatoki.judgels.michael;

public final class Application {

    private final long id;
    private final String jid;
    private final String name;
    private final ApplicationTypes type;

    public Application(long id, String jid, String name, ApplicationTypes type) {
        this.id = id;
        this.jid = jid;
        this.name = name;
        this.type = type;
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

    public ApplicationTypes getType() {
        return type;
    }
}

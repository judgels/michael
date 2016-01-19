package org.iatoki.judgels.michael.application.version;

public final class ApplicationVersion {

    private final long id;
    private final String applicationJid;
    private final String name;

    public ApplicationVersion(long id, String applicationJid, String name) {
        this.id = id;
        this.applicationJid = applicationJid;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getApplicationJid() {
        return applicationJid;
    }

    public String getName() {
        return name;
    }
}

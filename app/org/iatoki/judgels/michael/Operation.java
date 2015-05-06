package org.iatoki.judgels.michael;

import com.google.gson.Gson;

public final class Operation {

    private final long id;
    private final String jid;
    private final String name;
    private final String type;
    private final String command;

    public Operation(long id, String jid, String name, String type, String command) {
        this.id = id;
        this.jid = jid;
        this.name = name;
        this.type = type;
        this.command = command;
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

    public <T> T getCommand(Class<T> clazz) {
        return new Gson().fromJson(command, clazz);
    }
}

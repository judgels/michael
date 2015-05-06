package org.iatoki.judgels.michael;

public class MachineAccessKeyConf {

    private final String username;
    private final String key;
    private final int port;

    public MachineAccessKeyConf(String username, String key, int port) {
        this.username = username;
        this.key = key;
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public String getKey() {
        return key;
    }

    public int getPort() {
        return port;
    }
}

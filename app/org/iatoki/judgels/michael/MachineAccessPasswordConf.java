package org.iatoki.judgels.michael;

public class MachineAccessPasswordConf {

    private final String username;
    private final String password;
    private final int port;

    public MachineAccessPasswordConf(String username, String password, int port) {
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }
}

package org.iatoki.judgels.michael;

import com.typesafe.config.Config;

import java.io.File;

public final class MichaelProperties {
    private static MichaelProperties INSTANCE;

    private final Config config;

    private String michaelUsername;
    private String michaelPassword;

    public String getMichaelUsername() {
        return michaelUsername;
    }

    public String getMichaelPassword() {
        return michaelPassword;
    }

    private MichaelProperties(Config config) {
        this.config = config;
    }

    public static synchronized void buildInstance(Config config) {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException("MichaelProperties instance has already been built");
        }

        INSTANCE = new MichaelProperties(config);
        INSTANCE.build();
    }

    public static MichaelProperties getInstance() {
        if (INSTANCE == null) {
            throw new UnsupportedOperationException("MichaelProperties instance has not been built");
        }
        return INSTANCE;
    }

    private void build() {
        michaelUsername = requireStringValue("michael.username");
        michaelPassword = requireStringValue("michael.password");
    }

    private String getStringValue(String key) {
        if (!config.hasPath(key)) {
            return null;
        }
        return config.getString(key);
    }

    private String requireStringValue(String key) {
        return config.getString(key);
    }

    private Integer getIntegerValue(String key) {
        if (!config.hasPath(key)) {
            return null;
        }
        return config.getInt(key);
    }

    private int requireIntegerValue(String key) {
        return config.getInt(key);
    }

    private Boolean getBooleanValue(String key) {
        if (!config.hasPath(key)) {
            return null;
        }
        return config.getBoolean(key);
    }

    private boolean requireBooleanValue(String key) {
        return config.getBoolean(key);
    }

    private File requireDirectoryValue(String key) {
        String filename = config.getString(key);

        File dir = new File(filename);
        if (!dir.isDirectory()) {
            throw new IllegalStateException("Directory " + dir.getAbsolutePath() + " does not exist");
        }
        return dir;
    }
}

package org.iatoki.judgels.michael.machine.watcher;

public final class DataPoint {

    private final long time;
    private final double value;
    private final String unit;

    public DataPoint(long time, double value, String unit) {
        this.time = time;
        this.value = value;
        this.unit = unit;
    }
}

package org.iatoki.judgels.michael.machine.watcher;

import java.util.Date;
import java.util.List;

public interface GraphMachineWatcherAdapter extends MachineWatcherAdapter {

    List<DataPoint> getDataPoints(Date startTime, Date endTime, long period);
}

package org.iatoki.judgels.michael.adapters;

import org.iatoki.judgels.michael.DataPoint;

import java.util.Date;
import java.util.List;

public interface GraphMachineWatcherAdapter extends MachineWatcherAdapter {

    List<DataPoint> getDataPoints(Date startTime, Date endTime, long period);
}

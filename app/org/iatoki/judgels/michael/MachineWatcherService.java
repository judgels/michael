package org.iatoki.judgels.michael;

import java.util.List;

public interface MachineWatcherService {

    List<MachineWatcherType> findEnabledWatcherByMachineJid(String machineJid);

    boolean isWatcherActivated(String machineJid, MachineWatcherType types);

    MachineWatcher findByWatcherId(long watcherId) throws MachineWatcherNotFoundException;

    MachineWatcher findByMachineJidAndWatcherType(String machineJid, MachineWatcherType types);

    List<MachineWatcher> findAll(String machineJid);

    void createWatcher(String machineJid, MachineWatcherType types, String conf);

    void updateWatcher(long machineWatcherId, String machineJid, MachineWatcherType types, String conf);

    void removeWatcher(String machineJid, MachineWatcherType types);
}

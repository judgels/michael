package org.iatoki.judgels.michael;

import java.util.List;

public interface MachineWatcherService {

    List<MachineWatcherTypes> findEnabledWatcherByMachineJid(String machineJid);

    boolean isWatcherEnabled(String machineJid, MachineWatcherTypes types);

    MachineWatcher findByWatcherId(long watcherId) throws MachineWatcherNotFoundException;

    MachineWatcher findByMachineJidAndWatcherType(String machineJid, MachineWatcherTypes types);

    List<MachineWatcher> findAll(String machineJid);

    void createWatcher(String machineJid, MachineWatcherTypes types, String conf);

    void updateWatcher(long machineWatcherId, String machineJid, MachineWatcherTypes types, String conf);
}

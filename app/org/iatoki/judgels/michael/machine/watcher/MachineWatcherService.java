package org.iatoki.judgels.michael.machine.watcher;

import com.google.inject.ImplementedBy;

import java.util.List;

@ImplementedBy(MachineWatcherServiceImpl.class)
public interface MachineWatcherService {

    List<MachineWatcherType> getEnabledWatchersByMachineJid(String machineJid);

    boolean isWatcherActivated(String machineJid, MachineWatcherType types);

    MachineWatcher findMachineWatcherById(long watcherId) throws MachineWatcherNotFoundException;

    MachineWatcher findMachineWatcherByMachineJidAndType(String machineJid, MachineWatcherType types);

    List<MachineWatcher> getAllMachineWatchers(String machineJid);

    void createMachineWatcher(String machineJid, MachineWatcherType types, String conf);

    void updateMachineWatcher(long machineWatcherId, String machineJid, MachineWatcherType types, String conf);

    void removeMachineWatcher(String machineJid, MachineWatcherType types);
}

package org.iatoki.judgels.michael.services;

import org.iatoki.judgels.michael.MachineWatcher;
import org.iatoki.judgels.michael.MachineWatcherNotFoundException;
import org.iatoki.judgels.michael.MachineWatcherType;

import java.util.List;

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

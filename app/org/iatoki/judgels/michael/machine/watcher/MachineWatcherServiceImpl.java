package org.iatoki.judgels.michael.machine.watcher;

import com.google.common.collect.ImmutableMap;
import org.iatoki.judgels.play.IdentityUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public final class MachineWatcherServiceImpl implements MachineWatcherService {

    private final MachineWatcherDao machineWatcherDao;

    @Inject
    public MachineWatcherServiceImpl(MachineWatcherDao machineWatcherDao) {
        this.machineWatcherDao = machineWatcherDao;
    }

    @Override
    public List<MachineWatcherType> getEnabledWatchersByMachineJid(String machineJid) {
        List<MachineWatcherModel> machineWatcherModels = machineWatcherDao.findSortedByFiltersEq("id", "asc", "", ImmutableMap.of(MachineWatcherModel_.machineJid, machineJid), 0, -1);
        return machineWatcherModels.stream().map(m -> MachineWatcherType.valueOf(m.type)).collect(Collectors.toList());
    }

    @Override
    public boolean isWatcherActivated(String machineJid, MachineWatcherType types) {
        return machineWatcherDao.existsByMachineJidAndWatcherType(machineJid, types.name());
    }

    @Override
    public MachineWatcher findMachineWatcherById(long watcherId) throws MachineWatcherNotFoundException {
        MachineWatcherModel machineWatcherModel = machineWatcherDao.findById(watcherId);
        if (machineWatcherModel != null) {
            return createMachineWatcherFromModel(machineWatcherModel);
        } else {
            throw new MachineWatcherNotFoundException("Machine Watcher not found.");
        }
    }

    @Override
    public MachineWatcher findMachineWatcherByMachineJidAndType(String machineJid, MachineWatcherType types) {
        return createMachineWatcherFromModel(machineWatcherDao.findByMachineJidAndWatcherType(machineJid, types.name()));
    }

    @Override
    public List<MachineWatcher> getAllMachineWatchers(String machineJid) {
        return machineWatcherDao.findSortedByFiltersEq("id", "asc", "", ImmutableMap.of(MachineWatcherModel_.machineJid, machineJid), 0, -1).stream().map(m -> createMachineWatcherFromModel(m)).collect(Collectors.toList());
    }

    @Override
    public void createMachineWatcher(String machineJid, MachineWatcherType types, String conf) {
        MachineWatcherModel machineWatcherModel = new MachineWatcherModel();
        machineWatcherModel.machineJid = machineJid;
        machineWatcherModel.type = types.name();
        machineWatcherModel.conf = conf;

        machineWatcherDao.persist(machineWatcherModel, "michael", IdentityUtils.getIpAddress());
    }

    @Override
    public void updateMachineWatcher(long machineWatcherId, String machineJid, MachineWatcherType types, String conf) {
        MachineWatcherModel machineWatcherModel = machineWatcherDao.findById(machineWatcherId);
        machineWatcherModel.machineJid = machineJid;
        machineWatcherModel.type = types.name();
        machineWatcherModel.conf = conf;

        machineWatcherDao.edit(machineWatcherModel, "michael", IdentityUtils.getIpAddress());
    }

    @Override
    public void removeMachineWatcher(String machineJid, MachineWatcherType types) {
        MachineWatcherModel machineWatcherModel = machineWatcherDao.findByMachineJidAndWatcherType(machineJid, types.name());

        machineWatcherDao.remove(machineWatcherModel);
    }

    private MachineWatcher createMachineWatcherFromModel(MachineWatcherModel machineWatcherModel) {
        return new MachineWatcher(machineWatcherModel.id, machineWatcherModel.machineJid, MachineWatcherType.valueOf(machineWatcherModel.type), machineWatcherModel.conf);
    }
}

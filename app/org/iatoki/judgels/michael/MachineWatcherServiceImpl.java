package org.iatoki.judgels.michael;

import com.google.common.collect.ImmutableMap;
import org.iatoki.judgels.commons.IdentityUtils;
import org.iatoki.judgels.michael.models.dao.interfaces.MachineWatcherDao;
import org.iatoki.judgels.michael.models.domains.MachineWatcherModel;
import org.iatoki.judgels.michael.models.domains.MachineWatcherModel_;

import java.util.List;
import java.util.stream.Collectors;

public final class MachineWatcherServiceImpl implements MachineWatcherService {

    private final MachineWatcherDao machineWatcherDao;

    public MachineWatcherServiceImpl(MachineWatcherDao machineWatcherDao) {
        this.machineWatcherDao = machineWatcherDao;
    }

    @Override
    public List<MachineWatcherTypes> findEnabledWatcherByMachineJid(String machineJid) {
        List<MachineWatcherModel> machineWatcherModels = machineWatcherDao.findSortedByFilters("id", "asc", "", ImmutableMap.of(MachineWatcherModel_.machineJid, machineJid), ImmutableMap.of(), 0, -1);
        return machineWatcherModels.stream().map(m -> MachineWatcherTypes.valueOf(m.type)).collect(Collectors.toList());
    }

    @Override
    public boolean isWatcherEnabled(String machineJid, MachineWatcherTypes types) {
        return machineWatcherDao.existByMachineJidAndWatcherType(machineJid, types.name());
    }

    @Override
    public MachineWatcher findByWatcherId(long watcherId) throws MachineWatcherNotFoundException {
        MachineWatcherModel machineWatcherModel = machineWatcherDao.findById(watcherId);
        if (machineWatcherModel != null) {
            return createMachineWatcherFromModel(machineWatcherModel);
        } else {
            throw new MachineWatcherNotFoundException("Machine Watcher not found.");
        }
    }

    @Override
    public MachineWatcher findByMachineJidAndWatcherType(String machineJid, MachineWatcherTypes types) {
        return createMachineWatcherFromModel(machineWatcherDao.findByMachineJidAndWatcherType(machineJid, types.name()));
    }

    @Override
    public List<MachineWatcher> findAll(String machineJid) {
        return machineWatcherDao.findSortedByFilters("id", "asc", "", ImmutableMap.of(MachineWatcherModel_.machineJid, machineJid), ImmutableMap.of(), 0, -1).stream().map(m -> createMachineWatcherFromModel(m)).collect(Collectors.toList());
    }

    @Override
    public void createWatcher(String machineJid, MachineWatcherTypes types, String conf) {
        MachineWatcherModel machineWatcherModel = new MachineWatcherModel();
        machineWatcherModel.machineJid = machineJid;
        machineWatcherModel.type = types.name();
        machineWatcherModel.conf = conf;

        machineWatcherDao.persist(machineWatcherModel, "michael", IdentityUtils.getIpAddress());
    }

    @Override
    public void updateWatcher(long machineWatcherId, String machineJid, MachineWatcherTypes types, String conf) {
        MachineWatcherModel machineWatcherModel = machineWatcherDao.findById(machineWatcherId);
        machineWatcherModel.machineJid = machineJid;
        machineWatcherModel.type = types.name();
        machineWatcherModel.conf = conf;

        machineWatcherDao.edit(machineWatcherModel, "michael", IdentityUtils.getIpAddress());
    }

    private MachineWatcher createMachineWatcherFromModel(MachineWatcherModel machineWatcherModel) {
        return new MachineWatcher(machineWatcherModel.id, machineWatcherModel.machineJid, MachineWatcherTypes.valueOf(machineWatcherModel.type), machineWatcherModel.conf);
    }
}

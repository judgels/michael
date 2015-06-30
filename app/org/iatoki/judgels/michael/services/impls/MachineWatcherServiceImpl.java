package org.iatoki.judgels.michael.services.impls;

import com.google.common.collect.ImmutableMap;
import org.iatoki.judgels.commons.IdentityUtils;
import org.iatoki.judgels.michael.MachineWatcher;
import org.iatoki.judgels.michael.MachineWatcherNotFoundException;
import org.iatoki.judgels.michael.services.MachineWatcherService;
import org.iatoki.judgels.michael.MachineWatcherType;
import org.iatoki.judgels.michael.models.daos.MachineWatcherDao;
import org.iatoki.judgels.michael.models.entities.MachineWatcherModel;
import org.iatoki.judgels.michael.models.entities.MachineWatcherModel_;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Named("machineWatcherService")
public final class MachineWatcherServiceImpl implements MachineWatcherService {

    private final MachineWatcherDao machineWatcherDao;

    @Inject
    public MachineWatcherServiceImpl(MachineWatcherDao machineWatcherDao) {
        this.machineWatcherDao = machineWatcherDao;
    }

    @Override
    public List<MachineWatcherType> findEnabledWatcherByMachineJid(String machineJid) {
        List<MachineWatcherModel> machineWatcherModels = machineWatcherDao.findSortedByFilters("id", "asc", "", ImmutableMap.of(MachineWatcherModel_.machineJid, machineJid), ImmutableMap.of(), 0, -1);
        return machineWatcherModels.stream().map(m -> MachineWatcherType.valueOf(m.type)).collect(Collectors.toList());
    }

    @Override
    public boolean isWatcherActivated(String machineJid, MachineWatcherType types) {
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
    public MachineWatcher findByMachineJidAndWatcherType(String machineJid, MachineWatcherType types) {
        return createMachineWatcherFromModel(machineWatcherDao.findByMachineJidAndWatcherType(machineJid, types.name()));
    }

    @Override
    public List<MachineWatcher> findAll(String machineJid) {
        return machineWatcherDao.findSortedByFilters("id", "asc", "", ImmutableMap.of(MachineWatcherModel_.machineJid, machineJid), ImmutableMap.of(), 0, -1).stream().map(m -> createMachineWatcherFromModel(m)).collect(Collectors.toList());
    }

    @Override
    public void createWatcher(String machineJid, MachineWatcherType types, String conf) {
        MachineWatcherModel machineWatcherModel = new MachineWatcherModel();
        machineWatcherModel.machineJid = machineJid;
        machineWatcherModel.type = types.name();
        machineWatcherModel.conf = conf;

        machineWatcherDao.persist(machineWatcherModel, "michael", IdentityUtils.getIpAddress());
    }

    @Override
    public void updateWatcher(long machineWatcherId, String machineJid, MachineWatcherType types, String conf) {
        MachineWatcherModel machineWatcherModel = machineWatcherDao.findById(machineWatcherId);
        machineWatcherModel.machineJid = machineJid;
        machineWatcherModel.type = types.name();
        machineWatcherModel.conf = conf;

        machineWatcherDao.edit(machineWatcherModel, "michael", IdentityUtils.getIpAddress());
    }

    @Override
    public void removeWatcher(String machineJid, MachineWatcherType types) {
        MachineWatcherModel machineWatcherModel = machineWatcherDao.findByMachineJidAndWatcherType(machineJid, types.name());

        machineWatcherDao.remove(machineWatcherModel);
    }

    private MachineWatcher createMachineWatcherFromModel(MachineWatcherModel machineWatcherModel) {
        return new MachineWatcher(machineWatcherModel.id, machineWatcherModel.machineJid, MachineWatcherType.valueOf(machineWatcherModel.type), machineWatcherModel.conf);
    }
}

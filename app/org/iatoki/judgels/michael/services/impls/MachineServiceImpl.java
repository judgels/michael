package org.iatoki.judgels.michael.services.impls;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.michael.Machine;
import org.iatoki.judgels.michael.MachineNotFoundException;
import org.iatoki.judgels.michael.MachineType;
import org.iatoki.judgels.michael.models.daos.MachineDao;
import org.iatoki.judgels.michael.models.entities.MachineModel;
import org.iatoki.judgels.michael.services.MachineService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Named("machineService")
public final class MachineServiceImpl implements MachineService {

    private final MachineDao machineDao;

    @Inject
    public MachineServiceImpl(MachineDao machineDao) {
        this.machineDao = machineDao;
    }

    @Override
    public List<Machine> findAll() {
        return machineDao.findAll().stream().map(m -> createMachineFromModel(m)).collect(Collectors.toList());
    }

    @Override
    public Page<Machine> pageMachines(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = machineDao.countByFilters(filterString, ImmutableMap.of(), ImmutableMap.of());
        List<MachineModel> machineModels = machineDao.findSortedByFilters(orderBy, orderDir, filterString, ImmutableMap.of(), ImmutableMap.of(), pageIndex * pageSize, pageSize);

        List<Machine> machines = Lists.transform(machineModels, m -> createMachineFromModel(m));

        return new Page<>(machines, totalPages, pageIndex, pageSize);
    }

    @Override
    public boolean existByMachineJid(String machineJid) {
        return machineDao.existsByJid(machineJid);
    }

    @Override
    public Machine findByMachineJid(String machineJid) {
        return createMachineFromModel(machineDao.findByJid(machineJid));
    }

    @Override
    public Machine findByMachineId(long machineId) throws MachineNotFoundException {
        MachineModel machineModel = machineDao.findById(machineId);
        if (machineModel != null) {
            return createMachineFromModel(machineModel);
        } else {
            throw new MachineNotFoundException("Machine not found.");
        }
    }

    @Override
    public void createMachine(String instanceName, String displayName, String baseDir, MachineType machineTypes, String ipAddress) {
        MachineModel machineModel = new MachineModel();
        machineModel.instanceName = instanceName;
        machineModel.displayName = displayName;
        machineModel.baseDir = baseDir;
        machineModel.type = machineTypes.name();
        machineModel.ipAddress = ipAddress;

        machineDao.persist(machineModel, "michael", IdentityUtils.getIpAddress());
    }

    @Override
    public void updateMachine(long machineId, String instanceName, String displayName, String baseDir, MachineType machineTypes, String ipAddress) throws MachineNotFoundException {
        MachineModel machineModel = machineDao.findById(machineId);
        if (machineModel != null) {
            machineModel.instanceName = instanceName;
            machineModel.displayName = displayName;
            machineModel.baseDir = baseDir;
            machineModel.type = machineTypes.name();
            machineModel.ipAddress = ipAddress;

            machineDao.edit(machineModel, "michael", IdentityUtils.getIpAddress());
        } else {
            throw new MachineNotFoundException("Machine not found.");
        }
    }

    private Machine createMachineFromModel(MachineModel machineModel) {
        return new Machine(machineModel.id, machineModel.jid, machineModel.instanceName, machineModel.displayName, machineModel.baseDir, MachineType.valueOf(machineModel.type), machineModel.ipAddress);
    }
}

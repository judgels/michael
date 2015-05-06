package org.iatoki.judgels.michael;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.iatoki.judgels.commons.IdentityUtils;
import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.michael.models.dao.interfaces.MachineAccessDao;
import org.iatoki.judgels.michael.models.domains.MachineAccessModel;
import org.iatoki.judgels.michael.models.domains.MachineAccessModel_;

import java.util.List;
import java.util.stream.Collectors;

public final class MachineAccessServiceImpl implements MachineAccessService {

    private final MachineAccessDao machineAccessDao;

    public MachineAccessServiceImpl(MachineAccessDao machineAccessDao) {
        this.machineAccessDao = machineAccessDao;
    }

    @Override
    public Page<MachineAccess> pageMachineAccesses(String machineJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = machineAccessDao.countByFilters(filterString, ImmutableMap.of(MachineAccessModel_.machineJid, machineJid), ImmutableMap.of());
        List<MachineAccessModel> machineAccessModels = machineAccessDao.findSortedByFilters(orderBy, orderDir, filterString, ImmutableMap.of(MachineAccessModel_.machineJid, machineJid), ImmutableMap.of(), pageIndex * pageSize, pageSize);

        List<MachineAccess> machineAccesses = Lists.transform(machineAccessModels, m -> createMachineAccessFromModel(m));

        return new Page<>(machineAccesses, totalPages, pageIndex, pageSize);
    }

    @Override
    public List<MachineAccess> findByMachineJid(String machineJid) {
        List<MachineAccessModel> machineAccessModels = machineAccessDao.findSortedByFilters("id", "asc", "", ImmutableMap.of(MachineAccessModel_.machineJid, machineJid), ImmutableMap.of(), 0, -1);
        return machineAccessModels.stream().map(m -> createMachineAccessFromModel(m)).collect(Collectors.toList());
    }

    @Override
    public MachineAccess findByMachineAccessId(long machineAccessId) throws MachineAccessNotFoundException {
        MachineAccessModel machineAccessModel = machineAccessDao.findById(machineAccessId);
        if (machineAccessModel != null) {
            return createMachineAccessFromModel(machineAccessModel);
        } else {
            throw new MachineAccessNotFoundException("Machine Access not found.");
        }
    }

    @Override
    public <T> T getMachineAccessConf(long machineAccessId, Class<T> clazz) throws MachineAccessNotFoundException {
        MachineAccessModel machineAccessModel = machineAccessDao.findById(machineAccessId);
        if (machineAccessModel != null) {
            return new Gson().fromJson(machineAccessModel.conf, clazz);
        } else {
            throw new MachineAccessNotFoundException("Machine Access not found.");
        }
    }

    @Override
    public void createKeyMachineAccess(String machineJid, String name, String username, String key, int port) {
        MachineAccessModel machineAccessModel = new MachineAccessModel();
        machineAccessModel.machineJid = machineJid;
        machineAccessModel.name = name;
        machineAccessModel.type = MachineAccesses.KEY.name();
        machineAccessModel.conf = new Gson().toJson(new MachineAccessKeyConf(username, key, port));

        machineAccessDao.persist(machineAccessModel, "michael", IdentityUtils.getIpAddress());
    }

    @Override
    public void createPasswordMachineAccess(String machineJid, String name, String username, String password, int port) {
        MachineAccessModel machineAccessModel = new MachineAccessModel();
        machineAccessModel.machineJid = machineJid;
        machineAccessModel.name = name;
        machineAccessModel.type = MachineAccesses.PASSWORD.name();
        machineAccessModel.conf = new Gson().toJson(new MachineAccessPasswordConf(username, password, port));

        machineAccessDao.persist(machineAccessModel, "michael", IdentityUtils.getIpAddress());

    }

    private MachineAccess createMachineAccessFromModel(MachineAccessModel machineAccessModel) {
        return new MachineAccess(machineAccessModel.id, machineAccessModel.machineJid, machineAccessModel.name, machineAccessModel.type);
    }
}

package org.iatoki.judgels.michael.services.impls;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.michael.MachineAccess;
import org.iatoki.judgels.michael.MachineAccessNotFoundException;
import org.iatoki.judgels.michael.services.MachineAccessService;
import org.iatoki.judgels.michael.MachineAccessType;
import org.iatoki.judgels.michael.models.daos.MachineAccessDao;
import org.iatoki.judgels.michael.models.entities.MachineAccessModel;
import org.iatoki.judgels.michael.models.entities.MachineAccessModel_;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Named("machineAccessService")
public final class MachineAccessServiceImpl implements MachineAccessService {

    private final MachineAccessDao machineAccessDao;

    @Inject
    public MachineAccessServiceImpl(MachineAccessDao machineAccessDao) {
        this.machineAccessDao = machineAccessDao;
    }

    @Override
    public Page<MachineAccess> getPageOfMachineAccesses(String machineJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = machineAccessDao.countByFilters(filterString, ImmutableMap.of(MachineAccessModel_.machineJid, machineJid), ImmutableMap.of());
        List<MachineAccessModel> machineAccessModels = machineAccessDao.findSortedByFiltersEq(orderBy, orderDir, filterString, ImmutableMap.of(MachineAccessModel_.machineJid, machineJid), pageIndex * pageSize, pageSize);

        List<MachineAccess> machineAccesses = Lists.transform(machineAccessModels, m -> createMachineAccessFromModel(m));

        return new Page<>(machineAccesses, totalPages, pageIndex, pageSize);
    }

    @Override
    public List<MachineAccess> getMachineAccessesByMachineJid(String machineJid) {
        List<MachineAccessModel> machineAccessModels = machineAccessDao.findSortedByFiltersEq("id", "asc", "", ImmutableMap.of(MachineAccessModel_.machineJid, machineJid), 0, -1);
        return machineAccessModels.stream().map(m -> createMachineAccessFromModel(m)).collect(Collectors.toList());
    }

    @Override
    public MachineAccess findMachineAccessById(long machineAccessId) throws MachineAccessNotFoundException {
        MachineAccessModel machineAccessModel = machineAccessDao.findById(machineAccessId);
        if (machineAccessModel != null) {
            return createMachineAccessFromModel(machineAccessModel);
        } else {
            throw new MachineAccessNotFoundException("Machine Access not found.");
        }
    }

    @Override
    public <T> T getMachineAccessConfById(long machineAccessId, Class<T> clazz) throws MachineAccessNotFoundException {
        MachineAccessModel machineAccessModel = machineAccessDao.findById(machineAccessId);
        if (machineAccessModel != null) {
            return new Gson().fromJson(machineAccessModel.conf, clazz);
        } else {
            throw new MachineAccessNotFoundException("Machine Access not found.");
        }
    }

    @Override
    public void createMachineAccess(String machineJid, String name, MachineAccessType types, String conf) {
        MachineAccessModel machineAccessModel = new MachineAccessModel();
        machineAccessModel.machineJid = machineJid;
        machineAccessModel.name = name;
        machineAccessModel.type = types.name();
        machineAccessModel.conf = conf;

        machineAccessDao.persist(machineAccessModel, "michael", IdentityUtils.getIpAddress());
    }

    private MachineAccess createMachineAccessFromModel(MachineAccessModel machineAccessModel) {
        return new MachineAccess(machineAccessModel.id, machineAccessModel.machineJid, machineAccessModel.name, machineAccessModel.type);
    }
}

package org.iatoki.judgels.michael.services.impls;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.iatoki.judgels.commons.IdentityUtils;
import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.michael.DashboardMachine;
import org.iatoki.judgels.michael.DashboardMachineNotFoundException;
import org.iatoki.judgels.michael.Machine;
import org.iatoki.judgels.michael.MachineType;
import org.iatoki.judgels.michael.models.daos.DashboardMachineDao;
import org.iatoki.judgels.michael.models.daos.MachineDao;
import org.iatoki.judgels.michael.models.entities.DashboardMachineModel;
import org.iatoki.judgels.michael.models.entities.DashboardMachineModel_;
import org.iatoki.judgels.michael.models.entities.MachineModel;
import org.iatoki.judgels.michael.models.entities.MachineModel_;
import org.iatoki.judgels.michael.services.DashboardMachineService;

import java.util.List;
import java.util.stream.Collectors;

public final class DashboardMachineServiceImpl implements DashboardMachineService {

    private final DashboardMachineDao dashboardMachineDao;
    private final MachineDao machineDao;

    public DashboardMachineServiceImpl(DashboardMachineDao dashboardMachineDao, MachineDao machineDao) {
        this.dashboardMachineDao = dashboardMachineDao;
        this.machineDao = machineDao;
    }

    @Override
    public boolean existByDashboardJidAndMachineJid(String dashboardJid, String machineJid) {
        return dashboardMachineDao.existByDashboardJidAndMachineJid(dashboardJid, machineJid);
    }

    @Override
    public DashboardMachine findByDashboardMachineId(long dashboardMachineId) throws DashboardMachineNotFoundException {
        DashboardMachineModel dashboardMachineModel = dashboardMachineDao.findById(dashboardMachineId);
        if (dashboardMachineModel != null) {
            return createDashboardMachineFromModel(dashboardMachineModel);
        } else {
            throw new DashboardMachineNotFoundException("Dashboard Machine not found.");
        }
    }

    @Override
    public Page<DashboardMachine> pageDashboardMachines(String dashboardJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = dashboardMachineDao.countByFilters(filterString, ImmutableMap.of(DashboardMachineModel_.dashboardJid, dashboardJid), ImmutableMap.of());
        List<DashboardMachineModel> dashboardMachineModels = dashboardMachineDao.findSortedByFilters(orderBy, orderDir, filterString, ImmutableMap.of(DashboardMachineModel_.dashboardJid, dashboardJid), ImmutableMap.of(), pageIndex * pageSize, pageSize);

        List<DashboardMachine> dashboardMachines = Lists.transform(dashboardMachineModels, m -> createDashboardMachineFromModel(m));

        return new Page<>(dashboardMachines, totalPages, pageIndex, pageSize);
    }

    @Override
    public List<Machine> findAllIncludedMachinesByDashboardJid(String dashboardJid) {
        List<String> includedMachineJids = dashboardMachineDao.findMachineJidsByDashboardJid(dashboardJid);
        List<MachineModel> machineModels = machineDao.findSortedByFilters("id", "asc", "", ImmutableMap.of(), ImmutableMap.of(MachineModel_.jid, includedMachineJids), 0, -1);

        return machineModels.stream().map(m -> createMachineFromModel(m)).collect(Collectors.toList());
    }

    @Override
    public List<Machine> findAllNotIncludedMachinesByDashboardJid(String dashboardJid) {
        List<String> includedMachineJids = dashboardMachineDao.findMachineJidsByDashboardJid(dashboardJid);
        List<MachineModel> machineModels = machineDao.findMachinesNotInMachineJids(includedMachineJids);

        return machineModels.stream().map(m -> createMachineFromModel(m)).collect(Collectors.toList());
    }

    @Override
    public void createDashboardMachine(String dashboardJid, String machineJid) {
        DashboardMachineModel dashboardMachineModel = new DashboardMachineModel();
        dashboardMachineModel.dashboardJid = dashboardJid;
        dashboardMachineModel.machineJid = machineJid;

        dashboardMachineDao.persist(dashboardMachineModel, "michael", IdentityUtils.getIpAddress());
    }

    @Override
    public void removeDashboardMachine(long dashboardMachineId) throws DashboardMachineNotFoundException {
        DashboardMachineModel dashboardMachineModel = dashboardMachineDao.findById(dashboardMachineId);
        if (dashboardMachineModel != null) {
            dashboardMachineDao.remove(dashboardMachineModel);
        } else {
            throw new DashboardMachineNotFoundException("Dashboard Machine not found.");
        }
    }

    private Machine createMachineFromModel(MachineModel machineModel) {
        return new Machine(machineModel.id, machineModel.jid, machineModel.instanceName, machineModel.displayName, machineModel.baseDir, MachineType.valueOf(machineModel.type), machineModel.ipAddress);
    }

    private DashboardMachine createDashboardMachineFromModel(DashboardMachineModel model) {
        return new DashboardMachine(model.id, model.dashboardJid, model.machineJid);
    }
}

package org.iatoki.judgels.michael.dashboard.machine;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.michael.machine.Machine;
import org.iatoki.judgels.michael.machine.MachineType;
import org.iatoki.judgels.michael.machine.MachineDao;
import org.iatoki.judgels.michael.machine.MachineModel;
import org.iatoki.judgels.michael.machine.MachineModel_;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public final class DashboardMachineServiceImpl implements DashboardMachineService {

    private final DashboardMachineDao dashboardMachineDao;
    private final MachineDao machineDao;

    @Inject
    public DashboardMachineServiceImpl(DashboardMachineDao dashboardMachineDao, MachineDao machineDao) {
        this.dashboardMachineDao = dashboardMachineDao;
        this.machineDao = machineDao;
    }

    @Override
    public boolean dashboardMachineExists(String dashboardJid, String machineJid) {
        return dashboardMachineDao.existsByDashboardJidAndMachineJid(dashboardJid, machineJid);
    }

    @Override
    public DashboardMachine findDashboardMachineById(long dashboardMachineId) throws DashboardMachineNotFoundException {
        DashboardMachineModel dashboardMachineModel = dashboardMachineDao.findById(dashboardMachineId);
        if (dashboardMachineModel != null) {
            return createDashboardMachineFromModel(dashboardMachineModel);
        } else {
            throw new DashboardMachineNotFoundException("Dashboard Machine not found.");
        }
    }

    @Override
    public Page<DashboardMachine> getPageOfDashboardMachines(String dashboardJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = dashboardMachineDao.countByFilters(filterString, ImmutableMap.of(DashboardMachineModel_.dashboardJid, dashboardJid), ImmutableMap.of());
        List<DashboardMachineModel> dashboardMachineModels = dashboardMachineDao.findSortedByFiltersEq(orderBy, orderDir, filterString, ImmutableMap.of(DashboardMachineModel_.dashboardJid, dashboardJid), pageIndex * pageSize, pageSize);

        List<DashboardMachine> dashboardMachines = Lists.transform(dashboardMachineModels, m -> createDashboardMachineFromModel(m));

        return new Page<>(dashboardMachines, totalPages, pageIndex, pageSize);
    }

    @Override
    public List<Machine> getMachinesInDashboardByDashboardJid(String dashboardJid) {
        List<String> includedMachineJids = dashboardMachineDao.getMachineJidsByDashboardJid(dashboardJid);
        List<MachineModel> machineModels = machineDao.findSortedByFiltersIn("id", "asc", "", ImmutableMap.of(MachineModel_.jid, includedMachineJids), 0, -1);

        return machineModels.stream().map(m -> createMachineFromModel(m)).collect(Collectors.toList());
    }

    @Override
    public List<Machine> getMachinesNotInMachinesByDashboardJid(String dashboardJid) {
        List<String> includedMachineJids = dashboardMachineDao.getMachineJidsByDashboardJid(dashboardJid);
        List<MachineModel> machineModels = machineDao.getMachinesNotInJids(includedMachineJids);

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

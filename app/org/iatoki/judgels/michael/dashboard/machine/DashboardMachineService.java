package org.iatoki.judgels.michael.dashboard.machine;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.michael.machine.Machine;

import java.util.List;

@ImplementedBy(DashboardMachineServiceImpl.class)
public interface DashboardMachineService {

    boolean dashboardMachineExists(String dashboardJid, String machineJid);

    DashboardMachine findDashboardMachineById(long dashboardMachineId) throws DashboardMachineNotFoundException;

    Page<DashboardMachine> getPageOfDashboardMachines(String dashboardJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    List<Machine> getMachinesInDashboardByDashboardJid(String dashboardJid);

    List<Machine> getMachinesNotInMachinesByDashboardJid(String dashboardJid);

    void createDashboardMachine(String dashboardJid, String machineJid);

    void removeDashboardMachine(long dashboardMachineId) throws DashboardMachineNotFoundException;
}

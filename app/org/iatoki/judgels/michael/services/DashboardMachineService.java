package org.iatoki.judgels.michael.services;

import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.michael.DashboardMachine;
import org.iatoki.judgels.michael.DashboardMachineNotFoundException;
import org.iatoki.judgels.michael.Machine;

import java.util.List;

public interface DashboardMachineService {

    boolean dashboardMachineExists(String dashboardJid, String machineJid);

    DashboardMachine findDashboardMachineById(long dashboardMachineId) throws DashboardMachineNotFoundException;

    Page<DashboardMachine> getPageOfDashboardMachines(String dashboardJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    List<Machine> getMachinesInDashboardByDashboardJid(String dashboardJid);

    List<Machine> getMachinesNotInMachinesByDashboardJid(String dashboardJid);

    void createDashboardMachine(String dashboardJid, String machineJid);

    void removeDashboardMachine(long dashboardMachineId) throws DashboardMachineNotFoundException;
}

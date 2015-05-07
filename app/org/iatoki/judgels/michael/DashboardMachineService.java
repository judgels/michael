package org.iatoki.judgels.michael;

import org.iatoki.judgels.commons.Page;

import java.util.List;

public interface DashboardMachineService {

    boolean existByDashboardJidAndMachineJid(String dashboardJid, String machineJid);

    DashboardMachine findByDashboardMachineId(long dashboardMachineId) throws DashboardMachineNotFoundException;

    Page<DashboardMachine> pageDashboardMachines(String dashboardJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    List<Machine> findAllIncludedMachinesByDashboardJid(String dashboardJid);

    List<Machine> findAllNotIncludedMachinesByDashboardJid(String dashboardJid);

    void createDashboardMachine(String dashboardJid, String machineJid);

    void removeDashboardMachine(long dashboardMachineId) throws DashboardMachineNotFoundException;
}

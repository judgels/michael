package org.iatoki.judgels.michael.models.dao.interfaces;

import org.iatoki.judgels.commons.models.daos.interfaces.Dao;
import org.iatoki.judgels.michael.models.domains.DashboardMachineModel;

import java.util.List;

public interface DashboardMachineDao extends Dao<Long, DashboardMachineModel> {

    boolean existByDashboardJidAndMachineJid(String dashboardJid, String machineJid);

    List<String> findMachineJidsByDashboardJid(String dashboardJid);
}

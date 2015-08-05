package org.iatoki.judgels.michael.models.daos;

import org.iatoki.judgels.play.models.daos.Dao;
import org.iatoki.judgels.michael.models.entities.DashboardMachineModel;

import java.util.List;

public interface DashboardMachineDao extends Dao<Long, DashboardMachineModel> {

    boolean existByDashboardJidAndMachineJid(String dashboardJid, String machineJid);

    List<String> findMachineJidsByDashboardJid(String dashboardJid);
}

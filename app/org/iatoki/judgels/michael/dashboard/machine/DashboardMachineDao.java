package org.iatoki.judgels.michael.dashboard.machine;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

import java.util.List;

@ImplementedBy(DashboardMachineHibernateDao.class)
public interface DashboardMachineDao extends Dao<Long, DashboardMachineModel> {

    boolean existsByDashboardJidAndMachineJid(String dashboardJid, String machineJid);

    List<String> getMachineJidsByDashboardJid(String dashboardJid);
}

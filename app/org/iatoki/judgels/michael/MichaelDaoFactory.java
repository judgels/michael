package org.iatoki.judgels.michael;

import org.iatoki.judgels.michael.models.dao.interfaces.ApplicationDao;
import org.iatoki.judgels.michael.models.dao.interfaces.ApplicationVersionDao;
import org.iatoki.judgels.michael.models.dao.interfaces.DashboardDao;
import org.iatoki.judgels.michael.models.dao.interfaces.DashboardMachineDao;
import org.iatoki.judgels.michael.models.dao.interfaces.MachineAccessDao;
import org.iatoki.judgels.michael.models.dao.interfaces.MachineDao;
import org.iatoki.judgels.michael.models.dao.interfaces.MachineTagDao;
import org.iatoki.judgels.michael.models.dao.interfaces.MachineWatcherDao;
import org.iatoki.judgels.michael.models.dao.interfaces.OperationDao;

public interface MichaelDaoFactory {

    ApplicationDao createApplicationDao();

    ApplicationVersionDao createApplicationVersionDao();

    DashboardDao createDashboardDao();

    DashboardMachineDao createDashboardMachineDao();

    MachineAccessDao createMachineAccessDao();

    MachineDao createMachineDao();

    MachineTagDao createMachineTagDao();

    MachineWatcherDao createMachineWatcherDao();

    OperationDao createOperationDao();
}

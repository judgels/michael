package org.iatoki.judgels.michael.models.daos;

import org.iatoki.judgels.michael.models.daos.ApplicationDao;
import org.iatoki.judgels.michael.models.daos.ApplicationVersionDao;
import org.iatoki.judgels.michael.models.daos.DashboardDao;
import org.iatoki.judgels.michael.models.daos.DashboardMachineDao;
import org.iatoki.judgels.michael.models.daos.MachineAccessDao;
import org.iatoki.judgels.michael.models.daos.MachineDao;
import org.iatoki.judgels.michael.models.daos.MachineTagDao;
import org.iatoki.judgels.michael.models.daos.MachineWatcherDao;
import org.iatoki.judgels.michael.models.daos.OperationDao;

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

package org.iatoki.judgels.michael.models.daos.impls;

import org.iatoki.judgels.michael.models.daos.MichaelDaoFactory;
import org.iatoki.judgels.michael.models.daos.impls.ApplicationHibernateDao;
import org.iatoki.judgels.michael.models.daos.impls.ApplicationVersionHibernateDao;
import org.iatoki.judgels.michael.models.daos.impls.DashboardHibernateDao;
import org.iatoki.judgels.michael.models.daos.impls.DashboardMachineHibernateDao;
import org.iatoki.judgels.michael.models.daos.impls.MachineAccessHibernateDao;
import org.iatoki.judgels.michael.models.daos.impls.MachineHibernateDao;
import org.iatoki.judgels.michael.models.daos.impls.MachineTagHibernateDao;
import org.iatoki.judgels.michael.models.daos.impls.MachineWatcherHibernateDao;
import org.iatoki.judgels.michael.models.daos.impls.OperationHibernateDao;
import org.iatoki.judgels.michael.models.daos.ApplicationDao;
import org.iatoki.judgels.michael.models.daos.ApplicationVersionDao;
import org.iatoki.judgels.michael.models.daos.DashboardDao;
import org.iatoki.judgels.michael.models.daos.DashboardMachineDao;
import org.iatoki.judgels.michael.models.daos.MachineAccessDao;
import org.iatoki.judgels.michael.models.daos.MachineDao;
import org.iatoki.judgels.michael.models.daos.MachineTagDao;
import org.iatoki.judgels.michael.models.daos.MachineWatcherDao;
import org.iatoki.judgels.michael.models.daos.OperationDao;

public final class HibernateMichaelDaoFactory implements MichaelDaoFactory {

    @Override
    public ApplicationDao createApplicationDao() {
        return new ApplicationHibernateDao();
    }

    @Override
    public ApplicationVersionDao createApplicationVersionDao() {
        return new ApplicationVersionHibernateDao();
    }

    @Override
    public DashboardDao createDashboardDao() {
        return new DashboardHibernateDao();
    }

    @Override
    public DashboardMachineDao createDashboardMachineDao() {
        return new DashboardMachineHibernateDao();
    }

    @Override
    public MachineAccessDao createMachineAccessDao() {
        return new MachineAccessHibernateDao();
    }

    @Override
    public MachineDao createMachineDao() {
        return new MachineHibernateDao();
    }

    @Override
    public MachineTagDao createMachineTagDao() {
        return new MachineTagHibernateDao();
    }

    @Override
    public MachineWatcherDao createMachineWatcherDao() {
        return new MachineWatcherHibernateDao();
    }

    @Override
    public OperationDao createOperationDao() {
        return new OperationHibernateDao();
    }
}

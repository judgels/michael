package org.iatoki.judgels.michael;

import org.iatoki.judgels.michael.models.dao.hibernate.ApplicationHibernateDao;
import org.iatoki.judgels.michael.models.dao.hibernate.ApplicationVersionHibernateDao;
import org.iatoki.judgels.michael.models.dao.hibernate.DashboardHibernateDao;
import org.iatoki.judgels.michael.models.dao.hibernate.DashboardMachineHibernateDao;
import org.iatoki.judgels.michael.models.dao.hibernate.MachineAccessHibernateDao;
import org.iatoki.judgels.michael.models.dao.hibernate.MachineHibernateDao;
import org.iatoki.judgels.michael.models.dao.hibernate.MachineTagHibernateDao;
import org.iatoki.judgels.michael.models.dao.hibernate.MachineWatcherHibernateDao;
import org.iatoki.judgels.michael.models.dao.hibernate.OperationHibernateDao;
import org.iatoki.judgels.michael.models.dao.interfaces.ApplicationDao;
import org.iatoki.judgels.michael.models.dao.interfaces.ApplicationVersionDao;
import org.iatoki.judgels.michael.models.dao.interfaces.DashboardDao;
import org.iatoki.judgels.michael.models.dao.interfaces.DashboardMachineDao;
import org.iatoki.judgels.michael.models.dao.interfaces.MachineAccessDao;
import org.iatoki.judgels.michael.models.dao.interfaces.MachineDao;
import org.iatoki.judgels.michael.models.dao.interfaces.MachineTagDao;
import org.iatoki.judgels.michael.models.dao.interfaces.MachineWatcherDao;
import org.iatoki.judgels.michael.models.dao.interfaces.OperationDao;

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

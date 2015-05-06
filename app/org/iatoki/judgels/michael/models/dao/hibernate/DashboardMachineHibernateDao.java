package org.iatoki.judgels.michael.models.dao.hibernate;

import org.iatoki.judgels.commons.models.daos.hibernate.AbstractHibernateDao;
import org.iatoki.judgels.michael.models.dao.interfaces.DashboardMachineDao;
import org.iatoki.judgels.michael.models.domains.DashboardMachineModel;

public final class DashboardMachineHibernateDao extends AbstractHibernateDao<Long, DashboardMachineModel> implements DashboardMachineDao {

    public DashboardMachineHibernateDao() {
        super(DashboardMachineModel.class);
    }
}

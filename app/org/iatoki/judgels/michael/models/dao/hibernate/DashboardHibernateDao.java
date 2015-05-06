package org.iatoki.judgels.michael.models.dao.hibernate;

import org.iatoki.judgels.commons.models.daos.hibernate.AbstractJudgelsHibernateDao;
import org.iatoki.judgels.michael.models.dao.interfaces.DashboardDao;
import org.iatoki.judgels.michael.models.domains.DashboardModel;

public final class DashboardHibernateDao extends AbstractJudgelsHibernateDao<DashboardModel> implements DashboardDao {

    public DashboardHibernateDao() {
        super(DashboardModel.class);
    }
}

package org.iatoki.judgels.michael.models.daos.impls;

import org.iatoki.judgels.commons.models.daos.hibernate.AbstractJudgelsHibernateDao;
import org.iatoki.judgels.michael.models.daos.DashboardDao;
import org.iatoki.judgels.michael.models.entities.DashboardModel;

public final class DashboardHibernateDao extends AbstractJudgelsHibernateDao<DashboardModel> implements DashboardDao {

    public DashboardHibernateDao() {
        super(DashboardModel.class);
    }
}

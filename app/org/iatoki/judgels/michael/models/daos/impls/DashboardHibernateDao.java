package org.iatoki.judgels.michael.models.daos.impls;

import org.iatoki.judgels.play.models.daos.hibernate.AbstractJudgelsHibernateDao;
import org.iatoki.judgels.michael.models.daos.DashboardDao;
import org.iatoki.judgels.michael.models.entities.DashboardModel;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named("dashboardDao")
public final class DashboardHibernateDao extends AbstractJudgelsHibernateDao<DashboardModel> implements DashboardDao {

    public DashboardHibernateDao() {
        super(DashboardModel.class);
    }
}

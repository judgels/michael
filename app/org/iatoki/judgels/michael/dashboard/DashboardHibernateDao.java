package org.iatoki.judgels.michael.dashboard;

import org.iatoki.judgels.play.model.AbstractJudgelsHibernateDao;

import javax.inject.Singleton;

@Singleton
public final class DashboardHibernateDao extends AbstractJudgelsHibernateDao<DashboardModel> implements DashboardDao {

    public DashboardHibernateDao() {
        super(DashboardModel.class);
    }
}

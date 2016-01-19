package org.iatoki.judgels.michael.dashboard;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.JudgelsDao;

@ImplementedBy(DashboardHibernateDao.class)
public interface DashboardDao extends JudgelsDao<DashboardModel> {

}

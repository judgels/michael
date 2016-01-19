package org.iatoki.judgels.michael.application.version;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.michael.application.ApplicationHibernateDao;
import org.iatoki.judgels.play.model.JudgelsDao;
import org.iatoki.judgels.michael.application.ApplicationModel;

@ImplementedBy(ApplicationHibernateDao.class)
public interface ApplicationDao extends JudgelsDao<ApplicationModel> {

}

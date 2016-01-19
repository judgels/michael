package org.iatoki.judgels.michael.application;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.michael.application.version.ApplicationVersionHibernateDao;
import org.iatoki.judgels.play.model.Dao;
import org.iatoki.judgels.michael.application.version.ApplicationVersionModel;

@ImplementedBy(ApplicationVersionHibernateDao.class)
public interface ApplicationVersionDao extends Dao<Long, ApplicationVersionModel> {

}

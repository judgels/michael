package org.iatoki.judgels.michael.application.version;

import org.iatoki.judgels.play.model.AbstractHibernateDao;
import org.iatoki.judgels.michael.application.ApplicationVersionDao;

import javax.inject.Singleton;

@Singleton
public final class ApplicationVersionHibernateDao extends AbstractHibernateDao<Long, ApplicationVersionModel> implements ApplicationVersionDao {

    public ApplicationVersionHibernateDao() {
        super(ApplicationVersionModel.class);
    }
}

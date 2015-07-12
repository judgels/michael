package org.iatoki.judgels.michael.models.daos.impls;

import org.iatoki.judgels.play.models.daos.hibernate.AbstractHibernateDao;
import org.iatoki.judgels.michael.models.daos.ApplicationVersionDao;
import org.iatoki.judgels.michael.models.entities.ApplicationVersionModel;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named("applicationVersionDao")
public final class ApplicationVersionHibernateDao extends AbstractHibernateDao<Long, ApplicationVersionModel> implements ApplicationVersionDao {

    public ApplicationVersionHibernateDao() {
        super(ApplicationVersionModel.class);
    }
}

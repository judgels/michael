package org.iatoki.judgels.michael.models.daos.impls;

import org.iatoki.judgels.commons.models.daos.hibernate.AbstractHibernateDao;
import org.iatoki.judgels.michael.models.daos.ApplicationVersionDao;
import org.iatoki.judgels.michael.models.entities.ApplicationVersionModel;

public final class ApplicationVersionHibernateDao extends AbstractHibernateDao<Long, ApplicationVersionModel> implements ApplicationVersionDao {

    public ApplicationVersionHibernateDao() {
        super(ApplicationVersionModel.class);
    }
}

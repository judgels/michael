package org.iatoki.judgels.michael.models.dao.hibernate;

import org.iatoki.judgels.commons.models.daos.hibernate.AbstractHibernateDao;
import org.iatoki.judgels.michael.models.dao.interfaces.ApplicationVersionDao;
import org.iatoki.judgels.michael.models.domains.ApplicationVersionModel;

public final class ApplicationVersionHibernateDao extends AbstractHibernateDao<Long, ApplicationVersionModel> implements ApplicationVersionDao {

    public ApplicationVersionHibernateDao() {
        super(ApplicationVersionModel.class);
    }
}

package org.iatoki.judgels.michael.models.dao.hibernate;

import org.iatoki.judgels.commons.models.daos.hibernate.AbstractJudgelsHibernateDao;
import org.iatoki.judgels.michael.models.dao.interfaces.ApplicationDao;
import org.iatoki.judgels.michael.models.domains.ApplicationModel;

public final class ApplicationHibernateDao extends AbstractJudgelsHibernateDao<ApplicationModel> implements ApplicationDao {

    public ApplicationHibernateDao() {
        super(ApplicationModel.class);
    }
}

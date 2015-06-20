package org.iatoki.judgels.michael.models.daos.impls;

import org.iatoki.judgels.commons.models.daos.hibernate.AbstractJudgelsHibernateDao;
import org.iatoki.judgels.michael.models.daos.ApplicationDao;
import org.iatoki.judgels.michael.models.entities.ApplicationModel;

public final class ApplicationHibernateDao extends AbstractJudgelsHibernateDao<ApplicationModel> implements ApplicationDao {

    public ApplicationHibernateDao() {
        super(ApplicationModel.class);
    }
}
